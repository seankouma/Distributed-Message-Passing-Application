package cs455.scaling.task;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import cs455.scaling.server.BatchUnit;
import cs455.scaling.server.PoolThreadRunnable;
import cs455.scaling.util.Utility;

public class HashTask implements Task {
    
    LinkedBlockingDeque<BatchUnit> batchQueue;
    PoolThreadRunnable caller;
    ConcurrentHashMap<SocketChannel, Integer> countProcessed;
    AtomicInteger total;

    public HashTask(ConcurrentHashMap<SocketChannel, Integer> countProcessed, LinkedBlockingDeque<BatchUnit> batchQueue, AtomicInteger messageTotal) {
        this.batchQueue = new LinkedBlockingDeque(batchQueue);
        this.countProcessed = countProcessed;
        this.total = messageTotal;
    }

    @Override
    public void executeTask() {
        for (BatchUnit unit : batchQueue) {
            String hash = Utility.SHA1FromBytes(unit.data);
            // System.out.println("Hash: " + hash);
            try {
                unit.channel.write(ByteBuffer.wrap(hash.getBytes()));
                if (countProcessed.keySet().contains(unit.channel)) {
                    countProcessed.put(unit.channel, countProcessed.get(unit.channel) + 1);
                    total.incrementAndGet();
                } else {
                    countProcessed.put(unit.channel, 1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
