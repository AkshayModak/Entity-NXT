package architecture;

import architecture.utils.DebugWrapper;
import architecture.ReadEntityDefinition;
import architecture.NextEngine;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import java.sql.Connection;
import java.sql.Statement;


public class NextEngine {

    public static final String className = NextEngine.class.getName();

    public static String runEngine() {

        ReadEntityDefinition red = new ReadEntityDefinition();
        Map<String, Object> entityDefinitionMap = red.getEntityDefinition();

        SQLProcessor processor = new SQLProcessor();
        Map<String, Object> driverResult = processor.loadDriver();
        Connection conn = (Connection) driverResult.get("connection");
        Statement stmt = null;

        QueryGenerator queryGenerator = new QueryGenerator(entityDefinitionMap, conn);

        try {
            Map<String, Object> createTableQueries = queryGenerator.createTableQueries();
            Map<String, Object> dropTableQueries = queryGenerator.dropTableQueries();

            stmt = conn.createStatement();
            if (!("error").equalsIgnoreCase((String) createTableQueries.get("error"))) {
                for (String createTableQuery : (List<String>) createTableQueries.get("create_table")) {
                    stmt.addBatch(createTableQuery);
                }
            }
            if (!("error").equalsIgnoreCase((String) dropTableQueries.get("error"))) {
                for (String dropTableQuery : (List<String>) dropTableQueries.get("drop_table")) {
                    stmt.addBatch(dropTableQuery);
                }
            }
            DebugWrapper.logDebug("Executing Query Batch", className);
            stmt.executeBatch();//executing the batch

            conn.commit();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return "success";
    }
}