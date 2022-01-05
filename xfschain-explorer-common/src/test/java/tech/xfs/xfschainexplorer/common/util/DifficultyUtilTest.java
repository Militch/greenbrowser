package tech.xfs.xfschainexplorer.common.util;

import junit.framework.TestCase;

import java.math.BigInteger;

public class DifficultyUtilTest extends TestCase {

    public void testBitsUnzip() {
        BigInteger bn = DifficultyUtil.bitsUnzip(4278190109L/16);
        String hex = bn.toString(16);
        System.out.println(hex);
        long n = DifficultyUtil.bigByZip(bn);
        System.out.println(n);
    }

    public void testBigByZip() {
    }

    public void testCalcDifficultyByBits() {
        float d = DifficultyUtil.calcDifficultyByBits(4278190109L * 8);
        System.out.println(d);
    }

    public void testCalcWorkloadByBits() {
        BigInteger bn = DifficultyUtil.calcWorkloadByBits(4278190109L * 8);
        System.out.println(bn.toString(16));
    }

    public void testCalcHashRateByBits() {
        float f = DifficultyUtil.calcHashRateByBits(4278190109L * 8);
        System.out.println(f);
    }

    public void testCalcHashesByBits() {
        long f = DifficultyUtil.calcHashesByBits(4278190109L * 8);
        System.out.println(f);
    }
}