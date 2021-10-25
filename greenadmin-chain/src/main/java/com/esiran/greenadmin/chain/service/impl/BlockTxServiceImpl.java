package com.esiran.greenadmin.chain.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.esiran.greenadmin.chain.entity.Block;
import com.esiran.greenadmin.chain.entity.BlockTx;
import com.esiran.greenadmin.chain.mapper.BlockTxMapper;
import com.esiran.greenadmin.chain.service.IBlockTxService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Militch
 * @since 2021-10-24
 */
@Service
public class BlockTxServiceImpl extends ServiceImpl<BlockTxMapper, BlockTx> implements IBlockTxService {

    @Override
    public void insertTxs(List<BlockTx> txs) {
        if (txs == null || txs.size() <= 0){
            return;
        }
        for (BlockTx tx : txs) {
            insertTx(tx);
        }
    }

    @Override
    public void insertTx(BlockTx tx) {
        if (tx == null){
            return;
        }
        save(tx);
    }

    @Override
    public void remoteByTxHash(String hash) {
        Wrapper<BlockTx> wrapper = new LambdaQueryWrapper<BlockTx>().eq(BlockTx::getHash, hash);
        this.remove(wrapper);
    }


    @Override
    public List<BlockTx> listTxsByBlockHash(String hash) {
        Wrapper<BlockTx> wrapper = new LambdaQueryWrapper<BlockTx>().eq(BlockTx::getBlockHash, hash);
        return list(wrapper);
    }
}
