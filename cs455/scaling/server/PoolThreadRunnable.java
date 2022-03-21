package cs455.scaling.server;

import java.nio.channels.SocketChannel;
import java.util.Deque;

// Some code taken from http://tutorials.jenkov.com/java-concurrency/thread-pools.html

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import cs455.scaling.task.HashTask;
import cs455.scaling.task.Task;

public class PoolThreadRunnable implements Runnable {

    // private BlockingQueue<Task> taskQueue = null;
    LinkedBlockingQueue<PoolThreadRunnable> available;
    volatile Task task = null;
    private boolean       isStopped = false;
    ThreadPool manager;

    public PoolThreadRunnable(LinkedBlockingQueue<PoolThreadRunnable> available, ThreadPool manager) {
        this.available = available;
        this.manager = manager;
    }

    @Override
    public void run() {
        while (true) {
            while (task == null);
                // synchronized (task) {
                    task.executeTask();
                    this.available.add(this);
                // }
        }
    }
    
    // public void addTask(Task task) {
    //     this.taskQueue.add(task);
    // }

    public void setTask(Task task) {
        // if (this.task == null) this.task = task;
        // else {
        //     synchronized (this.task) {
        //         System.out.println("Test 3");
        //         this.task = task;
        //     }
        // }
        this.task = task;
    }

    public synchronized boolean isStopped(){
        return isStopped;
    }
}