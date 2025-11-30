package protocol.packets.play;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketClientStatus implements Packet {
    private int actionId;

    public PacketClientStatus() {}

    public PacketClientStatus(int actionId) {
        this.actionId = actionId;
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeVarInt(actionId);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.actionId = buffer.readVarInt();
    }

    @Override
    public int getPacketId() {
        return 0x16;
    }

    public int getActionId() {
        return actionId;
    }
}
