package protocol.packets.login;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketLoginStart implements Packet {
    private String username;

    public PacketLoginStart() {}

    public PacketLoginStart(String username) {
        this.username = username;
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(username);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.username = buffer.readString();
    }

    @Override
    public int getPacketId() {
        return 0x00;
    }

    public String getUsername() {
        return username;
    }
}
