package spaceio.core.scheduler;

import java.util.EventListener;

public interface TaskListener extends EventListener {
    /** Called when a task finishes running.
     * @param task the finished task
     */
    public void taskFinished (Task task);
}