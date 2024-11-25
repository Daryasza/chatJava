package server;

import client.MessageTypes;
import config.ConfigLoader;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private final static String MessagePartsDelimiter = ":";
    private final int port;
    private final String serverName;
    private final Set<String> bannedPhrases = new HashSet<>();
    protected String bannedPhrasesString;

    private static final ConcurrentHashMap<String, ConnectedClients> userMap = new ConcurrentHashMap<>();

    public Server(String configFilePath) {

        ConfigLoader configLoader = new ConfigLoader();
        configLoader.loadConfig(configFilePath);

        this.port = configLoader.getPort();
        this.serverName = configLoader.getServerName();
        this.bannedPhrases.addAll(configLoader.getBannedPhrases());
        this.bannedPhrasesString = String.join(", ", this.bannedPhrases);
    }

    protected boolean addUser(String username, ConnectedClients client) {
        //if key already exists, putIfAbsent returns it's value, otherwise null
         return userMap.putIfAbsent(username, client) == null;
    }

    protected void removeUser(String username) {
        userMap.remove(username);
    }

    protected Map<String, ConnectedClients> getUserMap() {
        return userMap;
    }

    public int getPort() {
        return port;
    }

    protected  String getServerName() {
        return serverName;
    }

    protected Set<String> getBannedPhrases() {
        return bannedPhrases;
    }

    protected void broadcastClientList() {
        String clientList = String.join(",", getUserMap().keySet());
        String message = String.join(MessagePartsDelimiter, MessageTypes.UserList, clientList);

        for (ConnectedClients client : getUserMap().values()) {
            try{
                client.out().println(message);
            } catch (Exception e) {
                System.err.println("Failed to send broadcast message to port: " + client.port() + ": " + e.getMessage());
            }
        }
    }

    protected void sendBannedPhrases(PrintWriter out) {
        String message = String.join(MessagePartsDelimiter, MessageTypes.BannedPhrases, bannedPhrasesString);

        try {
            out.println(message);
        } catch (Exception e) {
            System.err.println("Failed to send banned phrases" + e.getMessage());
        }
    }

    protected void broadcastMessage(String username, String message) {
        String formattedMessage = String.join(MessagePartsDelimiter, MessageTypes.Sent, username, message);

        for (ConnectedClients client : getUserMap().values()) {
            try {
                //send the message to every connected client
                client.out().println(formattedMessage);
            } catch (Exception e) {
                System.err.println("Failed to send message to " + client.port() + ": " + e.getMessage());
            }
        }
    }

    void sendToSpecificUsers(String username, String message) {
        //format: recipientsString, message
        String[] parts = message.split(":", 2);
        Set<String> targetUsers = Set.of(parts[0].split(","));
        String content = parts[1];

        String formattedMessage = String.join(MessagePartsDelimiter, MessageTypes.Sent, username, content);

        for (String recipient : targetUsers) {
            ConnectedClients client = getUserMap().get(recipient.trim());
            if (client != null) {
                try {
                    client.out().println(formattedMessage);
                } catch (Exception e) {
                    System.err.println("Failed to send message to " + recipient + ": " + e.getMessage());
                }
            }
        }
    }

    void excludeSpecificUsers(String username, String message) {
        //format: excludedRecipientsString, message
        String[] parts = message.split(":", 2);
        Set<String> excludedUsersSet = Set.of(parts[0].split(","));

        String content = parts[1];
        //join by delimiter as first argument
        String formattedMessage = String.join(MessagePartsDelimiter, MessageTypes.Sent, username, content);

        for (Map.Entry<String, ConnectedClients> entry : getUserMap().entrySet()) {
            username = entry.getKey();
            ConnectedClients client = entry.getValue();

            if (!excludedUsersSet.contains(username)) {
                try {
                    client.out().println(formattedMessage);
                } catch (Exception e) {
                    System.err.println("Failed to send message to " + username + ": " + e.getMessage());
                }
            }
        }
    }
}










