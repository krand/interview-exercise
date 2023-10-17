package interview.taskmanager.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileConfigLoader implements ConfigLoader {

    private final Path homeFolder;

    private Pattern property_pattern = Pattern.compile("([\\w\\-]+)\\.(enabled|cron-expr)");

    public FileConfigLoader(Path homeFolder) {
        this.homeFolder = homeFolder;
    }

    @Override
    public List<TaskConfig> load(String profile) {
        Map<String, TaskConfig> result = new LinkedHashMap<>();
        Properties localProperties = new Properties();
        Path localPropertiesFile = homeFolder.resolve(profile + ".properties");
        if (Files.exists(localPropertiesFile)) {
            try {
                localProperties.load(Files.newBufferedReader(localPropertiesFile));

                localProperties.forEach((key, value) -> {
                    Matcher matcher = property_pattern.matcher(key.toString());
                    if (matcher.matches()) {
                        String taskName = matcher.group(1);
                        TaskConfig currentConfig = result.get(taskName);
                        if (currentConfig == null) {
                            currentConfig = new TaskConfig(taskName, false, null);
                        }
                        if (key.toString().endsWith(".enabled")) {
                            result.put(taskName, new TaskConfig(taskName, "true".equalsIgnoreCase(value.toString()), currentConfig.chronExpression()));
                        } else if (key.toString().endsWith(".cron-expr")) {
                            result.put(taskName, new TaskConfig(taskName, currentConfig.enabled(), value.toString()));
                        }
                    }
                });
            } catch (IOException e) {
                //log the exception
            }
        }
        return new ArrayList<>(result.values());
    }
}
