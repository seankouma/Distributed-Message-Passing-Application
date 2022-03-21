package cs455.scaling.task;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptTask implements Task {
    Selector selector;
    ServerSocketChannel serverSocket;

    public AcceptTask(Selector selector, ServerSocketChannel serverSocket) {
        this.selector = selector;
        this.serverSocket = serverSocket;
    }

    @Override
    public void executeTask() {
        try {
			SocketChannel client; 
			synchronized(serverSocket){
            	client = serverSocket.accept();
			}

            client.configureBlocking(false);

			synchronized(selector){
            	selector.wakeup();
            	client.register(selector, SelectionKey.OP_READ);
			}
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("It works!");
    }
}
