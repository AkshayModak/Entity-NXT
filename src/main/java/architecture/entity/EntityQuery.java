package architecture.entity;

import architecture.SQLProcessor;
import architecture.utils.DebugWrapper;
import architecture.QueryGenerator;
import architecture.utils.Utility;

import java.util.List;
import java.util.Map;

public class EntityQuery {

    public static List<Map<String, Object>> getAll(String entityName) {
        QueryGenerator queryGenerator = new QueryGenerator();
        String getAllQuery = queryGenerator.setSelectQuery(entityName, null);

        SQLProcessor sqlProcessor = new SQLProcessor();
        List<Map<String, Object>> result = sqlProcessor.runQuery(getAllQuery);
        return result;
    }

    public static Map<String, Object> getFirst(String entityName, Map<String, Object> queryParams) {
        QueryGenerator queryGenerator = new QueryGenerator();
        String getFirstQuery = queryGenerator.setSelectQuery(entityName, queryParams);

        SQLProcessor sqlProcessor = new SQLProcessor();
        List<Map<String, Object>> result = sqlProcessor.runQuery(getFirstQuery);
        return (!result.isEmpty()) ? result.get(0) : null;
    }

    public static Map<String, Object> update(String entityName, Map<String, Object> updateParams, Map<String, Object> primaryKeyParams) {
        QueryGenerator queryGenerator = new QueryGenerator();
        String updateQuery = queryGenerator.setUpdateQuery(entityName, updateParams, primaryKeyParams);

        SQLProcessor sqlProcessor = new SQLProcessor();
        List<Map<String, Object>> result = sqlProcessor.runQuery(updateQuery);
        return (!result.isEmpty()) ? result.get(0) : null;
    }

    public static Map<String, Object> remove(String entityName, Map<String, Object> primaryKeyParams) {
        QueryGenerator queryGenerator = new QueryGenerator();
        String removeQuery = queryGenerator.setDeleteQuery(entityName, primaryKeyParams);

        SQLProcessor sqlProcessor = new SQLProcessor();
        List<Map<String, Object>> result = sqlProcessor.runQuery(removeQuery);
        Map<String, Object> resultMap = result.get(0);
        return (!result.isEmpty()) ? result.get(0) : null;
    }

    public static Map<String, Object> insert(String entityName, Map<String, Object> insertParams) {
        QueryGenerator queryGenerator = new QueryGenerator();
        String insertQuery = queryGenerator.setInsertQuery(entityName, insertParams);

        SQLProcessor sqlProcessor = new SQLProcessor();
        List<Map<String, Object>> result = sqlProcessor.runQuery(insertQuery);
        return (!result.isEmpty()) ? result.get(0) : null;
    }
}