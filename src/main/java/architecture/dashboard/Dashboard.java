package architecture.dashboard;

import architecture.ReadEntityDefinition;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Dashboard {

    public List<String> getTableNames() {
        ReadEntityDefinition red = new ReadEntityDefinition();
        Map<String, Object> result = red.getEntityDefinition();

        List<String> tableNames = new ArrayList<>();
        result.forEach((key, value) -> {
            tableNames.add(key);
        });
        return tableNames;
    }

    public Map<String, Object> getTableDetails(String tableName) {

        ReadEntityDefinition red = new ReadEntityDefinition();
        Map<String, Object> result = red.getEntityDefinition();

        Map<String, Object> filteredMap = result.entrySet().stream()
                .filter(map -> (tableName).equalsIgnoreCase(map.getKey()))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

        return filteredMap;

    }

}