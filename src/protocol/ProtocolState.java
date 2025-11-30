package protocol;

public enum ProtocolState {
    HANDSHAKING(0),
    STATUS(1),
    LOGIN(2),
    PLAY(3);

    private final int id;

    ProtocolState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ProtocolState fromId(int id) {
        for (ProtocolState state : values()) {
            if (state.id == id) {
                return state;
            }
        }
        return null;
    }
}
