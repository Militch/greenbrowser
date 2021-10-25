package com.esiran.greenadmin.chain.service;

import com.esiran.greenadmin.chain.entity.Block;
import com.esiran.greenadmin.chain.entity.BlockHeader;
import com.esiran.greenadmin.chain.entity.TxReceipt;

import java.util.List;

public interface IBlockChainService {
    void insertBlock(Block block) throws Exception;
    void insertBlocks(List<Block> block) throws Exception;
    BlockHeader getHeadBlock();
    Block getBlockByHash(String hash);
    BlockHeader getBlockHeaderByHash(String hash);
}
