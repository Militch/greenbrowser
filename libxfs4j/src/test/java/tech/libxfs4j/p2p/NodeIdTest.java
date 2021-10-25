package tech.libxfs4j.p2p;

import org.junit.Test;
import org.web3j.crypto.ECKeyPair;
import tech.xfs.libxfs4j.crypto.Crypto;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class NodeIdTest {
    @Test
    public void test() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair kp = Crypto.genKey();
        BigInteger bi = kp.getPublicKey();
        byte[] bis = bi.toByteArray();
        System.out.println("hello");
    }
}
