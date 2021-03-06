package tech.xfs.xfschainexplorer.web.tasks;

import tech.xfs.xfschainexplorer.chain.entity.Block;
import tech.xfs.xfschainexplorer.chain.entity.BlockHeader;
import tech.xfs.xfschainexplorer.chain.service.IBlockChainService;
import tech.xfs.xfschainexplorer.common.jsonrpci.Client;
import tech.xfs.xfschainexplorer.web.entity.NodeStatus;
import tech.xfs.xfschainexplorer.web.entity.RemoteBlock;
import tech.xfs.xfschainexplorer.web.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BackendAsyncTask {
    private static final Logger logger = LoggerFactory.getLogger(BackendAsyncTask.class);
    private final IBlockChainService chainService;
    private final Client cli;
    public BackendAsyncTask(Client cli, IBlockChainService chainService) {
        this.cli = cli;
        this.chainService = chainService;
    }



    @Async
    public void run() throws Exception {
        final NodeStatusSyncMgr nodeStatusSyncMgr = new NodeStatusSyncMgr(cli);
        nodeStatusSyncMgr.startSync();
        ChainSyncMgr mgr = new ChainSyncMgr(cli){
            @Override
            public long localHead() {
                BlockHeader bh = chainService.getHeadBlock();
                if (bh != null){
                    return bh.getHeight();
                }
                return 0;
            }

            @Override
            public NodeStatus nodeStatus() {
                return nodeStatusSyncMgr.getStatus();
            }

            @Override
            public BlockHeader getBlockByHash(String hash) {
                return chainService.getBlockHeaderByHash(hash);
            }

            @Override
            public void insertBlock(List<RemoteBlock> blocks) {
                if (blocks.size() == 0){
                    return;
                }
                List<Block> list = BeanUtils.coverBlocks(blocks);
                try {
                    chainService.insertBlocks(list);
                } catch (Exception e) {
                  e.printStackTrace();
                  logger.error("Failed insert blocks: err={}", e.getMessage());
                }
            }

            @Override
            public Runnable insertBlockAsync(List<RemoteBlock> blocks) {
                return ()->{
                    if (blocks.size() == 0){
                        return;
                    }

                    List<Block> list = BeanUtils.coverBlocks(blocks);
                    try {
                        chainService.insertBlocks(list);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("Failed insert blocks: err={}", e.getMessage());
                    }
                };
            }
        };
        mgr.startSync();
    }
}
