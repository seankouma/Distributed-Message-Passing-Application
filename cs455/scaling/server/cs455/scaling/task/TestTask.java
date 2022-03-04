package cs455.scaling.task;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cs455.scaling.server.PoolThreadRunnable;

public class TestTask implements Runnable {
    
    byte[] data;
    PoolThreadRunnable caller;

    public TestTask(byte[] testBytes) {
        this.data = testBytes;
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

    public void setCaller(PoolThreadRunnable caller) {
        this.caller = caller;
    }

    @Override
    public void run() {
        String hash = this.SHA1FromBytes(data);
        this.caller.setTaskResult(hash);
    }
}
