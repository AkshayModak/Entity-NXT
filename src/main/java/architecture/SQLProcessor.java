package architecture;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import architecture.utils.DebugWrapper;
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

    protected Map<String, Object> loadDriver() {
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
            return DefaultObjects.getErrorMap(e);
        }

        return successMessage;
    }
}