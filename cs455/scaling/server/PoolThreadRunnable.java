package cs455.scaling.server;

// Some code taken from http://tutorials.jenkov.com/java-concurrency/thread-pools.html

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cs455.scaling.task.HashTask;
import cs455.scaling.task.Task;

public class PoolThreadRunnable implements Runnable {

    private BlockingQueue<Task> taskQueue = null;
    private boolean       isStopped = false;
    public boolean available = true;
    ThreadPool manager;

    public PoolThreadRunnable(BlockingQueue<Task> queue, ThreadPool manager) {
        taskQueue = new LinkedBlockingQueue<Task>();
        this.manager = manager;
    }

    @Override
    public void run() {
        while (true) {
            if (taskQueue.size() > 0) {
                try {
                    Task task = taskQueue.take();
                    task.executeTask();
                    this.available = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void addTask(Task task) {
        this.taskQueue.add(task);
    }

    public synchronized boolean isStopped(){
        return isStopped;
    }

    public void makeUnavailable() {
        this.available = false;
    }
}