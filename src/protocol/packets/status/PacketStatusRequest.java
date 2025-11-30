package protocol.packets.status;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketStatusRequest implements Packet {
    @Override
    public void write(PacketBuffer buffer) throws IOException {
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
    }

    @Override
    public int getPacketId() {
        return 0x00;
    }
}
