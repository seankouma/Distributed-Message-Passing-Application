package cs455.scaling.server;

// Some code taken from http://tutorials.jenkov.com/java-concurrency/thread-pools.html

import java.util.concurrent.BlockingQueue;

import cs455.scaling.task.HashTask;

public class PoolThreadRunnable implements Runnable {

    private Thread        thread    = null;
    private BlockingQueue taskQueue = null;
    private boolean       isStopped = false;
    ThreadPool manager;

    public PoolThreadRunnable(BlockingQueue queue, ThreadPool manager){
        taskQueue = queue;
        this.manager = manager;
    }

    public void run(){
        this.thread = Thread.currentThread();
        while(!isStopped()){
            try{
                HashTask runnable = (HashTask) taskQueue.take();
                runnable.setCaller(this);
                runnable.run();
            } catch(Exception e){
                //log or otherwise report exception,
                //but keep pool thread alive.
            }
        }
    }

    public synchronized void doStop(){
        isStopped = true;
        //break pool thread out of dequeue() call.
        this.thread.interrupt();
    }

    public synchronized boolean isStopped(){
        return isStopped;
    }

    public void setTaskResult(String hashResult) {
        this.manager.remove(hashResult);
    }
}