package client.messages;

public final class ServerConnectedMessage extends MessageBase {
    private final String username;
    private final String bannedPrases;

    public ServerConnectedMessage(String username, String bannedPrases) {
        this.username = username;
        this.bannedPrases = bannedPrases;
    }

    public String getContent() {
        return bannedPrases;
    }
    public String getUsername() {
        return username;
    }
}
