package client;

import javax.swing.*;
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
            client = new Client("src/config/server.txt", commandExecutor);

            guiManager.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    client.disconnectFromServer();
                }
            });

            client.getFromServer().ifPresentOrElse(
                    message -> commandExecutor.dispatchCommand(MessageParser.parseMessage(message)),
                    () -> System.out.println("Command not recognised")
            );

            authoriseAtServer(guiManager, client);
            client.startReadingFromServer();

            guiManager.sendButton.addActionListener(e -> handleSendingMessage(guiManager.selectedUsers, guiManager, client));
            guiManager.inputField.addActionListener(e -> handleSendingMessage(guiManager.selectedUsers, guiManager, client));
            guiManager.queryBannedPhrasesButton.addActionListener(e -> {
                if (client.getAuthorized()) {
                    client.queryBannedPhrases();
                    return;
                }

                guiManager.showAlertWindow("Client is not authorized or not connected to the server.", "Error");
            });

        } catch (IOException e) {
            guiManager.showAlertWindow("Unable to connect to server: " + e.getMessage(), "Error");
        }
    }

    public static void authoriseAtServer(GUIManager guiManager, Client client) {
        try {
            while (!client.getAuthorized()) {
                // prompt for username
                String username = null;
                while (username == null || username.trim().isEmpty()) {
                    username = JOptionPane.showInputDialog(guiManager.frame, "Enter your username:", "Username", JOptionPane.PLAIN_MESSAGE);
                    //if user closed the window
                    if (username == null) {
                        client.disconnectFromServer();
                        System.exit(0);
                    }
                }

                boolean success = client.processUsername(username.trim());
                if (success) {
                    client.setAuthorized(true);
                    return;
                }

                guiManager.showAlertWindow("Failed to authorize username. Please try again.", "Authorization Error");
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

        if (message.isEmpty() || message.equals("Type your message here...")) {
            return;
        }

        if (client != null && client.getAuthorized()) {
            if (selectedUsers.isEmpty()) {
                // broadcast message
                client.sendBroadcastMessage(message);
            } else {
                if (guiManager.excludeModeCheckBox.isSelected()) {
                    // exclude selected users
                    client.sendMessageWithExclusion(selectedUsers, message);
                } else {
                    // send to selected users
                    client.sendMessageToSpecified(selectedUsers, message);
                }
            }
            guiManager.inputField.setText("");
        }
    }
}
