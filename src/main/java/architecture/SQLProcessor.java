package architecture;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import architecture.utils.DebugWrapper;
import architecture.utils.Utility;
import one.DefaultObjects;
import one.ReadXMLFile;

public class SQLProcessor {

    final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private String DB_URL, USER, PASS, DATABASE_NAME, HOST;
    Connection conn;
    public final String className = SQLProcessor.class.getName();
    private String nextrr_home = System.getProperty("user.dir") + "/";

    private void setDBUrl() {
        DB_URL = "jdbc:mysql://" + HOST + "/" + DATABASE_NAME;
    }

    public void loadDriver() {
        Map<String, Object> successMessage = DefaultObjects.getSuccessMap();

        String fileName = nextrr_home + "setup.xml";
        Map<String, Object> setupConfig = ReadXMLFile.getXMLData(fileName);
        USER = (String) setupConfig.get("username");
        PASS = (String) setupConfig.get("password");
        DATABASE_NAME = (String) setupConfig.get("database-name");
        HOST = (String) setupConfig.get("host");

        setDBUrl();

        try {
            Class.forName(JDBC_DRIVER);
            DebugWrapper.logInfo("Connecting to " + DATABASE_NAME + " database...", className);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);
            successMessage.put("connection", conn);
            DebugWrapper.logInfo("Connected database successfully...", className);
        } catch (Exception e) {
            DebugWrapper.logInfo("Error During Loading Driver" + e, className);
        }
    }

    public List<Map<String, Object>> runQuery(String query) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Statement statement = null;
        try {
            loadDriver();
            statement = conn.createStatement();
            Boolean isResultSet = statement.execute(query);
            //Reference: https://stackoverflow.com/questions/35544167/mysql-a-way-to-check-if-query-is-update-delete-create-or-select
            if (isResultSet) {
                try (ResultSet rs = statement.getResultSet()) {
                    while (rs.next()) {
                        Map<String, Object> rowMap = new HashMap<String, Object>();
                        ResultSetMetaData row = rs.getMetaData();
                        for (int rows = 1; rows <= row.getColumnCount(); rows++) {
                            rowMap.put(row.getColumnName(rows), rs.getString(rows));
                        }
                        resultList.add(rowMap);
                    }
                    rs.close();
                } catch (SQLException se) {

                }
            } else {
                DebugWrapper.logDebug("Custom Query Finished Running Successfully", className);
            }
            conn.commit();
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
                if (statement != null)
                    conn.close();
            } catch (SQLException se) {
                    DebugWrapper.logDebug("Nothing to Catch : Exception while closing connection." + se.getMessage(), className);
            } // do nothing
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                DebugWrapper.logError("SQL Exception -- " + se, className);
            } // end finally try
        } // end try
        DebugWrapper.logInfo("Data Fetched Successfully!!", className);
        return resultList;
    }

    public List<Map<String, Object>> runCustomQuery(String query) {
        loadDriver();
        return runQuery(query);
    }

    public Connection getConnection() {
        loadDriver();
        return conn;
    }
}