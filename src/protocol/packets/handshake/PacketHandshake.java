package protocol.packets.handshake;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketHandshake implements Packet {
    private int protocolVersion;
    private String serverAddress;
    private int serverPort;
    private int nextState;

    public PacketHandshake() {}

    public PacketHandshake(int protocolVersion, String serverAddress, int serverPort, int nextState) {
        this.protocolVersion = protocolVersion;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.nextState = nextState;
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeVarInt(protocolVersion);
        buffer.writeString(serverAddress);
        buffer.writeShort(serverPort);
        buffer.writeVarInt(nextState);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.protocolVersion = buffer.readVarInt();
        this.serverAddress = buffer.readString();
        this.serverPort = buffer.readShort();
        this.nextState = buffer.readVarInt();
    }

    @Override
    public int getPacketId() {
        return 0x00;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getNextState() {
        return nextState;
    }
}
