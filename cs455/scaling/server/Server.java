public class Server {
    Selector selector;
    ServerSocketChannel serverSocket;
    
    Server() {

    }

    public void setupServerSocketChannel(String host, int port) {
        selector = Selector.open(); // created once
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind( new InetSocketAddress( host, port ) );
        serverSocket.configureBlocking( false );
        serverSocket.register( selector, SelectionKey.OP_ACCEPT );
    }

    public static void main(String[] args) {
        int portNum = Integer.parseInt(args[0]);
        int threadPoolSize = Integer.parseInt(args[1]);
        int batchSize = Integer.parseInt(args[2]);
        int batchTime = Integer.parseInt(args[3]);

        Server server = new Server();
        server.setupServerSocketChannel("localhost", portNum);
    }
}
