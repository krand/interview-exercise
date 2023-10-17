package interview.taskmanager.config;

public record TaskConfig(String name,
                         boolean enabled,
                         String chronExpression) {

}
