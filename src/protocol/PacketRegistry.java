package protocol;

import protocol.packets.handshake.*;
import protocol.packets.status.*;
import protocol.packets.login.*;
import protocol.packets.play.*;

import java.util.HashMap;
import java.util.Map;

public class PacketRegistry {
    private static final Map<ProtocolState, Map<Integer, Class<? extends Packet>>> serverbound = new HashMap<>();
    private static final Map<ProtocolState, Map<Integer, Class<? extends Packet>>> clientbound = new HashMap<>();

    static {
        registerServerbound(ProtocolState.HANDSHAKING, 0x00, PacketHandshake.class);

        registerServerbound(ProtocolState.STATUS, 0x00, PacketStatusRequest.class);
        registerServerbound(ProtocolState.STATUS, 0x01, PacketStatusPing.class);
        registerClientbound(ProtocolState.STATUS, 0x00, PacketStatusResponse.class);
        registerClientbound(ProtocolState.STATUS, 0x01, PacketStatusPong.class);

        registerServerbound(ProtocolState.LOGIN, 0x00, PacketLoginStart.class);
        registerServerbound(ProtocolState.LOGIN, 0x01, PacketEncryptionResponse.class);
        registerClientbound(ProtocolState.LOGIN, 0x00, PacketLoginDisconnect.class);
        registerClientbound(ProtocolState.LOGIN, 0x01, PacketEncryptionRequest.class);
        registerClientbound(ProtocolState.LOGIN, 0x02, PacketLoginSuccess.class);
        registerClientbound(ProtocolState.LOGIN, 0x03, PacketSetCompression.class);

        registerServerbound(ProtocolState.PLAY, 0x00, PacketKeepAlive.class);
        registerServerbound(ProtocolState.PLAY, 0x04, PacketPlayerPosition.class);
        registerServerbound(ProtocolState.PLAY, 0x06, PacketPlayerPositionAndLookClient.class);
        registerServerbound(ProtocolState.PLAY, 0x15, PacketClientSettings.class);
        registerServerbound(ProtocolState.PLAY, 0x16, PacketClientStatus.class);
        registerClientbound(ProtocolState.PLAY, 0x00, PacketKeepAlive.class);
        registerClientbound(ProtocolState.PLAY, 0x01, PacketJoinGame.class);
        registerClientbound(ProtocolState.PLAY, 0x02, PacketChatMessage.class);
        registerClientbound(ProtocolState.PLAY, 0x08, PacketPlayerPositionAndLook.class);
        registerClientbound(ProtocolState.PLAY, 0x40, PacketDisconnect.class);
    }

    private static void registerServerbound(ProtocolState state, int id, Class<? extends Packet> packetClass) {
        serverbound.computeIfAbsent(state, k -> new HashMap<>()).put(id, packetClass);
    }

    private static void registerClientbound(ProtocolState state, int id, Class<? extends Packet> packetClass) {
        clientbound.computeIfAbsent(state, k -> new HashMap<>()).put(id, packetClass);
    }

    public static Packet createPacket(ProtocolState state, int packetId) {
        Map<Integer, Class<? extends Packet>> packets = clientbound.get(state);
        if (packets == null) return null;

        Class<? extends Packet> packetClass = packets.get(packetId);
        if (packetClass == null) return null;

        try {
            return packetClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
