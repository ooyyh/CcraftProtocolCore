package protocol.packets.play;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketDisconnect implements Packet {
    private String reason;

    public PacketDisconnect() {}

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(reason);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.reason = buffer.readString();
    }

    @Override
    public int getPacketId() {
        return 0x40;
    }

    public String getReason() {
        return reason;
    }
}
