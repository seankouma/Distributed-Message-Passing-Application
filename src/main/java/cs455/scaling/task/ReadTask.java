package cs455.scaling.task;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import cs455.scaling.server.BatchUnit;

public class ReadTask implements Task {
    SelectionKey key;
    LinkedBlockingDeque<BatchUnit> batchQueue;
	private int maxSize;
    AtomicInteger total;

    public ReadTask(SelectionKey key, LinkedBlockingDeque<BatchUnit> batchQueue, int maxSize, AtomicInteger total) {
        this.key = key;
        this.batchQueue = batchQueue;
		this.maxSize = maxSize;
        this.total = total;
    }
    
    @Override
    public void executeTask() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            SocketChannel client = (SocketChannel) key.channel();
            int bytesRead = 0;
            while ( buffer.hasRemaining() && bytesRead != -1 ) {
                bytesRead = client.read( buffer );
            }
			synchronized(batchQueue){
				if(batchQueue.size() >= maxSize){
					batchQueue.wait();
				}
            	batchQueue.offer(new BatchUnit(buffer.array(), client));
                synchronized (total) {
                    total.incrementAndGet();
                }
			}
            synchronized (key) {
                this.key.attach(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		finally{
			synchronized(key){
				key.attach(null);
			}
		}
    }
}
