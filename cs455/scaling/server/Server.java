package cs455.scaling.server;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import cs455.scaling.task.HashTask;
import cs455.scaling.util.Utility;

public class Server {
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private ThreadPool pool;
    private ConcurrentHashMap<String,byte[]> hashesToArrays; // This will get moved to only live in the ThreadPool class. Only here for testing
    
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
        server.setupThreadPool(5);
        server.listen();
    }

    private void listen() {
        try {
            while ( true ) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        this.acceptConnection(key);
                    }

                    if (key.isReadable()) {
                        this.readFromConnection(key);
                    }
                    iterator.remove();
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        System.out.println("It works!");
            this.register(selector, serverSocket);
    }

    private void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    private void readFromConnection(SelectionKey key) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        SocketChannel client = (SocketChannel) key.channel();
        int bytesRead = 0;
        while ( buffer.hasRemaining() && bytesRead != -1 ) {
            bytesRead = client.read( buffer );
        }
        HashTask task = new HashTask(buffer.array());
        this.pool.execute(task);
        // key.cancel();
        if (key.attachment() == null) {
            // ReadAndRespond readAndRespond = new ReadAndRespond(key);
            // threadPoolManager.addTask(readAndRespond);
        } else {
            // log.debug("\tAlreadyReadAndResponded");
        }
    }
}

// TEST CODE FOR THREAD POOL. Borrow from this code when adding thread pool functionality back in.
// server.setupThreadPool(threadPoolSize);

        //Test Code
        // Random r = new Random();
        // for (int i = 0; i < 10; ++i) {
        //     byte[] b = new byte[8192]; // Create 8KB byte array
        //     r.nextBytes(b); // Fill with random values
        //     String hash = Utility.SHA1FromBytes(b);
        //     server.hashesToArrays.put(hash, b);
        //     TestTask task = new TestTask(b);
        //     try {
        //         server.pool.execute(task);
        //     } catch (Exception e) {
        //         // TODO Auto-generated catch block
        //         e.printStackTrace();
        //     }
        // }
        // try {
        //     Thread.sleep(1000);
        //     System.out.println("Size: " + server.hashesToArrays.size()); // Should be 0 if everything went well
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
