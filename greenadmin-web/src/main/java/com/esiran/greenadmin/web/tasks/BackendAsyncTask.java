package com.esiran.greenadmin.web.tasks;

import com.esiran.greenadmin.chain.entity.BlockHeader;
import com.esiran.greenadmin.chain.entity.BlockTx;
import com.esiran.greenadmin.chain.entity.TxReceipt;
import com.esiran.greenadmin.chain.service.IBlockChainService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.web3j.crypto.ECKeyPair;
import tech.xfs.libxfs4j.crypto.Crypto;
import tech.xfs.libxfs4j.entity.Block;
import tech.xfs.libxfs4j.entity.Receipt;
import tech.xfs.libxfs4j.entity.Transaction;
import tech.xfs.libxfs4j.io.PacketReader;
import tech.xfs.libxfs4j.io.PacketWriter;
import tech.xfs.libxfs4j.p2p.*;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class BackendAsyncTask {
    private static final Logger logger = LoggerFactory.getLogger(BackendAsyncTask.class);
    private static final int DEFAULT_VERSION = 1;
    private static final int MESSAGE_TYPE_HELLO = 0;
    private static final int MESSAGE_TYPE_RE_HELLO = 1;
    private static final int MESSAGE_TYPE_PING = 2;
    private static final int MESSAGE_TYPE_PONG = 3;
    private static final int MESSAGE_TYPE_VERSION = 5;

    private final IBlockChainService chainService;
    public BackendAsyncTask(IBlockChainService chainService) {
        this.chainService = chainService;
    }

    static class HelloPacket extends DataPacket {
        NodeId nodeId;
        NodeId receiveId;
        @Override
        public byte[] getDataBytes() {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            byte[] nodeIdBytes = nodeId.toByteArray();
            byte[] receiveIdBytes = receiveId.toByteArray();
            buffer.put(nodeIdBytes);
            buffer.put(receiveIdBytes);
            byte[] dataarr = buffer.array();
            return Arrays.copyOfRange(dataarr, 0, buffer.position());
        }
        @Override
        public int getType() {
            return MESSAGE_TYPE_HELLO;
        }

        @Override
        public int getVersion() {
            return DEFAULT_VERSION;
        }
        @Override
        public void decode(byte[] raw) throws Exception {
            if (raw.length != 128) {
                throw new Exception("decode err");
            }
            byte[] nodeIdBytes = Arrays.copyOfRange(raw, 0, 64);
            byte[] receiveIdBytes = Arrays.copyOfRange(raw, 64, 128);
            this.nodeId = NodeId.pubKey2NodeId(new BigInteger(nodeIdBytes));
            this.receiveId = NodeId.pubKey2NodeId(new BigInteger(receiveIdBytes));
        }
    }
    static class HelloRePacket extends DataPacket {
        NodeId nodeId;
        NodeId receiveId;
        @Override
        public byte[] getDataBytes() {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            byte[] nodeIdBytes = nodeId.toByteArray();
            byte[] receiveIdBytes = receiveId.toByteArray();
            buffer.put(nodeIdBytes);
            buffer.put(receiveIdBytes);
            byte[] dataarr = buffer.array();
            return Arrays.copyOfRange(dataarr, 0, buffer.position());
        }

        @Override
        public int getType() {
            return MESSAGE_TYPE_RE_HELLO;
        }

        @Override
        public void decode(byte[] raw) throws Exception {
            if (raw.length != 128) {
                throw new Exception("decode err");
            }
            byte[] nodeIdBytes = Arrays.copyOfRange(raw, 0, 64);
            byte[] receiveIdBytes = Arrays.copyOfRange(raw, 64, 128);
            this.nodeId = NodeId.pubKey2NodeId(new BigInteger(nodeIdBytes));
            this.receiveId = NodeId.pubKey2NodeId(new BigInteger(receiveIdBytes));
        }

        @Override
        public int getVersion() {
            return DEFAULT_VERSION;
        }
    }

    @Data
    public static class StatusData {
        private int version;
        private int network;
        private String head;
        private long height;
    }

    static class ProtocolVersionPacket extends DataPacket {
        private StatusData statusData;
        private static final Gson g = new GsonBuilder().create();
        @Override
        public byte[] getDataBytes() {
            String jsonString = g.toJson(statusData);
            return jsonString.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public int getType() {
            return MESSAGE_TYPE_VERSION;
        }

        @Override
        public void decode(byte[] raw) throws Exception {
            String jsonString = new String(raw, StandardCharsets.UTF_8);
            this.statusData = g.fromJson(jsonString, StatusData.class);
        }

        @Override
        public int getVersion() {
            return DEFAULT_VERSION;
        }
    }
    static class NewBlockPacket extends DataPacket {
        private Block block;

        public byte[] encode(){
            return null;
        }

        @Override
        public void decode(byte[] raw) throws Exception {

        }

        @Override
        public byte[] getDataBytes() {
            return new byte[0];
        }
    }

    public static class PingPacket extends DataPacket{
        private String message;
        @Override
        public int getType() {
            return MESSAGE_TYPE_PING;
        }

        @Override
        public void decode(byte[] raw) throws Exception {
            if (raw == null){
                return;
            }
            this.message = new String(raw, StandardCharsets.UTF_8);
        }

        @Override
        public byte[] encode() {
            if (this.message == null){
                return new byte[0];
            }
            return this.message.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public byte[] getDataBytes() {
            if (this.message == null){
                return new byte[0];
            }
            return this.message.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public int getVersion() {
            return DEFAULT_VERSION;
        }
    }

    public static class PongPacket extends DataPacket{
        private String message;
        @Override
        public int getType() {
            return MESSAGE_TYPE_PONG;
        }
        @Override
        public int getVersion() {
            return DEFAULT_VERSION;
        }
        @Override
        public byte[] getDataBytes() {
            if (this.message == null){
                return new byte[0];
            }
            return this.message.getBytes(StandardCharsets.UTF_8);
        }
        @Override
        public void decode(byte[] raw) throws Exception {
            if (raw == null){
                return;
            }
            this.message = new String(raw, StandardCharsets.UTF_8);
        }
    }
    private int handshakeStatus;

    public abstract static class P2PNodeClient {
        private final NodeId selfId;
        private final Node remoteNode;
        private Socket client;
        public NodeId getSelfId() {
            return selfId;
        }

        public Node getRemoteNode() {
            return remoteNode;
        }
        public P2PNodeClient(NodeId selfId, Node remoteNode) {
            this.selfId = selfId;
            this.remoteNode = remoteNode;
        }
        abstract BlockHeader chainHeadHeader();
        abstract boolean checkHasBlock(String hash);
        abstract void insertBlocksToChain(List<Block> blocks);
        public void start() throws Exception {
            this.client = new Socket(remoteNode.getIp(),remoteNode.getTcpPort());
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            ClientConn conn = new ClientConn(this, in, out);
            conn.serve();
            P2PNodeClient self = this;
            SyncMgr mgr = new SyncMgr(conn.getPeer()){
                @Override
                long getCurrentHeight() {
                    BlockHeader bh = chainHeadHeader();
                    if (bh == null){
                        return 0;
                    }
//                    logger.info("sync current head hash: {}", bh.getHash());
                    return bh.getHeight();
                }

                @Override
                boolean hasBlock(String hash) {
                    return checkHasBlock(hash);
                }

                @Override
                void insertBlocksToChain(List<Block> blocks) {
                    self.insertBlocksToChain(blocks);
                }
            };
            mgr.startSync();
        }
        public boolean isClosed(){
            return this.client.isClosed();
        }
    }


    public static class PacketBuffer {
        private final PipedInputStream in = new PipedInputStream(1024*1024*1024);
        private final PipedOutputStream out = new PipedOutputStream(in);
        private final PacketReader reader = new PacketReader(in);
        private final PacketWriter writer = new PacketWriter(out);
        public PacketBuffer() throws IOException {

        }
        public void writePacket(DataPacket dataPacket) throws IOException {
            this.writer.writePacket(dataPacket);
        }
        public PacketReader getReader() {
            return this.reader;
        }
        public PacketWriter getWriter(){
            return this.writer;
        }

    }
    public static class ClientConn {
        private final P2PNodeClient client;
        private final InputStream in;
        private final OutputStream out;
        private int handshakeStatus = 0;
        private final PacketBuffer readBuffer;
        private final Peer peer;
        public P2PNodeClient getClient() {
            return client;
        }
        public PacketReader getReader() {
            return readBuffer.getReader();
        }

        public PacketWriter getWriter(){
            return new PacketWriter(out);
        }

        public Peer getPeer() {
            return peer;
        }
        public ClientConn(P2PNodeClient client, InputStream in, OutputStream out) throws IOException {
            this.client = client;
            this.in = in;
            this.out = out;
            this.readBuffer = new PacketBuffer();
            this.peer = new Peer();
        }
        public DataPacket readPacket() throws Exception {
            PacketReader packetReader = new PacketReader(this.in);
            DataPacket dataPacket = packetReader.readPacket();
            if (dataPacket.getType() == MESSAGE_TYPE_RE_HELLO){
                HelloRePacket hp = new HelloRePacket();
                hp.decode(dataPacket.getDataBytes());
                return hp;
            }else if(dataPacket.getType() == MESSAGE_TYPE_VERSION){
                ProtocolVersionPacket versionPacket = new ProtocolVersionPacket();
                versionPacket.decode(dataPacket.getDataBytes());
                return versionPacket;
            }else if (dataPacket.getType() == MESSAGE_TYPE_PING){
                PingPacket packet = new PingPacket();
                packet.decode(dataPacket.getDataBytes());
                return packet;
            }else if(dataPacket.getType() == MESSAGE_TYPE_PONG){
                PongPacket packet = new PongPacket();
                packet.decode(dataPacket.getDataBytes());
                return packet;
            }
            return dataPacket;
        }
        private void waiteProtocolHandshake() throws IOException, InterruptedException {
            final Object obj = new Object();
            synchronized (obj){
                new Thread(() -> {
                    synchronized(obj){
                        try {
                            DataPacket dp = readPacket();
                            int type = dp.getType();
                            if (type == MESSAGE_TYPE_VERSION && handshakeStatus == 1){
                                ProtocolVersionPacket pvp = (ProtocolVersionPacket) dp;
                                peer.setRemoteHead(pvp.statusData.getHead());
                                peer.setRemoteHeight(pvp.statusData.getHeight());
                                this.handshakeStatus = 2;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            obj.notify();
                        }
                    }
                }).start();
                ProtocolVersionPacket pvp = new ProtocolVersionPacket();
                StatusData statusData = new StatusData();
                statusData.setHead("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
                statusData.setVersion(1);
                statusData.setHeight(0);
                statusData.setNetwork(1);
                pvp.statusData = statusData;
                sendPacket(pvp);
                obj.wait();
            }
        }
        private void waitNodeHandshake() throws IOException, InterruptedException {
            final Object obj = new Object();
            synchronized (obj){
                new Thread(() -> {
                    synchronized(obj){
                        try {
                            DataPacket dp = readPacket();
                            int type = dp.getType();
                            if (type == MESSAGE_TYPE_RE_HELLO && handshakeStatus == 0){
                                HelloRePacket hrp = (HelloRePacket) dp;
                                NodeId remoteNodeId = hrp.receiveId;
                                if (!remoteNodeId.equals(client.getSelfId())) {
                                    throw new Exception("hello err");
                                }
                                this.handshakeStatus = 1;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            obj.notify();
                        }
                    }
                }).start();
                HelloPacket hp = new HelloPacket();
                hp.nodeId = client.getSelfId();
                hp.receiveId = client.remoteNode.getNodeId();
                sendPacket(hp);
                obj.wait();
            }
        }

        public void sendPacket(DataPacket dataPacket) throws IOException {
            byte[] data = dataPacket.encode();
            this.out.write(data);
        }
        public void serve() throws Exception {
            waitNodeHandshake();
            if (this.handshakeStatus != 1 ) {
                throw new Exception("p2p handshake err");
            }
            waiteProtocolHandshake();
            if (this.handshakeStatus != 2 ) {
                throw new Exception("protocol handshake err");
            }
            peer.setConn(new PeerConn(this.getReader(), this.getWriter()));
            new Thread(()->{
                while (!getClient().isClosed()) {
                    try {
                        DataPacket dataPacket = readPacket();
                        switch (dataPacket.getType()) {
                            case MESSAGE_TYPE_PING:
                                PongPacket packet = new PongPacket();
                                packet.message = "hello";
                                sendPacket(packet);
                                break;
                            case MESSAGE_TYPE_PONG:
                                break;
                            default:
                                readBuffer.writePacket(dataPacket);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Async
    public void run() throws Exception {
        ECKeyPair keyPair = Crypto.genKey();
        NodeId nodeId = NodeId.pubKey2NodeId(keyPair.getPublicKey());
        String remoteNodeUri = "xfsnode://127.0.0.1:9002/?id=20f4948244d8ccaa055b4a668e499d8f33bdcd8eeb360e0046c3e47edf1f1dccfa3408c3c1a88b169e9fabd717d25b47dbc34afc7e68a21719fe886defd3a867";
        Node remoteNode = Node.parseNode(remoteNodeUri);
        ExecutorService tpool = Executors.newCachedThreadPool();
        P2PNodeClient client = new P2PNodeClient(nodeId, remoteNode){
            @Override
            BlockHeader chainHeadHeader() {
                return chainService.getHeadBlock();
            }

            @Override
            boolean checkHasBlock(String hash) {
                BlockHeader blockHeader = chainService.getBlockHeaderByHash(hash);
                return blockHeader != null;
            }

            @Override
            void insertBlocksToChain(List<Block> blocks) {
                List<com.esiran.greenadmin.chain.entity.Block> bs = blocks.stream().
                        map(BackendAsyncTask::coverBlock).collect(Collectors.toList());
                tpool.execute(() -> {
                    try {
                        chainService.insertBlocks(bs);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        client.start();
    }


    private static BlockHeader coverBlockHeader(tech.xfs.libxfs4j.entity.BlockHeader header){
        if (header == null){
            return null;
        }
        BlockHeader target = new BlockHeader();
        target.setHeight(header.getHeight());
        target.setVersion(header.getVersion());
        target.setHashPrevBlock(header.getHash_prev_block());
        target.setTimestamp(header.getTimestamp());
        target.setCoinbase(header.getCoinbase());
        target.setStateRoot(header.getState_root());
        target.setTransactionsRoot(header.getTransactions_root());
        target.setReceiptsRoot(header.getReceipts_root());
        target.setGasLimit(header.getGas_limit());
        target.setGasUsed(header.getGas_used());
        target.setBits(header.getBits());
        target.setNonce(header.getNonce());
        target.setHash(header.getHash());
        return target;
    }

    private static BlockTx coverTx(Transaction tx, String blockhash, Long blockHeight){
        if (tx == null){
            return null;
        }
        BlockTx target = new BlockTx();
        target.setVersion(tx.getVersion());
        target.setFrom(tx.getFrom());
        target.setTo(tx.getTo());
        target.setGasPrice(tx.getGas_price());
        target.setGasLimit(tx.getGas_limit());
        target.setData(tx.getData());
        target.setNonce(tx.getNonce());
        target.setValue(tx.getValue());
        target.setTimestamp(tx.getTimestamp());
        target.setSignature(tx.getSignature());
        target.setHash(tx.getHash());
        target.setBlockHash(blockhash);
        target.setBlockHeight(blockHeight);
        return target;
    }
    private static TxReceipt coverReceipt(Receipt receipt){
        if (receipt == null){
            return null;
        }
        TxReceipt target = new TxReceipt();
        target.setVersion(receipt.getVersion());
        target.setStatus(receipt.getStatus());
        target.setTxHash(receipt.getTx_hash());
        target.setGasUsed(receipt.getGas_used());
        return target;
    }
    public static com.esiran.greenadmin.chain.entity.Block coverBlock(Block block){
        if (block == null){
            return null;
        }
        com.esiran.greenadmin.chain.entity.Block target = new com.esiran.greenadmin.chain.entity.Block();
        tech.xfs.libxfs4j.entity.BlockHeader header = block.getHeader();
        BlockHeader bh = coverBlockHeader(header);
        if (block.getTransactions() != null){
            bh.setTxCount(block.getTransactions().size());
        }else {
            bh.setTxCount(0);
        }
        target.setHeader(bh);
        if (block.getTransactions() != null && block.getTransactions().size() > 0){
            target.setTransactions(block.getTransactions().stream()
                    .map((tx)-> BackendAsyncTask.coverTx(tx, block.getHeader().getHash(), block.getHeader().getHeight()))
                    .collect(Collectors.toList()));
        }
        if (block.getReceipts() != null  && block.getReceipts().size() > 0){
            target.setReceipts(block.getReceipts().stream()
                    .map(BackendAsyncTask::coverReceipt).collect(Collectors.toList()));
        }
        return target;
    }
}
