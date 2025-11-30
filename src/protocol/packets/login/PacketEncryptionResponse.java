package protocol.packets.login;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketEncryptionResponse implements Packet {
    private byte[] sharedSecret;
    private byte[] verifyToken;

    public PacketEncryptionResponse() {}

    public PacketEncryptionResponse(byte[] sharedSecret, byte[] verifyToken) {
        this.sharedSecret = sharedSecret;
        this.verifyToken = verifyToken;
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeVarInt(sharedSecret.length);
        buffer.writeBytes(sharedSecret);
        buffer.writeVarInt(verifyToken.length);
        buffer.writeBytes(verifyToken);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        int sharedSecretLength = buffer.readVarInt();
        this.sharedSecret = buffer.readBytes(sharedSecretLength);
        int verifyTokenLength = buffer.readVarInt();
        this.verifyToken = buffer.readBytes(verifyTokenLength);
    }

    @Override
    public int getPacketId() {
        return 0x01;
    }

    public byte[] getSharedSecret() {
        return sharedSecret;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }
}
