package client.messages;

import java.text.SimpleDateFormat;
import java.util.Date;

public sealed abstract class Message permits BannedPhrasesMessage, ChatMessage, ErrorMessage, ExcludeRecipientsMessage, SentToSpecificMessage, ServerConnectedMessage, UserListMessage {

    private final long timestamp;

    protected Message() {
        this.timestamp = System.currentTimeMillis();
    }

    public String getTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(new Date(timestamp));
    }

    public abstract String getContent();
}
