package tech.xfs.xfschainexplorer.chain.entity;

import lombok.Data;

import java.util.List;

@Data
public class Block {
    private BlockHeader header;
    private List<BlockTx> transactions;
}
