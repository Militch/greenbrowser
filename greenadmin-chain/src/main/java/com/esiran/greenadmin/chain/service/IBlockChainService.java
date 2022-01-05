package com.esiran.greenadmin.chain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenadmin.chain.entity.*;
import org.apache.ibatis.transaction.Transaction;

import java.util.List;

public interface IBlockChainService {
    void insertBlock(Block block) throws Exception;
    void insertBlocks(List<Block> block) throws Exception;
    ChainStatus getChainStatus();
    BlockHeader getHeadBlock();
    Block getBlockByHash(String hash);
    BlockHeader getBlockHeaderByHash(String hash);
    LatestData getLatestData();
    IPage<BlockHeader> getBlockHeadersByPage(Page<BlockHeader> pg);
    BlockTx getTxByHash(String hash);
    IPage<BlockTx> getTxsByPage(Page<BlockTx> pg);
    SearchResult search(String q);
}
