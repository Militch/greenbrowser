package com.esiran.greenadmin.web.tasks;

import com.esiran.greenadmin.chain.entity.TxReceipt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.xfs.libxfs4j.entity.Block;
import tech.xfs.libxfs4j.entity.Receipt;
import tech.xfs.libxfs4j.entity.Transaction;
import tech.xfs.libxfs4j.io.Chan;
import tech.xfs.libxfs4j.p2p.DataPacket;
import tech.xfs.libxfs4j.p2p.Peer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

class HashesFromNumberParams {
    private long from;
    private long count;

    public HashesFromNumberParams(long from, long count) {
        this.from = from;
        this.count = count;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}

class HashesFromNumberPacket extends DataPacket {
    private static final int DEFAULT_VERSION = 1;
    private static final Gson g = new GsonBuilder().create();
    private HashesFromNumberParams params;
    @Override
    public int getType() {
        return 6;
    }
    @Override
    public int getVersion() {
        return DEFAULT_VERSION;
    }
    @Override
    public byte[] getDataBytes() {
        if (params == null){
            return new byte[0];
        }
        String jsonString = g.toJson(params);
        return jsonString.getBytes(StandardCharsets.UTF_8);
    }
    @Override
    public void decode(byte[] raw) throws Exception {
        if (raw == null){
            return;
        }
        String datajson = new String(raw, StandardCharsets.UTF_8);
        this.params = g.fromJson(datajson, HashesFromNumberParams.class);
    }
    public HashesFromNumberParams getParams() {
        return params;
    }

    public void setParams(HashesFromNumberParams params) {
        this.params = params;
    }
}

class HashesPacket extends DataPacket {
    private static final int DEFAULT_VERSION = 1;
    private static final Gson g = new GsonBuilder().create();
    private List<String> hashes;
    @Override
    public int getType() {
        return 7;
    }
    @Override
    public int getVersion() {
        return DEFAULT_VERSION;
    }
    @Override
    public byte[] getDataBytes() {
        return new byte[0];
    }
    @Override
    public void decode(byte[] raw) throws Exception {
        if (raw == null){
            return;
        }
        String datajson = new String(raw, StandardCharsets.UTF_8);
        this.hashes = g.fromJson(datajson, new TypeToken<List<String>>(){}.getType());
    }

    public List<String> getHashes() {
        return hashes;
    }
}

class BlockHashesPacket extends DataPacket {
    private static final int DEFAULT_VERSION = 1;
    private static final Gson g = new GsonBuilder().create();
    private List<String> hashes;
    @Override
    public int getType() {
        return 8;
    }
    @Override
    public int getVersion() {
        return DEFAULT_VERSION;
    }
    @Override
    public byte[] getDataBytes() {
        if (hashes == null || hashes.size() <= 0){
            return new byte[0];
        }
        String jsonString = g.toJson(hashes);
        return jsonString.getBytes(StandardCharsets.UTF_8);
    }
    @Override
    public void decode(byte[] raw) throws Exception {
    }

    public List<String> getHashes() {
        return hashes;
    }

    public void setHashes(List<String> hashes) {
        this.hashes = hashes;
    }
}


class ReceiptsReqPacket extends DataPacket {
    private static final int DEFAULT_VERSION = 1;
    private static final Gson g = new GsonBuilder().create();
    private List<String> hashes;
    @Override
    public int getType() {
        return 12;
    }
    @Override
    public int getVersion() {
        return DEFAULT_VERSION;
    }
    @Override
    public byte[] getDataBytes() {
        if (hashes == null || hashes.size() <= 0){
            return new byte[0];
        }
        String jsonString = g.toJson(hashes);
        return jsonString.getBytes(StandardCharsets.UTF_8);
    }
    @Override
    public void decode(byte[] raw) throws Exception {}

    public List<String> getHashes() {
        return hashes;
    }

    public void setHashes(List<String> hashes) {
        this.hashes = hashes;
    }
}

class ReceiptsDataPacket extends DataPacket {
    private static final int DEFAULT_VERSION = 1;
    private static final Gson g = new GsonBuilder().create();
    private List<Receipt> receipts;
    @Override
    public int getType() {
        return 13;
    }
    @Override
    public int getVersion() {
        return DEFAULT_VERSION;
    }
    @Override
    public byte[] getDataBytes() {
        return new byte[0];
    }
    @Override
    public void decode(byte[] raw) throws Exception {
        if (raw == null){
            return;
        }
        String datajson = new String(raw, StandardCharsets.UTF_8);
        this.receipts = g.fromJson(datajson, new TypeToken<List<Receipt>>(){}.getType());
    }

    public List<Receipt> getReceipts() {
        return receipts;
    }
}

class BlocksPacket extends DataPacket {
    private static final int DEFAULT_VERSION = 1;
    private static final Gson g = new GsonBuilder().create();
    private List<Block> blocks;
    @Override
    public int getType() {
        return 9;
    }
    @Override
    public int getVersion() {
        return DEFAULT_VERSION;
    }
    @Override
    public byte[] getDataBytes() {
        return new byte[0];
    }
    @Override
    public void decode(byte[] raw) throws Exception {
        String datajson = new String(raw, StandardCharsets.UTF_8);
        this.blocks = g.fromJson(datajson, new TypeToken<List<Block>>(){}.getType());
    }

    public List<Block> getBlocks() {
        return blocks;
    }
}

public abstract class SyncMgr extends TimerTask{
    private static final Logger log = LoggerFactory.getLogger(SyncMgr.class);

    private static final int MESSAGE_TYPE_GET_BLOCK_HASHES_FROM = 6;
    private static final int MESSAGE_TYPE_BLOCK_HASHES = 7;
    private static final int MESSAGE_TYPE_GET_BLOCK = 8;
    private static final int MESSAGE_TYPE_BLOCKS = 9;
    private static final int MESSAGE_TYPE_NEW_BLOCK = 10;
    private static final int MESSAGE_TYPE_RECEIPTS = 13;
    private static final long MAX_HASH_FETCH_NUMBER = 512;
    private final Peer peer;
    private final Timer syncerTimer = new Timer();

    private final Chan<HashesPacket> hashesPacketChan;
    private final Chan<BlocksPacket> blocksPacketChan;
    private final Chan<ReceiptsDataPacket> receiptsDataPacketChan;
    public SyncMgr(Peer peer) {
        this.peer = peer;
        hashesPacketChan = new Chan<>();
        blocksPacketChan = new Chan<>();
        receiptsDataPacketChan = new Chan<>();
        new Thread(this::readLoop).start();
    }

    private void readLoop() {
        while (true) {
            try {
                DataPacket packet = this.peer.readPacket();
                byte[] dataBytes = packet.getDataBytes();
//                String dataString = new String(dataBytes, StandardCharsets.UTF_8);
//                log.info("packet: version={}, type={}, data={}", packet.getVersion(),packet.getType(),dataString);
                switch (packet.getType()) {
                    case MESSAGE_TYPE_GET_BLOCK_HASHES_FROM:
                        break;
                    case MESSAGE_TYPE_BLOCK_HASHES:
                        HashesPacket mPacket = new HashesPacket();
                        mPacket.decode(dataBytes);
                        hashesPacketChan.put(mPacket);
                        break;
                    case MESSAGE_TYPE_BLOCKS:
                        BlocksPacket blocksPacket = new BlocksPacket();
                        blocksPacket.decode(dataBytes);
                        blocksPacketChan.put(blocksPacket);
                        break;
                    case MESSAGE_TYPE_RECEIPTS:
                        ReceiptsDataPacket dataPacket = new ReceiptsDataPacket();
                        dataPacket.decode(dataBytes);
                        receiptsDataPacketChan.put(dataPacket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void syncer(){
        syncerTimer.schedule(this, 0, 10000);
    }

    public void startSync(){
        syncer();
    }

    abstract long getCurrentHeight();
    abstract boolean hasBlock(String hash);
    abstract void insertBlocksToChain(List<Block> blocks);

    public void synchronise() throws IOException, TimeoutException {
        long num = findAncestor();
        fetchBlockHashes(num+1);
        handleBlock();
    }

    public void handleBlock() throws TimeoutException {
        BlocksPacket packet = blocksPacketChan.takeTimeout(5000);
        List<Block> blocks = packet.getBlocks();
        if (blocks == null || blocks.size() == 0){
            return;
        }
        for (Block block : blocks){
            if (block.getTransactions() == null || block.getTransactions().size() == 0){
                continue;
            }
            List<String> txhashes = new ArrayList<>();
            for (Transaction tx : block.getTransactions()){
                txhashes.add(tx.getHash());
            }
            ReceiptsReqPacket reqPacket = new ReceiptsReqPacket();
            reqPacket.setHashes(txhashes);
            new Thread(()->{
                try {
                    this.peer.writePacket(reqPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            ReceiptsDataPacket receiptsPacket = receiptsDataPacketChan.takeTimeout(5000);
            List<Receipt> receipts = receiptsPacket.getReceipts();
            block.setReceipts(receipts);
        }
        insertBlocksToChain(blocks);
    }
    public void fetchBlockHashes(long num) throws IOException, TimeoutException {

        log.info("fetchBlockHashes: num={}", num);
        HashesFromNumberPacket packet = new HashesFromNumberPacket();
        packet.setParams(new HashesFromNumberParams(num, MAX_HASH_FETCH_NUMBER));
        new Thread(()->{
            try {
                this.peer.writePacket(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        HashesPacket hs = hashesPacketChan.takeTimeout(5000);
        List<String> hashes = hs.getHashes();
        if (hashes == null){
            return;
        }
        BlockHashesPacket blockHashesPacket = new BlockHashesPacket();
        blockHashesPacket.setHashes(hashes);
        this.peer.writePacket(blockHashesPacket);
    }

    public long findAncestor() throws TimeoutException {
        long head = getCurrentHeight();
        long from =  head - MAX_HASH_FETCH_NUMBER;
        if (from < 0){
            from = 0;
        }
        long finalFrom = from;
        log.info("find ancestor: from={}, to={}", finalFrom, finalFrom + MAX_HASH_FETCH_NUMBER);
        HashesFromNumberPacket packet = new HashesFromNumberPacket();
        packet.setParams(new HashesFromNumberParams(finalFrom, MAX_HASH_FETCH_NUMBER));
        new Thread(()->{
            try {
                this.peer.writePacket(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        HashesPacket hs = hashesPacketChan.takeTimeout(5000);
        List<String> hashes = hs.getHashes();
        if (hashes == null){
            return 0;
        }
        long num = 0;
        String haveHash = null;
        for (int i = hashes.size() -1; i>= 0; i--){
            if (hasBlock(hashes.get(i))){
                num = from + i;
                haveHash = hashes.get(i);
                break;
            }
        }
        if (haveHash != null) {
            return num;
        }
        long left = 0;
        long right = head;
        log.info("notfound ancestor2: left={}, right={}", left, right);
        while ((left+1) < right) {
            long mid = left + right / 2;
            HashesFromNumberPacket packet2 = new HashesFromNumberPacket();
            packet2.setParams(new HashesFromNumberParams(mid, 0));
            new Thread(()->{
                try {
                    this.peer.writePacket(packet2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            HashesPacket nPacket = hashesPacketChan.takeTimeout(5000);
            List<String> nHashes = nPacket.getHashes();
            if (nHashes.size() < 1) {
                return 0;
            }
            if (hasBlock(nHashes.get(0))) {
                left = mid;
            } else {
                right = mid;
            }
        }
        return left;
    }


    @SneakyThrows
    @Override
    public void run() {
        log.info("exec---");
        try {
            this.synchronise();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
