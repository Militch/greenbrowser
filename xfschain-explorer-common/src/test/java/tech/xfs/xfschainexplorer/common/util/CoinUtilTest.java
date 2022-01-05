package tech.xfs.xfschainexplorer.common.util;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CoinUtilTest extends TestCase {

    public void testBaseCoin2atto() {
        long coinbase = 100;
        String atto = "100000000000000000000";
        BigInteger bn = CoinUtil.baseCoinToAtto(100);
        BigDecimal bn2 = CoinUtil.attoToBaseCoin(new BigInteger(atto,10));
        long gotCoinbase = bn2.longValue();
        assertEquals(coinbase, gotCoinbase);
//        System.out.println(bn);
    }
}