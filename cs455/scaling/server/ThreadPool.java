package cs455.scaling.server;
// Some code taken from http://tutorials.jenkov.com/java-concurrency/thread-pools.html

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import cs455.scaling.task.Task;

public class ThreadPool {

    private LinkedBlockingQueue<Task> taskQueue = null;
    private List<PoolThreadRunnable> runnables = new ArrayList<>();
    private Deque<PoolThreadRunnable> available = new ArrayDeque<PoolThreadRunnable>();

    public ThreadPool(int noOfThreads){
        taskQueue = new LinkedBlockingQueue<Task>();

        for(int i=0; i<noOfThreads; i++){
            PoolThreadRunnable ptr = new PoolThreadRunnable(taskQueue, this);
            runnables.add(ptr);
            available.add(ptr);
        }
        for(PoolThreadRunnable runnable : runnables) {
            new Thread(runnable).start();
        }
    }

    public synchronized void execute(Task task) {
        PoolThreadRunnable current =  available.pollLast();
        while (current == null) current = available.pollLast();
        current.addTask(task);
        available.addFirst(current);
    }

    public synchronized void waitUntilAllTasksFinished() {
        while(this.taskQueue.size() > 0) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}