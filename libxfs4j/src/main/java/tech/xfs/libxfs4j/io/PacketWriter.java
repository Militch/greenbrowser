package tech.xfs.libxfs4j.io;

import tech.xfs.libxfs4j.p2p.DataPacket;

import java.io.IOException;
import java.io.OutputStream;

public class PacketWriter {
    private final OutputStream output;

    public PacketWriter(OutputStream output) {
        this.output = output;
    }
    public void writePacket(DataPacket packet) throws IOException {
        if (packet == null) {
            return;
        }
        byte[] data = packet.encode();
        this.output.write(data);
    }
}
