package client;

import client.messages.*;
import config.ConfigLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.Set;

public final class Client {
    private final static String MessagePartsDelimiter = ":";
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final CommandExecutor commandExecutor;
    private Optional<String> currentUsername = Optional.empty();
    private boolean authorized;

    //use Socket to connect to the server
    public Client(String pathToConfig, CommandExecutor commandExecutor) throws IOException {
        ConfigLoader configLoader = new ConfigLoader();
        configLoader.loadConfig(pathToConfig);

        this.socket = new Socket(configLoader.getHost(), configLoader.getPort());
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.commandExecutor = commandExecutor;
    }

    //disconnecting
    public void disconnectFromServer() {
        try {
            sendToServer("DISCONNECT");
            socket.close();
            authorized = false;
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }



    // separate thread for reading server messages
    void startReadingFromServer() {
        // separate thread for receiving messages
        new Thread(() -> {
            try {
                while (isSocketValid()) {
                    Optional<String> serverMessage = getFromServer();

                    serverMessage.ifPresentOrElse(
                            message -> {
                            try {
                                commandExecutor.dispatchCommand(MessageParser.parseMessage(message));
                            } catch (IllegalArgumentException e) {
                                System.err.println("Error processing server message: " + e.getMessage());
                            }
                        },
                        () -> System.err.println("Server returned an empty message")
                    );
                }
            } finally {
                disconnectFromServer();
            }
        }).start();
    }

    //request username approval by server
    boolean processUsername(String username) throws IOException {
        sendToServer(username);
        System.out.println("Username: " + username);
        Optional<String> response = getFromServer();
        System.out.println("Response: " + response);

        if (response.isEmpty()) {
            System.err.println("No response from server while processing username.");
            return false;
        }

        String content = response.get();
        System.out.println(content);
        try {
            MessageBase messageBase = MessageParser.parseMessage(content);
            commandExecutor.dispatchCommand(messageBase);

            if (messageBase instanceof ServerConnectedMessage serverConnectedMessage) {
                setAuthorized(true);
                currentUsername = Optional.of(serverConnectedMessage.getUsername());

                return true;
            }
        } catch (IllegalArgumentException e) {
            // Handle unexpected message content or parsing issues
            System.err.println("Error parsing server message: " + e.getMessage());
        }

        // if messageBase instance of ErrorMessage -> handled by CommandExecutor, username not approved yet
        return false;
    }

    //check for authorisation (username approval by server)
    boolean isAuthorized() {
        if (!authorized) {
            System.err.println("Not connected to the server. Unable to send message.");
        }

        return authorized;
    }

    private boolean isSocketValid() {
        return socket != null && !socket.isClosed();
    }

    // sendBroadcastMessage, sendMessageToSpecified, sendMessageWithExclusion can not be sent before authorising
    public void sendBroadcastMessage(String message) {
        if (!isAuthorized()) {
            return;
        }

        var messageString = String.join(MessagePartsDelimiter, MessageTypes.Broadcast, message);
        sendToServer(messageString);
    }

    public void sendMessageToSpecified(Set<String> recipients, String message) {
        if (!isAuthorized()) {
            return;
        }

        recipients.add(currentUsername.get());
        var recipientsString = String.join(",", recipients);
        var messageString = String.join(MessagePartsDelimiter, MessageTypes.SentToSpecific, recipientsString, message);

        sendToServer(messageString);
    }

    public void sendMessageWithExclusion(Set<String> excludedUsers, String message) {
        if (!isAuthorized()) {
            return;
        }

        var exludedUsersString = String.join(",", excludedUsers);
        var messageString = String.join(MessagePartsDelimiter, MessageTypes.ExcludeRecipients, exludedUsersString, message);

        sendToServer(messageString);
    }

    //query before authorisation
    public void queryBannedPhrases() {
        sendToServer("QUERY_BANNED");
    }

    //helper functions
    private void sendToServer(String str) {
        if (str != null && !str.isEmpty()) {
            writer.println(str);
        }
    }

    private Optional<String> getFromServer() {
        try {
            String message = reader.readLine();

            if (message == null) {
                return Optional.empty();
            }
            return Optional.of(message);
        } catch (IOException e) {
            System.err.println("Error getting from server: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean getAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}

