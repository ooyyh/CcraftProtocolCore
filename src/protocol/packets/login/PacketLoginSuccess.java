package protocol.packets.login;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketLoginSuccess implements Packet {
    private String uuid;
    private String username;

    public PacketLoginSuccess() {}

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(uuid);
        buffer.writeString(username);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.uuid = buffer.readString();
        this.username = buffer.readString();
    }

    @Override
    public int getPacketId() {
        return 0x02;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }
}
