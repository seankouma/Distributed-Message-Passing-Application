package cs455.scaling.task;

import cs455.scaling.server.PoolThreadRunnable;

public interface Task {
    public void executeTask();
    // public void setCaller(PoolThreadRunnable caller);
}
