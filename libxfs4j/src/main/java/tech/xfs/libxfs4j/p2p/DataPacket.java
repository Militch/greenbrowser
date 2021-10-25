package tech.xfs.libxfs4j.p2p;

import tech.xfs.libxfs4j.util.BytesUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class DataPacket {
    private int version;
    private int type;
    private byte[] dataBytes;

    public void setVersion(int version) {
        this.version = version;
    }

    public void setType(int type) {
        this.type = type;
    }
    public void setDataBytes(byte[] dataBytes){
        this.dataBytes = dataBytes;
    }
    public int getVersion(){
        return version;
    }
    public byte[] getVersionBytes(){
        byte[] versionBytes = new byte[4];
        BytesUtil.LittleEndian.putInt32(versionBytes, this.getVersion());
        return versionBytes;
    }
    public int getType(){
        return type;
    }
    public byte[] getTypeBytes(){
        byte[] typeBytes = new byte[4];
        BytesUtil.LittleEndian.putInt32(typeBytes,  this.getType());
        return typeBytes;
    }
    public byte[] encode() {
        byte[] versionBytes = this.getVersionBytes();
        byte[] typeBytes = this.getTypeBytes();
        byte[] dataBytes = this.getDataBytes();
        byte[] dataLenBytes = new byte[4];
        BytesUtil.LittleEndian.putInt32(dataLenBytes, dataBytes.length);
        ByteBuffer buffer = ByteBuffer.allocate(6 + dataBytes.length);
        buffer.put(versionBytes[0]);
        buffer.put(typeBytes[0]);
        buffer.put(dataLenBytes);
        buffer.put(dataBytes);
        byte[] dataarr = buffer.array();
        return Arrays.copyOfRange(dataarr, 0, buffer.position());
    }

    public void decode(byte[] raw) throws Exception {}

    public byte[] getDataBytes() {
        return this.dataBytes;
    }
}
