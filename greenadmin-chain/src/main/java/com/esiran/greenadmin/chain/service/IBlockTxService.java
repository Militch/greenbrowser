package com.esiran.greenadmin.chain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenadmin.chain.entity.BlockTx;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Militch
 * @since 2021-10-24
 */
public interface IBlockTxService extends IService<BlockTx> {
    void insertTxs(List<BlockTx> txs);
    void insertTx(BlockTx tx);
    void removeByTxHash(String hash);
    List<BlockTx> listTxsByBlockHash(String hash);
    IPage<BlockTx> getAddressTxsByPage(Page<BlockTx> pg, String address);
//    Integer getCountByAddress()
}
