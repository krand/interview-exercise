package interview.taskmanager.config;

import java.util.List;

public interface ConfigLoader {
    List<TaskConfig> load(String profile);
}
