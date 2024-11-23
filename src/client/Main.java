
////TODO move client methods from guiManager


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

            guiManager.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (client != null) {
                        client.disconnectFromServer();
                    }
                }
            });

            authoriseAtServer(guiManager, client);
            client.startReadingFromServer();

            guiManager.sendButton.addActionListener(e -> handleSendingMessage(guiManager.selectedUsers, guiManager, client));
            guiManager.inputField.addActionListener(e -> handleSendingMessage(guiManager.selectedUsers, guiManager, client));
            guiManager.queryBannedPhrasesButton.addActionListener(e -> {
                if (client != null && client.getAuthorized()) {
                    client.queryBannedPhrases();
                } else {
                    guiManager.showAlertWindow("Client is not authorized or not connected to the server.", "Error");
                }
            });

        } catch (IOException e) {
            guiManager.showAlertWindow("Unable to connect to server: " + e.getMessage(), "Error");
        }
    }

    public static void authoriseAtServer(GUIManager guiManager, Client client) {
        try {
            while (!client.getAuthorized()) {
                // prompt for username
                String username = guiManager.promptUsername();

                boolean success = client.processUsername(username);
                System.out.println(success ? "Authorised" : "Unauthorised");

                if (success) {
                    client.setAuthorized(true);
                } else {
                    guiManager.showAlertWindow("Failed to authorize username. Please try again.", "Authorization Error");
                }
            }

        } catch (IOException e) {
            client.setAuthorized(false);
            guiManager.showAlertWindow("Failed to connect to server: " + e.getMessage(), "Error");
        }
    }

    private static void handleSendingMessage(Set<String> selectedUsers, GUIManager guiManager, Client client) {
        if (selectedUsers == null) {
            selectedUsers = new HashSet<>();
        }

        String message = guiManager.inputField.getText().trim();

        if (!message.isEmpty() && client != null && client.getAuthorized()) {
            if (selectedUsers.isEmpty()) {
                // broadcast message
                client.sendBroadcastMessage(message);
            } else {
                String userList = String.join(",", selectedUsers);
                if (guiManager.excludeModeCheckBox.isSelected()) {
                    // exclude selected users
                    client.sendMessageWithExclusion(userList, message);
                } else {
                    // send to selected users
                    client.sendMessageToSpecified(userList, message);
                }
            }
            guiManager.inputField.setText("");
        }
    }
}
