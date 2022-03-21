package cs455.scaling.server;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.Date;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.atomic.AtomicInteger;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import cs455.scaling.task.Task;
import cs455.scaling.task.AcceptTask;
import cs455.scaling.task.HashTask;
import cs455.scaling.task.ReadTask;
import cs455.scaling.util.Utility;

public class Server {
    private LinkedBlockingQueue<Task> taskQueue;
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private ThreadPool pool;
    public ConcurrentHashMap<SocketChannel, Integer> messageCount = new ConcurrentHashMap<SocketChannel, Integer>();
	  private long timeSinceLastBatch;
	  private LinkedBlockingDeque<BatchUnit> batchQueue;
	public Server(){

        batchQueue = new LinkedBlockingDeque<BatchUnit>();
		taskQueue = new LinkedBlockingQueue<Task>();
	}

    private void setupServerSocketChannel(int port) {
        try {
            selector = Selector.open(); // created once
            serverSocket = ServerSocketChannel.open();
            serverSocket.socket().bind(new InetSocketAddress(port));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupThreadPool(int threadPoolSize) {
        this.pool = new ThreadPool(threadPoolSize);
    }

    public static void main(String[] args) {
        int portNum = Integer.parseInt(args[1]);
        int threadPoolSize = Integer.parseInt(args[2]);
        int batchSize = Integer.parseInt(args[3]);
        int batchTime = Integer.parseInt(args[4]);

        Server server = new Server();
        server.setupServerSocketChannel(portNum);
        server.setupThreadPool(threadPoolSize);
        server.listen(batchSize, batchTime);
    }

    private void listen(int batchSize, int batchTime) {
		timeSinceLastBatch = System.nanoTime();
        try {
          Timer timer = new Timer();
          PrintStats ps = new PrintStats(this);
          timer.scheduleAtFixedRate(ps, 20000L, 20000L);
          LinkedBlockingDeque<BatchUnit> batchQueue = new LinkedBlockingDeque<BatchUnit>();
          while ( true ) {
				synchronized(selector){
                	selector.select();
                	Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    int received = 0;
                	while (iterator.hasNext()) {
                    	SelectionKey key = iterator.next();
						synchronized(key){
                        	if (key.attachment() != null) continue;
						}
                    	if (key.isAcceptable()) {
                        	SocketChannel client = serverSocket.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                    	}
                    	if (shouldSendBatch(batchSize, batchTime)){
							sendBatch();
                        	System.out.println("Batch");
                    	}
                    	if (key.isReadable()) {
							synchronized(key){
                            	key.attach(42);
							}
                        	// System.out.println("Readable. Size: " + batchQueue.size());
                        	this.addReadTask(key, batchQueue);
                    	}
                    	iterator.remove();
                	}
				}
				assignTasks();
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private boolean shouldSendBatch(int batchSize, int batchTime){
		return batchQueue.size() >= batchSize || System.nanoTime() - timeSinceLastBatch > Math.pow(10, 9) * batchTime;
	}

	private void sendBatch(){
		taskQueue.add(new HashTask(batchQueue));
		batchQueue.clear(); 
		timeSinceLastBatch = System.nanoTime();
	}

	private void assignTasks(){
		synchronized(pool){
			while(!taskQueue.isEmpty() && pool.hasWorker()){
				pool.execute(taskQueue.poll());
			}
		}
	}
		
    public void printStatistics() {
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        int size = pool.messageCount.size();
        if (size == 0) return;
        synchronized (pool.messageTotal) {
            double mean = pool.messageTotal.get() / size;
            double deviationSum = 0;
            for (Integer sum : pool.messageCount.values()) {
                double diff = mean - sum;
                deviationSum += diff * diff;
            }
            double dev = Math.sqrt(deviationSum / (size - 1));
            System.out.format(ts + " Server Throughput: " + pool.messageTotal.get() + ", Active Client Connections: " + size + ", Mean Per-client Throughput: " + mean + ", Std. Dev. Of Per-client Throughput: " + dev + "\n");
            pool.messageTotal.set(0);
        }
        pool.messageCount = new ConcurrentHashMap<SocketChannel, Integer>();
    }
    
    private void addAcceptTask(Selector selector, ServerSocketChannel serverSocket) {
        AcceptTask task = new AcceptTask(selector, serverSocket);
		taskQueue.add(task);	
    }

    private void addReadTask(SelectionKey key, LinkedBlockingDeque<BatchUnit> batchQueue) throws Exception {
        ReadTask task = new ReadTask(key, batchQueue);
		taskQueue.add(task);	
    }
}
