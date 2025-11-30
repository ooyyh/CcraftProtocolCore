package protocol.packets.login;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketLoginDisconnect implements Packet {
    private String reason;

    public PacketLoginDisconnect() {}

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
        return 0x00;
    }

    public String getReason() {
        return reason;
    }
}
