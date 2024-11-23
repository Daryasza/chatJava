package client;

import client.messages.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

public final class Client {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final CommandExecutor commandExecutor;
    private boolean authorized;
    boolean usernameAccepted = false;

    //use Socket to connect to the server
    public Client(String host, int port, CommandExecutor commandExecutor) throws IOException {
        this.socket = new Socket(host, port);
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
                while (socket != null && !socket.isClosed()) {
                    Optional<String> serverMessage = getFromServer();

                    if (serverMessage.isPresent()) {
                        commandExecutor.dispatchCommand(MessageParser.parseMessage(serverMessage.get()));
                    } else {
                        System.err.println("Server returned an empty message");
                    }
                }
            } finally {
                authorized = false;
            }
        }).start();
    }

    //request username approval by server
    boolean processUsername(String username) throws IOException {

        sendToServer(username);
        Optional<String> response = getFromServer();

        if (response.isPresent()) {

            String content = response.get();
            MessageBase messageBase = MessageParser.parseMessage(content);
            commandExecutor.dispatchCommand(messageBase);

            if (messageBase instanceof ServerConnectedMessage) {
                setAuthorized(true);
                return true;
            }

        }
        // if messageBase instance of ErrorMessage -> handled by CommandExecutor, username not approved yet
        return false;
    }

//     check for authorisation (username approval by server)
    boolean isAuthorized() {
        if (!authorized) {
            System.err.println("Not connected to the server. Unable to send message.");
        }

        return authorized;
    }

    // sendBroadcastMessage, sendMessageToSpecified, sendMessageWithExclusion can not be sent before authorising
    public void sendBroadcastMessage(String message) {
        if (!isAuthorized()) {
            return;
        }
        sendToServer("BROADCAST:" + message);
    }

    public void sendMessageToSpecified(String recipients, String message) {
        if (!isAuthorized()) {
            return;
        }
        sendToServer("SEND_TO:" + recipients + ":" + message);
    }

    public void sendMessageWithExclusion(String excludedUsers, String message) {
        if (!isAuthorized()) {
            return;
        }
        sendToServer("EXCLUDE:" + excludedUsers + ":" + message);
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
        String message = null;
        try {
            message = reader.readLine();
        } catch (IOException e) {
            System.err.println("Error getting from server: " + e.getMessage());
        }
        return Optional.ofNullable(message);
    }

    public boolean getAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}

