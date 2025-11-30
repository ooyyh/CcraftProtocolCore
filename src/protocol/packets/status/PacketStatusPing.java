package protocol.packets.status;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketStatusPing implements Packet {
    private long payload;

    public PacketStatusPing() {}

    public PacketStatusPing(long payload) {
        this.payload = payload;
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeLong(payload);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.payload = buffer.readLong();
    }

    @Override
    public int getPacketId() {
        return 0x01;
    }

    public long getPayload() {
        return payload;
    }
}
