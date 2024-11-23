package client.messages;

public final class BroadcastMessage extends MessageBase {
    private final String sender;
    private final String content;

    public BroadcastMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

}
