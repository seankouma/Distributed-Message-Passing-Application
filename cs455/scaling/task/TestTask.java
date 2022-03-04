package cs455.scaling.task;


public class TestTask implements Runnable {
    
    byte[] data;
    PoolThreadRunnable caller;

    TestTask(byte[] testBytes) {
        this.data = testBytes;
    }

    private String SHA1FromBytes(byte[] data) {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        byte[] hash = digest.digest(data);
        BigInteger hashInt = new BigInteger(1, hash);
        return hashInt.toString(16);
    }

    public void setCaller(PoolThreadRunnable caller) {
        this.caller = caller;
    }

    run() {
        this.caller.setTaskResult(this.SHA1FromBytes(data));
    }
}
