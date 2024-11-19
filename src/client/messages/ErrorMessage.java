package client.messages;

public final class ErrorMessage extends Message {
    private final String content;

    public ErrorMessage(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }
}
