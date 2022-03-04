package cs455.scaling.server;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import cs455.scaling.task.TestTask;

public class Server {
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private ThreadPool pool;
    private ConcurrentHashMap hashesToArrays; // This will get moved to only live in the ThreadPool class. Only here for testing
    
    Server() {
        this.hashesToArrays = new ConcurrentHashMap<String, byte[]>();
    }

    private void setupServerSocketChannel(String host, int port) {
        try {
            selector = Selector.open(); // created once
            serverSocket = ServerSocketChannel.open();
            serverSocket.socket().bind( new InetSocketAddress( host, port ) );
            serverSocket.configureBlocking( false );
            serverSocket.register( selector, SelectionKey.OP_ACCEPT );
        } catch (ClosedChannelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setupThreadPool(int threadPoolSize) {
        this.pool = new ThreadPool(threadPoolSize, hashesToArrays);
    }

    public static void main(String[] args) {
        int portNum = Integer.parseInt(args[0]);
        int threadPoolSize = Integer.parseInt(args[1]);
        int batchSize = Integer.parseInt(args[2]);
        int batchTime = Integer.parseInt(args[3]);
        Server server = new Server();
        server.setupServerSocketChannel("localhost", portNum);
        server.setupThreadPool(threadPoolSize);
        //Test Code
        Random r = new Random();
        for (int i = 0; i < 10; ++i) {
            byte[] b = new byte[8192]; // Create 8KB byte array
            r.nextBytes(b); // Fill with random values
            String hash = server.SHA1FromBytes(b);
            server.hashesToArrays.put(hash, b);
            TestTask task = new TestTask(b);
            try {
                server.pool.execute(task);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(1000);
            System.out.println("Size: " + server.hashesToArrays.size()); // Should be 0 if everything went well
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.exit(0);
    }

    private String SHA1FromBytes(byte[] data) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA1");
            byte[] hash = digest.digest(data);
            BigInteger hashInt = new BigInteger(1, hash);
            return hashInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
