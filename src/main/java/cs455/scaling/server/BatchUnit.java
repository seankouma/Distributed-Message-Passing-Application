package cs455.scaling.server;

import java.nio.channels.SocketChannel;

public class BatchUnit {
    public byte[] data;
    public SocketChannel channel;

    public BatchUnit(byte[] data, SocketChannel channel) {
        this.data = data;
        this.channel = channel;
    }
}
