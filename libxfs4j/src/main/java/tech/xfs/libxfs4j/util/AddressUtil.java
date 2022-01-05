package tech.xfs.libxfs4j.util;

import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.web3j.crypto.Hash;
import tech.xfs.libxfs4j.core.Binary;
import tech.xfs.libxfs4j.core.MyAddress;

public class AddressUtil {
    public static MyAddress createAddress(MyAddress address,long nonce){
        byte[] fromAddress = address.toBytes();
        byte[] addressHash = Hash.sha256(fromAddress);
        byte[] nonceData = Binary.LittleEndian.fromInt64(nonce);
        byte[] mix = BytesUtil.concat(addressHash, nonceData);
        byte[] hash = Hash.sha256(mix);
        return new MyAddress(hash);
    }
}
