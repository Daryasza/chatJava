package client.messages;

//TODO String[] getUsers, new abstract class that extends messsage - for util messages without payload, rename message to messageBase or similar

import java.util.Date;

public sealed abstract class MessageBase permits BannedPhrasesMessage, BroadcastMessage, ErrorMessage, ExcludeRecipientsMessage, SentToSpecificMessage, ServerConnectedMessage, UserListMessage {

    public final Date date;

    protected MessageBase() {
        this.date = new Date();
    }
}
