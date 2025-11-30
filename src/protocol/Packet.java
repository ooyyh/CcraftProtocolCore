package protocol;

import java.io.IOException;

public interface Packet {
    void write(PacketBuffer buffer) throws IOException;
    void read(PacketBuffer buffer) throws IOException;
    int getPacketId();
}
