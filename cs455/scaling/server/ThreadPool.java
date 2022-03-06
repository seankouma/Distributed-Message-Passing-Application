package cs455.scaling.server;
// Some code taken from http://tutorials.jenkov.com/java-concurrency/thread-pools.html

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {

    private BlockingQueue taskQueue = null;
    private List<PoolThreadRunnable> runnables = new ArrayList<>();
    private boolean isStopped = false;

    ConcurrentHashMap hashesToArrays;

    public ThreadPool(int noOfThreads, ConcurrentHashMap hashesToArrays){
        taskQueue = new LinkedBlockingQueue();
        this.hashesToArrays = hashesToArrays;

        for(int i=0; i<noOfThreads; i++){
            runnables.add(new PoolThreadRunnable(taskQueue, this));
        }
        for(PoolThreadRunnable runnable : runnables){
            new Thread(runnable).start();
        }
    }

    public synchronized void  execute(Runnable task) throws Exception {
        if(this.isStopped) throw
                new IllegalStateException("ThreadPool is stopped");

        this.taskQueue.offer(task);
    }

    public synchronized void stop(){
        this.isStopped = true;
        for(PoolThreadRunnable runnable : runnables){
            runnable.doStop();
        }
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

    public void remove(String hash) {
        this.hashesToArrays.remove(hash);
    }

}