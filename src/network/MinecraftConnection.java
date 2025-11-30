package network;

import protocol.*;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.util.zip.Inflater;
import java.util.zip.Deflater;

public class MinecraftConnection {
    private Socket socket;
    private InputStream rawInput;
    private OutputStream rawOutput;
    private DataInputStream input;
    private DataOutputStream output;
    private ProtocolState state;
    private int compressionThreshold = -1;
    private boolean encrypted = false;

    public MinecraftConnection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.rawInput = socket.getInputStream();
        this.rawOutput = socket.getOutputStream();
        this.input = new DataInputStream(rawInput);
        this.output = new DataOutputStream(rawOutput);
        this.state = ProtocolState.HANDSHAKING;
    }

    public void enableEncryption(SecretKey sharedSecret) throws Exception {
        Cipher encryptCipher = createAESCipher(Cipher.ENCRYPT_MODE, sharedSecret);
        Cipher decryptCipher = createAESCipher(Cipher.DECRYPT_MODE, sharedSecret);

        this.output = new DataOutputStream(new CipherOutputStream(rawOutput, encryptCipher));
        this.input = new DataInputStream(new CipherInputStream(rawInput, decryptCipher));
        this.encrypted = true;
    }

    private Cipher createAESCipher(int mode, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(mode, key, new javax.crypto.spec.IvParameterSpec(key.getEncoded()));
        return cipher;
    }

    public void sendPacket(Packet packet) throws IOException {
        PacketBuffer buffer = new PacketBuffer();
        buffer.writeVarInt(packet.getPacketId());
        packet.write(buffer);

        byte[] data = buffer.toByteArray();

        if (compressionThreshold >= 0) {
            data = compressPacket(data);
        }

        PacketBuffer lengthBuffer = new PacketBuffer();
        lengthBuffer.writeVarInt(data.length);

        output.write(lengthBuffer.toByteArray());
        output.write(data);
        output.flush();
    }

    public Packet receivePacket() throws IOException {
        try {
            int length = readVarInt(input);
            byte[] data = new byte[length];
            input.readFully(data);

            if (compressionThreshold >= 0) {
                data = decompressPacket(data);
            }

            PacketBuffer buffer = new PacketBuffer(data);
            int packetId = buffer.readVarInt();

            return createPacket(packetId, buffer);
        } catch (java.io.EOFException e) {
            return null;
        }
    }

    private Packet createPacket(int packetId, PacketBuffer buffer) throws IOException {
        Packet packet = PacketRegistry.createPacket(state, packetId);
        if (packet != null) {
            packet.read(buffer);
        }
        return packet;
    }

    private byte[] compressPacket(byte[] data) throws IOException {
        PacketBuffer buffer = new PacketBuffer();

        if (data.length >= compressionThreshold) {
            buffer.writeVarInt(data.length);

            Deflater deflater = new Deflater();
            deflater.setInput(data);
            deflater.finish();

            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            byte[] temp = new byte[8192];
            while (!deflater.finished()) {
                int count = deflater.deflate(temp);
                compressed.write(temp, 0, count);
            }
            deflater.end();

            buffer.writeBytes(compressed.toByteArray());
        } else {
            buffer.writeVarInt(0);
            buffer.writeBytes(data);
        }

        return buffer.toByteArray();
    }

    private byte[] decompressPacket(byte[] data) throws IOException {
        PacketBuffer buffer = new PacketBuffer(data);
        int uncompressedSize = buffer.readVarInt();

        if (uncompressedSize == 0) {
            return buffer.readBytes(buffer.getReadableBytes());
        }

        byte[] compressed = buffer.readBytes(buffer.getReadableBytes());
        Inflater inflater = new Inflater();
        inflater.setInput(compressed);

        byte[] uncompressed = new byte[uncompressedSize];
        try {
            inflater.inflate(uncompressed);
        } catch (Exception e) {
            throw new IOException("Failed to decompress packet", e);
        }
        inflater.end();

        return uncompressed;
    }

    private int readVarInt(DataInputStream input) throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = input.readByte();
            value |= (currentByte & 127) << position;

            if ((currentByte & 128) == 0) break;

            position += 7;

            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }

        return value;
    }

    public void setState(ProtocolState state) {
        this.state = state;
    }

    public ProtocolState getState() {
        return state;
    }

    public void setCompressionThreshold(int threshold) {
        this.compressionThreshold = threshold;
    }

    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
