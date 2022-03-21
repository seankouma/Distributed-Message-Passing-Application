package cs455.scaling.server;
// Some code taken from http://tutorials.jenkov.com/java-concurrency/thread-pools.html

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import cs455.scaling.task.Task;

public class ThreadPool {

    private Deque<PoolThreadRunnable> available = new ArrayDeque<PoolThreadRunnable>();

    public ThreadPool(int noOfThreads){

        for(int i=0; i<noOfThreads; i++){
            PoolThreadRunnable ptr = new PoolThreadRunnable(this);
            available.add(ptr);
        }
        for(PoolThreadRunnable runnable : available) {
            runnable.start();
        }
    }

    public synchronized void execute(Task task) {
		PoolThreadRunnable current = available.pollLast();
       	current.addTask(task);
    }

	public synchronized void returnThread(PoolThreadRunnable toReturn){
		available.addFirst(toReturn);
	}

	public synchronized boolean hasWorker(){
		return !available.isEmpty();
	}
}
