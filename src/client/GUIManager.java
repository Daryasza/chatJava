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


    public GUIManager() {
        setupGUI();
        connectToServer();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUIManager());
    }

    private void setupGUI() {

        // Main Frame
        frame = new JFrame("BibaChat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        excludeModeCheckBox = new JCheckBox("Exclude Mode");

        // Client list
        clientListModel = new DefaultListModel<>();
        clientList = new JList<>(clientListModel);
        clientList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        Set<String> selectedUsers = new HashSet<>();

        clientList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedUsers.clear();
                selectedUsers.addAll(clientList.getSelectedValuesList());
                System.out.println("Selected users: " + selectedUsers);
            }
        });

        JScrollPane clientScrollPane = new JScrollPane(clientList);
        clientScrollPane.setPreferredSize(new Dimension(150, 0));

        // Add title to the client list
        JPanel clientPanel = new JPanel(new BorderLayout());
        JLabel clientTitle = new JLabel("Connected Users:", JLabel.CENTER);
        clientTitle.setFont(new Font("Arial", Font.BOLD, 14));
        clientPanel.add(clientTitle, BorderLayout.NORTH);
        clientPanel.add(clientScrollPane, BorderLayout.CENTER);
        clientPanel.setPreferredSize(new Dimension(150, 0));

        // Add a button to clear selected users
        JButton clearSelectionButton = new JButton("Unselect all");

        clearSelectionButton.addActionListener(e -> {
            clientList.clearSelection();
            selectedUsers.clear();
        });

        clientPanel.add(clearSelectionButton, BorderLayout.SOUTH);


        JButton queryBannedPhrasesButton = new JButton("Banned Phrases");
        queryBannedPhrasesButton.addActionListener(e -> client.queryBannedPhrases());

        // Message List (JList with DefaultListModel)
        messageListModel = new DefaultListModel<>();
        messageList = new JList<>(messageListModel);
        messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane messageScrollPane = new JScrollPane(messageList);

        // Input Field
        inputField = new JTextField();
        inputField.setToolTipText("Type your message...");
        inputField.addActionListener(e -> handleSendingMessage(selectedUsers));

        // Send Button
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> handleSendingMessage(selectedUsers));

        // Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(queryBannedPhrasesButton, BorderLayout.WEST);
        inputPanel.add(excludeModeCheckBox, BorderLayout.NORTH);

        // Layout
        frame.setLayout(new BorderLayout());
        frame.add(messageScrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.add(clientPanel, BorderLayout.WEST);

        // Show Frame
        frame.setVisible(true);




    }


    protected void setTitle() {
        frame.setTitle("BibaChat: " + getCurrentUsername());
    }


    private void connectToServer() {
        try {
            client = new Client("localhost", 9005, this);

            if (client.isConnected()) {
                CommandDispatcher dispatcher = new CommandDispatcher(this);
                client.receiveMessages(dispatcher);
            }
        } catch (IOException e) {
            showAlertWindow("Error: Unable to connect to server - " + e.getMessage(), "Error");
        }
    }

    public String promptUsername() {
        String username = null;
        while (username == null || username.trim().isEmpty()) {
            username = JOptionPane.showInputDialog(frame, "Enter your username:", "Username", JOptionPane.PLAIN_MESSAGE);
            if (username == null) {
                JOptionPane.showMessageDialog(frame, "You need a username to proceed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        username = username.trim();
        setCurrentUsername(username);
        return username;
    }

    private void handleSendingMessage(Set<String> selectedUsers) {
        String message = inputField.getText();

        System.out.println("Sending message: " + message);

        if (!message.isEmpty() && client != null && client.isConnected()) {
            if (!selectedUsers.isEmpty()) {
                String userList = String.join(",", selectedUsers);

                if (excludeModeCheckBox.isSelected()) {
                    // Exclude selected users
                    client.sendMessageWithExclusion(userList, getCurrentUsername(), message);
                } else {
                    // Send to specific users
                    client.sendMessageToSpecified(userList, getCurrentUsername(), message);
                }
            } else {
                // Broadcast to all
                client.sendBroadcastMessage(message);
            }
            inputField.setText("");
        }
    }

    public void updateClientList(String[] clients) {
        SwingUtilities.invokeLater(() -> {
            clientListModel.clear();
            for (String client : clients) {
                clientListModel.addElement(client);
            }
        });
    }

    public void addMessageToChat(String sender, String message) {
        SwingUtilities.invokeLater(() -> messageListModel.addElement(sender + ": " + message));
    }

    public void showAlertWindow(String message, String title) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }


    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }


}
