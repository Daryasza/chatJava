package client.messages;

import client.commands.BroadcastMessageCommand;

public sealed abstract class Message permits ChatMessage, BannedPhrasesMessage, ErrorMessage, ExcludeRecipientsMessage, SentToSpecificMessage, UserListMessage {
    private final long timestamp;

    protected Message() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    abstract String getContent();
}
