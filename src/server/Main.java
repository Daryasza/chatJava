package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {

        // ServerSocket to listen for connections: for each client a new thread created
        try {
            Server server = new Server("src/server/server.properties");
            try(ServerSocket serverSocket = new ServerSocket(server.getPort())) {

                System.out.println(server.getServerName() + " started on port " + server.getPort());
                System.out.println("Banned Phrases: " + server.getBannedPhrases());

                boolean running = true;

                while (running) {
                    try  {
                        Socket clientSocket = serverSocket.accept();
                        ClientHandler clientHandler = new ClientHandler(clientSocket, server);
                        new Thread(clientHandler).start();
                    } catch (IOException e) {
                        System.out.println("Error when accepting client connection " + e.getMessage());
                        running = false;
                    }

                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            System.out.println("Server is shutting down");
        }
    }
}
