package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    // use ServerSocket to listen for connections: for each client, create a new thread to manage communication

    public static void main(String[] args) {
        try {
            Server server = new Server("src/server/server.properties");
            ServerSocket serverSocket = new ServerSocket(server.getPort());
            System.out.println(server.getServerName() + " started on port " + server.getPort());
            System.out.println("Banned phrases: " + server.getBannedPhrases());


            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket, server)).start();
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
