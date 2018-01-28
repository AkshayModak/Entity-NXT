package architecture;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Field;

import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import architecture.utils.DebugWrapper;
import architecture.utils.Utility;

public class QueryGenerator {

    private Map<String, Object> tablesMap = new HashMap<String, Object>();
    public static final String className = QueryGenerator.class.getName();
    private Connection conn = null;

    public QueryGenerator(Map<String, Object> tablesMap, Connection conn) {
        this.tablesMap = tablesMap;
        this.conn = conn;
    }

    private String getJdbcTypeName(int jdbcType) {
        Map map = new HashMap();

        // Get all field in java.sql.Types
        Field[] fields = java.sql.Types.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                String name = fields[i].getName();
                Integer value = (Integer) fields[i].get(null);
                map.put(value, name);
            } catch (IllegalAccessException e) {
            }
        }
        return (String) map.get(jdbcType);
    }

    protected Map<String, Object> createTableQueries() throws SQLException {

        if (tablesMap.isEmpty()) {
            Utility.returnError();
        }

        DatabaseMetaData metadata = null;
        metadata = conn.getMetaData();

        ResultSet resultSet;
        Map<String, Object> queryMap = new HashMap<String, Object>();
        List<String> queryList = new ArrayList<String>();

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
                    Map<String, Object> validate_result = validateColumns(column);
                    if (!("success").equalsIgnoreCase((String) validate_result.get("status"))) {
                        return Utility.returnError();
                    }
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
                    if (column.containsKey("nullable") && ("false").equalsIgnoreCase((String) column.get("nullable"))) {
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

    protected Map<String, Object> createPrimaryKeyConstraint(String tableName) throws SQLException {

        if (tableName == null || tableName.length() <= 0) {
            Utility.returnError();
        }

        DatabaseMetaData metadata = null;
        metadata = conn.getMetaData();

        ResultSet resultSet;
        Map<String, Object> queryMap = new HashMap<String, Object>();
        List<String> queryList = new ArrayList<String>();

        ResultSet tableRs = metadata.getTables(null, null, tableName, null);

        if (tableRs.next()) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("ALTER TABLE ");
            stringBuffer.append(tableName);
            stringBuffer.append(" ADD PRIMARY KEY ");
            stringBuffer.append("(");

            List<Map<String, Object>> columnsList = (List<Map<String, Object>>) tablesMap.get(tableName);
            int pk_index = 0;

            String catalog = tableRs.getString("TABLE_CAT");
            String schema = tableRs.getString("TABLE_SCHEM");
            ResultSet primaryKeys = metadata.getPrimaryKeys(catalog, schema, tableName);
            List<String> pk_list = new ArrayList();
            while (primaryKeys.next()) {
                pk_list.add(primaryKeys.getString("COLUMN_NAME"));
            }

            //TODO: Need to improve the code to update primary key.
            Boolean dropPrimaryKey = false;
            for (Map<String, Object> column : columnsList) {
                if (column.containsKey("primary-key") && ("true").equalsIgnoreCase((String) column.get("primary-key"))) {
                    if (!column.containsKey("nullable") || !("false").equalsIgnoreCase((String) column.get("nullable"))) {
                        DebugWrapper.logDebug("Can't Update Primary Key for table " + tableName + " Column " + column.get("name") + " is not nullable", className);
                        break;
                    }
                    if (!pk_list.isEmpty()) {
                        if (!pk_list.contains((String) column.get("name"))) {
                            dropPrimaryKey = true;
                            break;
                        }
                    }
                } else if (!pk_list.isEmpty() && pk_list.contains((String) column.get("name"))) {
                    dropPrimaryKey = true;
                    break;
                }
            }
            if (dropPrimaryKey) {
                queryList.add("ALTER TABLE " + tableName + " DROP PRIMARY KEY");
                for (Map<String, Object> column : columnsList) {
                    if (column.containsKey("primary-key") && ("true").equalsIgnoreCase((String) column.get("primary-key"))) {
                        if (pk_index > 0) {
                            stringBuffer.append(",");
                        }
                        stringBuffer.append(column.get("name"));
                        pk_index++;
                    }
                }
            } else if (pk_list.isEmpty()) {
                for (Map<String, Object> column : columnsList) {
                    if (column.containsKey("primary-key") && ("true").equalsIgnoreCase((String) column.get("primary-key"))) {
                        if (pk_index > 0) {
                            stringBuffer.append(",");
                        }
                        stringBuffer.append(column.get("name"));
                        pk_index++;
                    }
                }
            }
            stringBuffer.append(");");
            if (pk_index > 0) {
                DebugWrapper.logDebug("Adding Primary Key Constraint on Table: "+tableName, className);
                queryList.add(stringBuffer.toString());
            }
        }
        queryMap.put("pk_constraint", queryList);
        return queryMap;
    }

    protected Map<String, Object> updateColumn() throws SQLException {

        if (tablesMap.isEmpty()) {
            Utility.returnError();
        }

        DatabaseMetaData metadata = null;
        metadata = conn.getMetaData();
        List<String> alterList = new ArrayList<>();

        ResultSet resultSet;
        Map<String, Object> queryMap = new HashMap<String, Object>();
        List<String> queryList = new ArrayList<String>();

        for (Map.Entry<String, Object> table : tablesMap.entrySet()) {
            String tableName = table.getKey();
            List<Map<String, Object>> tableList = (List<Map<String, Object>>) tablesMap.get(tableName);

            ResultSet columnRs = metadata.getColumns(null, null, tableName, "%");

            Boolean dropColumn = false;
            while (columnRs.next()) {
                String rsColumnName = columnRs.getString("COLUMN_NAME");
                Boolean hasKeyValue = Utility.hasMapKeyValuePair(tableList, "name", rsColumnName);
                if (!hasKeyValue) {
                    queryList.add("ALTER TABLE " + tableName + " DROP COLUMN " + rsColumnName);
                }
            }

            for (Map<String, Object> columnMap : tableList) {
                Map<String, Object> validate_result = validateColumns(columnMap);
                if (!("success").equalsIgnoreCase((String) validate_result.get("status"))) {
                    return Utility.returnError();
                }
                alterList.clear();
                String columnName = (String) columnMap.get("name");
                columnRs = metadata.getColumns(null, null, tableName, columnName);

                if (columnRs.next()) {
                    String rsColumnName = columnRs.getString("COLUMN_NAME");
                    String rsDataType = getJdbcTypeName(columnRs.getShort("DATA_TYPE"));
                    int rsColumnSize = columnRs.getInt("COLUMN_SIZE");
                    Boolean isNullable = isNullable(columnRs.getString("NULLABLE"));
                    Boolean rsIsAutoIncrement = isAutoIncrement(columnRs.getString("IS_AUTOINCREMENT"));

                    if (("int").equalsIgnoreCase((String) columnMap.get("data-type"))) {
                        columnMap.put("data-type", "INTEGER");
                        columnMap.put("column-size", "11"); //Integer column-size will always be 11
                    }

                    if (!rsDataType.equalsIgnoreCase((String) columnMap.get("data-type"))) {
                        alterList.add(columnMap.get("data-type") + " (" + columnMap.get("column-size") + ") ");
                    }

                    if (columnMap.containsKey("column-size") && (rsColumnSize != (Integer.parseInt((String) columnMap.get("column-size"))))
                            && (rsDataType.equalsIgnoreCase((String) columnMap.get("data-type")))) {
                        if (!("INTEGER").equalsIgnoreCase((String) columnMap.get("data-type"))) {
                            alterList.add(columnMap.get("data-type") + " (" + columnMap.get("column-size") + ") ");
                        }
                    }
                    if(columnMap.containsKey("nullable") && (isNullable != (Boolean.parseBoolean((String) columnMap.get("nullable")))) && !("true".equalsIgnoreCase((String) columnMap.get("auto-increment")))) {
                        if ("false".equalsIgnoreCase((String) columnMap.get("nullable"))) {
                            if (!("INTEGER").equalsIgnoreCase((String) columnMap.get("data-type"))) {
                                alterList.add((String) columnMap.get("data-type") + " (" + columnMap.get("column-size") + ") ");
                            } else if (("INTEGER").equalsIgnoreCase((String) columnMap.get("data-type"))) {
                                alterList.add((String) columnMap.get("data-type"));
                            }
                            alterList.add(" NOT NULL");
                        } else {
                            alterList.add(" NULL");
                        }
                    } else if (!columnMap.containsKey("nullable")) {
                        if (!isNullable) {
                            if (!("INTEGER").equalsIgnoreCase((String) columnMap.get("data-type"))) {
                                alterList.add((String) columnMap.get("data-type") + " (" + columnMap.get("column-size") + ") ");
                            } else if (("INTEGER").equalsIgnoreCase((String) columnMap.get("data-type"))) {
                                alterList.add((String) columnMap.get("data-type"));
                            }
                            alterList.add(" NULL");
                        }
                    }

                    if (columnMap.containsKey("auto-increment") && (("INTEGER").equalsIgnoreCase((String) columnMap.get("data-type")))
                            && (rsIsAutoIncrement != (Boolean.parseBoolean((String) columnMap.get("auto-increment")))) && ("true".equalsIgnoreCase((String) columnMap.get("auto-increment")))) {
                        if (!("INTEGER").equalsIgnoreCase((String) columnMap.get("data-type"))) {
                            alterList.add((String) columnMap.get("data-type") + " (" + columnMap.get("column-size") + ") ");
                        } else if (("INTEGER").equalsIgnoreCase((String) columnMap.get("data-type"))) {
                            alterList.add((String) columnMap.get("data-type"));
                        }
                        alterList.add(" AUTO_INCREMENT");
                    } else if (!columnMap.containsKey("auto-increment") && (("INTEGER").equalsIgnoreCase((String) columnMap.get("data-type")))
                            && (rsIsAutoIncrement != (Boolean.parseBoolean((String) columnMap.get("auto-increment"))))) {
                        queryList.add("ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("ALTER TABLE " + tableName);
                        stringBuffer.append(" ADD COLUMN " + columnName);
                        stringBuffer.append(" " + columnMap.get("data-type"));
                        if (!"INTEGER".equalsIgnoreCase((String) columnMap.get("data-type"))) {
                            stringBuffer.append(" (" + columnMap.get("column-size") + ") ");
                        }
                        DebugWrapper.logDebug("Altering column: " + columnName + " of table: " + tableName, className);
                        queryList.add(stringBuffer.toString());
                    } else if (columnMap.containsKey("auto-increment") && ("false".equalsIgnoreCase((String) columnMap.get("auto-increment")))
                                && rsIsAutoIncrement != (Boolean.parseBoolean((String) columnMap.get("auto-increment")))) {
                        queryList.add("ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("ALTER TABLE " + tableName);
                        stringBuffer.append(" ADD COLUMN " + columnName);
                        stringBuffer.append(" " + columnMap.get("data-type"));
                        if (!"INTEGER".equalsIgnoreCase((String) columnMap.get("data-type"))) {
                            stringBuffer.append(" (" + columnMap.get("column-size") + ") ");
                        }
                        DebugWrapper.logDebug("Altering column: " + columnName + " of table: " + tableName, className);
                        queryList.add(stringBuffer.toString());
                    }
                    if (!alterList.isEmpty()) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("ALTER TABLE " + tableName);
                        stringBuffer.append(" MODIFY COLUMN " + columnName);
                        for (String alterQuery : alterList) {
                            stringBuffer.append(" " + alterQuery);
                        }
                        DebugWrapper.logDebug("Altering column: " + columnName + " of table: " + tableName, className);
                        queryList.add(stringBuffer.toString());
                    }
                } else if (!columnRs.next()){
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("ALTER TABLE " + tableName);
                    stringBuffer.append(" ADD COLUMN " + columnName);
                    stringBuffer.append(" " + columnMap.get("data-type"));
                    if (!"int".equalsIgnoreCase((String) columnMap.get("data-type"))) {
                        stringBuffer.append(" (" + columnMap.get("column-size") + ") ");
                    }
                    if (columnMap.containsKey("nullable") && "false".equalsIgnoreCase((String) columnMap.get("nullable"))) {
                        stringBuffer.append(" NOT NULL");
                    }
                    if (("INTEGER".equalsIgnoreCase((String) columnMap.get("data-type"))) && columnMap.containsKey("auto-increment")
                            && "true".equalsIgnoreCase((String) columnMap.get("auto-increment"))) {
                        stringBuffer.append(" AUTO_INCREMENT");
                    }
                    DebugWrapper.logDebug("Altering column: " + columnName + " of table: " + tableName, className);
                    queryList.add(stringBuffer.toString());
                }
            }
            Map<String, Object> pk_result = createPrimaryKeyConstraint(tableName);
            if (pk_result.containsKey("error")) {
                queryMap.put("status", "error");
            }
            List<String> pkList = (List<String>) pk_result.get("pk_constraint");
            for (String pkString : pkList) {
                queryList.add(pkString);
            }
        }

        queryMap.put("update_column", queryList);
        return queryMap;
    }

    private Boolean isNullable(String value) {
        if (("0").equalsIgnoreCase(value)) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean isAutoIncrement(String value) {
        if ("NO".equalsIgnoreCase(value)) {
            return false;
        } else {
            return true;
        }
    }

    private Map<String, Object> validateColumns(Map<String, Object> column) {
        if (!column.containsKey("name") || !column.containsKey("data-type")) {
            return Utility.returnError();
        }
        String name = (String) column.get("name");
        String data_type = (String) column.get("data-type");

        if (name == null || name.length() <= 0) {
            return Utility.returnError();
        }

        if (data_type == null || data_type.length() <= 0) {
            return Utility.returnError();
        }

        return Utility.returnSuccess();
    }
}