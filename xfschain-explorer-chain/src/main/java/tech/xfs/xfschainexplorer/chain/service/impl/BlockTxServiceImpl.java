package tech.xfs.xfschainexplorer.chain.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import tech.xfs.xfschainexplorer.chain.entity.BlockTx;
import tech.xfs.xfschainexplorer.chain.mapper.BlockTxMapper;
import tech.xfs.xfschainexplorer.chain.service.IBlockTxService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        tx.setCreateTime(LocalDateTime.now());
        tx.setUpdateTime(LocalDateTime.now());
        save(tx);
    }

    @Override
    public void removeByTxHash(String hash) {
        Wrapper<BlockTx> wrapper = new LambdaQueryWrapper<BlockTx>().eq(BlockTx::getHash, hash);
        this.remove(wrapper);
    }


    @Override
    public List<BlockTx> listTxsByBlockHash(String hash) {
        Wrapper<BlockTx> wrapper = new LambdaQueryWrapper<BlockTx>()
                .eq(BlockTx::getBlockHash, hash)
                .orderByDesc(BlockTx::getCreateTime);
        return list(wrapper);
    }

    @Override
    public IPage<BlockTx> getAddressTxsByPage(Page<BlockTx> pg, String address) {
        Wrapper<BlockTx> blockTxWrapper = new LambdaQueryWrapper<BlockTx>()
                .eq(BlockTx::getFrom, address)
                .orderByDesc(BlockTx::getCreateTime);
        return page(pg, blockTxWrapper);
    }
}
