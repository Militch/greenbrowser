package com.esiran.greenadmin.fetcher;

import com.esiran.greenadmin.chain.entity.Block;
import com.esiran.greenadmin.web.entity.RemoteBlock;

import java.util.List;

public interface BlockQueue {
    void insert(List<String> hashes);
    void reset();
    int pending();
    List<String> reserve(int num);
    void deliver(RemoteBlock block);
    void prepare(long offset);
    List<RemoteBlock> takeBlocks();
}
