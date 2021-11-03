package com.esiran.greenadmin.chain.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenadmin.chain.entity.*;
import com.esiran.greenadmin.chain.service.*;
import org.apache.ibatis.transaction.Transaction;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BlockChainServiceImpl implements IBlockChainService {
    private static final Logger log = LoggerFactory.getLogger(BlockChainServiceImpl.class);
    private final IHeadIndexService headIndexService;
    private final IBlockHeaderService headerService;
    private final IBlockTxService blockTxService;
    private final ITxReceiptService receiptService;
    private static final ModelMapper mp = new ModelMapper();
    static {
        mp.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }
    private BlockHeader head;
    public BlockChainServiceImpl(
            IHeadIndexService headIndexService, IBlockHeaderService headerService,
            IBlockTxService blockTxService, ITxReceiptService receiptService) {
        this.headIndexService = headIndexService;
        this.headerService = headerService;
        this.blockTxService = blockTxService;
        this.receiptService = receiptService;
    }
    @Transactional
    public synchronized void reorg(Block old, Block next) throws Exception {
        if (old == null){
            throw new Exception("invalid new chain");
        }
        List<Block> newBlocks = new ArrayList<>();
        BlockHeader oldBlockHeader = old.getHeader();
        long oldBlockHeight = oldBlockHeader.getHeight();
        Block newBlock = next;
        while (newBlock != null){
            BlockHeader nBlockHeader = newBlock.getHeader();
            long nBlockHeight = nBlockHeader.getHeight();
            if (nBlockHeight == oldBlockHeight){
                break;
            }
            newBlocks.add(newBlock);
            newBlock = getBlockByHash(newBlock.getHeader().getHashPrevBlock());
        }
        if (newBlock == null){
            throw new Exception("invalid new chain");
        }

        Block mOldBlock = old;
        Block mNewBlock = newBlock;
        String oldHash = mOldBlock.getHeader().getHash();
        String newHash = mNewBlock.getHeader().getHash();
        List<BlockTx> deletedTxs = new ArrayList<>();
        List<BlockHeader> deletedHeaders = new ArrayList<>();
        while(!oldHash.equals(newHash)){
            oldHash = mOldBlock.getHeader().getHash();
            newHash = mNewBlock.getHeader().getHash();
            newBlocks.add(mNewBlock);

            deletedTxs.addAll(mOldBlock.getTransactions());
            deletedHeaders.add(mOldBlock.getHeader());

            String oldPrevHash = mOldBlock.getHeader().getHashPrevBlock();
            mOldBlock = getBlockByHash(oldPrevHash);
            String newPrevHash = mNewBlock.getHeader().getHashPrevBlock();
            mNewBlock = getBlockByHash(newPrevHash);
            if (mOldBlock == null){
                throw new Exception("invalid new chain");
            }
            if (mNewBlock == null){
                throw new Exception("invalid new chain");
            }
        }
        List<BlockHeader> addedHeaders = new ArrayList<>();
        List<BlockTx> addedTxs = new ArrayList<>();
        for (Block addBlock : newBlocks){
            headerService.save(addBlock.getHeader());
            blockTxService.insertTxs(addBlock.getTransactions());
            receiptService.insertReceipts(addBlock.getReceipts());
            addedHeaders.add(addBlock.getHeader());
            if (addBlock.getTransactions() != null) {
                addedTxs.addAll(addBlock.getTransactions());
            }
        }
        deletedHeaders = blockHeaderDifference(deletedHeaders, addedHeaders);
        for (BlockHeader deletedHeader : deletedHeaders){
            headerService.remoteByBlockHash(deletedHeader.getHash());
        }
        deletedTxs = txDifference(deletedTxs, addedTxs);
        for (BlockTx deletedTx : deletedTxs){
            blockTxService.remoteByTxHash(deletedTx.getHash());
            receiptService.remoteByTxHash(deletedTx.getHash());
        }

    }

    private static List<BlockHeader> blockHeaderDifference(List<BlockHeader> a, List<BlockHeader> b){
        List<BlockHeader> keep = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        for (BlockHeader bh : b){
            map.put(bh.getHash(), new Object());
        }
        for (BlockHeader bh : a){
            if (!map.containsKey(bh.getHash())){
                keep.add(bh);
            }
        }
        return keep;
    }
    private static List<BlockTx> txDifference(List<BlockTx> a, List<BlockTx> b){
        List<BlockTx> keep = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        for (BlockTx bt : b){
            map.put(bt.getHash(), new Object());
        }
        for (BlockTx bt : a){
            if (!map.containsKey(bt.getHash())){
                keep.add(bt);
            }
        }
        return keep;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void insertBlock(Block block) throws Exception {
        if (block == null){ return; }
        BlockHeader old = getBlockHeaderByHash(block.getHeader().getHash());
        if (old != null){
            return;
        }
        BlockHeader head = getHeadBlock();
        if (head == null){
            return;
        }
        Block headBlock = getBlockByHash(head.getHash());
        String headHash = head.getHash();
        BlockHeader nextHeader = block.getHeader();
        String nextPrevHash = nextHeader.getHashPrevBlock();
        if (nextHeader.getHeight() > head.getHeight()){
            if (!headHash.equals(nextPrevHash)){
                this.reorg(headBlock, block);
            }
            this.head = nextHeader;
            headIndexService.setHead(nextHeader);
        }
        headerService.insertBlockHeader(block.getHeader());
        blockTxService.insertTxs(block.getTransactions());
        receiptService.insertReceipts(block.getReceipts());
    }

    @Override
    public synchronized void insertBlocks(List<Block> blocks) throws Exception {
        if (blocks == null || blocks.size() == 0){
            return;
        }
        for (Block blk : blocks){
            insertBlock(blk);
        }
    }


    @Override
    public BlockHeader getHeadBlock() {
        if (head != null){
            return head;
        }
        HeadIndex hi = headIndexService.getById(1);
        if (hi==null){
            return null;
        }
        Wrapper<BlockHeader> query = new LambdaQueryWrapper<BlockHeader>()
                .eq(BlockHeader::getHash, hi.getHead());
        BlockHeader got = headerService.getOne(query);
        head = got;
        return got;
    }

    @Override
    public Block getBlockByHash(String hash) {
        BlockHeader header = getBlockHeaderByHash(hash);
        if (header == null){
            return null;
        }
        Block block = new Block();
        block.setHeader(header);
        List<BlockTx> txs = blockTxService.listTxsByBlockHash(hash);
        block.setTransactions(txs);
        List<TxReceipt> allReceipts = new ArrayList<>();
        for (BlockTx tx : txs){
            TxReceipt re = receiptService.getReceiptByTxHash(tx.getHash());
            if (re == null){
                continue;
            }
            allReceipts.add(re);
        }
        block.setReceipts(allReceipts);
        return block;
    }

    @Override
    public BlockDTO getBlockDTOByHash(String hash) {
        BlockHeader header = getBlockHeaderByHash(hash);
        if (header == null){
            return null;
        }
        BlockDTO blockDTO = new BlockDTO();
        blockDTO.setHeader(header);
        List<BlockTx> txs = blockTxService.listTxsByBlockHash(hash);
        List<BlockTxDTO> txDTOS = new ArrayList<>();
        for (BlockTx tx : txs){
            BlockTxDTO txDTO = mp.map(tx, BlockTxDTO.class);
            TxReceipt re = receiptService.getReceiptByTxHash(tx.getHash());
            if (re == null){
                txDTOS.add(txDTO);
                continue;
            }
            BigInteger gasUsed = new BigInteger(re.getGasUsed());
            BigInteger gasPrice = new BigInteger(tx.getGasPrice());
            BigInteger gasFee = gasUsed.multiply(gasPrice);
            txDTO.setGasFee(gasFee.toString(10));
            txDTO.setGasUsed(re.getGasUsed());
            txDTO.setStatus(re.getStatus());
            txDTOS.add(txDTO);
        }
        blockDTO.setTransactions(txDTOS);
        return blockDTO;
    }

    @Override
    public BlockTxDTO getTxByHash(String hash) {
        Wrapper<BlockTx> query = new LambdaQueryWrapper<BlockTx>()
                .eq(BlockTx::getHash, hash);
        BlockTx blockTx = blockTxService.getOne(query);
        if (blockTx == null){
            return null;
        }
        BlockTxDTO dto = mp.map(blockTx, BlockTxDTO.class);
        TxReceipt txReceipt = receiptService.getReceiptByTxHash(hash);
        if (txReceipt == null){
            return dto;
        }
        BigInteger gasUsed = new BigInteger(txReceipt.getGasUsed());
        BigInteger gasPrice = new BigInteger(dto.getGasPrice());
        BigInteger gasFee = gasUsed.multiply(gasPrice);
        dto.setGasFee(gasFee.toString(10));
        dto.setGasUsed(txReceipt.getGasUsed());
        dto.setStatus(txReceipt.getStatus());
        return dto;
    }

    @Override
    public BlockHeader getBlockHeaderByHash(String hash) {
        Wrapper<BlockHeader> query = new LambdaQueryWrapper<BlockHeader>()
                .eq(BlockHeader::getHash, hash);
        return headerService.getOne(query);
    }

    @Override
    public LatestData getLatestData() {
        Wrapper<BlockHeader> blockQuery = new LambdaQueryWrapper<BlockHeader>()
                .orderByDesc(BlockHeader::getHeight).last("LIMIT 10");
        List<BlockHeader> headers = headerService.list(blockQuery);
        Wrapper<BlockTx> txQuery = new LambdaQueryWrapper<BlockTx>()
                .orderByDesc(BlockTx::getTimestamp).last("LIMIT 10");
        List<BlockTx> txes = blockTxService.list(txQuery);
        LatestData latestData = new LatestData();
        latestData.setBlocks(headers);
        latestData.setTxs(txes);
        return latestData;
    }

    @Override
    public Page<BlockHeader> getBlockHeadersByPage(Page<BlockHeader> pg) {
        Wrapper<BlockHeader> blockQuery = new LambdaQueryWrapper<BlockHeader>()
                .orderByDesc(BlockHeader::getHeight);
        return headerService.page(pg, blockQuery);
    }

    @Override
    public IPage<BlockTxDTO> getTxsByPage(Page<BlockTx> pg) {
        Wrapper<BlockTx> blockTxWrapper = new LambdaQueryWrapper<BlockTx>()
                .orderByDesc(BlockTx::getTimestamp);
        IPage<BlockTx> p = blockTxService.page(pg, blockTxWrapper);
        return p.convert((BlockTx tx)-> {
            BlockTxDTO dto = mp.map(tx, BlockTxDTO.class);
            TxReceipt txReceipt = receiptService.getReceiptByTxHash(tx.getHash());
            if (txReceipt == null){
                return dto;
            }
            BigInteger gasUsed = new BigInteger(txReceipt.getGasUsed());
            BigInteger gasPrice = new BigInteger(dto.getGasPrice());
            BigInteger gasFee = gasUsed.multiply(gasPrice);
            dto.setGasFee(gasFee.toString(10));
            dto.setGasUsed(txReceipt.getGasUsed());
            dto.setStatus(txReceipt.getStatus());
            return dto;
        });
    }
}
