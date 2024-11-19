package client;

// TODO

// give instructions on how to use the messaging facility
// give possibility to exclude some users from broadcast or send just to specified person/persons using their username
// give possibility to query the server for the list of banned phrases
// force to rewrite messages containing banned phrases

import client.messages.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean connected;


    //use Socket to connect to the server
    public Client(String host, int port, GUIManager guiManager) throws IOException {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Prompt for username and send it to the server and if no connection - throw an error
            boolean usernameAccepted = false;

            while (!usernameAccepted) {
                // Prompt for username using GUIManager
                String username = guiManager.promptUsername();
                out.println(username);

                // Handle server response
                String res = in.readLine();

                if (res == null) {
                    guiManager.showAlertWindow("Server disconnected unexpectedly.", "Error");
                    throw new IOException("Server disconnected.");
                }

                switch (res) {
                    case "OK" -> {
                        usernameAccepted = true;
                        connected = true;
                        guiManager.setTitle();
                    }
                    case "ERROR: Username already taken." ->
                            guiManager.showAlertWindow("Username already taken. Please choose a different username.", "Error");
                    case "ERROR: Invalid username." ->
                            guiManager.showAlertWindow("Invalid username. Please enter a valid username.", "Error");
                    default -> guiManager.showAlertWindow("Unexpected server response: " + res, "Error");
                }
            }
        } catch (IOException e) {
            connected = false;
            throw new IOException("Failed to connect.", e);
        }
    }


    // send messages to the server using the main thread
    public void sendBroadcastMessage(String message) {
        if (connected && out != null) {
            try {
                out.println(message);
            } catch (Exception e) {
                System.err.println("Error sending message: " + e.getMessage());
            }
        }
    }

    public void sendMessageWithExclusion(String excludedUsers, String sender, String message) {
        if (connected && out != null) {
            try {
                out.println("EXCLUDE:" + excludedUsers + ":" + message);
            } catch (Exception e) {
                System.err.println("Error sending exclusion message: " + e.getMessage());
            }
        } else {
            System.err.println("Not connected to the server. Unable to send message.");
        }
    }

    public void sendMessageToSpecified(String recipients, String sender, String message) {
        if (connected && out != null) {
            try {
                out.println("SEND_TO:" + recipients + ":" + message);
            } catch (Exception e) {
                System.err.println("Error sending message to specified users: " + e.getMessage());
            }
        } else {
            System.err.println("Not connected to the server. Unable to send message.");
        }
    }

    public void queryBannedPhrases() {
        if (connected && out != null) {
            try {
                out.println("QUERY_BANNED");
            } catch (Exception e) {
                System.err.println("Error querying banned phrases: " + e.getMessage());
            }
        } else {
            System.err.println("Not connected to the server. Unable to query banned phrases.");
        }
    }

    public Message parseMessage(String serverMessage) {
        System.out.println(serverMessage);
        if (serverMessage.startsWith("CHAT:")) {
            // Expected format: "CHAT:sender:content"
            String[] parts = serverMessage.substring(5).split(":", 2);
            return new ChatMessage(parts[0], parts[1]);
        }
        if (serverMessage.startsWith("SEND_TO:")) {
            // Format: "SEND_TO:recipients:sender:content"
            String[] parts = serverMessage.substring(8).split(":", 3);

            if (parts.length < 3) {
                throw new IllegalArgumentException("Invalid SEND_TO message format: " + serverMessage);
            }

            String recipients = parts[0];
            String sender = parts[1];
            String content = parts[2];

            return new SentToSpecificMessage(recipients, sender, content);

        } else if (serverMessage.startsWith("EXCLUDE:")) {
            // Format: "EXCLUDE:excludedUsers:sender:content"
            String[] parts = serverMessage.substring(8).split(":", 3);
            System.out.println("Parsed parts: " + Arrays.toString(parts));

            if (parts.length < 3) {
                throw new IllegalArgumentException("Invalid EXCLUDE message format: " + serverMessage);
            }
            return new ExcludeRecipientsMessage(parts[0], parts[1], parts[2]);

        }
        else if (serverMessage.startsWith("SERVER:")) {
            // Expected format: "SERVER:content"
            return new ChatMessage(null, serverMessage.substring(7));
        }
        else if (serverMessage.startsWith("CLIENT_LIST:")) {
            String[] clients = serverMessage.substring(12).split(",");
            return new UserListMessage(clients);
        }
        else if (serverMessage.startsWith("BANNED_PHRASES:")) {
            String[] bannedPhr = serverMessage.substring(15).split(",");
            return new BannedPhrasesMessage(bannedPhr);
        }
        else if (serverMessage.startsWith("ERROR:")) {
            return new ErrorMessage(serverMessage.substring(6));
        }
        throw new IllegalArgumentException("Invalid server message: " + serverMessage);
    }

    // separate thread for receiving messages
    public void receiveMessages(CommandDispatcher dispatcher) {
        new Thread(() -> {
            try {
                String serverMessage;
                while (socket != null && !socket.isClosed() && (serverMessage = in.readLine()) != null) {
                        Message message = parseMessage(serverMessage);
                        dispatcher.dispatchCommand(message);
                }
            } catch (IOException e) {
                dispatcher.dispatchCommand(new ErrorMessage(e.getMessage()));
            } finally {
                connected = false;
            }
        }).start();
    }

    protected boolean isConnected() {
        return connected;
    }
}

