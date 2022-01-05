package com.esiran.greenadmin.web.tasks;

import com.esiran.greenadmin.common.jsonrpci.Client;
import com.esiran.greenadmin.web.entity.NodeStatus;
import com.esiran.greenadmin.web.entity.RemoteBlockHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NodeStatusSyncMgr extends TimerTask {
    private static final Logger log = LoggerFactory.getLogger(NodeStatusSyncMgr.class);
    private final NodeStatus status = new NodeStatus();
    private Client client;
    private final Timer syncerTimer = new Timer();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();
    public NodeStatusSyncMgr(Client client){
        this.client = client;
    }
    public NodeStatus getStatus(){
        r.lock();
        NodeStatus t;
        t = status;
        r.unlock();
        return t;
    }
    public void startSync(){
        syncerTimer.schedule(this, 0, 10000);
    }

    private void fetchNodeStatus() throws Exception {
        RemoteBlockHeader header = client.call("Chain.Head", null, RemoteBlockHeader.class);
        w.lock();
        status.setHeight(header.getHeight());
        status.setLatestBlock(header.getHash());
        w.unlock();
    }

    @Override
    public void run() {
        try {
            fetchNodeStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
