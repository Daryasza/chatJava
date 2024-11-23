package client;

import client.messages.*;

import java.util.Set;

public class MessageParser {
    public static MessageBase parseMessage(String serverMessage) {
        System.out.println("serverMessage= " + serverMessage);
        //format: "MESSAGE_TYPE:content"
        String[] messageParts = serverMessage.split(":", 2);

        if (messageParts.length < 2) {
            throw new IllegalArgumentException("Invalid server message: " + serverMessage);
        }

        String messageType = messageParts[0];
        String messageContent = messageParts[1];

        switch (messageType) {
            case MessageTypes.Broadcast -> {
                // format: "sender:content"
                String[] parts = messageContent.split(":", 2);
                String sender = parts[0];
                String content = parts[1];

                return new ChatMessage(sender, content);
            }
            case MessageTypes.SentToSpecific -> {
                // format: "recipients:sender:content"
                String[] parts = messageContent.split(":", 3);

                String recipients = parts[0];
                String sender = parts[1];
                String content = parts[2];

                return new ChatMessage(sender, content);
            }
            case MessageTypes.ExcludeRecipients -> {
                // format: "excludedUsers:sender:content"
                String[] parts = messageContent.split(":", 3);
                String excludedUsers = parts[0];
                String sender = parts[1];
                String content = parts[2];

                return new ChatMessage(sender, content);
            }
            case MessageTypes.UserList -> {
                // format: "users"
                Set<String> clients = Set.of(messageContent.split(","));
                return new UserListMessage(clients);
            }
            case MessageTypes.BannedPhrases -> {
                // format: "bannedPhrases"
                return new BannedPhrasesMessage(messageContent);
                // format: "bannedPhrases"
            }
            case MessageTypes.Error -> {
                // format: "error"
                return new ErrorMessage(messageContent);
                // format: "error"
            }
            case MessageTypes.ServerConnected -> {
                //format: "username:bannedPhrases"
                String[] parts = messageContent.split(":", 2);

                String username = parts[0];
                String bannedPhrases = parts[1];

                return new ServerConnectedMessage(username, bannedPhrases);
            }
        }

        throw new IllegalArgumentException("Invalid server message: " + serverMessage);
    }
}
