package client;


//Use a background thread for receiving messages to avoid blocking the GUI
// use SwingUtilities.invokeLater to update the UI safely

//Fields: sender, recipients, content, and timestamp.

//Features:
//Handle system notifications like new client connections or disconnections.

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class GUIManager {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private JFrame frame;
    private JList<String> messageList;
    private DefaultListModel<String> messageListModel;
    private JTextField inputField;
    private JButton sendButton;
    private JList<String> clientList;
    private DefaultListModel<String> clientListModel;
    private JCheckBox excludeModeCheckBox;
    private String currentUsername;
    private Client client;

    public GUIManager() throws IOException {
        setupGUI();
        try {
            client = new Client("localhost", 9005, this, new CommandDispatcher(this));
        } catch (IOException e) {
            showAlertWindow("Unable to connect to server" + e.getMessage(), "Error");
        }
    }

    private void setupGUI() {
        Set<String> selectedUsers = new HashSet<>();

        frame = new JFrame("BibaChat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        //exclude checkbox
        excludeModeCheckBox = new JCheckBox("Exclude Mode");

        // button to clear selected users
        JButton clearSelectionButton = new JButton("Unselect all");
        clearSelectionButton.addActionListener(e -> {
            clientList.clearSelection();
            selectedUsers.clear();
        });

        // client list
        clientListModel = new DefaultListModel<>();
        clientList = new JList<>(clientListModel);
        clientList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        clientList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedUsers.clear();
                selectedUsers.addAll(clientList.getSelectedValuesList());
                System.out.println("Selected users: " + selectedUsers);
            }
        });

        //client pane
        JScrollPane clientScrollPane = new JScrollPane(clientList);
        clientScrollPane.setPreferredSize(new Dimension(150, 0));
        JPanel clientPanel = new JPanel(new BorderLayout());
        JLabel clientTitle = new JLabel("Connected Users:", JLabel.CENTER);
        clientTitle.setFont(new Font("Arial", Font.BOLD, 14));
        clientPanel.add(clientTitle, BorderLayout.NORTH);
        clientPanel.add(clientScrollPane, BorderLayout.CENTER);
        clientPanel.setPreferredSize(new Dimension(150, 0));
        clientPanel.add(clearSelectionButton, BorderLayout.SOUTH);

        //query banned button
        JButton queryBannedPhrasesButton = new JButton("Banned Phrases");
        queryBannedPhrasesButton.addActionListener(e -> client.queryBannedPhrases());

        // message box
        messageListModel = new DefaultListModel<>();
        messageList = new JList<>(messageListModel);
        messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane messageScrollPane = new JScrollPane(messageList);

        // input field
        inputField = new JTextField();
        inputField.setToolTipText("Type your message...");
        inputField.addActionListener(e -> handleSendingMessage(selectedUsers));

        // send button
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> handleSendingMessage(selectedUsers));

        // input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(queryBannedPhrasesButton, BorderLayout.WEST);
        inputPanel.add(excludeModeCheckBox, BorderLayout.NORTH);

        // layout
        frame.add(messageScrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.add(clientPanel, BorderLayout.WEST);
        frame.setVisible(true);
    }

    public String promptUsername() {
        //username is null before entered
        String username = null;

        while (username == null || username.trim().isEmpty()) {
            username = JOptionPane.showInputDialog(frame, "Enter your username:", "Username", JOptionPane.PLAIN_MESSAGE);
        }

        currentUsername = username.trim();
        return currentUsername;
    }
    private void handleSendingMessage(Set<String> selectedUsers) {
        String message = inputField.getText();

        if (!message.isEmpty() && client != null && client.isConnected()) {
            if (selectedUsers.isEmpty()) {
                // Broadcast to all
                client.sendBroadcastMessage(message);

            } else {
                String userList = String.join(",", selectedUsers);

                if (excludeModeCheckBox.isSelected()) {
                    // Exclude selected users
                    client.sendMessageWithExclusion(userList, getCurrentUsername(), message);
                } else {
                    // Send to specific users
                    client.sendMessageToSpecified(userList, message);
                }
            }
            inputField.setText("");
        }
    }

    public void updateClientList(String clients) {
        String[] clientArray = clients.split(",");

        SwingUtilities.invokeLater(() -> {
            clientListModel.clear();
            for (String client : clientArray) {
                clientListModel.addElement(client);
            }
        });
    }

    public void addMessageToChat(String sender, String message, String time) {
        SwingUtilities.invokeLater(() -> messageListModel.addElement(sender + ": " + message + "   [" + time+ "]"));
    }

    public void showAlertWindow(String message, String title) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void setChatTitle(String username) {
        frame.setTitle("BibaChat: " + username);
    }

    public void showInstructions(String bannedPhrases) {
        showAlertWindow("Please do not use following phrases: " + bannedPhrases + ". \n" +
                        "To send message exclusively just select the desired user/users before sending. \n" +
                "To skip messaging users you don’t like, just select them under Exclude Mode.",
                "Intrstuctions");
    }

    public String getCurrentUsername() {
        return currentUsername;
    }
}
