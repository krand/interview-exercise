package interview;

import interview.thirdparties.CloudClient;
import interview.thirdparties.TaskScheduler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Application {

    public static void main(String[] args) throws IOException {
        String profile = "default";
        if (args.length == 2 && args[0].equals("--profile")) {
            profile = args[1];
        }

        Map<String, Boolean> tasksStatusMap = new HashMap<>();
        Map<String, String> tasksCronExpressionMap = new HashMap<>();

        Properties localProperties = new Properties();
        Path localPropertiesFile = Paths.get(".", profile + ".properties");
        if (Files.exists(localPropertiesFile)) {
            localProperties.load(Files.newBufferedReader(localPropertiesFile));
        }
        localProperties.forEach((key, value) -> {
            if (key.toString().endsWith(".enabled")) {
                tasksStatusMap.put(key.toString().replace(".enabled", ""), "true".equalsIgnoreCase(value.toString()));
            } else if (key.toString().endsWith(".cron-expr")) {
                tasksCronExpressionMap.put(key.toString().replace(".cron-expr", ""), value.toString());
            }
        });


        CloudClient cloudClient = new CloudClient("user123", "strongPassword$11");
        List<CloudClient.Property> tasksConfig = cloudClient.readPropertyByPrefix("config/" + profile + "/tasks/");
        for (CloudClient.Property property : tasksConfig) {
            if (property.key().endsWith(".enabled")) {
                String taskName = property.key()
                        .replace("config/" + profile + "/tasks/", "")
                        .replace(".enabled", "");
                tasksStatusMap.put(taskName, "true".equalsIgnoreCase(property.value()));
            } else if (property.key().endsWith(".cron-expr")) {
                String taskName = property.key()
                        .replace("config/" + profile + "/tasks/", "")
                        .replace(".cron-expr", "");
                tasksCronExpressionMap.put(taskName, property.value());
            }
        }

        TaskScheduler taskScheduler = new TaskScheduler();
        tasksStatusMap.forEach((key, value) -> {
            if (value) {
                taskScheduler.schedule(key, tasksCronExpressionMap.get(key));
            }
        });

    }


}
