package server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    protected PrintWriter out;
    public BufferedReader in;
    private String username;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            boolean validUsername = false;
            while (!validUsername) {
                // read username
                username = in.readLine();

                String error = UsernameValidator.getError(username, server);
                if (error == null) validUsername = true;
                else out.println(error);
            }

            // register the client
            ConnectedClients newClient = new ConnectedClients(socket.getPort(), out);
            server.addUser(username, newClient);


            // confirm connection
            out.println("OK");
            server.broadcastClientList();
            server.sendBannedPhrases(out, server.getPort());

            // read and broadcast messages from this client
            String message;
            while ((message = in.readLine()) != null) {
                boolean validMessage = false;

                while (!validMessage) {
                    if (message.trim().isEmpty()) {
                        out.println("ERROR: Message cannot be empty..");
                        message = in.readLine();
                        continue;
                    }

                    if (server.getBannedPhrases().stream().anyMatch(message::contains)) {
                        out.println("ERROR: Message contains banned phrases!");
                        message = in.readLine();
                        continue;
                    } else {
                        validMessage = true;
                    }
                }

                if (message.startsWith("SEND_TO:")) {
                    System.out.println("SEND_TO: " + message);
                    server.sendToSpecificUsers(username, message.substring(8));
                } else if (message.startsWith("EXCLUDE:")) {
                    server.excludeSpecificUsers(username, message.substring(8));
                } else if (message.equals("QUERY_BANNED")) {
                    server.sendBannedPhrases(out, server.getPort());
                } else {
                    server.broadcastMessage(username, message);
                }
            }
        } catch (IOException e) {
            System.err.println("Connection error with client (" + username + "): " + e.getMessage());
        } finally {
            disconnectClient();
        }
    }

    private void disconnectClient() {
        if (username != null) {
            server.removeUser(username);
            server.broadcastClientList();
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }
}



