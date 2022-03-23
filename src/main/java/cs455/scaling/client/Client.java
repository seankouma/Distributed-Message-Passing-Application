package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;
import java.sql.Timestamp;

public class Client {

    private ConcurrentHashMap<String,byte[]> hashesToArrays = new ConcurrentHashMap<String, byte[]>();
	private int sent, recieved;

	public Client(){
		sent = 0;
		recieved = 0;
	}
    
    public void sendPackets(SocketChannel channel, long period) {
		long startTime = System.nanoTime();
        Random r = new Random();

        Timer timer = new Timer();

        SendTask task = new SendTask(channel, hashesToArrays);

        timer.scheduleAtFixedRate(task, 0L, period);

        ByteBuffer buffer = ByteBuffer.allocate(40);
        int bytesRead = 0;
        while (true) {
            try {
                channel.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String hash = new String(buffer.array()).trim();
			System.out.println(hash);
			System.exit(0);
            this.hashesToArrays.remove(hash);
            buffer.rewind();
			if(shouldDie(startTime)){
				System.exit(0);
			}
        }
    }

    public SocketChannel setupServerConnection(String hostname, int port) {
        try {
			System.out.println(SocketChannel.open(new InetSocketAddress( hostname, port )));
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress( hostname, port ));
            socketChannel.configureBlocking( false );
            return socketChannel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args ) {
        String hostname = args[1];
        int port = Integer.parseInt(args[2]);
        int rate = Integer.parseInt(args[3]);

        Client client = new Client();
        SocketChannel serverChannel = client.setupServerConnection(hostname, port);
        client.sendPackets(serverChannel, 1000L / rate);
    }

	public synchronized void printStatitics(){
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);
		System.out.println(ts + "Total Sent Count: " + sent + ", Total Recieved Count: " + recieved);
		sent = 0;
		recieved = 0;
	}

	public synchronized void incrementSent(){
		sent++;
	}

	public synchronized void incrementRecieved(){
		recieved++;
	}

	private boolean shouldDie(long startTime){
		return System.nanoTime() - startTime > 5 * 60 * Math.pow(10, 9);
	}
}
