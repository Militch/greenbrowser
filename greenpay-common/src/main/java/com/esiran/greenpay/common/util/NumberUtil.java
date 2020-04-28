package com.esiran.greenpay.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {
    public static String amountFen2Yuan(Integer fen){
        if (fen == null) return null;
        BigDecimal amount = new BigDecimal(fen);
        BigDecimal amountUnit = new BigDecimal(100);
        BigDecimal amountDisplay = amount.divide(amountUnit,2,BigDecimal.ROUND_HALF_UP);
        return String.format("%.2f",amountDisplay.floatValue());
    }
    public static Integer amountYuan2fen(BigDecimal src){
        if (src == null) return null;
        BigDecimal amount = src.setScale(2, RoundingMode.HALF_UP);
        BigDecimal amountUnit = new BigDecimal(100);
        return amount.multiply(amountUnit).intValue();
    }
    public static String twoDecimals(BigDecimal src){
        if (src == null) return null;
        BigDecimal amount = src.setScale(2, RoundingMode.HALF_UP);
        return String.format("%.2f",amount.floatValue());
    }
}
