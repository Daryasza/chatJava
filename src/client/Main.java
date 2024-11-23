//package client;
//
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import java.io.IOException;
//import java.util.HashSet;
//import java.util.Set;
//
//
////TODO move client methods from guiManager
//// load port and ip from .txt file
//
//
//public class Main {
//    public static void main(String[] args) throws IOException {
//        GUIManager guiManager = new GUIManager();
//
//        try {
//            CommandExecutor commandExecutor = new CommandExecutor(guiManager);
//            Client client = new Client("localhost", 9005, commandExecutor);
//
//
//            guiManager.sendButton.addActionListener(e -> handleSendingMessage(guiManager.selectedUsers, guiManager, client));
//            guiManager.inputField.addActionListener(e -> handleSendingMessage(guiManager.selectedUsers, guiManager, client));
//            guiManager.queryBannedPhrasesButton.addActionListener(e -> client.queryBannedPhrases());
//
//            guiManager.frame.addWindowListener(new WindowAdapter() {
//                @Override
//                public void windowClosing(WindowEvent e) {
//                    client.disconnectFromServer();
//                }
//            });
//
//            //query list of banned phrases before sending a username
////            client.queryBannedPhrases();
//            authoriseAtServer(guiManager, client);
//
//
//        } catch (IOException e) {
//            guiManager.showAlertWindow("Unable to connect to server" + e.getMessage(), "Error");
//        }
//    }
//
//    public static void authoriseAtServer(GUIManager guiManager, Client client) throws IOException {
//        try {
//            while (!client.usernameAccepted) {
//                // prompt for username
//                String username = guiManager.promptUsername();
//                client.usernameAccepted = client.processUsername(username);
//            }
//
//        } catch (IOException e) {
//            client.setAuthorized(false);
//            guiManager.showAlertWindow("Failed to connect to server", "Error");
//            throw new IOException("Failed to connect.", e);
//        }
//    }
//
//
//    private static void handleSendingMessage(Set<String> selectedUsers, GUIManager guiManager, Client client) {
//        String message = guiManager.inputField.getText();
//
//        if (!message.isEmpty() && client != null && client.getAuthorized()) {
//            if (selectedUsers.isEmpty()) {
//                // Broadcast to all
//                client.sendBroadcastMessage(message);
//
//            } else {
//                String userList = String.join(",", selectedUsers);
//
//                if (guiManager.excludeModeCheckBox.isSelected()) {
//                    // Exclude selected users
//                    client.sendMessageWithExclusion(userList, message);
//                } else {
//                    // Send to specific users
//                    client.sendMessageToSpecified(userList, message);
//                }
//            }
//            guiManager.inputField.setText("");
//        }
//    }
//
//
//}

package client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        GUIManager guiManager = new GUIManager();
        Client client;

        try {
            CommandExecutor commandExecutor = new CommandExecutor(guiManager);
            client = new Client("localhost", 9005, commandExecutor);

            guiManager.sendButton.addActionListener(e -> handleSendingMessage(guiManager.selectedUsers, guiManager, client));
            guiManager.inputField.addActionListener(e -> handleSendingMessage(guiManager.selectedUsers, guiManager, client));
            guiManager.queryBannedPhrasesButton.addActionListener(e -> {
                if (client != null && client.getAuthorized()) {
                    client.queryBannedPhrases();
                } else {
                    guiManager.showAlertWindow("Client is not authorized or not connected to the server.", "Error");
                }
            });

            guiManager.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (client != null) {
                        client.disconnectFromServer();
                    }
                }
            });

            // Query list of banned phrases before sending a username
            authoriseAtServer(guiManager, client);

        } catch (IOException e) {
            guiManager.showAlertWindow("Unable to connect to server: " + e.getMessage(), "Error");
        }
    }

    public static void authoriseAtServer(GUIManager guiManager, Client client) {
        try {
            while (!client.getAuthorized()) {
                // Prompt for username
                String username = guiManager.promptUsername();

                // Process the username
                boolean success = client.processUsername(username);
                System.out.println(success ? "Authorised" : "Unauthorised");

                if (success) {
                    client.setAuthorized(true);
                } else {
                    guiManager.showAlertWindow("Failed to authorize username. Please try again.", "Authorization Error");
                }
            }
            client.startReadingFromServer();

        } catch (IOException e) {
            client.setAuthorized(false);
            guiManager.showAlertWindow("Failed to connect to server: " + e.getMessage(), "Error");
        }
    }

    private static void handleSendingMessage(Set<String> selectedUsers, GUIManager guiManager, Client client) {
        // Ensure selectedUsers is initialized
        if (selectedUsers == null) {
            selectedUsers = new HashSet<>();
        }

        String message = guiManager.inputField.getText().trim();

        if (!message.isEmpty() && client != null && client.getAuthorized()) {
            if (selectedUsers.isEmpty()) {
                // Broadcast to all
                client.sendBroadcastMessage(message);
            } else {
                String userList = String.join(",", selectedUsers);
                if (guiManager.excludeModeCheckBox.isSelected()) {
                    // Exclude selected users
                    client.sendMessageWithExclusion(userList, message);
                } else {
                    // Send to specific users
                    client.sendMessageToSpecified(userList, message);
                }
            }

            // Clear the input field after sending
            guiManager.inputField.setText("");
        }
    }
}
