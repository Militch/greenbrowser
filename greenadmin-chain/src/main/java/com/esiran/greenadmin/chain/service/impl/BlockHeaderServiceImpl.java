package com.esiran.greenadmin.chain.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenadmin.chain.entity.BlockHeader;
import com.esiran.greenadmin.chain.entity.BlockTx;
import com.esiran.greenadmin.chain.mapper.BlockHeaderMapper;
import com.esiran.greenadmin.chain.service.IBlockHeaderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Militch
 * @since 2021-10-24
 */
@Service
public class BlockHeaderServiceImpl extends ServiceImpl<BlockHeaderMapper, BlockHeader> implements IBlockHeaderService {

    @Override
    public void insertBlockHeader(BlockHeader blockHeader) {
        Wrapper<BlockHeader> queryWrapper = new LambdaQueryWrapper<BlockHeader>()
                .eq(BlockHeader::getHash, blockHeader.getHash());
        BlockHeader old = getOne(queryWrapper);
        if (old != null){
            blockHeader.setId(old.getId());
            updateById(blockHeader);
        }else {
            save(blockHeader);
        }
    }

    @Override
    public void remoteByBlockHash(String hash) {
        Wrapper<BlockHeader> wrapper = new LambdaQueryWrapper<BlockHeader>().eq(BlockHeader::getHash, hash);
        this.remove(wrapper);
    }
}
