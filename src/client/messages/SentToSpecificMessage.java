package client.messages;

import java.util.Set;

public final class SentToSpecificMessage extends MessageBase {
    private final String sender;
    private final String content;
    private final String recipients;

    public SentToSpecificMessage(String recipients, String sender, String content) {
        this.sender = sender;
        this.recipients = recipients;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public Set<String> getRecipients() {
        if (recipients == null || recipients.isEmpty()) {
            //empty set
            return Set.of();
        }

        return Set.of(recipients.split(","));
    }

    public String getContent() {
        return content;
    }
}
