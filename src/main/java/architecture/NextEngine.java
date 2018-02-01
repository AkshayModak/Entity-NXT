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
        Connection conn = processor.getConnection();
        Statement stmt = null;

        QueryGenerator queryGenerator = new QueryGenerator(entityDefinitionMap, conn);

        try {
            Map<String, Object> createTableQueries = queryGenerator.createTableQueries();
            Map<String, Object> dropTableQueries = queryGenerator.dropTableQueries();
            Map<String, Object> updateColumnQueries = queryGenerator.updateColumn();

            stmt = conn.createStatement();
            if (!("error").equalsIgnoreCase((String) createTableQueries.get("status"))) {
                for (String createTableQuery : (List<String>) createTableQueries.get("create_table")) {
                    stmt.addBatch(createTableQuery);
                }
            } else {
                DebugWrapper.logDebug("Unable to create table(s). Bad Table(s) Definition", className);
            }
            if (!("error").equalsIgnoreCase((String) dropTableQueries.get("status"))) {
                for (String dropTableQuery : (List<String>) dropTableQueries.get("drop_table")) {
                    stmt.addBatch(dropTableQuery);
                }
            }
            if (!("error").equalsIgnoreCase((String) updateColumnQueries.get("status"))) {
                for (String updateColumnQuery : (List<String>) updateColumnQueries.get("update_column")) {
                    stmt.addBatch(updateColumnQuery);
                }
            } else {
                DebugWrapper.logDebug("Unable to update Column(s), Bad Column(s) definition.", className);
            }

            NextEngine nextEngine = new NextEngine();
            nextEngine.runQuery();
            DebugWrapper.logDebug("Executing Query Batch", className);
            int[] totalBatches = stmt.executeBatch();//executing the batch
            DebugWrapper.logDebug("Total Records Updated: "+totalBatches.length, className);

            conn.commit();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return "success";
    }

    private void runQuery() {
        DebugWrapper.logDebug("========Inside runQuery======", className);
        QueryGenerator queryGenerator = new QueryGenerator();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Id", "0");
        map.put("TestColumn1", "1");
        map.put("TestColumn2", "2");
        map.put("TestColumn3", "3");
        map.put("TestColumn4", "Value 5");
        map.put("TestColumn5", "Value 6");
        map.put("TestColumn7", "7");

        String result = queryGenerator.setInsertQuery("TestEntityReader", map);
        DebugWrapper.logDebug("====result===="+result, className);

        map.clear();
        map.put("Id", "8");
        map.put("TestColumn1", "11");

        Map<String, Object> updateMap = new HashMap<String, Object>();
        updateMap.put("TestColumn1", "1");
        updateMap.put("TestColumn2", "2");
        updateMap.put("TestColumn3", "3");
        updateMap.put("TestColumn5", "Value 6");

        String updateResult = queryGenerator.setUpdateQuery("TestEntityReader", map, updateMap);
        DebugWrapper.logDebug("====updateResult===="+updateResult, className);

        String deleteResult = queryGenerator.setDeleteQuery("TestEntityReader", updateMap);
        DebugWrapper.logDebug("====deleteResult===="+deleteResult, className);

        String selectResult = queryGenerator.setSelectQuery("TestEntityReader", updateMap);
        DebugWrapper.logDebug("====selectResult===="+selectResult, className);
        SQLProcessor sqlProcessor = new SQLProcessor();
        List<Map<String, Object>> resultList = sqlProcessor.runQuery(selectResult);
        for (Map<String, Object> resultMap : resultList) {
            DebugWrapper.logDebug("====resultMap========"+resultMap, className);
        }

    }
}