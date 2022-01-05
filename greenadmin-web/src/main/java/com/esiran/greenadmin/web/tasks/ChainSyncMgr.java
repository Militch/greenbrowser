package com.esiran.greenadmin.web.tasks;

import com.esiran.greenadmin.chain.entity.BlockHeader;
import com.esiran.greenadmin.fetcher.BlockQueue;
import com.esiran.greenadmin.fetcher.BlockQueueImpl;
import com.esiran.greenadmin.common.jsonrpci.Client;
import com.esiran.greenadmin.common.jsonrpci.MethodParams;
import com.esiran.greenadmin.web.entity.NodeStatus;
import com.esiran.greenadmin.web.entity.RemoteBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ChainSyncMgr extends TimerTask {
    private static final Logger log = LoggerFactory.getLogger(ChainSyncMgr.class);
    private static final long MAX_HASHES_FETCH = 512;
    private final Timer syncerTimer = new Timer();
    private final Client client;
    private final BlockQueue blockQueue = new BlockQueueImpl();
    private final ExecutorService tpool = Executors.newCachedThreadPool();
    private final ExecutorService insertPool = Executors.newCachedThreadPool();
    private final AtomicInteger ln = new AtomicInteger(0);
    private final AtomicInteger ln2 = new AtomicInteger(0);
    public ChainSyncMgr(Client client){
        this.client = client;
    }
    public abstract long localHead();
    public abstract NodeStatus nodeStatus();
    public abstract BlockHeader getBlockByHash(String hash);
    public abstract void insertBlock(List<RemoteBlock> blocks);
    public abstract Runnable insertBlockAsync(List<RemoteBlock> blocks);
    public void startSync(){
        syncerTimer.schedule(this, 0, 10000);
        Thread t = new Thread(processBlockTask());
        t.start();
    }

    public Runnable processBlockTask(){
        return ()->{
            while (true) {
                try {
                    List<RemoteBlock> rbs = blockQueue.takeBlocks();
                    if (rbs.size() == 0) {
                        Thread.sleep(1000);
                        continue;
                    }
                    insertBlock(rbs);
//                    Thread.sleep(1000);
//                    insertPool.execute(insertBlockAsync(rbs));
//                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private long findAncestor(final long currentHeight) throws Exception {
        long from = 0;
        from = currentHeight - MAX_HASHES_FETCH;
        if (from < 0){
            from = 0;
        }
        List<String> hashes = requestGetBlockHashes(from, MAX_HASHES_FETCH);
        for (int index = hashes.size() - 1; index >= 0; index-- ){
            String hash = hashes.get(index);
            BlockHeader bh = getBlockByHash(hash);
            if (bh != null){
                return bh.getHeight();
            }
        }
        long left = 0, right = currentHeight;
        while (left+1 < right) {
            long mid = (left + right) / 2;
            List<String> hashes2 = requestGetBlockHashes(mid, 1);
            if (hashes2.size() != 1){
                throw new Exception("bad hashes");
            }
            String hash = hashes2.get(0);
            BlockHeader bh = getBlockByHash(hash);
            if (bh != null){
                left = mid;
            }else {
                right = mid;
            }
        }
        return left;
    }

    private List<String> requestGetBlockHashes(long from, long count) throws Exception {
        MethodParams mp = new MethodParams();
        mp.addAttribute("number", String.valueOf(from));
        mp.addAttribute("count",String.valueOf(count));
//        List<String> hashes = client.callList("Chain.GetBlockHashes", mp);
        return client.callList("Chain.GetBlockHashes", mp);
    }

    private RemoteBlock requestGetBlock(String hash) throws Exception {
        MethodParams mp = new MethodParams();
        mp.addAttribute("hash", hash);
        return client.call("Chain.GetBlockByHash", mp, RemoteBlock.class);
    }
    private void fetchHashes(long from) throws Exception {
        while (true){
            List<String> hashes = requestGetBlockHashes(from, MAX_HASHES_FETCH);
            if (hashes.size() == 0){
                ln2.set(1);
                return;
            }
            blockQueue.insert(hashes);
            from += hashes.size();
        }
    }
    private List<Runnable> makeRequestTask(List<String> hashes){
        List<Runnable> rs = new ArrayList<>();
        for (String hash : hashes){
            rs.add(() -> {
                try {
                    RemoteBlock rb = requestGetBlock(hash);
                    blockQueue.deliver(rb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return rs;
    }
    private void fetchBlocks(long from) throws Exception {
        blockQueue.prepare(from);

        while (true) {
            if (blockQueue.pending() == 0) {
                int ln2v = ln2.get();
                if (ln2v == 1){
                    log.info("fetch finished");
                    ln2.set(0);
                    return;
                }
                continue;
            }
            List<String> hashes = blockQueue.reserve(BlockQueueImpl.BLOCK_CACHE_SIZE);
            if (hashes == null) {
                continue;
            }
            List<Runnable> rs = makeRequestTask(hashes);
            for (Runnable r : rs){
                tpool.execute(r);
            }
        }
    }

    private Runnable runSyncTask(long lh){
        blockQueue.reset();
        return ()->{
            try {
                long a = findAncestor(lh);
                final long from = a == 0?0:a + 1;
                new Thread(()->{
                    try {
                        fetchHashes(from);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                fetchBlocks(from);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                ln.set(0);
            }
        };
    }
    @Override
    public void run() {
        NodeStatus ns = nodeStatus();
        long lh = localHead();
        if (lh >= ns.getHeight()){
            return;
        }
        if (!ln.compareAndSet(0, 1)){
            return;
        }
        log.info("run.");
        Thread t = new Thread(runSyncTask(lh));
        t.start();
    }
}
