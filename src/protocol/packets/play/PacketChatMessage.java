package protocol.packets.play;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketChatMessage implements Packet {
    private String message;
    private byte position;

    public PacketChatMessage() {}

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(message);
        buffer.writeByte(position);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.message = buffer.readString();
        this.position = buffer.readByte();
    }

    @Override
    public int getPacketId() {
        return 0x02;
    }

    public String getMessage() {
        return message;
    }

    public byte getPosition() {
        return position;
    }
}
