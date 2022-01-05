package tech.xfs.xfschainexplorer.jsonrpc;

import tech.xfs.xfschainexplorer.chain.entity.Address;
import tech.xfs.xfschainexplorer.common.jsonrpci.MethodParams;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HTTPClientTest {
    private static final String JSON_RPC_URL = "http://127.0.0.1:9012";

    private static final ModelMapper mp = new ModelMapper();
    static {
        mp.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
    }
    @Test
    void call() throws Exception {
        HTTPClient client = new HTTPClient(JSON_RPC_URL);
        MethodParams mp = new MethodParams();
        int wantSize = 1;
        mp.addAttribute("number","1");
        mp.addAttribute("count",String.valueOf(wantSize));
        List<String> hashes = client.callList("Chain.GetBlockHashes", mp);
        assertEquals(wantSize,hashes.size());
    }
    @Test
    void a(){
        Address a = new Address();
        a.setAddress("abc");
        a.setBalance("cde");
        Address b =new Address();
        b.setCode("aaa");
        b.setId(9);
        mp.map(a, b);
        System.out.println();
    }
}