package protocol.packets.play;

import protocol.Packet;
import protocol.PacketBuffer;

import java.io.IOException;

public class PacketClientSettings implements Packet {
    private String locale;
    private byte viewDistance;
    private byte chatMode;
    private boolean chatColors;
    private int displayedSkinParts;

    public PacketClientSettings() {}

    public PacketClientSettings(String locale, byte viewDistance, byte chatMode, boolean chatColors, int displayedSkinParts) {
        this.locale = locale;
        this.viewDistance = viewDistance;
        this.chatMode = chatMode;
        this.chatColors = chatColors;
        this.displayedSkinParts = displayedSkinParts;
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(locale);
        buffer.writeByte(viewDistance);
        buffer.writeByte(chatMode);
        buffer.writeBoolean(chatColors);
        buffer.writeByte(displayedSkinParts);
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        this.locale = buffer.readString();
        this.viewDistance = buffer.readByte();
        this.chatMode = buffer.readByte();
        this.chatColors = buffer.readBoolean();
        this.displayedSkinParts = buffer.readByte();
    }

    @Override
    public int getPacketId() {
        return 0x15;
    }

    public String getLocale() {
        return locale;
    }

    public byte getViewDistance() {
        return viewDistance;
    }

    public byte getChatMode() {
        return chatMode;
    }

    public boolean isChatColors() {
        return chatColors;
    }

    public int getDisplayedSkinParts() {
        return displayedSkinParts;
    }
}
