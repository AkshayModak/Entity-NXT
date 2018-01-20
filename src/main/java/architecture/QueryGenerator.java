package architecture;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import architecture.utils.DebugWrapper;

public class QueryGenerator {

    private Map<String, Object> tablesMap = new HashMap<String, Object>();
    public static final String className = QueryGenerator.class.getName();
    private Connection conn = null;

    public QueryGenerator(Map<String, Object> tablesMap, Connection conn) {
        this.tablesMap = tablesMap;
        this.conn = conn;
    }

    protected Map<String, Object> createTableQueries() throws SQLException {

        DatabaseMetaData metadata = null;
        metadata = conn.getMetaData();

        ResultSet resultSet;
        Map<String, Object> queryMap = new HashMap<String, Object>();
        List<String> queryList = new ArrayList<String>();

        if (tablesMap.isEmpty()) {
            queryMap.put("error", "error");
            return queryMap;
        }

        for (Map.Entry<String, Object> table : tablesMap.entrySet()) {
            String tableName = table.getKey();
            ResultSet tableRs = metadata.getTables(null, null, tableName, null);

            if (!tableRs.next()) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("CREATE TABLE IF NOT EXISTS ");
                stringBuffer.append(tableName);
                stringBuffer.append(" (");

                List<Map<String, Object>> columnsList = (List<Map<String, Object>>) tablesMap.get(tableName);
                int column_index = 0;
                for (Map<String, Object> column : columnsList) {
                    stringBuffer.append(column.get("name"));
                    stringBuffer.append(" " + column.get("data-type"));
                    if (!("int").equalsIgnoreCase((String) column.get("data-type"))) {
                        stringBuffer.append("(");
                        stringBuffer.append(column.get("column-size"));
                        stringBuffer.append(")");
                    }
                    if (column.containsKey("unique")) {
                        stringBuffer.append(" UNIQUE");
                    }
                    if (column.containsKey("null") && ("false").equalsIgnoreCase((String) column.get("null"))) {
                        stringBuffer.append(" NOT NULL");
                    }
                    column_index++;
                    if (column_index < columnsList.size()) {
                        stringBuffer.append(", ");
                    }
                }
                stringBuffer.append(");");
                DebugWrapper.logDebug("Creating Table: "+tableName, className);
                queryList.add(stringBuffer.toString());
            }
        }
        queryMap.put("create_table", queryList);
        return queryMap;
    }

    protected Map<String, Object> dropTableQueries() throws SQLException {

        DatabaseMetaData metadata = null;
        metadata = conn.getMetaData();

        ResultSet resultSet;
        Map<String, Object> queryMap = new HashMap<String, Object>();
        List<String> queryList = new ArrayList<String>();

        ResultSet rs = metadata.getTables(null, null, "%", null);
        while (rs.next()) {
            String mysqlTableName = rs.getString(3);
            if (tablesMap.isEmpty()) {
                DebugWrapper.logDebug("Dropping Table: "+mysqlTableName, className);
                queryList.add("DROP TABLE " + mysqlTableName);
            } else {
                if (tablesMap.get(mysqlTableName) == null) {
                    DebugWrapper.logDebug("Dropping Table: "+mysqlTableName, className);
                    queryList.add("DROP TABLE " + mysqlTableName);
                }
            }
        }

        queryMap.put("drop_table", queryList);
        return queryMap;
    }

    /*private Map<String, Object> setCreateQuery() {

        DatabaseMetaData metadata = null;
        try {
            metadata = conn.getMetaData();
        } catch (SQLException e) {
            System.out.println(e);
        }

        ResultSet resultSet;
        Map<String, Object> queriesMap = new HashMap<String, Object>();

        try {
            Map<String, Object> map = tablesMap;
            for (Map.Entry<String, Object> table : map.entrySet()) {
                String tableName = table.getKey();
                ResultSet tableRs = metadata.getTables(null, null, tableName, null);
                HashSet<String> createQueries = new HashSet<String>();
                if (!tableRs.next()) {
                    for (Map<String, Object> column : (List<Map<String, Object>>) table.getValue()) {
                        String createQuery = "";
                        String dataType = (String) column.get("data-type");

                        if ("int".equals(dataType)) {
                            createQuery += " " + column.get("name") + " " + dataType;
                        } else {
                            createQuery += " " + column.get("name") + " " + dataType + "(" + column.get("column-size")
                                    + ")";
                        }

                        if ("true".equals(column.get("primary-key"))) {
                            String primaryKey = " primary key";
                            createQuery += primaryKey;
                        } else if ("true".equals(column.get("distinct"))) {
                            String distinct = " DISTINCT";
                            createQuery += distinct;
                        }

                        if ("false".equals(column.get("null")) && "true".equals(column.get("primary-key"))) {
                            createQuery += " NOT NULL";
                        }

                        if ("true".equals(column.get("auto-increment"))) {
                            createQuery += " AUTO_INCREMENT";
                        }

                        if (DefaultObjects.isNotEmpty(createQuery)) {
                            createQueries.add(createQuery);
                        }
                    }
                }
                if (!createQueries.isEmpty()) {
                    queriesMap.put("create-" + tableName, createQueries);
                }
            }

            resultSet = metadata.getTables(null, null, "%", null);
            while (resultSet.next()) {
                String tableName = resultSet.getString(3);
                List<Map<String, Object>> table = (List<Map<String, Object>>) tablesMap.get(tableName);

                if (table != null) {
                    HashSet<String> alterQueries = new HashSet<String>();
                    HashSet<String> modifyQueries = new HashSet<String>();
                    Boolean removeColumn = false;
                    for (Map<String, Object> column : table) {
                        String columnName = (String) column.get("name");

                        ResultSet tableRs = metadata.getColumns(null, null, tableName, columnName);

                        //HACK: Need to improve the logic to remove column if not defined in the XML.
                        Boolean keepColumn = true;
                        for (Map<String, Object> tableColumn : table) {
                            columnName = (String) tableColumn.get("name");
                            ResultSet columnRs = metadata.getColumns(null, null, tableName, null);
                            while (columnRs.next()) {
                                String rsTableName = columnRs.getString("TABLE_NAME");
                                String rsColumnName = columnRs.getString("COLUMN_NAME");
                                keepColumn = Utility.hasMapKeyValuePair(table, "name", rsColumnName);

                                if (!keepColumn) {
                                    String alterQuery = " DROP COLUMN " + rsColumnName;
                                    if (alterQueries.contains(alterQuery)) {
                                        continue;
                                    }
                                    alterQueries.add(alterQuery);
                                    queriesMap.put("alter-" + tableName, alterQueries);
                                }
                            }
                        }

                        if (!tableRs.next()) {
                            String alterQuery = " ADD";
                            String dataType = (String) column.get("data-type");
                            alterQuery += " " + columnName + " " + dataType + "(" + column.get("column-size") + ")";

                            if ("true".equals(column.get("primary-key"))) {
                                String primaryKey = " primary key";
                                alterQuery += primaryKey;
                            } else if ("true".equals(column.get("distinct"))) {
                                String distinct = " distinct";
                                alterQuery += distinct;
                            }

                            if ("false".equals(column.get("null"))) {
                                alterQuery += " NOT NULL";
                            }

                            if ("true".equals(column.get("auto-increment"))) {
                                alterQuery += " AUTO_INCREMENT";
                            }

                            if (DefaultObjects.isNotEmpty(alterQuery)) {
                                alterQueries.add(alterQuery);
                            }
                            if (!alterQueries.isEmpty()) {
                                queriesMap.put("alter-" + tableName, alterQueries);
                            }
                        } else {

                            *//*
                             * Reference --
                             * https://docs.oracle.com/javase/7/docs/api/java/
                             * sql/DatabaseMeta
                             * Data.html#getColumns(java.lang.String,%20java.
                             * lang.String,%20java.
                             * lang.String,%20java.lang.String)
                             *//*
                            String modifyQuery = "";
                            columnName = (String) column.get("name");
                            ResultSet columnRs = metadata.getColumns(null, null, tableName, columnName);
                            while (columnRs.next()) {
                                String rsTableName = columnRs.getString("TABLE_NAME");
                                String rsColumnName = columnRs.getString("COLUMN_NAME");

                                if (rsTableName.equalsIgnoreCase(tableName)
                                        && rsColumnName.equalsIgnoreCase(columnName)) {
                                    Boolean columnText = true;
                                    Boolean dataTypeText = true;
                                    String rsDataType = getJdbcTypeName(columnRs.getShort("DATA_TYPE"));
                                    int rsColumnSize = columnRs.getInt("COLUMN_SIZE");
                                    *//*
                                     * if (rsColumnSize == 10) { rsColumnSize =
                                     * Integer.parseInt((String)
                                     * column.get("column-size")); }
                                     *//*
                                    String rsNullable = columnRs.getString("NULLABLE");
                                    String nullable = null;
                                    if (rsNullable.equalsIgnoreCase(String.valueOf(0))) {
                                        nullable = "true";
                                    } else if (rsNullable.equalsIgnoreCase(String.valueOf(1))) {
                                        nullable = "false";
                                    }
                                    String rsIsAutoIncrement = columnRs.getString("IS_AUTOINCREMENT");
                                    String autoIncrement = null;
                                    if (rsIsAutoIncrement.equalsIgnoreCase("NO")) {
                                        autoIncrement = "false";
                                    } else if (rsIsAutoIncrement.equalsIgnoreCase("YES")) {
                                        autoIncrement = "true";
                                    }

                                    if (column.get("data-type").equals("int")) {
                                        column.put("data-type", "INTEGER");
                                    }

                                    if (!rsDataType.equalsIgnoreCase((String) column.get("data-type"))) {
                                        String dataType = (String) column.get("data-type");
                                        dataTypeText = !dataTypeText;
                                        String columnSize = (String) column.get("column-size");
                                        if (columnText) {
                                            columnText = !columnText;
                                            if (column.get("data-type").equals("INTEGER")) {
                                                modifyQuery += " " + columnName + " " + dataType;
                                            } else {
                                                modifyQuery += " " + columnName + " " + dataType + "(" + columnSize
                                                        + ")";
                                            }
                                        } else {
                                            if (column.get("data-type").equals("INTEGER")) {
                                                modifyQuery += " " + dataType;
                                            } else {
                                                modifyQuery += " " + dataType + "(" + columnSize + ")";
                                            }
                                        }
                                    }
                                    int tableColumnSize = 0;
                                    if (column.get("column-size") != null) {
                                        tableColumnSize = Integer.parseInt((String) column.get("column-size"));
                                    }
                                    if (column.get("column-size") != null && rsColumnSize != tableColumnSize) {
                                        String columnSize = (String) column.get("column-size");
                                        if (column.get("data-type").equals("INTEGER")) {
                                            throw new SQLException("Fatal Error: Column Size of Integer defined.");
                                        }
                                        if (columnText) {
                                            columnText = !columnText;
                                            modifyQuery += " " + columnName + " " + (String) column.get("data-type")
                                                    + "(" + columnSize + ")";
                                        } else if (dataTypeText) {
                                            modifyQuery += " " + (String) column.get("data-type") + "(" + columnSize
                                                    + ")";
                                        } else {
                                            modifyQuery += "(" + columnSize + ")";
                                        }
                                    }

                                    if (DefaultObjects.isNotEmpty(column.get("null"))
                                            && "false".equals(column.get("null"))
                                            && (nullable.equalsIgnoreCase((String) column.get("null")))) {
                                        if (columnText) {
                                            columnText = !columnText;
                                            String columnSize = (String) column.get("column-size");
                                            if (column.get("data-type").equals("INTEGER")) {
                                                modifyQuery += " " + columnName + " " + (String) column.get("data-type")
                                                        + " NOT NULL";
                                            } else {
                                                modifyQuery += " " + columnName + " " + (String) column.get("data-type")
                                                        + "(" + columnSize + ")" + " NOT NULL";
                                            }
                                        } else {
                                            modifyQuery += " NOT NULL";
                                        }
                                    }

                                    if ((DefaultObjects.isNotEmpty(column.get("auto-increment"))
                                            && "true".equalsIgnoreCase((String) column.get("auto-increment")))
                                            && (DefaultObjects.isNotEmpty(column.get("auto-increment"))
                                            && !autoIncrement
                                            .equalsIgnoreCase((String) column.get("auto-increment")))) {
                                        if (columnText) {
                                            columnText = !columnText;
                                            String columnSize = (String) column.get("column-size");
                                            if (column.get("data-type").equals("INTEGER")) {
                                                modifyQuery += " " + columnName + " " + column.get("data-type")
                                                        + " AUTO_INCREMENT";
                                            } else {
                                                modifyQuery += " " + columnName + " " + column.get("data-type") + "("
                                                        + columnSize + ")" + " AUTO_INCREMENT";
                                            }
                                        } else {
                                            modifyQuery += " AUTO_INCREMENT";
                                        }
                                    }
                                    columnRs.close();
                                    columnRs = metadata.getPrimaryKeys(null, null, tableName);

                                    while (columnRs.next()) {
                                        String pkColName = columnRs.getString("COLUMN_NAME");
                                        DebugWrapper.logDebug("====pkColName==="+pkColName, className);
                                        if (DefaultObjects.isNotEmpty(column.get("primary-key"))
                                                && "true".equals(column.get("primary-key"))
                                                && !pkColName.equalsIgnoreCase(columnName)) {
                                            if (columnText) {
                                                columnText = !columnText;
                                                String columnSize = (String) column.get("column-size");
                                                if (column.get("data-type").equals("INTEGER")) {
                                                    modifyQuery += " " + columnName + " "
                                                            + (String) column.get("data-type") + " primary key";
                                                } else {
                                                    modifyQuery += " " + columnName + " "
                                                            + (String) column.get("data-type") + "(" + columnSize + ")"
                                                            + " primary key";
                                                }
                                            } else {
                                                modifyQuery += " primary key";
                                            }
                                        } else if (DefaultObjects.isEmpty(column.get("primary-key"))
                                                && pkColName.equalsIgnoreCase(columnName)
                                                && DefaultObjects.isEmpty(column.get("auto-increment"))) {
                                            *//*String dropPrimaryKey = *//*
                                            DebugWrapper.logDebug("===Primary Key can be removed===", className);
                                        }
                                    }

                                    if (DefaultObjects.isNotEmpty(modifyQuery)) {
                                        modifyQueries.add(modifyQuery);
                                    }
                                }
                            }
                            if (!modifyQuery.isEmpty()) {
                                queriesMap.put("modify-" + tableName, modifyQueries);
                            }
                        }
                    }
                } else if (table == null) {
                    *//*
                     * System.out.println("====table doesn't exist==="+tableName
                     * );
                     *//*
                    queriesMap.put("drop-" + tableName, tableName);

                }
            }
        } catch (SQLException e) {
            DebugWrapper.logError(e.getMessage(), className);
        }
        return queriesMap;
    }*/
}