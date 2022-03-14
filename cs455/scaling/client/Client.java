package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

public class Client {

    private ConcurrentHashMap<String,byte[]> hashesToArrays = new ConcurrentHashMap<String, byte[]>();
    
    public void sendPackets(SocketChannel channel, long period) {
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
            this.hashesToArrays.remove(hash);
            buffer.rewind();
        }
    }

    public SocketChannel setupServerConnection(String hostname, int port) {
        try {
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress( hostname, port ));
            socketChannel.configureBlocking( false );
            return socketChannel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args ) {
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        int rate = Integer.parseInt(args[2]);

        Client client = new Client();
        SocketChannel serverChannel = client.setupServerConnection(hostname, port);
        client.sendPackets(serverChannel, 1000L / rate);
    }
}