package server;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//TODO .properties -> .txt, Work on message types

public class Server {
    private int port;
    private String serverName;
    private String bannedPhrasesString;
    private final Set<String> bannedPhrases = new HashSet<>();

    private static final ConcurrentHashMap<String, ConnectedClients> userMap = new ConcurrentHashMap<>();

    public Server(String configFilePath) {

        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=", 2);

                String key = parts[0].trim();
                String value = parts[1].trim();

                switch (key) {
                    case "port": {
                        port = Integer.parseInt(value);
                        break;
                    }
                    case "name": {
                        serverName = value;
                        break;
                    }
                    case "banned_phrases": {
                        bannedPhrasesString = value;
                        String[] phrases = value.split(",\\s*");
                        bannedPhrases.addAll(Arrays.asList(phrases));
                        break;
                    }
                    default: System.err.println("Unknown key: " + key);
                }
            }

            // default values
            if (port == 0) {
                port = 8080;
            }
            if (serverName == null) {
                serverName = "DefaultServer";
            }

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + configFilePath);
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
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
        String message = "CLIENT_LIST:" + clientList;

        for (ConnectedClients client : getUserMap().values()) {
            PrintWriter out = client.out();
            try{
                out.println(message);
            } catch (Exception e) {
                System.err.println("Failed to send broadcast message to port: " + client.port() + ": " + e.getMessage());
            }
        }
    }

    protected void sendBannedPhrases(PrintWriter out) {
        String message = "BANNED_PHRASES:" + bannedPhrasesString;

        try {
            //send the message to PrintWriter of every connected client
            out.println(message);
        } catch (Exception e) {
            System.err.println("Failed to send banned phrases" + e.getMessage());
        }
    }

    protected void broadcastMessage(String username, String message) {

        String formattedMessage = "BROADCAST:" + username + ": " + message;

        for (ConnectedClients client : getUserMap().values()) {
            try {
                //send the message to PrintWriter of every connected client
                client.out().println(formattedMessage);
            } catch (Exception e) {
                System.err.println("Failed to send message to " + client.port() + ": " + e.getMessage());
            }
        }
    }

    void sendToSpecificUsers(String username, String message) {
        String[] parts = message.split(":", 2);
        Set<String> targetUsers = Set.of(parts[0].split(","));
        String content = parts[1];

        String formattedMessage = "SEND_TO:" + String.join(",", targetUsers) + ":" + username + ":" + content;

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
        String[] parts = message.split(":", 2);
        Set<String> excludedUsersSet = Set.of(parts[0].split(","));

        String content = parts[1];
        String formattedMessage = "EXCLUDE:"  + String.join(",", excludedUsersSet) + ":" + username + ":" + content;

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










