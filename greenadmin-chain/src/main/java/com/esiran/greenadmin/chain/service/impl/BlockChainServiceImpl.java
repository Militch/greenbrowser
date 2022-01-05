package com.esiran.greenadmin.chain.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenadmin.chain.entity.*;
import com.esiran.greenadmin.chain.service.*;
import com.esiran.greenadmin.common.jsonrpci.Client;
import com.esiran.greenadmin.common.jsonrpci.MethodParams;
import com.esiran.greenadmin.common.util.CoinUtil;
import com.esiran.greenadmin.common.util.DifficultyUtil;
import com.google.gson.annotations.SerializedName;
import io.swagger.models.auth.In;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.xfs.libxfs4j.core.MyAddress;
import tech.xfs.libxfs4j.util.AddressUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
public class BlockChainServiceImpl implements IBlockChainService {
    private static final Logger log = LoggerFactory.getLogger(BlockChainServiceImpl.class);

    private static final long BLOCK_REWARDS_TEST_NET = 17;
    private static final long BLOCK_REWARDS_DEFAULT = BLOCK_REWARDS_TEST_NET;
    private final IBlockHeaderService headerService;
    private final IBlockTxService blockTxService;
    private static final ModelMapper mp = new ModelMapper();
    static {
        mp.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }
    private BlockHeader head;
    private final Client rpcCli;
    private final IAddressService addressService;
    public BlockChainServiceImpl(
            Client rpcCli,
            IBlockHeaderService headerService,
            IBlockTxService blockTxService,
            IAddressService addressService) {
        this.rpcCli = rpcCli;
        this.headerService = headerService;
        this.blockTxService = blockTxService;
        this.addressService = addressService;
    }
    @Transactional
    public synchronized void reorg(Block old, Block next) throws Exception {
        if (old == null || next == null){
            throw new Exception("reorg chain err: params empty");
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
            BlockHeader nextHeader = next.getHeader();
            throw new Exception(String.format("reorg chain err, newBlock value is null: block=%s, height=%d",
                    nextHeader.getHash(), nextHeader.getHeight()));
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
                BlockHeader nextHeader = next.getHeader();
                throw new Exception(String.format(
                        "reorg chain err: mOldBlock value is null: block=%s, height=%d",
                        nextHeader.getHash(), nextHeader.getHeight()));
            }
            if (mNewBlock == null){
                BlockHeader nextHeader = next.getHeader();
                throw new Exception(String.format(
                        "reorg chain err: mOldBlock mNewBlock is null: block=%s, height=%d",
                        nextHeader.getHash(), nextHeader.getHeight()));
            }
        }
        List<BlockHeader> addedHeaders = new ArrayList<>();
        List<BlockTx> addedTxs = new ArrayList<>();
        for (Block addBlock : newBlocks){
            headerService.save(addBlock.getHeader());
            blockTxService.insertTxs(addBlock.getTransactions());
            addedHeaders.add(addBlock.getHeader());
            if (addBlock.getTransactions() != null) {
                addedTxs.addAll(addBlock.getTransactions());
            }
        }
        deletedHeaders = blockHeaderDifference(deletedHeaders, addedHeaders);
        for (BlockHeader deletedHeader : deletedHeaders){
            headerService.removeByBlockHash(deletedHeader.getHash());
        }
        deletedTxs = txDifference(deletedTxs, addedTxs);
        for (BlockTx deletedTx : deletedTxs){
            blockTxService.removeByTxHash(deletedTx.getHash());
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

    private static BigInteger calcBlockRewards() {
        return CoinUtil.baseCoinToAtto(BLOCK_REWARDS_DEFAULT);
    }
    private static Block presetBlockHeader(Block block){
        if (block == null){
            return null;
        }
        BlockHeader bh = block.getHeader();
        BigInteger blockRewards = calcBlockRewards();
        bh.setRewards(blockRewards.toString(10));
        if (block.getTransactions() != null){
            bh.setTxCount(block.getTransactions().size());
        }
        block.setHeader(bh);
        return block;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void insertBlock(Block block) throws Exception {
        if (block == null){ return; }
        log.debug("Inserting Block: {}", block);
        BlockHeader old = getBlockHeaderByHash(block.getHeader().getHash());
        if (old != null){
            return;
        }
        Long blockHeight = block.getHeader().getHeight();
        BlockHeader head = getHeadBlock();
        if (head == null && !blockHeight.equals(0L)){
            return;
        }
        block = presetBlockHeader(block);
        if (head != null){
            Block headBlock = getBlockByHash(head.getHash());
            String headHash = head.getHash();
            BlockHeader nextHeader = block.getHeader();
            String nextPrevHash = nextHeader.getHashPrevBlock();
            if (nextHeader.getHeight() > head.getHeight()){
                if (!headHash.equals(nextPrevHash)){
                    this.reorg(headBlock, block);
                }
                this.head = nextHeader;
            }
        }
        List<BlockTx> mTxs = applyTransactions(block.getHeader(), block.getTransactions());
        headerService.insertBlockHeader(block.getHeader());
        blockTxService.insertTxs(mTxs);
        updateAccountByStateRoot(block.getHeader(), block.getHeader().getCoinbase());
    }
    private void updateAccountByStateRoot(BlockHeader header, String address){
        if (header == null || address == null){
            return;
        }
        try {
            RemoteAccount ra = fetchAccount(address, header.getStateRoot());
            if (ra == null){ return; }
            Address account = new Address();
            account.setAddress(address);
            account.setBalance(ra.balance.toBigInteger().toString(10));
            account.setNonce(ra.nonce);
            account.setStateRoot(ra.getStateRoot());
            int type = ra.getCode() == null ? 0 : 1;
            account.setType(type);
            account.setFromStateRoot(header.getStateRoot());
            account.setFromBlockHeight(header.getHeight());
            account.setFromBlockHash(header.getHash());
            account.setDisplay(true);
            addressService.saveOrUpdateAddress(account);
        } catch (Exception e) {
//            e.printStackTrace();
            log.warn("Update account by block err: {}", e.getMessage());
        }
    }
    private static BigInteger calcGasFee(
            BigInteger gasUsed, BigInteger price){
        return gasUsed.multiply(price);
    }
    private BlockTx presetTransaction(BlockHeader bh, BlockTx tx, RemoteReceipt receipt){
        tx.setBlockHash(bh.getHash());
        tx.setBlockHeight(bh.getHeight());
        tx.setBlockTime(bh.getTimestamp());
        tx.setSignature(null);
        int type = tx.getTo() == null ? 1 : 0;
        tx.setType(type);
        BigInteger gasUsed = BigInteger.ZERO;
        int status = 0;
        if (receipt != null){
            gasUsed = receipt.getGasUsed().toBigInteger();
            status = receipt.getStatus();
        }
        tx.setGasUsed(gasUsed.toString(10));
        BigInteger gasFee = calcGasFee(gasUsed, new BigInteger(tx.getGasPrice()));
        tx.setGasFee(gasFee.toString(10));
        tx.setStatus(status);
        return tx;
    }
    private String createContractAddress(String address, Long nonce){
        MyAddress from = MyAddress.fromString(address);
        MyAddress contractAddress = AddressUtil.createAddress(from, nonce);
        return contractAddress.toBase58();
    }
    private void createContractAccount(BlockHeader bh, BlockTx tx){
        String address = createContractAddress(tx.getFrom(), tx.getNonce());
        Address account = new Address();
        account.setAddress(address);
        account.setBalance(BigInteger.ZERO.toString());
        account.setNonce(0L);
        account.setStateRoot(null);
        account.setDisplay(true);
        account.setType(1);
        account.setFromStateRoot(bh.getStateRoot());
        account.setFromBlockHeight(bh.getHeight());
        account.setFromBlockHash(bh.getHash());
        // set create
        account.setCreateFromAddress(tx.getFrom());
        account.setCreateFromBlockHeight(bh.getHeight());
        account.setCreateFromBlockHash(bh.getHash());
        account.setCreateFromStateRoot(bh.getStateRoot());
        account.setCreateFromTxHash(tx.getHash());
        addressService.saveOrUpdateAddress(account);
    }

    private void applyTransaction(BlockHeader bh, Set<String> address, BlockTx tx) {
        address.add(tx.getFrom());
        if (!tx.getType().equals(1)){
            address.add(tx.getTo());
        }else {
            createContractAccount(bh, tx);
        }
    }
    private List<BlockTx> applyTransactions(BlockHeader bh, List<BlockTx> txs) throws Exception {
        if (bh == null || txs == null){
            return null;
        }
        List<BlockTx> mTxs = new ArrayList<>();
        Set<String> addressPending = new HashSet<>();
        for (BlockTx tx : txs){
            log.debug("Apply Transaction: {}, {}", tx, bh);
            RemoteReceipt remoteReceipt = fetchReceipt(tx.getHash());
            BlockTx mTx = presetTransaction(bh, tx, remoteReceipt);
            applyTransaction(bh, addressPending, mTx);
            mTxs.add(mTx);
        }
        for (String address : addressPending){
            updateAccountByStateRoot(bh, address);
        }
        return mTxs;
    }

    @Data
    private static final class RemoteAccount {
        @SerializedName("balance")
        private BigDecimal balance;
        @SerializedName("nonce")
        private Long nonce;
        @SerializedName("code")
        private String code;
        @SerializedName("state_root")
        private String stateRoot;
    }
    private RemoteAccount fetchAccount(String address, String stateRoot) throws Exception {
        MethodParams mp = new MethodParams();
        mp.addAttribute("address", address);
        mp.addAttribute("root_hash", stateRoot);
        return rpcCli.call("State.GetAccount", mp, RemoteAccount.class);
    }

    @Data
    private static final class RemoteReceipt {
        @SerializedName("version")
        private Integer version;
        @SerializedName("status")
        private Integer status;
        @SerializedName("tx_hash")
        private String txHash;
        @SerializedName("block_hash")
        private String blockHash;
        @SerializedName("block_index")
        private Integer blockIndex;
        @SerializedName("gas_used")
        private BigDecimal gasUsed;
    }
    private RemoteReceipt fetchReceipt(String txHash) throws Exception {
        MethodParams mp = new MethodParams();
        mp.addAttribute("hash", txHash);
        return rpcCli.call("Chain.GetReceiptByHash", mp, RemoteReceipt.class);
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

    private List<BlockHeader> get24hBlocks(){
        BlockHeader bh = getHeadBlock();
        long endTime = bh.getTimestamp();
        long startTime = endTime - (24*60*60);
        Wrapper<BlockHeader> wrapper = new LambdaQueryWrapper<BlockHeader>()
                .ge(BlockHeader::getTimestamp, startTime).le(BlockHeader::getTimestamp, endTime);
        return this.headerService.list(wrapper);
    }

    private long get24hAvgBlockTime(){
        List<BlockHeader> list = get24hBlocks();
        int count = list.size();
        double n = 1 / ((double) count / 24 / 60 / 60);
        return (long) n;
    }
    private long get24hAvgTxsInBlock(){
        List<BlockHeader> list = get24hBlocks();
        long transactions = 0;
        for (BlockHeader bh : list){
            transactions += bh.getTxCount();
        }
        double n = (double) transactions / (double) list.size();
        return (long) n;
    }

    private long get24hTPS(){
        List<BlockHeader> list = get24hBlocks();
        long transactions = 0;
        for (BlockHeader bh : list){
            transactions += bh.getTxCount();
        }
        double n = (double) transactions / 24 / 60 / 60;
        return (long) n;
    }
    private BigInteger get24hBlockRewards(){
        List<BlockHeader> list = get24hBlocks();
        BigInteger total = BigInteger.valueOf(0);
        for (BlockHeader bh : list){
            total = total.add(new BigInteger(bh.getRewards()));
        }
        return total.divide(BigInteger.valueOf(list.size()));
    }

    @Override
    public ChainStatus getChainStatus() {
        BlockHeader bh = getHeadBlock();
        long blockTime = get24hAvgBlockTime();
        long avgTxsInBlock = get24hAvgTxsInBlock();
        long avgTps = get24hTPS();
        BigInteger avgBlockRewards = get24hBlockRewards();
        int txsCount = blockTxService.count();
        int addressCount = addressService.count();
        long diff = DifficultyUtil.calcHashesByBits(bh.getBits());
        float pow = DifficultyUtil.calcHashRateByBits(bh.getBits());
        ChainStatus status = new ChainStatus();
        status.setLatestHeight(bh.getHeight());
        status.setTransactions((long) txsCount);
        status.setDifficulty(diff);
        status.setPower((long) pow);
        status.setAccounts((long) addressCount);
        status.setBlockTime(blockTime);
        status.setBlockRewards(avgBlockRewards.toString(10));
        status.setTxsInBlock(avgTxsInBlock);
        status.setTps(avgTps);
        return status;
    }


    @Override
    public BlockHeader getHeadBlock() {
        if (head != null){
            return head;
        }
        BlockHeader last = headerService.getLast();
        head = last;
        return last;
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
        return block;
    }


    @Override
    public BlockHeader getBlockHeaderByHash(String hash) {
        Wrapper<BlockHeader> query = new LambdaQueryWrapper<BlockHeader>()
                .eq(BlockHeader::getHash, hash);
        return headerService.getOne(query);
    }
    public BlockHeader getBlockHeaderByHeight(Long height) {
        Wrapper<BlockHeader> query = new LambdaQueryWrapper<BlockHeader>()
                .eq(BlockHeader::getHeight, height);
        return headerService.getOne(query);
    }
    @Override
    public LatestData getLatestData() {
        Wrapper<BlockHeader> blockQuery = new LambdaQueryWrapper<BlockHeader>()
                .orderByDesc(BlockHeader::getHeight).last("LIMIT 10");
        List<BlockHeader> headers = headerService.list(blockQuery);
        Wrapper<BlockTx> txQuery = new LambdaQueryWrapper<BlockTx>()
                .orderByDesc(BlockTx::getBlockTime).last("LIMIT 10");
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
    public BlockTx getTxByHash(String hash) {
        Wrapper<BlockTx> queryWrapper = new LambdaQueryWrapper<BlockTx>()
                .eq(BlockTx::getHash, hash);
        return blockTxService.getOne(queryWrapper);
    }

    @Override
    public IPage<BlockTx> getTxsByPage(Page<BlockTx> pg) {
        Wrapper<BlockTx> blockTxWrapper = new LambdaQueryWrapper<BlockTx>()
                .orderByDesc(BlockTx::getCreateTime);
        return blockTxService.page(pg, blockTxWrapper);
    }

    private static boolean isHash(String q){
        q = q.replace("0x", "");
        int ql = q.length();
        return ql == 64;
    }
    private static boolean isAddress(String q){
        return q.length() == 33;
    }
    private static boolean isNumber(String q){
        return q.chars().allMatch(Character::isDigit);
    }

    private int getSearchType(String q){
        if (isHash(q)){
            BlockHeader block = getBlockHeaderByHash(q);
            if (block != null){
                return 1;
            }
            BlockTx tx = getTxByHash(q);
            if (tx != null){
                return 2;
            }
        }else if (isAddress(q)) {
            Address address = addressService.getAddress(q);
            if (address != null){
                return 3;
            }
        }else if (isNumber(q)){
            Long qn = Long.valueOf(q);
            BlockHeader block = getBlockHeaderByHeight(qn);
            if (block != null){
                return 4;
            }
        }
        return 0;
    }

    @Override
    public SearchResult search(String q) {
        SearchResult r = new SearchResult();
        r.setType(0);
        out: {
            if (isHash(q)){
                BlockHeader block = getBlockHeaderByHash(q);
                if (block != null){
                    r.setType(1);
                    break out;
                }
                BlockTx tx = getTxByHash(q);
                if (tx != null){
                    r.setType(2);
                }
            }else if (isAddress(q)) {
                Address address = addressService.getAddress(q);
                if (address != null){
                    r.setType(3);
                }
            }else if (isNumber(q)){
                Long qn = Long.valueOf(q);
                BlockHeader block = getBlockHeaderByHeight(qn);
                if (block != null){
                    r.setType(4);
                    r.setPathValue(block.getHash());
                }
            }
        }
        return r;
    }
}
