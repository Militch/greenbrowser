package tech.xfs.xfschainexeplorer.util;

import org.junit.Test;
import tech.xfs.xfschainexeplorer.core.MyAddress;

public class AddressUtilTest {

    @Test
    public void createAddress() {
        String fromAddressText = "aJTobAyvdXeEGW7DHA1Yqc6PaVa2apHdX";
        MyAddress fromAddress = MyAddress.fromString(fromAddressText);
        MyAddress gotAddress = AddressUtil.createAddress(fromAddress, 1);
        assert !fromAddress.toBase58().equals(gotAddress.toBase58());
    }
}