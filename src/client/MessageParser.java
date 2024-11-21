package client;

import client.messages.*;

public class MessageParser {
    public static Message parseMessage(String serverMessage) {

        if (serverMessage.startsWith("CHAT:")) {
            // Expected format: "CHAT:sender:content"
            String[] parts = serverMessage.substring(5).split(":", 2);
            String sender = parts[0];
            String content = parts[1];

            return new ChatMessage(sender, content);
        }
        else if (serverMessage.startsWith("SEND_TO:")) {
            // Format: "SEND_TO:recipients:sender:content"
            String[] parts = serverMessage.substring(8).split(":", 3);
            String recipients = parts[0];
            String sender = parts[1];
            String content = parts[2];

            return new SentToSpecificMessage(recipients, sender, content);

        }
        else if (serverMessage.startsWith("EXCLUDE:")) {
            // Format: "EXCLUDE:excludedUsers:sender:content"
            String[] parts = serverMessage.substring(8).split(":", 3);
            String excludedUsers = parts[0];
            String sender = parts[1];
            String content = parts[2];

            return new ExcludeRecipientsMessage(excludedUsers, sender, content);

        }
        else if (serverMessage.startsWith("CLIENT_LIST:")) {
            String clients = serverMessage.substring(12);
            return new UserListMessage(clients);
        }
        else if (serverMessage.startsWith("BANNED_PHRASES:")) {
            String bannedPhr = serverMessage.substring(15);
            return new BannedPhrasesMessage(bannedPhr);
        }
        else if (serverMessage.startsWith("ERROR:")) {
            String err = serverMessage.substring(6);
            return new ErrorMessage(err);
        }
        else if (serverMessage.startsWith("OK:")) {
            //Format: "OK:username:bannedPhrases"
            String[] parts = serverMessage.substring(3).split(":", 2);
            String username = parts[0];
            String bannedPhrases = parts[1];
            return new ServerConnectedMessage(username, bannedPhrases);
        }
        throw new IllegalArgumentException("Invalid server message: " + serverMessage);
    }
}
