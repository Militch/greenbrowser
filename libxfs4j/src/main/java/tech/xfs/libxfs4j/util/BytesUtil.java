package tech.xfs.libxfs4j.util;

public class BytesUtil {
    public static class LittleEndian {
        public static int readInt32(byte[] bs) {
            return bs[0] & 0xff |
                    (bs[1] & 0xff) << 8 |
                    (bs[2] & 0xff) << 16 |
                    (bs[3] & 0xff) << 24;
        }
        public static void putInt32(byte[] bs, int num){
            bs[0] = (byte) (num & 0xff);
            bs[1] = (byte) ((num >> 8) & 0xff);
            bs[2] = (byte) ((num >> 16) & 0xff);
            bs[3] = (byte) ((num >> 24) & 0xff);
        }
    }
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
}
