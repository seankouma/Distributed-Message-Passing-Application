package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import cs455.scaling.server.Server;
import cs455.scaling.util.Utility;

public class Client {
    public static void main(String[] args ) {
        try {
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress( "localhost", 5000 ));
            socketChannel.configureBlocking( false );
            Random r = new Random();

            for (int i = 0; i < 100; i++) {
                byte[] b = new byte[8192]; // Create 8KB byte array
                r.nextBytes(b); // Fill with random values
                String hash = Utility.SHA1FromBytes(b);
                System.out.println("Hash: " + hash);
                ByteBuffer buffer = ByteBuffer.wrap(b);
                String response = null;
                try {
                    socketChannel.write(buffer);
                    buffer.clear();
                    socketChannel.read(buffer);
                    response = new String(buffer.array()).trim();
                    // System.out.println("response=" + response);
                    buffer.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            while(true); /* This keeps the SocketChannel open and prevents a bug where
                          the server node keeps reading empty byte arrays from a closed SocketChanel.
                          Once we put this code in a while loop rather than a for-loop, it will fix this.*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}