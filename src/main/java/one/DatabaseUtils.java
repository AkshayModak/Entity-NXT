package one;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.nextrr.helper.Formula1Helper;

import architecture.ReadEntityDefinition;
import architecture.utils.DebugWrapper;

public class DatabaseUtils {

    static String USER;
    static String PASS;
    static String DATABASE_NAME;
    static String HOST;
    String ENTITY;
    public static final String className = DatabaseUtils.class.getName();

    final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    String DB_URL;

    Connection conn;
    String sqlQuery = "";

    public void setDBUrl() {
        DB_URL = "jdbc:mysql://" + HOST + "/" + DATABASE_NAME;
    }

    private static String nextrr_home = System.getProperty("user.dir") + "/";

    private Map<String, Object> loadDriver() {

        Map<String, Object> successMessage = DefaultObjects.getSuccessMap();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            DebugWrapper.logInfo("Connecting to " + DATABASE_NAME + " database...", className);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            DebugWrapper.logInfo("Connected database successfully...", className);
        } catch (Exception e) {
            DebugWrapper.logError("Error During Loading Driver" + e, className);
            return DefaultObjects.getErrorMap(e);
        }

        return successMessage;
    }

    private void setSelectQuery(Map<String, Object> queryParams) {

        String selectSqlQuery = "";

        if (queryParams != null && !queryParams.isEmpty()) {
            Iterator<Map.Entry<String, Object>> entries = queryParams.entrySet().iterator();

            while (entries.hasNext()) {
                Map.Entry<String, Object> entry = entries.next();
                selectSqlQuery += " " + entry.getKey() + "='" + entry.getValue() + "'";
                if (entries.hasNext()) {
                    selectSqlQuery = selectSqlQuery + " AND";
                }
            }
            sqlQuery = "SELECT * FROM " + ENTITY + " WHERE" + selectSqlQuery + ";";
        } else {
            sqlQuery = "SELECT * FROM " + ENTITY + ";";
        }
    }

    private void setInsertQuery(Map<String, Object> queryParams) {

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

        Iterator<Map.Entry<String, Object>> valueEntries = queryParams.entrySet().iterator();
        String values = " ";

        while (valueEntries.hasNext()) {
            Map.Entry<String, Object> entry = valueEntries.next();
            values = values + "\"" + (String) queryParams.get(entry.getKey()) + "\"";
            if (valueEntries.hasNext()) {
                values = values + ", ";
            } else {
                values = values + " ";
            }
        }

        sqlQuery = "INSERT INTO " + ENTITY + "(" + columnNames + ") VALUES (" + values.toString() + ");";
    }

    private void setUpdateQuery(Map<String, Object> queryParams, String primaryKey) {

        Iterator<Map.Entry<String, Object>> columnEntries = queryParams.entrySet().iterator();

        String columnNames = "";
        while (columnEntries.hasNext()) {
            Map.Entry<String, Object> entry = columnEntries.next();
            columnNames += entry.getKey() + " = '" + NextrrUtils.escapeMetaCharacters((String) entry.getValue()) + "'";
            if (columnEntries.hasNext()) {
                columnNames = columnNames + ", ";
            } else {
                columnNames = columnNames + " ";
            }
        }

        sqlQuery = "UPDATE " + ENTITY + " SET " + columnNames + " WHERE " + primaryKey + " = '"
                + queryParams.get(primaryKey) + "';";
    }

    public void setDeleteQuery(String primaryId, String primaryKey) {
        sqlQuery = "DELETE FROM " + ENTITY + " WHERE " + primaryKey + " = " + primaryId;
    }

    public synchronized Map<String, Object> getDbData() {
        Map<String, Object> result = new HashMap<String, Object>();
        Statement stmt = null;
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        try {

            loadDriver();
            stmt = conn.createStatement();
            String sql = sqlQuery;
            ResultSet rs = stmt.executeQuery(sql);
            // STEP 5: Extract data from result set
            while (rs.next()) {
                Map<String, Object> rowMap = new HashMap<String, Object>();
                ResultSetMetaData row = rs.getMetaData();
                for (int rows = 1; rows <= row.getColumnCount(); rows++) {
                    rowMap.put(row.getColumnName(rows), rs.getString(rows));
                }
                resultList.add(rowMap);
            }
            result.put("result", resultList);
            rs.close();
            conn.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            DebugWrapper.logError("SQL Exception -- " + se, className);
        } catch (Exception e) {
            // Handle errors for Class.forName
            DebugWrapper.logError("Exception -- " + e, className);
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            } // do nothing
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                DebugWrapper.logError("SQL Exception -- " + se, className);
            } // end finally try
        } // end try
        DebugWrapper.logInfo("Data Fetched Successfully!!", className);
        return result;
    }

    public void setDbData() {

        try {
            loadDriver();
            Statement st = conn.createStatement();
            st.executeUpdate(sqlQuery);
            conn.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    public void removeDbData() {

        try {
            loadDriver();
            Statement st = conn.createStatement();
            st.executeUpdate(sqlQuery);
            conn.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    public void runDbUtils(Map<String, Object> paramsMap) {
        String fileName = nextrr_home + "setup.xml";
        DebugWrapper.logInfo(fileName, className);
        Map<String, Object> setupConfig = ReadXMLFile.getXMLData(fileName);
        USER = (String) setupConfig.get("username");
        PASS = (String) setupConfig.get("password");
        DATABASE_NAME = (String) setupConfig.get("database-name");
        HOST = (String) setupConfig.get("host");

        Iterator<Map.Entry<String, Object>> valueEntries = paramsMap.entrySet().iterator();
        Map<String, Object> fieldMap = new HashMap<String, Object>();

        while (valueEntries.hasNext()) {
            Map.Entry<String, Object> entry = valueEntries.next();
            fieldMap.put(entry.getKey(), entry.getValue());
        }

        setDBUrl();
        setInsertQuery(fieldMap);
        setDbData();
    }

    public Map<String, Object> getF1Schedule(String entityName, Map<String, Object> paramsMap) {
        String fileName = nextrr_home + "setup.xml";
        Map<String, Object> setupConfig = ReadXMLFile.getXMLData(fileName);
        USER = (String) setupConfig.get("username");
        PASS = (String) setupConfig.get("password");
        DATABASE_NAME = (String) setupConfig.get("database-name");
        HOST = (String) setupConfig.get("host");
        ENTITY = entityName;

        setDBUrl();
        setSelectQuery(paramsMap);
        Map<String, Object> result = getDbData();

        return result;
    }

    public Map<String, Object> getAllEntityData(String entityName) {
        String fileName = nextrr_home + "setup.xml";
        Map<String, Object> setupConfig = ReadXMLFile.getXMLData(fileName);
        USER = (String) setupConfig.get("username");
        PASS = (String) setupConfig.get("password");
        DATABASE_NAME = (String) setupConfig.get("database-name");
        HOST = (String) setupConfig.get("host");
        ENTITY = entityName;

        setDBUrl();
        setSelectQuery(null);
        Map<String, Object> result = getDbData();

        return result;
    }

    public Map<String, Object> getEntityDataWithConditions(String entityName, Map<String, Object> queryParams) {
        String fileName = nextrr_home + "setup.xml";
        Map<String, Object> setupConfig = ReadXMLFile.getXMLData(fileName);
        USER = (String) setupConfig.get("username");
        PASS = (String) setupConfig.get("password");
        DATABASE_NAME = (String) setupConfig.get("database-name");
        HOST = (String) setupConfig.get("host");
        this.ENTITY = entityName;

        setDBUrl();
        setSelectQuery(queryParams);
        Map<String, Object> result = getDbData();

        return result;
    }

    public Map<String, Object> getFirstEntityDataWithConditions(String entityName, Map<String, Object> queryParams) {
        Map<String, Object> dataMap = getEntityDataWithConditions(entityName, queryParams);
        List resultList = (List) dataMap.get("result");
        if (!dataMap.isEmpty()) {
            Map<String, Object> resultMap = (Map<String, Object>) resultList.get(0);
            return resultMap;
        }
        return DefaultObjects.getErrorMap();
    }

    public void runUpdateQuery(String entityName, Map<String, Object> queryMap, String primaryKey) {
        String fileName = nextrr_home + "setup.xml";
        Map<String, Object> setupConfig = ReadXMLFile.getXMLData(fileName);
        USER = (String) setupConfig.get("username");
        PASS = (String) setupConfig.get("password");
        DATABASE_NAME = (String) setupConfig.get("database-name");
        HOST = (String) setupConfig.get("host");
        ENTITY = entityName;

        setDBUrl();
        setUpdateQuery(queryMap, primaryKey);
        setDbData();
    }

    public void runCreateQuery(String entityName, Map<String, Object> queryMap) {
        String fileName = nextrr_home + "setup.xml";
        Map<String, Object> setupConfig = ReadXMLFile.getXMLData(fileName);
        USER = (String) setupConfig.get("username");
        PASS = (String) setupConfig.get("password");
        DATABASE_NAME = (String) setupConfig.get("database-name");
        HOST = (String) setupConfig.get("host");
        ENTITY = entityName;

        setDBUrl();
        setInsertQuery(queryMap);
        setDbData();
    }

    public void runDeleteQuery(String entityName, String primaryId, String primaryKey) {
        String fileName = nextrr_home + "setup.xml";
        Map<String, Object> setupConfig = ReadXMLFile.getXMLData(fileName);
        USER = (String) setupConfig.get("username");
        PASS = (String) setupConfig.get("password");
        DATABASE_NAME = (String) setupConfig.get("database-name");
        HOST = (String) setupConfig.get("host");
        ENTITY = entityName;

        setDBUrl();
        setDeleteQuery(primaryId, primaryKey);
        removeDbData();
    }
}
