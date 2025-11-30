package protocol.packets.play;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketPlayerPositionAndLookClient implements Packet {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;

    public PacketPlayerPositionAndLookClient() {}

    public PacketPlayerPositionAndLookClient(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);
        buffer.writeBoolean(onGround);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
        this.yaw = buffer.readFloat();
        this.pitch = buffer.readFloat();
        this.onGround = buffer.readBoolean();
    }

    @Override
    public int getPacketId() {
        return 0x06;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }
}
