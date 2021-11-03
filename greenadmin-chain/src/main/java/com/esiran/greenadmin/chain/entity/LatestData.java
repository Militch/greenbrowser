package com.esiran.greenadmin.chain.entity;

import lombok.Data;

import java.util.List;

@Data
public class LatestData {
    private List<BlockHeader> blocks;
    private List<BlockTx> txs;
}
