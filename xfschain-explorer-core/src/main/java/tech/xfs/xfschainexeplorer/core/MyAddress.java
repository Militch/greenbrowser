package tech.xfs.xfschainexeplorer.core;

import java.util.Arrays;

public class MyAddress {
    private static final int ADDRESS_LEN = 25;
    private final byte[] data;
    public MyAddress(byte[] data){
        if (data.length > ADDRESS_LEN){
            data = Arrays.copyOfRange(data, data.length - ADDRESS_LEN,  data.length);
        }
        this.data = Arrays.copyOfRange(data, 0, ADDRESS_LEN);
    }
    public static MyAddress fromString(String text){
        return new MyAddress(Base58.decode(text));
    }
    public byte[] toBytes(){
        return data;
    }

    public String toBase58(){
        return Base58.encode(data);
    }
}
