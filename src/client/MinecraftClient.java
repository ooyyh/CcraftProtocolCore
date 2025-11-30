package client;

import crypto.MinecraftEncryption;
import network.MinecraftConnection;
import protocol.*;
import protocol.packets.handshake.PacketHandshake;
import protocol.packets.login.*;
import protocol.packets.play.*;
import protocol.packets.status.*;

import javax.crypto.SecretKey;
import java.security.PublicKey;

public class MinecraftClient {
    private MinecraftConnection connection;
    private String username;
    private boolean running;
    private double x, y, z;
    private float yaw, pitch;

    public MinecraftClient(String username) {
        this.username = username;
    }

    public void connect(String host, int port) throws Exception {
        System.out.println("Connecting to " + host + ":" + port);
        connection = new MinecraftConnection(host, port);

        PacketHandshake handshake = new PacketHandshake(47, host, port, 2);
        connection.sendPacket(handshake);
        connection.setState(ProtocolState.LOGIN);

        PacketLoginStart loginStart = new PacketLoginStart(username);
        connection.sendPacket(loginStart);

        running = true;
        handleLogin();
    }

    private void handleEncryption(PacketEncryptionRequest request) throws Exception {
        System.out.println("Setting up encryption...");

        PublicKey publicKey = MinecraftEncryption.decodePublicKey(request.getPublicKey());
        SecretKey sharedSecret = MinecraftEncryption.generateSharedSecret();

        byte[] encryptedSharedSecret = MinecraftEncryption.encryptRSA(publicKey, sharedSecret.getEncoded());
        byte[] encryptedVerifyToken = MinecraftEncryption.encryptRSA(publicKey, request.getVerifyToken());

        PacketEncryptionResponse response = new PacketEncryptionResponse(encryptedSharedSecret, encryptedVerifyToken);
        connection.sendPacket(response);

        connection.enableEncryption(sharedSecret);
        System.out.println("Encryption enabled");
    }

    private void handleLogin() throws Exception {
        while (running && connection.isConnected()) {
            Packet packet = connection.receivePacket();

            if (packet == null) {
                System.out.println("Received unknown packet");
                continue;
            }

            if (connection.getState() == ProtocolState.LOGIN) {
                if (packet instanceof PacketLoginDisconnect) {
                    PacketLoginDisconnect disconnect = (PacketLoginDisconnect) packet;
                    System.out.println("Disconnected during login: " + disconnect.getReason());
                    running = false;
                    break;
                } else if (packet instanceof PacketEncryptionRequest) {
                    PacketEncryptionRequest encRequest = (PacketEncryptionRequest) packet;
                    System.out.println("Server requested encryption");
                    handleEncryption(encRequest);
                } else if (packet instanceof PacketSetCompression) {
                    PacketSetCompression compression = (PacketSetCompression) packet;
                    System.out.println("Compression enabled with threshold: " + compression.getThreshold());
                    connection.setCompressionThreshold(compression.getThreshold());
                } else if (packet instanceof PacketLoginSuccess) {
                    PacketLoginSuccess success = (PacketLoginSuccess) packet;
                    System.out.println("Login successful! UUID: " + success.getUuid() + ", Username: " + success.getUsername());
                    connection.setState(ProtocolState.PLAY);
                    handlePlay();
                    break;
                }
            }
        }
    }

    private void handlePlay() throws Exception {
        System.out.println("Entered PLAY state, waiting for packets...");

        while (running && connection.isConnected()) {
            Packet packet = connection.receivePacket();

            if (packet == null) {
                if (!connection.isConnected()) {
                    System.out.println("Connection closed by server");
                    running = false;
                    break;
                }
                continue;
            }

            System.out.println("Received packet: " + packet.getClass().getSimpleName() + " (ID: 0x" + Integer.toHexString(packet.getPacketId()) + ")");

            if (packet instanceof PacketJoinGame) {
                PacketJoinGame joinGame = (PacketJoinGame) packet;
                System.out.println("Joined game! Entity ID: " + joinGame.getEntityId());
                System.out.println("Gamemode: " + joinGame.getGamemode());
                System.out.println("Dimension: " + joinGame.getDimension());
                System.out.println("Difficulty: " + joinGame.getDifficulty());
                System.out.println("Level Type: " + joinGame.getLevelType());

                PacketClientSettings settings = new PacketClientSettings("en_US", (byte) 10, (byte) 0, true, 127);
                connection.sendPacket(settings);
            } else if (packet instanceof PacketPlayerPositionAndLook) {
                PacketPlayerPositionAndLook posLook = (PacketPlayerPositionAndLook) packet;
                this.x = posLook.getX();
                this.y = posLook.getY();
                this.z = posLook.getZ();
                this.yaw = posLook.getYaw();
                this.pitch = posLook.getPitch();

                System.out.println("Position updated: X=" + x + ", Y=" + y + ", Z=" + z);
                System.out.println("Rotation: Yaw=" + yaw + ", Pitch=" + pitch);

                PacketPlayerPositionAndLookClient response = new PacketPlayerPositionAndLookClient(x, y, z, yaw, pitch, true);
                connection.sendPacket(response);
            } else if (packet instanceof PacketKeepAlive) {
                PacketKeepAlive keepAlive = (PacketKeepAlive) packet;
                connection.sendPacket(new PacketKeepAlive(keepAlive.getKeepAliveId()));
            } else if (packet instanceof PacketChatMessage) {
                PacketChatMessage chat = (PacketChatMessage) packet;
                System.out.println("Chat: " + chat.getMessage());
            } else if (packet instanceof PacketDisconnect) {
                PacketDisconnect disconnect = (PacketDisconnect) packet;
                System.out.println("Disconnected: " + disconnect.getReason());
                running = false;
                break;
            }
        }
    }

    public void disconnect() {
        running = false;
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return running;
    }
}
