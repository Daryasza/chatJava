package client;


//Use a background thread for receiving messages to avoid blocking the GUI
// use SwingUtilities.invokeLater to update the UI safely

//Fields: sender, recipients, content, and timestamp.

//Features:
//Handle system notifications like new client connections or disconnections.

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class GUIManager {
    protected JFrame frame;

    Set<String> selectedUsers;
    private JList<String> clientList;
    private DefaultListModel<String> clientListModel;
    private DefaultListModel<String> messageListModel;

    protected JTextField inputField;
    protected JButton sendButton;
    protected JButton queryBannedPhrasesButton;
    JCheckBox excludeModeCheckBox;

    private String currentUsername;

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");


    public GUIManager() throws IOException {
        setupGUI();
    }

    private void setupGUI() {
        selectedUsers = new HashSet<>();

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
        queryBannedPhrasesButton = new JButton("Banned Phrases");

        // message box
        messageListModel = new DefaultListModel<>();
        JList<String> messageList = new JList<>(messageListModel);
        messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane messageScrollPane = new JScrollPane(messageList);

        // input field
        inputField = new JTextField();
        inputField.setToolTipText("Type your message...");


        // send button
        sendButton = new JButton("Send");

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

    public String getCurrentUsername() {
        return currentUsername;
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


    public void updateClientList(Set<String> clients) {
        SwingUtilities.invokeLater(() -> {
            clientListModel.clear();
            for (String client : clients) {
                clientListModel.addElement(client);
            }
        });
    }

    public void addMessageToChat(String sender, String message, Date date) {
        String displayDate = dateFormatter.format(date);

        SwingUtilities.invokeLater(() -> messageListModel.addElement(sender + ": " + message + "   [" + displayDate + "]"));
    }

    public void showAlertWindow(String message, String title) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void setChatTitle(String username) {
        frame.setTitle("BibaChat: " + username);
    }

    public void showInstructions(String bannedPhrases) {
        showAlertWindow("Please do not use following phrases: " + bannedPhrases + ". \n" +
                        "Also, please, do not send \"Good Morning\" before 12PM on Mondays. \n" +
                        "To skip messaging users you don’t like, just select them under Exclude Mode.",
                "Intrstuctions");
    }





}
