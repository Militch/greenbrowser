package tech.xfs.libxfs4j.util;

import org.junit.Test;
import tech.xfs.libxfs4j.core.MyAddress;

import static org.junit.Assert.*;

public class AddressUtilTest {

    @Test
    public void createAddress() {
        String fromAddressText = "aJTobAyvdXeEGW7DHA1Yqc6PaVa2apHdX";
        MyAddress fromAddress = MyAddress.fromString(fromAddressText);
        MyAddress gotAddress = AddressUtil.createAddress(fromAddress, 1);
        System.out.println(gotAddress.toBase58());
    }
}