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
import cs455.scaling.util.Node;
import cs455.scaling.server.*;

public class Client implements Node {

    private ConcurrentHashMap<String,byte[]> hashesToArrays = new ConcurrentHashMap<String, byte[]>();
	private int sent, recieved;

	public Client(){
		sent = 0;
		recieved = 0;
	}
    
    public void sendPackets(SocketChannel channel, long period) {
        Random r = new Random();

        Timer timer = new Timer();

        SendTask task = new SendTask(channel, hashesToArrays, this);

        timer.scheduleAtFixedRate(task, 0L, period);
        PrintStats ps = new PrintStats(this);
        timer.scheduleAtFixedRate(ps, 10000L, 10000L);

        ByteBuffer buffer = ByteBuffer.allocate(40);
        int bytesRead = 0;
        while (true) {
            try {
                int valid = channel.read(buffer);
                if (valid > 0) {
                    String hash = new String(buffer.array()).trim();
                    this.hashesToArrays.remove(hash);
                    buffer.rewind();
                    this.incrementRecieved();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // if (hash.length() > 0) System.out.println((int) hash.charAt(0));
            
        }
    }

    public SocketChannel setupServerConnection(String hostname, int port) {
        try {
			// System.out.println(SocketChannel.open(new InetSocketAddress( hostname, port )));
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

    @Override
    public synchronized void printStatistics(){
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
}
