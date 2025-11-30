import examples.ConnectToDedicatedServer;
import examples.ConnectToLANServer;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String mode = args[0].toLowerCase();
        String[] clientArgs = new String[args.length - 1];
        System.arraycopy(args, 1, clientArgs, 0, args.length - 1);

        switch (mode) {
            case "dedicated":
            case "server":
                ConnectToDedicatedServer.main(clientArgs);
                break;
            case "lan":
                ConnectToLANServer.main(clientArgs);
                break;
            default:
                printUsage();
                break;
        }
    }

    private static void printUsage() {
        System.out.println("=== Minecraft 1.8.9 Protocol Core ===");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java Main <mode> [options]");
        System.out.println();
        System.out.println("Modes:");
        System.out.println("  dedicated [host] [port] [username]");
        System.out.println("    Connect to a dedicated server");
        System.out.println("    Default: localhost 25565 TestBot");
        System.out.println();
        System.out.println("  lan [username]");
        System.out.println("    Scan and connect to LAN servers");
        System.out.println("    Default username: TestBot");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java Main dedicated");
        System.out.println("  java Main dedicated localhost 25565 MyBot");
        System.out.println("  java Main lan");
        System.out.println("  java Main lan MyBot");
    }
}