package tech.xfs.libxfs4j.p2p;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

public class NodeId {
    private final BigInteger pubkey;

    public NodeId(BigInteger pubkey) {
        this.pubkey = pubkey;
    }

    public static NodeId pubKey2NodeId(BigInteger pubkey){
        return new NodeId(pubkey);
    }

    public byte[] toByteArray(){
        byte[] bs = pubkey.toByteArray();
        return Arrays.copyOfRange(bs, bs.length-64, bs.length);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NodeId)){
            return false;
        }
        NodeId target = (NodeId) o;
        return Arrays.equals(target.toByteArray(), this.toByteArray());
    }

}
