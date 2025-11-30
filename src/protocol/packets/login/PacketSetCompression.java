package protocol.packets.login;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketSetCompression implements Packet {
    private int threshold;

    public PacketSetCompression() {}

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeVarInt(threshold);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.threshold = buffer.readVarInt();
    }

    @Override
    public int getPacketId() {
        return 0x03;
    }

    public int getThreshold() {
        return threshold;
    }
}
