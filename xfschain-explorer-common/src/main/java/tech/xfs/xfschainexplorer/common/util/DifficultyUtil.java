package tech.xfs.xfschainexplorer.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DifficultyUtil {
    private static final long GENESIS_BITS_TEST_NET = 4278190109L;
    private static final long GENESIS_BITS_MAIN_NET = 267386909L;
    private static final long DEFAULT_GENESIS_BITS = GENESIS_BITS_TEST_NET;
    private static final BigInteger BIG_2 = BigInteger.valueOf(2);
    private static final int INT256 = 256;
    private static final BigInteger BIG_256BITS = BIG_2.pow(INT256);
    private static final long TARGET_TIME_PER_Block = 3 * 60;
    public static BigInteger bitsUnzip(long bits){
        long mantissa = bits & 0xffffff00;
        mantissa >>= 8;
        byte e = (byte) (bits & 0xff);
        byte c = (3 & 0xff);
        BigInteger bn;
        if (e <= c){
            int shift = (8 * (e - c));
            mantissa >>= shift;
            bn = BigInteger.valueOf(mantissa);
        }else {
            bn = BigInteger.valueOf(mantissa);
            int shift = 8 * (e - c);
            bn = bn.shiftLeft(shift);
        }
        return bn;
    }
    public static long bigByZip(BigInteger bn){
        if (bn == null){ return 0; }
        if (bn.signum() <= 0){
            return 0;
        }
        int c = 3;
        int bc = bn.toByteArray().length;
        if (bc < 1){
            return 0;
        }
        int e = (bc - 1);
        long mantissa;
        if (e <= c){
            mantissa = bn.bitCount();
            int shift = 8 * ( c - e);
            mantissa <<= shift;
        }else {
            int shift = 8 * ( e - c);
            BigInteger mantissaBn = bn.shiftRight(shift);
            mantissa = mantissaBn.intValue();
        }
        mantissa <<= 8;
        return (mantissa | e);
    }


    public static float calcDifficultyByBits(long bits){
        return (float) DEFAULT_GENESIS_BITS / (float) bits;
    }

    public static BigInteger calcWorkloadByBits(long bits){
        BigInteger target = bitsUnzip(bits);
        BigInteger max = bitsUnzip(DEFAULT_GENESIS_BITS);
        if (target.signum() <= 0){
            return BigInteger.ZERO;
        }
        BigInteger diff = max.divide(target);
        BigInteger n1 = diff.multiply(BIG_256BITS);
        return n1.divide(max);
    }
    public static long calcHashesByBits(long bits){
        float df = calcDifficultyByBits(bits);
        BigInteger diff = BigInteger.valueOf((long) df);
        BigInteger n1 = diff.multiply(BIG_256BITS);
        BigInteger workload = n1.divide(bitsUnzip(DEFAULT_GENESIS_BITS));
        return workload.longValue();
    }
    public static float calcHashRateByBits(long bits){
        float df = calcDifficultyByBits(bits);
        BigInteger diff = BigInteger.valueOf((long) df);
        BigInteger n1 = diff.multiply(BIG_256BITS);
        BigInteger workload = n1.divide(bitsUnzip(DEFAULT_GENESIS_BITS));
        long workload64 = workload.longValue();
        return (float) workload64 / TARGET_TIME_PER_Block;
    }
}
