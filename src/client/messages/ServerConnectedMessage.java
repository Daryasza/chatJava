package client.messages;

public final class ServerConnectedMessage extends MessageBase {
    private final String username;

    public ServerConnectedMessage(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
