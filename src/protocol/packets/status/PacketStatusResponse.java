package protocol.packets.status;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketStatusResponse implements Packet {
    private String jsonResponse;

    public PacketStatusResponse() {}

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(jsonResponse);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.jsonResponse = buffer.readString();
    }

    @Override
    public int getPacketId() {
        return 0x00;
    }

    public String getJsonResponse() {
        return jsonResponse;
    }
}
