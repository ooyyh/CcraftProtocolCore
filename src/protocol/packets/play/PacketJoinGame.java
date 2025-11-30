package protocol.packets.play;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketJoinGame implements Packet {
    private int entityId;
    private int gamemode;
    private int dimension;
    private int difficulty;
    private int maxPlayers;
    private String levelType;
    private boolean reducedDebugInfo;

    public PacketJoinGame() {}

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeInt(entityId);
        buffer.writeByte(gamemode);
        buffer.writeByte(dimension);
        buffer.writeByte(difficulty);
        buffer.writeByte(maxPlayers);
        buffer.writeString(levelType);
        buffer.writeBoolean(reducedDebugInfo);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.entityId = buffer.readInt();
        this.gamemode = buffer.readByte();
        this.dimension = buffer.readByte();
        this.difficulty = buffer.readByte();
        this.maxPlayers = buffer.readByte();
        this.levelType = buffer.readString();
        this.reducedDebugInfo = buffer.readBoolean();
    }

    @Override
    public int getPacketId() {
        return 0x01;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getGamemode() {
        return gamemode;
    }

    public int getDimension() {
        return dimension;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getLevelType() {
        return levelType;
    }

    public boolean isReducedDebugInfo() {
        return reducedDebugInfo;
    }
}
