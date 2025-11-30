package examples;

import client.MinecraftClient;

import java.io.IOException;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectToLANServer {
    private static final int MULTICAST_PORT = 4445;
    private static final String MULTICAST_ADDRESS = "224.0.2.60";
    private static final Pattern LAN_PATTERN = Pattern.compile("\\[MOTD\\](.+?)\\[/MOTD\\]\\[AD\\](\\d+)\\[/AD\\]");

    public static void main(String[] args) {
        String username = "TestBot";

        if (args.length >= 1) {
            username = args[0];
        }

        System.out.println("=== Minecraft 1.8.9 Protocol Client - LAN Mode ===");
        System.out.println("Username: " + username);
        System.out.println("Scanning for LAN servers...");
        System.out.println();

        try {
            LANServerInfo serverInfo = scanForLANServers(10000);

            if (serverInfo == null) {
                System.out.println("No LAN servers found. Make sure:");
                System.out.println("1. A Minecraft client has opened a world to LAN");
                System.out.println("2. You are on the same network");
                System.out.println("3. Multicast is not blocked by your firewall");
                return;
            }

            System.out.println("Found LAN server!");
            System.out.println("MOTD: " + serverInfo.motd);
            System.out.println("Host: " + serverInfo.host);
            System.out.println("Port: " + serverInfo.port);
            System.out.println();
            System.out.println("Connecting...");

            MinecraftClient client = new MinecraftClient(username);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down...");
                client.disconnect();
            }));

            client.connect(serverInfo.host, serverInfo.port);

            while (client.isRunning()) {
                Thread.sleep(100);
            }

            client.disconnect();
            System.out.println("Client stopped.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static LANServerInfo scanForLANServers(int timeout) throws IOException {
        MulticastSocket socket = new MulticastSocket(MULTICAST_PORT);
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);

        try {
            socket.joinGroup(group);
            socket.setSoTimeout(timeout);

            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            System.out.println("Listening for LAN server broadcasts...");

            while (true) {
                try {
                    socket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength());
                    Matcher matcher = LAN_PATTERN.matcher(message);

                    if (matcher.find()) {
                        String motd = matcher.group(1);
                        int port = Integer.parseInt(matcher.group(2));
                        String host = packet.getAddress().getHostAddress();

                        LANServerInfo info = new LANServerInfo();
                        info.motd = motd;
                        info.host = host;
                        info.port = port;

                        return info;
                    }
                } catch (SocketTimeoutException e) {
                    break;
                }
            }
        } finally {
            socket.leaveGroup(group);
            socket.close();
        }

        return null;
    }

    private static class LANServerInfo {
        String motd;
        String host;
        int port;
    }
}
