package tech.xfs.libxfs4j.p2p;

import java.io.IOException;

public class Peer {
    private PeerConn conn;
    private NodeId remoteNodeId;
    private long remoteHeight;
    private String remoteHead;

    public void setConn(PeerConn conn) {
        this.conn = conn;
    }

    public PeerConn getConn() {
        return conn;
    }

    public NodeId getRemoteNodeId() {
        return remoteNodeId;
    }

    public void setRemoteNodeId(NodeId remoteNodeId) {
        this.remoteNodeId = remoteNodeId;
    }

    public long getRemoteHeight() {
        return remoteHeight;
    }

    public void setRemoteHeight(long remoteHeight) {
        this.remoteHeight = remoteHeight;
    }

    public String getRemoteHead() {
        return remoteHead;
    }

    public void setRemoteHead(String remoteHead) {
        this.remoteHead = remoteHead;
    }

    public DataPacket readPacket() throws Exception {
        return conn.readPacket();
    }
    public void writePacket(DataPacket dataPacket) throws IOException {
        conn.writePacket(dataPacket);
    }
}
