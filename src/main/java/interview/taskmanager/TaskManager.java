package interview.taskmanager;

import interview.taskmanager.config.ConfigLoader;
import interview.taskmanager.config.TaskConfig;
import interview.thirdparties.TaskScheduler;

import java.util.*;
import java.util.stream.Stream;

public class TaskManager {


    private final TaskScheduler taskScheduler;
    private final String profile;

    public TaskManager(TaskScheduler taskScheduler, String profile) {
        this.taskScheduler = taskScheduler;
        this.profile = profile;
    }

    public List<TaskConfig> loadConfig(ConfigLoader... loaders) {
        Map<String, TaskConfig> result = new LinkedHashMap<>();
        Stream.of(loaders)
                .map(loader -> loader.load(profile))
                .flatMap(Collection::stream)
                .forEach(taskConfig -> result.put(taskConfig.name(), taskConfig));

        return new ArrayList<>(result.values());
    }

    public void schedule(List<TaskConfig> configs) {
        configs.stream()
                .filter(TaskConfig::enabled)
                .forEach(taskConfig -> taskScheduler.schedule(taskConfig.name(), taskConfig.chronExpression()));
    }
}
