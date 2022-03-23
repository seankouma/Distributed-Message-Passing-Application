package cs455.scaling.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import cs455.scaling.util.Utility;

public class SendTask extends TimerTask {
    Random r;
    SocketChannel serverChannel;
    ConcurrentHashMap<String,byte[]> hashesToArrays;
    Client client;

    SendTask(SocketChannel channel, ConcurrentHashMap<String, byte[]> hashesToArrays, Client client) {
        r = new Random();
        this.serverChannel = channel;
        this.hashesToArrays = hashesToArrays;
        this.client = client;
    }
    public void run() {
        byte[] b = new byte[8192]; // Create 8KB byte array
        r.nextBytes(b); // Fill with random values
        String hash = Utility.SHA1FromBytes(b);
        hashesToArrays.put(hash, b);
        // System.out.println("Hash: " + hash + ", Size: " + hashesToArrays.size());
        ByteBuffer buffer = ByteBuffer.wrap(b);
        try {
            serverChannel.write(buffer);
            client.incrementSent();
            buffer.clear();
            this.hashesToArrays.put(hash, b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
