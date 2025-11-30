package protocol.packets.play;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketKeepAlive implements Packet {
    private int keepAliveId;

    public PacketKeepAlive() {}

    public PacketKeepAlive(int keepAliveId) {
        this.keepAliveId = keepAliveId;
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeVarInt(keepAliveId);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.keepAliveId = buffer.readVarInt();
    }

    @Override
    public int getPacketId() {
        return 0x00;
    }

    public int getKeepAliveId() {
        return keepAliveId;
    }
}
