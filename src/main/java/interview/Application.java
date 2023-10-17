package interview;

import interview.taskmanager.TaskManager;
import interview.taskmanager.config.CloudConfigLoader;
import interview.taskmanager.config.FileConfigLoader;
import interview.taskmanager.config.TaskConfig;
import interview.thirdparties.TaskScheduler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Application {

    public static final String DEFAULT_PROFILE = "default";

    public static void main(String[] args) throws IOException {
        String profile = resolveProfile(args);

        TaskManager taskManager = new TaskManager(new TaskScheduler(), profile);
        List<TaskConfig> config = taskManager.loadConfig(new FileConfigLoader(Path.of(".")), new CloudConfigLoader("user123", "strongPassword$11"));
        taskManager.schedule(config);
    }

    private static String resolveProfile(String[] args) {
        String profile = DEFAULT_PROFILE;
        if (args.length == 2 && args[0].equals("--profile")) {
            profile = args[1];
        }
        return profile;
    }

}
