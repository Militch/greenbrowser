package tech.xfs.xfschainexplorer.web.util;

import tech.xfs.xfschainexplorer.chain.entity.Block;
import tech.xfs.xfschainexplorer.chain.entity.BlockHeader;
import tech.xfs.xfschainexplorer.chain.entity.BlockTx;
import tech.xfs.xfschainexplorer.web.entity.RemoteBlock;
import tech.xfs.xfschainexplorer.web.entity.RemoteTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class BeanUtils {
    private static String big2string(BigDecimal num){
        if (num == null){
            return null;
        }
        return num.toBigInteger().toString(10);
    }
    public static BlockTx coverBlockTx(RemoteTransaction rtx){
        if (rtx == null){
            return null;
        }
        BlockTx tx = new BlockTx();
        tx.setVersion(rtx.getVersion());
        tx.setFrom(rtx.getFrom());
        tx.setTo(rtx.getTo());
        tx.setGasPrice(big2string(rtx.getGasPrice()));
        tx.setGasLimit(big2string(rtx.getGasLimit()));
        tx.setData(rtx.getData());
        tx.setNonce(rtx.getNonce());
        tx.setValue(rtx.getValue().toBigInteger().toString(10));
        tx.setHash(rtx.getHash());
        return tx;
    }
    public static List<BlockTx> coverBlockTxs(List<RemoteTransaction> rtxs){
        if (rtxs == null){
            return null;
        }
        return rtxs.stream().map(BeanUtils::coverBlockTx)
                .collect(Collectors.toList());
    }
    public static Block coverBlock(RemoteBlock rb){
        if (rb == null){
            return null;
        }
        BlockHeader blockHeader = new BlockHeader();
        blockHeader.setHeight(rb.getHeight());
        blockHeader.setVersion(rb.getVersion());
        blockHeader.setHashPrevBlock(rb.getHashPrevBlock());
        blockHeader.setTimestamp(rb.getTimestamp());
        blockHeader.setCoinbase(rb.getCoinbase());
        blockHeader.setStateRoot(rb.getStateRoot());
        blockHeader.setTransactionsRoot(rb.getTransactionsRoot());
        blockHeader.setReceiptsRoot(rb.getReceiptsRoot());
        blockHeader.setGasLimit(rb.getGasLimit());
        blockHeader.setGasUsed(rb.getGasUsed());
        blockHeader.setBits(rb.getBits());
        blockHeader.setNonce(rb.getNonce());
        blockHeader.setExtraNonce(big2string(rb.getExtraNonce()));
        blockHeader.setHash(rb.getHash());
        Block block = new Block();
        block.setHeader(blockHeader);
        block.setTransactions(coverBlockTxs(rb.getTransactions()));
        return block;
    }
    public static List<Block> coverBlocks(List<RemoteBlock> rbs){
        if (rbs == null){
            return null;
        }
        return rbs.stream().map(BeanUtils::coverBlock)
                .collect(Collectors.toList());
    }
}
