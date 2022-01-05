package com.esiran.greenadmin.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class CoinUtil {
    private static final BigInteger BIG_TEN = BigInteger.valueOf(10);
    private static final BigInteger BIG_10E9 = BIG_TEN.pow(9);
    private static final BigInteger BIG_10E18 = BIG_TEN.pow(18);

    public static BigInteger baseCoinToAtto(BigDecimal num){
        if (num == null){
            return BigInteger.ZERO;
        }
        num = num.multiply(new BigDecimal(BIG_10E18));
        return num.toBigInteger();
    }
    public static BigInteger baseCoinToAtto(double num){
        return baseCoinToAtto(BigDecimal.valueOf(num));
    }

    public static BigDecimal attoToBaseCoin(BigInteger num){
        if (num == null){
            return BigDecimal.ZERO;
        }
        BigDecimal base = new BigDecimal(num);
        base = base.divide(new BigDecimal(BIG_10E18), RoundingMode.FLOOR);
        return base;
    }

    public static BigDecimal attoToBaseCoin(long num){
        return attoToBaseCoin(BigInteger.valueOf(num));
    }

    public static BigDecimal attoToNano(BigInteger num){
        if (num == null){
            return BigDecimal.ZERO;
        }
        BigDecimal base = new BigDecimal(num);
        base = base.divide(new BigDecimal(BIG_10E18), RoundingMode.FLOOR);
        return base;
    }
    public static BigDecimal attoToNano(long num){
        return attoToNano(BigInteger.valueOf(num));
    }
    public static BigInteger baseCoinToNano(BigDecimal num){
        if (num == null){
            return BigInteger.ZERO;
        }
        num = num.multiply(new BigDecimal(BIG_10E9));
        return num.toBigInteger();
    }
    public static BigInteger baseCoinToNano(double num){
        return baseCoinToNano(BigDecimal.valueOf(num));
    }

    public static BigDecimal nanoToBaseCoin(BigInteger num){
        if (num == null){
            return BigDecimal.ZERO;
        }
        BigDecimal base = new BigDecimal(num);
        base = base.divide(new BigDecimal(BIG_10E9), RoundingMode.FLOOR);
        return base;
    }
    public static BigDecimal nanoToBaseCoin(long num){
        return nanoToBaseCoin(BigInteger.valueOf(num));
    }

    public static BigInteger nanoToAtto(BigDecimal num){
        if (num == null){
            return BigInteger.ZERO;
        }
        num = num.multiply(new BigDecimal(BIG_10E9));
        return num.toBigInteger();
    }

    public static BigInteger nanoToAtto(double num){
        return nanoToAtto(BigDecimal.valueOf(num));
    }
}
