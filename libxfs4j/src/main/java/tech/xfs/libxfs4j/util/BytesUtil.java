package tech.xfs.libxfs4j.util;

import java.util.Arrays;

public class BytesUtil {

    public static String toHexString(byte[] bs){
        if (bs == null || bs.length == 0){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bs) {
            int var = b & 0xff;
            String hexvar = Integer.toHexString(var);
            if (hexvar.length() < 2) {
                sb.append(0);
            }
            sb.append(hexvar);
        }
        return sb.toString();
    }
    public static byte[] concat(byte[] a, byte[] b){
        byte[] result = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
