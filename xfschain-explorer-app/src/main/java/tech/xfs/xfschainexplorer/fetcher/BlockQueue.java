package tech.xfs.xfschainexplorer.fetcher;

import tech.xfs.xfschainexplorer.web.entity.RemoteBlock;

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
