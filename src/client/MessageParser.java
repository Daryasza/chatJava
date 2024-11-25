package client;

import client.messages.*;

import java.util.Set;

public class MessageParser {
    public static MessageBase parseMessage(String serverMessage) {
        //format: "Message.Type:content"
        String[] messageParts = serverMessage.split(":", 2);

        if (messageParts.length < 2) {
            throw new IllegalArgumentException("Invalid server message: " + serverMessage);
        }

        String messageType = messageParts[0];
        String messageContent = messageParts[1];

        switch (messageType) {
            case MessageTypes.Sent -> {
                // format: "sender:content"
                String[] parts = messageContent.split(":", 2);
                String sender = parts[0];
                String content = parts[1];

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
                //format: "username"
                return new ServerConnectedMessage(messageContent);
            }
            case MessageTypes.Instructions -> {
                // format: "bannedPhrases:instructions"
                String[] parts = messageContent.split(":", 2);

                String bannedPhrases = parts[0];
                String instructions = parts[1];

                return new InstructionsMessage(bannedPhrases, instructions);
            }
        }

        throw new IllegalArgumentException("Invalid server message: " + serverMessage);
    }
}
