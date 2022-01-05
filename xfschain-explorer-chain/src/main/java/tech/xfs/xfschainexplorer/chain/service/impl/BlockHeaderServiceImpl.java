package tech.xfs.xfschainexplorer.chain.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import tech.xfs.xfschainexplorer.chain.entity.BlockHeader;
import tech.xfs.xfschainexplorer.chain.mapper.BlockHeaderMapper;
import tech.xfs.xfschainexplorer.chain.service.IBlockHeaderService;
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
    public void removeByBlockHash(String hash) {
        Wrapper<BlockHeader> wrapper = new LambdaQueryWrapper<BlockHeader>().eq(BlockHeader::getHash, hash);
        this.remove(wrapper);
    }

    @Override
    public BlockHeader getLast() {
        Wrapper<BlockHeader> wrapper = new LambdaQueryWrapper<BlockHeader>()
                .orderByDesc(BlockHeader::getHeight).last("limit 1");
        return this.getOne(wrapper);
    }
}
