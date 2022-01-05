package tech.xfs.xfschainexplorer.chain.service;

import tech.xfs.xfschainexplorer.chain.entity.BlockHeader;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Militch
 * @since 2021-10-24
 */
public interface IBlockHeaderService extends IService<BlockHeader> {
    void insertBlockHeader(BlockHeader blockHeader);
    void removeByBlockHash(String hash);
    BlockHeader getLast();
}
