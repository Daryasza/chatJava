package client;

// TODO
// give instructions on how to use the messaging facility

import client.messages.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private GUIManager guiManager;
    private CommandDispatcher cd;
    private boolean connected;
    boolean usernameAccepted = false;

    //use Socket to connect to the server
    public Client(String host, int port, GUIManager guiManager, CommandDispatcher cd) throws IOException {
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.guiManager = guiManager;
        this.cd = cd;

        connectToServer();
    }

    public void connectToServer() throws IOException {
        try {
            while (!usernameAccepted) {
                // prompt for username
                String username = guiManager.promptUsername();
                usernameAccepted = processUsername(username, cd);
            }
            startGettingMessages(cd);

        } catch (IOException e) {
            connected = false;
            guiManager.showAlertWindow("Failed to connect to server", "Error");
            throw new IOException("Failed to connect.", e);
        }
    }

    private boolean processUsername(String username, CommandDispatcher cd) throws IOException {
        sendToServer(username);

        String response = getFromServer();

        if (response == null) {
            guiManager.showAlertWindow("Server disconnected unexpectedly", "Error");
            return false;
        }

        Message message = MessageParser.parseMessage(response);
        cd.dispatchCommand(message);

        if (message instanceof ServerConnectedMessage) {
            connected = true;
            return true;
        }
        return false;
    }

    // send messages to the server using the main thread
    public void sendBroadcastMessage(String message) {
        if (connected) {
                sendToServer(message);
        } else {
            System.err.println("Not connected to the server. Unable to send message.");
        }
    }

    public void sendMessageWithExclusion(String excludedUsers, String sender, String message) {
        if (connected) {
            try {
                sendToServer("EXCLUDE:" + excludedUsers + ":" + message);
            } catch (Exception e) {
                System.err.println("Error sending exclusion message: " + e.getMessage());
            }
        } else {
            System.err.println("Not connected to the server. Unable to send message.");
        }
    }

    public void sendMessageToSpecified(String recipients, String message) {
        if (connected) {
            sendToServer("SEND_TO:" + recipients + ":" + message);
        } else {
            System.err.println("Not connected to the server. Unable to send message.");
        }
    }

    public void queryBannedPhrases() {
        if (connected) {
            sendToServer("QUERY_BANNED");
        } else {
            System.err.println("Not connected to the server. Unable to query banned phrases.");
        }
    }



    // separate thread for receiving messages
    public void startGettingMessages(CommandDispatcher dispatcher) {
        new Thread(() -> {
            try {
                while (socket != null && !socket.isClosed()) {
                    String serverMessage = getFromServer();
                    if (serverMessage != null && !serverMessage.isEmpty()) {
                        dispatcher.dispatchCommand(MessageParser.parseMessage(serverMessage));
                    }
                }
            } finally {
                connected = false;
            }
        }).start();
    }

    protected boolean isConnected() {
        return connected;
    }

    protected void sendToServer(String str) {
        out.println(str);
    }

    protected String getFromServer() {
        String res = "";
        try {
            res = in.readLine();
        } catch (IOException e) {
            System.err.println("Error getting from server: " + e.getMessage());
        }
        return res;
    }
}

