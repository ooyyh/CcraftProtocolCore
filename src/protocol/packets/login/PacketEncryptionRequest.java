package protocol.packets.login;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketEncryptionRequest implements Packet {
    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    public PacketEncryptionRequest() {}

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(serverId);
        buffer.writeVarInt(publicKey.length);
        buffer.writeBytes(publicKey);
        buffer.writeVarInt(verifyToken.length);
        buffer.writeBytes(verifyToken);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.serverId = buffer.readString();
        int publicKeyLength = buffer.readVarInt();
        this.publicKey = buffer.readBytes(publicKeyLength);
        int verifyTokenLength = buffer.readVarInt();
        this.verifyToken = buffer.readBytes(verifyTokenLength);
    }

    @Override
    public int getPacketId() {
        return 0x01;
    }

    public String getServerId() {
        return serverId;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }
}
