package server;

import java.io.*;
import java.net.Socket;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Server server;
    private String username;
    protected PrintWriter out;
    public BufferedReader in;




    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            boolean validUsername = false;
            while (!validUsername) {
                // read username
                username = serverGet();
                if (username == null) {
                    throw new IOException("Client disconnected during username validation.");
                }

                String error = UsernameValidator.getError(username, server);
                if (error == null) validUsername = true;
                else serverSend(error);
            }

            // register the client
            ConnectedClients newClient = new ConnectedClients(clientSocket.getPort(), out);
            server.addUser(username, newClient);


            // confirm connection
            serverSend("OK:" + username + ":" + server.getBannedPhrases());
            server.broadcastClientList();

            // read and broadcast messages from this client
            String message;
            while ((message = in.readLine()) != null) {
                boolean validMessage = false;

                while (!validMessage) {

                    if (Arrays.stream(server.getBannedPhrases().split(",\\s*")).anyMatch(message.toLowerCase()::contains)) {
                        serverSend("ERROR: Message contains banned phrases!");
                        message = serverGet();
                        if (message == null) {
                            System.err.println("Client disconnected during message validation.");
                            break;
                        }
                    }
                    else if (message.toLowerCase().contains("good morning")) {
                        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
                        LocalTime startMorning = LocalTime.of(6, 0);
                        LocalTime endMorning = LocalTime.of(12, 0);
                        LocalTime now = LocalTime.now();

                        boolean isMorning = now.isAfter(startMorning) && now.isBefore(endMorning);

                        if (dayOfWeek == DayOfWeek.MONDAY && isMorning) {
                            serverSend("ERROR: Mornings cannot be Good before 12 PM on Mondays!");

                            message = serverGet();
                            if (message == null) {
                                System.err.println("Client disconnected while retrying.");
                                break;
                            }
                        } else {
                            validMessage = true;
                        }
                    }
                    else {
                        validMessage = true;
                    }
                }

                if (message != null) {
                    if (message.startsWith("SEND_TO:")) {
                        server.sendToSpecificUsers(username, message.substring(8));
                    } else if (message.startsWith("EXCLUDE:")) {
                        server.excludeSpecificUsers(username, message.substring(8));
                    } else if (message.equals("QUERY_BANNED")) {
                        server.sendBannedPhrases(out);
                    } else {
                        server.broadcastMessage(username, message);
                    }
                } else {
                    System.err.println("Client disconnected.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Connection error with client (" + username + "): " + e.getMessage());
        } finally {
            disconnectClient();
        }
    }

    private void disconnectClient() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            clientSocket.close();
            if (username != null) {
                server.removeUser(username);
                server.broadcastClientList();
            }
            System.out.println("Client disconnected.");
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }
    private void serverSend(String message) {
        out.println(message);
    }
    private String serverGet() throws IOException {
        return in.readLine();
    }
}



