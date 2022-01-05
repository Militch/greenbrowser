package tech.xfs.xfschainexeplorer.util;

import org.web3j.crypto.Hash;
import tech.xfs.xfschainexeplorer.core.Binary;
import tech.xfs.xfschainexeplorer.core.MyAddress;

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
