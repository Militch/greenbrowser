package com.esiran.greenadmin.chain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class ChainStatus {
    private Long latestHeight;
    private Long transactions;
    private Long difficulty;
    private Long power;
    private Long accounts;
    private Long blockTime;
    private String blockRewards;
    private Long txsInBlock;
    private Long tps;
}
