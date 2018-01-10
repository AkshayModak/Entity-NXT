package admin.insert;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import one.DefaultObjects;
import one.ReadXMLFile;

public class InsertData {
    
	static String USER;
	static String PASS;
	static String DATABASE_NAME;
	static String HOST;
	static String ENTITY;

	final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	String DB_URL;
	
	Connection conn;
	String sqlQuery = "";
	
	public void setDBUrl() {
		DB_URL = "jdbc:mysql://" + HOST + "/" + DATABASE_NAME;
	}
	
	private Map<String, Object> loadDriver() {
		
		Map<String, Object> successMessage = DefaultObjects.getSuccessMap();
		
		try {
		      Class.forName("com.mysql.jdbc.Driver");
		      System.out.println("Connecting to "+ DATABASE_NAME + " database...");
		      System.out.println("=====DB_URL======"+DB_URL);
		      System.out.println("=====USER======"+USER);
		      System.out.println("=====PASS======"+PASS);
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("Connected database successfully...");
		} catch(Exception e) {
			System.out.println("Error During Loading Driver"+e);
			return DefaultObjects.getErrorMap(e);
		}
		
		return successMessage;
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
		    values = values + "\"" + (String)queryParams.get(entry.getKey()) + "\"";
		    if (valueEntries.hasNext()) {
		    	values = values + ", ";
		    } else {
		    	values = values + " ";
		    }
		}
		
		sqlQuery = "INSERT INTO " + ENTITY + "(" + columnNames + ") VALUES (" + values.toString() + ");";
	}
	
	private String getInsertSqlQuery() {
		return sqlQuery;
	}
	
	public void setDbData(Map<String, Object> queryParams) {
		
		try {
		loadDriver();
		Statement st = conn.createStatement();
		st.executeUpdate(sqlQuery);
		conn.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		
	}
	
	public static void main(String...ar) {
		Map<String, Object> setupConfig = ReadXMLFile.getXMLData("/home/akshay/Java/RestCheck/setup.xml");
		USER = (String) setupConfig.get("username");
		PASS = (String) setupConfig.get("password");
		DATABASE_NAME = (String) setupConfig.get("database-name");
		HOST = (String) setupConfig.get("host");
		ENTITY = "formula_one";
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("id", "1");
		queryParams.put("name", "Akshay");
		queryParams.put("surname", "Modak");
		
		InsertData id = new InsertData();
		id.setDBUrl();
		id.loadDriver();
		id.setInsertQuery(queryParams);
		System.out.println(id.getInsertSqlQuery());
	}

}
	