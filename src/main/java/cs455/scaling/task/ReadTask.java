package cs455.scaling.task;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingDeque;

import cs455.scaling.server.BatchUnit;

public class ReadTask implements Task {
    SelectionKey key;
    LinkedBlockingDeque<BatchUnit> batchQueue;

    public ReadTask(SelectionKey key, LinkedBlockingDeque<BatchUnit> batchQueue) {
        this.key = key;
        this.batchQueue = batchQueue;
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
			System.out.println("Read: " + buffer);
			synchronized(batchQueue){
            	batchQueue.offer(new BatchUnit(buffer.array(), client));
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
