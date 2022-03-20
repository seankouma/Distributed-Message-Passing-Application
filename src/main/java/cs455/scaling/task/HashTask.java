package cs455.scaling.task;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.LinkedBlockingDeque;

import cs455.scaling.server.BatchUnit;
import cs455.scaling.server.PoolThreadRunnable;
import cs455.scaling.util.Utility;

public class HashTask implements Task {
    
    LinkedBlockingDeque<BatchUnit> batchQueue;
    PoolThreadRunnable caller;

    public HashTask(LinkedBlockingDeque<BatchUnit> batchQueue) {
        this.batchQueue = new LinkedBlockingDeque(batchQueue);
    }

    @Override
    public void executeTask() {
        for (BatchUnit unit : batchQueue) {
            String hash = Utility.SHA1FromBytes(unit.data);
            System.out.println("Hash: " + hash);
            try {
                unit.channel.write(ByteBuffer.wrap(hash.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
