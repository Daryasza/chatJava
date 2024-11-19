package client.messages;

import java.util.Set;

public final class SentToSpecificMessage extends Message {
    private final String sender;
    private final String content;
    private final String recipients;

    public SentToSpecificMessage(String recipients, String sender, String content) {
        this.recipients = recipients;
        this.sender = sender;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }
    public String getContent() {
        return content;
    }
    public Set<String> getRecipients() {
        if (recipients == null || recipients.isEmpty()) {
            return Set.of(); // Return an empty set if no recipients are excluded
        }
        return Set.of(recipients.split(","));

    }
}
