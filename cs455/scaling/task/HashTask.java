package cs455.scaling.task;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cs455.scaling.server.PoolThreadRunnable;
import cs455.scaling.util.Utility;

public class HashTask implements Runnable {
    
    byte[] data;
    PoolThreadRunnable caller;

    public HashTask(byte[] testBytes) {
        this.data = testBytes;
    }

    public void setCaller(PoolThreadRunnable caller) {
        this.caller = caller;
    }

    @Override
    public void run() {
        String hash = Utility.SHA1FromBytes(data);
        System.out.println("Hash: " + hash);
        this.caller.setTaskResult(hash);
    }
}
