package com.esiran.greenadmin.web.tasks;

import com.esiran.greenadmin.chain.entity.Block;
import com.esiran.greenadmin.chain.entity.BlockHeader;
import com.esiran.greenadmin.chain.service.IBlockChainService;
import com.esiran.greenadmin.common.jsonrpci.Client;
import com.esiran.greenadmin.jsonrpc.HTTPClient;
import com.esiran.greenadmin.web.entity.NodeStatus;
import com.esiran.greenadmin.web.entity.RemoteBlock;
import com.esiran.greenadmin.web.util.BeanUtils;
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
