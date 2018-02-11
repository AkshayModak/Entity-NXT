package architecture;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

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
    private List<String> queryList = new ArrayList<String>();

    public QueryGenerator() {

    }

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

    protected void createTableQueries() throws SQLException {

        if (tablesMap.isEmpty()) {
            Utility.returnError();
        }

        DatabaseMetaData metadata = null;
        metadata = conn.getMetaData();

        ResultSet resultSet;
        Map<String, Object> queryMap = new HashMap<String, Object>();

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
                List<String> pk_list = new ArrayList<>();
                for (Map<String, Object> column : columnsList) {
                    Map<String, Object> validate_result = validateColumns(column);
                    if (!("success").equalsIgnoreCase((String) validate_result.get("status"))) {
                        return;
                    }
                    stringBuffer.append(column.get("name"));
                    stringBuffer.append(" " + column.get("data-type"));
                    if (!("int").equalsIgnoreCase((String) column.get("data-type"))) {
                        stringBuffer.append("(");
                        stringBuffer.append(column.get("column-size"));
                        stringBuffer.append(")");
                    }
                    if (column.containsKey("primary-key") && ("true").equalsIgnoreCase((String) column.get("primary-key"))) {
                        pk_list.add((String) column.get("name"));
                    }
                    if (column.containsKey("unique")) {
                        stringBuffer.append(" UNIQUE");
                    }
                    if (column.containsKey("nullable") && ("false").equalsIgnoreCase((String) column.get("nullable"))) {
                        stringBuffer.append(" NOT NULL");
                    }
                    if (column.containsKey("auto-increment") && ("true").equalsIgnoreCase((String) column.get("auto-increment"))) {
                        stringBuffer.append(" AUTO_INCREMENT");
                    }
                    column_index++;
                    if ((column_index < columnsList.size()) || !pk_list.isEmpty()) {
                        stringBuffer.append(", ");
                    }
                }
                if (!pk_list.isEmpty()) {
                    stringBuffer.append(" PRIMARY KEY (");
                    Iterator<String> entries = pk_list.iterator();

                    String columns = "";
                    while (entries.hasNext()) {
                        columns = columns + entries.next();
                        if (entries.hasNext()) {
                            columns = columns + ", ";
                        }
                    }
                    stringBuffer.append(columns);
                    stringBuffer.append(")");
                }
                stringBuffer.append(");");
                DebugWrapper.logDebug("Creating Table: "+tableName, className);
                if (!queryList.contains(stringBuffer.toString())) {
                    queryList.add(stringBuffer.toString());
                }
            }
        }
    }

    protected void dropTableQueries() throws SQLException {

        DatabaseMetaData metadata = null;
        metadata = conn.getMetaData();

        ResultSet resultSet;
        Map<String, Object> queryMap = new HashMap<String, Object>();

        ResultSet rs = metadata.getTables(null, null, "%", null);
        while (rs.next()) {
            String mysqlTableName = rs.getString(3);
            if (tablesMap.isEmpty()) {
                DebugWrapper.logDebug("Dropping Table: "+mysqlTableName, className);
                if (!queryList.contains("DROP TABLE " + mysqlTableName)) {
                    queryList.add("DROP TABLE " + mysqlTableName);
                }
            } else {
                if (tablesMap.get(mysqlTableName) == null) {
                    DebugWrapper.logDebug("Dropping Table: "+mysqlTableName, className);
                    if (!queryList.contains("DROP TABLE " + mysqlTableName)) {
                        queryList.add("DROP TABLE " + mysqlTableName);
                    }
                }
            }
        }
    }

    protected void createPrimaryKeyConstraint(String tableName) throws SQLException {
        if (tableName == null || tableName.length() <= 0) {
            return;
        }

        DatabaseMetaData metadata = null;
        metadata = conn.getMetaData();

        ResultSet resultSet;
        Map<String, Object> queryMap = new HashMap<String, Object>();

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
                    /*if (!column.containsKey("nullable") || !("false").equalsIgnoreCase((String) column.get("nullable"))) {
                        DebugWrapper.logDebug("Can't Update Primary Key for table " + tableName + " Column " + column.get("name") + " is not nullable", className);
                        break;
                    }*/
                    if (!pk_list.isEmpty()) {
                        if (!pk_list.contains((String) column.get("name"))) {
                            dropPrimaryKey = true;
                            break;
                        }
                    }
                } else if (!pk_list.isEmpty() && pk_list.contains((String) column.get("name"))) {
                    /* TODO: Create a generic code or improve logic to remove auto-increment and primary-key with a single Query */
                    if ((!column.containsKey("auto-increment") || !("true").equalsIgnoreCase((String) column.get("auto-increment")))
                            && (!column.containsKey("primary-key") || !("true").equalsIgnoreCase((String) column.get("primary-key")))) {
                        String columnName = (String) column.get("name");
                        ResultSet columnRs = metadata.getColumns(null, null, tableName, columnName);
                        if (columnRs.next()) {
                            Boolean rsIsAutoIncrement = isAutoIncrement(columnRs.getString("IS_AUTOINCREMENT"));
                            if (rsIsAutoIncrement) {
                                if (!queryList.contains("ALTER TABLE " + tableName + " DROP COLUMN " + columnName)) {
                                    queryList.add("ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
                                }
                            } else {
                                dropPrimaryKey = true;
                                break;
                            }
                        }
                    } else {
                        dropPrimaryKey = true;
                        break;
                    }
                }
            }
            if (dropPrimaryKey) {
                if (!queryList.contains("ALTER TABLE " + tableName + " DROP PRIMARY KEY")) {
                    queryList.add("ALTER TABLE " + tableName + " DROP PRIMARY KEY");
                }
                for (Map<String, Object> column : columnsList) {
                    if (column.containsKey("primary-key") && !("true").equalsIgnoreCase((String) column.get("primary-key"))) {
                        if (pk_index > 0) {
                            stringBuffer.append(", ");
                        }
                        stringBuffer.append(column.get("name"));
                        pk_index++;
                    }
                }
            } else if (pk_list.isEmpty()) {
                for (Map<String, Object> column : columnsList) {
                    if (column.containsKey("primary-key") && ("true").equalsIgnoreCase((String) column.get("primary-key"))) {
                        if (pk_index > 0) {
                            stringBuffer.append(", ");
                        }
                        stringBuffer.append(column.get("name"));
                        pk_index++;
                    }
                }
            }
            stringBuffer.append(")");
            if (pk_index > 0) {
                DebugWrapper.logDebug("Adding Primary Key Constraint on Table: "+tableName, className);
                if (!queryList.contains(stringBuffer.toString())) {
                    queryList.add(stringBuffer.toString());
                }
            }
        }
    }

    protected void updateColumn() throws SQLException {

        if (tablesMap.isEmpty()) {
            return;
        }

        DatabaseMetaData metadata = null;
        metadata = conn.getMetaData();
        List<String> alterList = new ArrayList<>();

        ResultSet resultSet;
        Map<String, Object> queryMap = new HashMap<String, Object>();

        for (Map.Entry<String, Object> table : tablesMap.entrySet()) {
            String tableName = table.getKey();

            Boolean isNewDefinition = Utility.containsAKeyword("CREATE TABLE IF NOT EXISTS "+tableName, queryList);

            if (isNewDefinition) {
                continue;
            }

            List<Map<String, Object>> tableList = (List<Map<String, Object>>) tablesMap.get(tableName);

            ResultSet columnRs = metadata.getColumns(null, null, tableName, "%");

            Boolean dropColumn = false;
            while (columnRs.next()) {
                String rsColumnName = columnRs.getString("COLUMN_NAME");
                Boolean hasKeyValue = Utility.hasMapKeyValuePair(tableList, "name", rsColumnName);
                if (!hasKeyValue) {
                    if (!queryList.contains("ALTER TABLE " + tableName + " DROP COLUMN " + rsColumnName)) {
                        queryList.add("ALTER TABLE " + tableName + " DROP COLUMN " + rsColumnName);
                    }
                }
            }

            for (Map<String, Object> columnMap : tableList) {
                Map<String, Object> validate_result = validateColumns(columnMap);
                if (!("success").equalsIgnoreCase((String) validate_result.get("status"))) {
                    return;
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
                    } else if (!columnMap.containsKey("nullable") && !(columnMap.containsKey("primary-key")) && ("true").equalsIgnoreCase((String) columnMap.get("data-type"))) {
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

                        if (!queryList.contains("ALTER TABLE " + tableName + " DROP COLUMN " + columnName)) {
                            queryList.add("ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
                        }
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("ALTER TABLE " + tableName);
                        stringBuffer.append(" ADD COLUMN " + columnName);
                        stringBuffer.append(" " + columnMap.get("data-type"));
                        if (!"INTEGER".equalsIgnoreCase((String) columnMap.get("data-type"))) {
                            stringBuffer.append("(" + columnMap.get("column-size") + ") ");
                        }
                        if(columnMap.containsKey("nullable") && "false".equalsIgnoreCase((String) columnMap.get("nullable"))) {
                            if (!columnMap.containsKey("primary-key") || ("false").equalsIgnoreCase((String) columnMap.get("primary-key"))) {
                                stringBuffer.append(" NOT NULL");
                            }
                        }
                        DebugWrapper.logDebug("Altering column: " + columnName + " of table: " + tableName, className);
                        if (!queryList.contains(stringBuffer.toString())) {
                            queryList.add(stringBuffer.toString());
                        }
                    } else if (columnMap.containsKey("auto-increment") && !("true".equalsIgnoreCase((String) columnMap.get("auto-increment")))
                                && rsIsAutoIncrement != (Boolean.parseBoolean((String) columnMap.get("auto-increment")))) {
                        if (!queryList.contains("ALTER TABLE " + tableName + " DROP COLUMN " + columnName)) {
                            queryList.add("ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
                        }
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("ALTER TABLE " + tableName);
                        stringBuffer.append(" ADD COLUMN " + columnName);
                        stringBuffer.append("" + columnMap.get("data-type"));
                        if (!"INTEGER".equalsIgnoreCase((String) columnMap.get("data-type"))) {
                            stringBuffer.append("(" + columnMap.get("column-size") + ") ");
                        }
                        DebugWrapper.logDebug("Altering column: " + columnName + " of table: " + tableName, className);
                        if (!queryList.contains(stringBuffer.toString())) {
                            queryList.add(stringBuffer.toString());
                        }
                    }
                    if (!alterList.isEmpty()) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("ALTER TABLE " + tableName);
                        stringBuffer.append(" MODIFY COLUMN " + columnName);
                        for (String alterQuery : alterList) {
                            stringBuffer.append(" " + alterQuery);
                        }
                        DebugWrapper.logDebug("Altering column: " + columnName + " of table: " + tableName, className);
                        if (!queryList.contains(stringBuffer.toString())) {
                            queryList.add(stringBuffer.toString());
                        }
                    }
                } else if (!columnRs.next()){
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("ALTER TABLE " + tableName);
                    stringBuffer.append(" ADD COLUMN " + columnName);
                    stringBuffer.append(" " + columnMap.get("data-type"));
                    if (!"int".equalsIgnoreCase((String) columnMap.get("data-type"))) {
                        stringBuffer.append("(" + columnMap.get("column-size") + ") ");
                    }
                    if (columnMap.containsKey("nullable") && "false".equalsIgnoreCase((String) columnMap.get("nullable"))) {
                        stringBuffer.append(" NOT NULL");
                    }
                    if (("INTEGER".equalsIgnoreCase((String) columnMap.get("data-type"))) && columnMap.containsKey("auto-increment")
                            && "true".equalsIgnoreCase((String) columnMap.get("auto-increment"))) {
                        stringBuffer.append(" AUTO_INCREMENT");
                    }
                    DebugWrapper.logDebug("Altering column: " + columnName + " of table: " + tableName, className);
                    if (!queryList.contains(stringBuffer.toString())) {
                        queryList.add(stringBuffer.toString());
                    }
                }
            }
            createPrimaryKeyConstraint(tableName);
        }
    }

    public String setSelectQuery(String entity, Map<String, Object> queryParams) {

        String conditions = "";
        String sqlQuery = null;

        if (queryParams != null && !queryParams.isEmpty()) {
            Iterator<Map.Entry<String, Object>> entries = queryParams.entrySet().iterator();

            while (entries.hasNext()) {
                Map.Entry<String, Object> entry = entries.next();
                conditions += " " + entry.getKey() + "='" + entry.getValue() + "'";
                if (entries.hasNext()) {
                    conditions = conditions + " AND";
                }
            }
            sqlQuery = "SELECT * FROM " + entity + " WHERE" + conditions + ";";
        } else {
            sqlQuery = "SELECT * FROM " + entity + ";";
        }

        return sqlQuery;
    }

    public String setInsertQuery(String entity, Map<String, Object> queryParams) {

        Iterator<Map.Entry<String, Object>> columnEntries = queryParams.entrySet().iterator();

        String columnNames = "";
        while (columnEntries.hasNext()) {
            Map.Entry<String, Object> entry = columnEntries.next();
            columnNames = columnNames + entry.getKey();
            if (columnEntries.hasNext()) {
                columnNames = columnNames + ", ";
            } else {
                columnNames = columnNames + " ";
            }
        }

        DebugWrapper.logDebug("====columnNames===="+columnNames, className);

        Iterator<Map.Entry<String, Object>> valueEntries = queryParams.entrySet().iterator();
        String values = "";

        while (valueEntries.hasNext()) {
            Map.Entry<String, Object> entry = valueEntries.next();
            values = values + "\"" + (String) queryParams.get(entry.getKey()) + "\"";
            if (valueEntries.hasNext()) {
                values = values + ", ";
            } else {
                values = values + " ";
            }
        }

        String sqlQuery = "INSERT INTO " + entity + "(" + columnNames + ") VALUES (" + values + ");";
        return sqlQuery;
    }

    public String setUpdateQuery(String entity, Map<String, Object> queryParams, Map<String, Object> primaryKeyParams) {

        Iterator<Map.Entry<String, Object>> columnEntries = queryParams.entrySet().iterator();

        String columnNames = " ";
        while (columnEntries.hasNext()) {
            Map.Entry<String, Object> entry = columnEntries.next();
            String columnName = entry.getKey() + " = '" + Utility.escapeMetaCharacters((String) entry.getValue()) + "'";
            if (columnEntries.hasNext()) {
                columnNames = columnNames + columnName + ", ";
            } else {
                columnNames = columnNames + columnName + " ";
            }
        }

        Iterator<Map.Entry<String, Object>> primaryKeysEntries = primaryKeyParams.entrySet().iterator();

        String primaryKeys = " ";
        while (primaryKeysEntries.hasNext()) {
            Map.Entry<String, Object> entry = primaryKeysEntries.next();
            String primaryKey = entry.getKey() + " = '" + Utility.escapeMetaCharacters((String) entry.getValue()) + "'";
            if (primaryKeysEntries.hasNext()) {
                primaryKeys = primaryKeys + primaryKey + " AND ";
            } else {
                primaryKeys = primaryKeys + primaryKey;
            }
        }

        String sqlQuery = "UPDATE " + entity + " SET " + columnNames + " WHERE " + primaryKeys + ";";
        return sqlQuery;
    }

    public String setDeleteQuery(String entity, Map<String, Object> queryParams) {

        Iterator<Map.Entry<String, Object>> primaryKeysEntries = queryParams.entrySet().iterator();

        String primaryKeys = "";
        while (primaryKeysEntries.hasNext()) {
            Map.Entry<String, Object> entry = primaryKeysEntries.next();
            primaryKeys = primaryKeys + entry.getKey() + " = '" + Utility.escapeMetaCharacters((String) entry.getValue()) + "'";
            if (primaryKeysEntries.hasNext()) {
                primaryKeys = primaryKeys + " AND ";
            } else {
                primaryKeys = primaryKeys;
            }
        }

        String sqlQuery = "DELETE FROM " + entity + " WHERE " + primaryKeys.toString();
        return sqlQuery;
    }

    public String getSearchQuery(String entity, Map<String, String[]> queryParams) {
        Iterator<Map.Entry<String, String[]>> searchEntries = queryParams.entrySet().iterator();

        String searchKeywords = "";
        while (searchEntries.hasNext()) {
            Map.Entry<String, String[]> entry = searchEntries.next();
            String keyword = entry.getValue()[0];
            if (keyword.length() > 0) {
                if ((searchKeywords != "")) {
                    searchKeywords = searchKeywords + " AND ";
                }
                searchKeywords = searchKeywords + entry.getKey() + " LIKE ('%" + Utility.escapeMetaCharacters(keyword) + "%')";
            }

        }
        String sqlQuery = "";
        if (searchKeywords != "") {
            sqlQuery = "SELECT * FROM " + entity + " WHERE " + searchKeywords;
        } else {
            sqlQuery = "error";
        }
        return sqlQuery;
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

    public List<String> getQueries() {
        return queryList;
    }
}