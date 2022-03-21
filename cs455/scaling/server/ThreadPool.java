package cs455.scaling.server;
// Some code taken from http://tutorials.jenkov.com/java-concurrency/thread-pools.html

import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import cs455.scaling.task.Task;

public class ThreadPool {

    private LinkedBlockingQueue<Task> taskQueue = null;
    private List<PoolThreadRunnable> runnables = new ArrayList<>();
    private LinkedBlockingQueue<PoolThreadRunnable> available = new LinkedBlockingQueue<PoolThreadRunnable>();
    public  ConcurrentHashMap<SocketChannel, Integer> messageCount = new ConcurrentHashMap<SocketChannel, Integer>();
    public AtomicInteger messageTotal = new AtomicInteger(0);

    public ThreadPool(int noOfThreads){
        taskQueue = new LinkedBlockingQueue<Task>();

        for(int i=0; i<noOfThreads; i++){
            PoolThreadRunnable ptr = new PoolThreadRunnable(available, this);
            runnables.add(ptr);
            available.add(ptr);
        }
        for(PoolThreadRunnable runnable : runnables) {
            new Thread(runnable).start();
        }
    }

    public void execute(Task task) {
        PoolThreadRunnable current =  available.poll();
        while (current == null) current = available.poll();
        current.setTask(task);
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