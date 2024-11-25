package client.messages;

import java.util.Date;

public sealed abstract class MessageBase permits BannedPhrasesMessage, ChatMessage, ErrorMessage, InstructionsMessage, ServerConnectedMessage, UserListMessage {

    public final Date date;

    protected MessageBase() {
        this.date = new Date();
    }
}
