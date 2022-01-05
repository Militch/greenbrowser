package tech.xfs.xfschainexeplorer.crypto;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Crypto {
    public static ECKeyPair genKey() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return Keys.createEcKeyPair();
    }

    public static byte[] publicKeyEncode(BigInteger pub) {
        return pub.toByteArray();
    }
}
