package interview.thirdparties;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CloudClient {

    public CloudClient(String key, String secret) {
    }

    private static final Map<String, String> storage = Map.ofEntries(
            Map.entry("config/dev/tasks/taskA.enabled", "true"),
            Map.entry("config/dev/tasks/taskA.cron-expr", "0 0/5 * * * ?"),
            Map.entry("config/dev/tasks/taskB.enabled", "false"),
            Map.entry("config/dev/tasks/taskB.cron-expr", "0 30 10-13 ? * WED,FRI")
    );

    public List<Property> readPropertyByPrefix(String prefix) {
        return storage.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .map(entry -> new Property(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public record Property(String key, String value) {
    }
}
