package protocol;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PacketBuffer {
    private final ByteArrayOutputStream buffer;
    private final DataOutputStream output;
    private byte[] readBuffer;
    private int readIndex;

    public PacketBuffer() {
        this.buffer = new ByteArrayOutputStream();
        this.output = new DataOutputStream(buffer);
    }

    public PacketBuffer(byte[] data) {
        this.buffer = null;
        this.output = null;
        this.readBuffer = data;
        this.readIndex = 0;
    }

    public void writeVarInt(int value) throws IOException {
        while ((value & -128) != 0) {
            output.writeByte(value & 127 | 128);
            value >>>= 7;
        }
        output.writeByte(value);
    }

    public int readVarInt() throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = readByte();
            value |= (currentByte & 127) << position;

            if ((currentByte & 128) == 0) break;

            position += 7;

            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }

        return value;
    }

    public void writeString(String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(bytes.length);
        output.write(bytes);
    }

    public String readString() throws IOException {
        int length = readVarInt();
        byte[] bytes = new byte[length];
        System.arraycopy(readBuffer, readIndex, bytes, 0, length);
        readIndex += length;
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void writeShort(int value) throws IOException {
        output.writeShort(value);
    }

    public short readShort() throws IOException {
        short value = (short) (((readBuffer[readIndex] & 0xFF) << 8) | (readBuffer[readIndex + 1] & 0xFF));
        readIndex += 2;
        return value;
    }

    public void writeByte(int value) throws IOException {
        output.writeByte(value);
    }

    public byte readByte() throws IOException {
        return readBuffer[readIndex++];
    }

    public void writeBytes(byte[] bytes) throws IOException {
        output.write(bytes);
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] bytes = new byte[length];
        System.arraycopy(readBuffer, readIndex, bytes, 0, length);
        readIndex += length;
        return bytes;
    }

    public void writeLong(long value) throws IOException {
        output.writeLong(value);
    }

    public long readLong() throws IOException {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value = (value << 8) | (readBuffer[readIndex++] & 0xFF);
        }
        return value;
    }

    public void writeInt(int value) throws IOException {
        output.writeInt(value);
    }

    public int readInt() throws IOException {
        int value = ((readBuffer[readIndex] & 0xFF) << 24) |
                    ((readBuffer[readIndex + 1] & 0xFF) << 16) |
                    ((readBuffer[readIndex + 2] & 0xFF) << 8) |
                    (readBuffer[readIndex + 3] & 0xFF);
        readIndex += 4;
        return value;
    }

    public void writeBoolean(boolean value) throws IOException {
        output.writeBoolean(value);
    }

    public boolean readBoolean() throws IOException {
        return readByte() != 0;
    }

    public void writeFloat(float value) throws IOException {
        output.writeFloat(value);
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public void writeDouble(double value) throws IOException {
        output.writeDouble(value);
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public void writeUUID(UUID uuid) throws IOException {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    public UUID readUUID() throws IOException {
        long mostSig = readLong();
        long leastSig = readLong();
        return new UUID(mostSig, leastSig);
    }

    public byte[] toByteArray() {
        return buffer.toByteArray();
    }

    public int getReadableBytes() {
        return readBuffer.length - readIndex;
    }
}
