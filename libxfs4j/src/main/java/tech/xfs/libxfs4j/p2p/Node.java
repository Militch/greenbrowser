package tech.xfs.libxfs4j.p2p;

import org.web3j.utils.Numeric;
import tech.xfs.libxfs4j.util.UriUtil;

import java.math.BigInteger;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Node {
    private final String ip;
    private final int tcpPort;
    private final NodeId nodeId;
    private String hash;
    public Node(String ip, int tcpPort, NodeId nodeId) {
        this.ip = ip;
        this.tcpPort = tcpPort;
        this.nodeId = nodeId;
    }
    public static Node parseNode(String uri) throws Exception {
        URI mUri = URI.create(uri);
        String scheme = mUri.getScheme();
        if (!scheme.equals("xfsnode")){
            throw new Exception("uri scheme err");
        }
        String host = mUri.getHost();
        int port = mUri.getPort();
        Map<String,String> params = UriUtil.n(mUri.getRawQuery());
        String idhex = params.get("id");
        if ( idhex == null || idhex.length() == 0){
            throw new Exception("parse node id err");
        }
        byte[] idBytes = Numeric.hexStringToByteArray(idhex);
        return new Node(host,port, NodeId.pubKey2NodeId(new BigInteger(idBytes)));
    }

    public String getIp() {
        return ip;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "";
    }
}
