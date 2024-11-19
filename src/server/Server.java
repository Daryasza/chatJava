package server;

// TODO

// broadcast messages to all or specified clientMap


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Server {
    private int port;
    private String serverName;
    private final Set<String> bannedPhrases = new HashSet<>();
    private static final ConcurrentHashMap<String, ConnectedClients> userMap = new ConcurrentHashMap<>();

    public Server(String configFilePath) {
        loadConfig(configFilePath);
    }

    protected void addUser(String username, ConnectedClients client) {
        userMap.put(username, client);
    }

    protected void removeUser(String username) {
        userMap.remove(username);
    }

    protected Set<String> getConnectedUsernames() {
        return new HashSet<>(userMap.keySet());
    }

    protected Map<String, ConnectedClients> getUserMap() {
        return userMap;
    }

    protected int getPort() {
        return port;
    }

    protected  String getServerName() {
        return serverName;
    }

    protected Set<String> getBannedPhrases() {
        return bannedPhrases;
    }

    protected void broadcastMessage(String username, String message) {
        String formattedMessage = "CHAT:" + username + ": " + message;

        for (ConnectedClients client : userMap.values()) {
            try {
                //send the message to PrintWriter of every connected client
                client.getOut().println(formattedMessage);
            } catch (Exception e) {
                System.err.println("Failed to send message to " + client.getPort() + ": " + e.getMessage());
            }
        }
    }

    protected void broadcastClientList() {
        String clientList = String.join(",", userMap.keySet());
        String message = "CLIENT_LIST:" + clientList;

        for (ConnectedClients client : userMap.values()) {
            PrintWriter out = client.getOut();
            if (out != null) {
                out.println(message);
            }
        }
    }

    protected void sendBannedPhrases(PrintWriter out, Integer port) {
        String message = "BANNED_PHRASES:" + getBannedPhrases();

        try {
            //send the message to PrintWriter of every connected client
            out.println(message);
        } catch (Exception e) {
            System.err.println("Failed to send banned phrases to " + port + ": " + e.getMessage());
        }

    }

    void sendToSpecificUsers(String username, String message) {
        String[] parts = message.split(":", 2);
        if (parts.length < 2) return;

        Set<String> targetUsers = Set.of(parts[0].split(","));
        String content = parts[1];

        String formattedMessage = "SEND_TO:" + String.join(",", targetUsers) + ":" + username + ":" + content;

        for (String recipient : targetUsers) {
            ConnectedClients client = getUserMap().get(recipient.trim());
            if (client != null) {
                try {
                    client.getOut().println(formattedMessage);
                } catch (Exception e) {
                    System.err.println("Failed to send message to " + recipient + ": " + e.getMessage());
                }
            }
        }
    }

    void excludeSpecificUsers(String username, String message) {
        String[] parts = message.split(":", 2);

        Set<String> excludedUsersSet = Set.of(parts[0].split(","));
        String content = parts[1];

        String formattedMessage = "EXCLUDE:"  + String.join(",", excludedUsersSet) + ":" + username + ":" + content;

        for (Map.Entry<String, ConnectedClients> entry : getUserMap().entrySet()) {
            username = entry.getKey();
            ConnectedClients client = entry.getValue();

            if (!excludedUsersSet.contains(username)) {
                try {
                    client.getOut().println(formattedMessage);
                } catch (Exception e) {
                    System.err.println("Failed to send message to " + username + ": " + e.getMessage());
                }
            }
        }
    }

    private void loadConfig(String configFilePath) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            properties.load(fis);

            port = Integer.parseInt(properties.getProperty("port"));
            serverName = properties.getProperty("name");
            String bannedPhrasesStr = properties.getProperty("banned_phrases");

            String[] phrases = bannedPhrasesStr.split(",\\s*");
            for (String phrase : phrases) {
                bannedPhrases.add(phrase.trim());
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading properties file: " + e.getMessage(), e);
        }
    }
}










