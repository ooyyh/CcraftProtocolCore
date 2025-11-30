package examples;

import client.MinecraftClient;

public class ConnectToDedicatedServer {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 38342;
        String username = "TestBot";

        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            port = Integer.parseInt(args[1]);
        }
        if (args.length >= 3) {
            username = args[2];
        }

        System.out.println("=== Minecraft 1.8.9 Protocol Client ===");
        System.out.println("Connecting to dedicated server...");
        System.out.println("Host: " + host);
        System.out.println("Port: " + port);
        System.out.println("Username: " + username);
        System.out.println("Note: Supports both offline and encrypted connections");
        System.out.println();

        MinecraftClient client = new MinecraftClient(username);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down...");
            client.disconnect();
        }));

        try {
            client.connect(host, port);

            while (client.isRunning()) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                System.err.println("Error: " + e.getMessage());
            }
        } finally {
            client.disconnect();
            System.out.println("Client stopped.");
        }
    }
}
