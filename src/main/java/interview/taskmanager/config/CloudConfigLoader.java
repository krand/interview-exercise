package interview.taskmanager.config;

import interview.thirdparties.CloudClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CloudConfigLoader implements ConfigLoader {

    private Pattern property_pattern = Pattern.compile("config/[\\w\\-]+/tasks/([\\w\\-]+)\\.(enabled|cron-expr)");

    private final String user;
    private final String secret;

    public CloudConfigLoader(String user, String secret) {
        this.user = user;
        this.secret = secret;
    }

    @Override
    public List<TaskConfig> load(String profile) {
        Map<String, TaskConfig> result = new LinkedHashMap<>();
        CloudClient cloudClient = new CloudClient(user, secret);
        List<CloudClient.Property> tasksConfig = cloudClient.readPropertyByPrefix("config/" + profile + "/tasks/");
        for (CloudClient.Property property : tasksConfig) {
            Matcher matcher = property_pattern.matcher(property.key());
            if (matcher.matches()) {
                String taskName = matcher.group(1);
                TaskConfig currentConfig = result.get(taskName);
                if (currentConfig == null) {
                    currentConfig = new TaskConfig(taskName, false, null);
                }
                if (property.key().endsWith(".enabled")) {
                    result.put(taskName, new TaskConfig(taskName, "true".equalsIgnoreCase(property.value()), currentConfig.chronExpression()));
                } else if (property.key().endsWith(".cron-expr")) {
                    result.put(taskName, new TaskConfig(taskName, currentConfig.enabled(), property.value()));
                }
            }
        }
        return new ArrayList<>(result.values());
    }
}