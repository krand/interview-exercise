package interview.thirdparties;

public class TaskScheduler {
    public boolean schedule(String taskName, String cronExpression){
        System.out.printf("%s task was scheduled at %s\n", taskName, cronExpression);
        return true;
    }
}
