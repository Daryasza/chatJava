package server;

import server.messageFilters.BannedPhrasesMessageFilter;
import server.messageFilters.GoodMorningMessageFilter;
import server.messageFilters.MessageFilter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Server server = new Server("src/config/server.txt");
        System.out.println(server.getServerName() + " started on port " + server.getPort());
        System.out.println("Banned Phrases: " + server.bannedPhrasesString);

        // try with resources - automatically calls serverSocket.close() when the try block exits (serverSocket implements AutoCloseable)
        try (ServerSocket serverSocket = new ServerSocket(server.getPort())) {
            // loop for accepting client connections
            while (true) {
                try {
                    //blocking call - NEXT LINES WILL NOT be executed until clientSocket is accepted - otherwise catch block
                    Socket clientSocket = serverSocket.accept();

                    ArrayList<MessageFilter> filters = new ArrayList<>();
                    filters.add(new BannedPhrasesMessageFilter(server.getBannedPhrases()));
                    filters.add(new GoodMorningMessageFilter());

                    try {
                        //if error reader clientHandler instantiation - CLIENTSOCKET IS VALID AND OPEN
                        ClientHandler clientHandler = new ClientHandler(clientSocket, server, filters);
                        //for each client a new thread created
                        new Thread(clientHandler).start();
                    } catch (Exception e) {
                        System.err.println("Error creating or starting ClientHandler: " + e.getMessage());
                        if (!clientSocket.isClosed()) {
                            clientSocket.close();
                        }
                    }

                } catch (IOException e) {
                    System.err.println("Error when accepting client connection: " + e.getMessage());
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Error creating ServerSocket: " + e.getMessage());
        } finally {
            System.out.println("Server is shutting down...");
        }
    }
}
