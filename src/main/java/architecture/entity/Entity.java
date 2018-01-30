package architecture.entity;

import architecture.SQLProcessor;
import architecture.utils.DebugWrapper;
import architecture.QueryGenerator;
import architecture.utils.Utility;
import one.ReadXMLFile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import java.sql.*;

public class Entity {

    public final String className = Entity.class.getName();

    Connection conn = null;
    String sqlQuery = "";
    static String USER;
    static String PASS;
    static String DATABASE_NAME;
    static String HOST;
    String ENTITY;

    private static String nextrr_home = System.getProperty("user.dir") + "/";

    private void loadDriver() {
        SQLProcessor processor = new SQLProcessor();
        Map<String, Object> loadDriver = processor.loadDriver();
        Connection conn = (Connection) loadDriver.get("connection");
    }

    public Map<String, Object> getDbData() {
        Map<String, Object> result = new HashMap<String, Object>();

        Statement stmt = null;

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        try {
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

        QueryGenerator queryGenerator = new QueryGenerator();
        queryGenerator.setInsertQuery("DEMO", fieldMap);
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

        QueryGenerator queryGenerator = new QueryGenerator();
        queryGenerator.setSelectQuery("DEMO", paramsMap);
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

        QueryGenerator queryGenerator = new QueryGenerator();
        queryGenerator.setSelectQuery("DEMO", null);
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

        QueryGenerator queryGenerator = new QueryGenerator();
        queryGenerator.setSelectQuery("DEMO", queryParams);
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
        return Utility.returnError();
    }

    public void runUpdateQuery(String entityName, Map<String, Object> queryMap, Map<String, Object> primaryKey) {
        String fileName = nextrr_home + "setup.xml";
        Map<String, Object> setupConfig = ReadXMLFile.getXMLData(fileName);
        USER = (String) setupConfig.get("username");
        PASS = (String) setupConfig.get("password");
        DATABASE_NAME = (String) setupConfig.get("database-name");
        HOST = (String) setupConfig.get("host");
        ENTITY = entityName;

        QueryGenerator queryGenerator = new QueryGenerator();
        queryGenerator.setUpdateQuery("DEMO", queryMap, primaryKey);
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

        QueryGenerator queryGenerator = new QueryGenerator();
        queryGenerator.setInsertQuery("DEMO", queryMap);
        setDbData();
    }

    public void runDeleteQuery(String entityName, String primaryId, Map<String, Object> primaryKey) {
        String fileName = nextrr_home + "setup.xml";
        Map<String, Object> setupConfig = ReadXMLFile.getXMLData(fileName);
        USER = (String) setupConfig.get("username");
        PASS = (String) setupConfig.get("password");
        DATABASE_NAME = (String) setupConfig.get("database-name");
        HOST = (String) setupConfig.get("host");
        ENTITY = entityName;

        QueryGenerator queryGenerator = new QueryGenerator();
        queryGenerator.setDeleteQuery("DEMO", primaryKey);
        removeDbData();
    }
}