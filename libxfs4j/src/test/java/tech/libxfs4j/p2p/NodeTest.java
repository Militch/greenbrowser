package tech.libxfs4j.p2p;

import org.junit.Test;
import tech.xfs.libxfs4j.p2p.Node;

public class NodeTest {
    @Test
    public void test() throws Exception {
        Node n = Node.parseNode("xfsnode://127.0.0.1:9002/?id=89f28935b219230f0294e3f1102ce2840fe8e4d025a4794885c974356b639450f9777b98d0bdbfe8be52e8b0a1fb65f94e5b5009c74d93f5bc60b8825f1419fd");
        System.out.println(n);
    }
}
