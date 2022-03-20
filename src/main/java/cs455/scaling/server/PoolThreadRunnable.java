package cs455.scaling.server;

// Some code taken from http://tutorials.jenkov.com/java-concurrency/thread-pools.html

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cs455.scaling.task.HashTask;
import cs455.scaling.task.Task;

public class PoolThreadRunnable extends Thread{

    private Task toDo;
    private boolean isStopped;
    private ThreadPool manager;

    public PoolThreadRunnable(ThreadPool manager) {
        this.manager = manager;
		isStopped = false;
		toDo = null;
    }

    @Override
    public void run() {
        while (true) {
            if(hasTask()) {
                try {
                    toDo.executeTask();
                }finally{
					toDo = null;
					manager.returnThread(this);
				}
            }
        }
    }
    
    public synchronized void addTask(Task task) {
        toDo = task;
    }

    public synchronized boolean isStopped(){
        return isStopped;
    }

	public synchronized boolean hasTask(){
		return toDo != null;
	}
}
