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
import java.util.concurrent.LinkedBlockingDeque;

import cs455.scaling.task.AcceptTask;
import cs455.scaling.task.HashTask;
import cs455.scaling.task.ReadTask;
import cs455.scaling.util.Utility;

public class Server {
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private ThreadPool pool;

    private void setupServerSocketChannel(String host, int port) {
        try {
            selector = Selector.open(); // created once
            serverSocket = ServerSocketChannel.open();
            serverSocket.socket().bind( new InetSocketAddress( host, port ) );
            serverSocket.configureBlocking( false );
            serverSocket.register( selector, SelectionKey.OP_ACCEPT );
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
        int portNum = Integer.parseInt(args[0]);
        int threadPoolSize = Integer.parseInt(args[1]);
        int batchSize = Integer.parseInt(args[2]);
        int batchTime = Integer.parseInt(args[3]);

        Server server = new Server();
        server.setupServerSocketChannel("localhost", portNum);
        server.setupThreadPool(threadPoolSize);
        server.listen(batchSize, batchTime);
    }

    private void listen(int batchSize, int batchTime) {
        try {
            LinkedBlockingDeque<BatchUnit> batchQueue = new LinkedBlockingDeque<BatchUnit>();
            while ( true ) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        System.out.println("Acceptable");
                        this.addAcceptTask(this.selector, this.serverSocket);
                    }
                    if (batchQueue.size() >= batchSize) {
                        System.out.println("Batch");
                        this.pool.execute(new HashTask(batchQueue));
                        batchQueue = new LinkedBlockingDeque<BatchUnit>();
                    }
                    if (key.isReadable()) {
                        System.out.println("Readable. Size: " + batchQueue.size());
                        this.addReadTask(key, batchQueue);
                    }
                    iterator.remove();
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAcceptTask(Selector selector, ServerSocketChannel serverSocket) {
        AcceptTask task = new AcceptTask(selector, serverSocket);
        this.pool.execute(task);
    }

    private void addReadTask(SelectionKey key, LinkedBlockingDeque<BatchUnit> batchQueue) throws Exception {
        ReadTask task = new ReadTask(key, batchQueue);
        this.pool.execute(task);
    }
}