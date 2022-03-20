package cs455.scaling.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utility {
    private static MessageDigest digest;

    public static String SHA1FromBytes(byte[] data) {
        try {
            digest = MessageDigest.getInstance("SHA1");
            byte[] hash = digest.digest(data);
            BigInteger hashInt = new BigInteger(1, hash);
            String strHash = hashInt.toString(16);
            while (strHash.length() != 40) {
                strHash = "0" + strHash;
            }
            return strHash;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
