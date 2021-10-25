package tech.xfs.libxfs4j.io;

import tech.xfs.libxfs4j.p2p.DataPacket;
import tech.xfs.libxfs4j.util.BytesUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PacketReader {
    private final InputStream in;
    public PacketReader(InputStream in) {
        this.in = in;
    }
    public DataPacket readPacket() throws Exception {
        byte[] header = new byte[6];
        int headerLen = 0;
        while(headerLen < 6){
            int n = this.in.read(header);
            headerLen = headerLen + n;
        }
        int version = header[0] & 0xff;
        int type = header[1] & 0xff;
        byte[] datalenbuf = Arrays.copyOfRange(header,2,headerLen);
        int datalen = BytesUtil.LittleEndian.readInt32(datalenbuf);
        int finaldatalen = datalen;
        ByteBuffer buffer = ByteBuffer.allocate(finaldatalen);
        while (datalen > 0){
            byte[] readbuf = new byte[finaldatalen - (finaldatalen - datalen)];
            int n = this.in.read(readbuf);
            readbuf = Arrays.copyOfRange(readbuf,0,n);
            try {
                buffer.put(readbuf);
            }catch (Exception e) {
                e.printStackTrace();
            }
            datalen = datalen - n;
        }
//        System.out.printf("packet: version=%d, type=%d, dataSize=%d%n", version, type, finaldatalen);
        DataPacket dp = new DataPacket();
        dp.setType(type);
        dp.setVersion(version);
        dp.setDataBytes(buffer.array());
        dp.decode(buffer.array());
        return dp;
    }
}
