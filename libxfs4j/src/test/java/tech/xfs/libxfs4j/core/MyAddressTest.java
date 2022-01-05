package tech.xfs.libxfs4j.core;

import junit.framework.TestCase;

public class MyAddressTest extends TestCase {

    public void testFromString() {
        String addressText = "msZZuvZ9Weu2GpvAreTbKpqDVdkHMBbuf";
        MyAddress address = MyAddress.fromString(addressText);
        String gotAddressText = address.toBase58();
        assert addressText.equals(gotAddressText);
    }

    public void testToBytes() {

    }
}