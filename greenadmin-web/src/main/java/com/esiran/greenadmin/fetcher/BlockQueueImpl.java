package com.esiran.greenadmin.fetcher;

import com.esiran.greenadmin.web.entity.RemoteBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BlockQueueImpl implements BlockQueue{
    public static final int BLOCK_CACHE_SIZE = 50;
    private static final class HashItem {
        private final String hash;
        private final int priority;
        private HashItem(String hash, int priority) {
            this.hash = hash;
            this.priority = priority;
        }
    }
    private static final class BlockItem {
        private RemoteBlock block;
        public BlockItem(RemoteBlock block) {
            this.block = block;
        }
    }
    private final Comparator<HashItem> hashItemComparator = Comparator.comparingInt(o -> o.priority);
    private static final Logger logger = LoggerFactory.getLogger(BlockQueueImpl.class);
    private Map<String,Integer> hashPool = new HashMap<>();
    private int hashCounter = 0;
    private long blockOffset = 0;
    private final Queue<HashItem> hashQueue = new PriorityQueue<>(hashItemComparator);
    private HashMap<String,Long> blockPool = new HashMap<>();
    private BlockItem[] blockCaches = new BlockItem[BLOCK_CACHE_SIZE];
    private Map<String,Integer> pendingPool = new HashMap<>();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();
    public BlockQueueImpl() {
    }

    @Override
    public void insert(List<String> hashes) {
        w.lock();
        for (String hash : hashes){
            Integer old = hashPool.get(hash);
            if (old != null){
                continue;
            }
            hashCounter += 1;
            hashPool.put(hash, hashCounter);
            hashQueue.add(new HashItem(hash, hashCounter));
        }
        w.unlock();
    }

    @Override
    public void reset() {
        w.lock();
        hashCounter = 0;
        hashPool = new HashMap<>();
        blockPool =  new HashMap<>();
        pendingPool = new HashMap<>();
        hashQueue.clear();
        blockCaches = new BlockItem[BLOCK_CACHE_SIZE];
        blockOffset = 0;
        w.unlock();
    }

    @Override
    public int pending() {
        r.lock();
        int size;
        size = hashQueue.size();
        r.unlock();
        return size;
    }

    @Override
    public List<String> reserve(int num) {
        w.lock();
        if (hashQueue.isEmpty()){
            w.unlock();
            return null;
        }
        int space = blockCaches.length - blockPool.size();
        space -= pendingPool.size();
        List<String> send = new ArrayList<>();
        for (int i = 0; i<space && send.size() < num && !hashQueue.isEmpty();i++){
            HashItem hashItem = hashQueue.poll();
            send.add(hashItem.hash);
        }
        if (send.size() == 0){
            w.unlock();
            return null;
        }
        for (String hash : send){
            pendingPool.put(hash, 1);
        }
        w.unlock();
        return send;
    }

    @Override
    public void deliver(RemoteBlock block) {
        w.lock();
        String hash = block.getHash();
//        logger.info("deliver: BlockHeight={}, BlockHash={}", block.getHeight(), hash);
        Integer pend = pendingPool.get(hash);
        if (pend == null){
            w.unlock();
            return;
        }
        pendingPool.remove(hash);
        int index = (int) (block.getHeight() - blockOffset);
        if (index >= blockCaches.length || index < 0 ) {
            w.unlock();
            return;
        }

        blockCaches[index] = new BlockItem(block);
        hashPool.remove(hash);
        blockPool.put(hash, block.getHeight());
        w.unlock();
    }

    @Override
    public void prepare(long offset) {
        w.lock();
        if (blockOffset < offset) {
            blockOffset = offset;
        }
        w.unlock();
    }

    @Override
    public List<RemoteBlock> takeBlocks() {
        w.lock();
        List<RemoteBlock> blocks = new ArrayList<>();
        for (BlockItem bi : blockCaches){
            if (bi == null){
                break;
            }
            blocks.add(bi.block);
            blockPool.remove(bi.block.getHash());
        }
        int bc = blocks.size();
        BlockItem[] tmp = Arrays.copyOfRange(blockCaches, bc, blockCaches.length);
        blockCaches = Arrays.copyOf(tmp, BLOCK_CACHE_SIZE);
        for (int k = blockCaches.length - blocks.size(), n = blockCaches.length; k < n; k++){
            blockCaches[k] = null;
        }
        blockOffset += blocks.size();
        w.unlock();
        return blocks;
    }
}
