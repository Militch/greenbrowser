package tech.xfs.libxfs4j.p2p;

import tech.xfs.libxfs4j.io.PacketReader;
import tech.xfs.libxfs4j.io.PacketWriter;

import java.io.IOException;

public class PeerConn {
    private final PacketReader reader;
    private final PacketWriter writer;

    public PeerConn(PacketReader reader, PacketWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public DataPacket readPacket() throws Exception {
        return reader.readPacket();
    }
    public void writePacket(DataPacket dataPacket) throws IOException {
        writer.writePacket(dataPacket);
    }
}
