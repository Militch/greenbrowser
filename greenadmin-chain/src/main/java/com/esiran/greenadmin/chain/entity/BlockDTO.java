package com.esiran.greenadmin.chain.entity;

import lombok.Data;

import java.util.List;

@Data
public class BlockDTO {
    private BlockHeader header;
    private List<BlockTxDTO> transactions;
}
