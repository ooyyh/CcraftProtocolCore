package protocol.packets.play;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketPlayerPosition implements Packet {
    private double x;
    private double y;
    private double z;
    private boolean onGround;

    public PacketPlayerPosition() {}

    public PacketPlayerPosition(double x, double y, double z, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.onGround = onGround;
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
        buffer.writeBoolean(onGround);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
        this.onGround = buffer.readBoolean();
    }

    @Override
    public int getPacketId() {
        return 0x04;
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

    public boolean isOnGround() {
        return onGround;
    }
}
