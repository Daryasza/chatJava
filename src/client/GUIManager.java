package client;

//TODO Handle system notifications like new client connections or disconnections.
// block user input until reconnected to server

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class GUIManager {
    protected JFrame frame;

    private JList<String> clientList;
    private DefaultListModel<String> clientListModel;
    private DefaultListModel<String> messageListModel;

    protected Set<String> selectedUsers;
    protected JTextField inputField;
    protected JButton sendButton;
    protected JButton queryBannedPhrasesButton;
    protected JCheckBox excludeModeCheckBox;

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
        FontMetrics fontMetrics = clearSelectionButton.getFontMetrics(clearSelectionButton.getFont());
        int textWidth = fontMetrics.stringWidth(clearSelectionButton.getText());
        clearSelectionButton.setPreferredSize(new Dimension(textWidth + (35), clearSelectionButton.getPreferredSize().height));
        clearSelectionButton.addActionListener(e -> {
            clientList.clearSelection();
            selectedUsers.clear();
        });

        JPanel buttonWrapperPanel = new JPanel();
        buttonWrapperPanel.setLayout(new BoxLayout(buttonWrapperPanel, BoxLayout.Y_AXIS));
        buttonWrapperPanel.add(clearSelectionButton);
        buttonWrapperPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        buttonWrapperPanel.add(excludeModeCheckBox);
        buttonWrapperPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));


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
        clientTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 2, 10));
        clientTitle.setFont(new Font("Dialog", Font.BOLD, 14));
        clientPanel.add(clientTitle, BorderLayout.NORTH);
        clientPanel.add(clientScrollPane, BorderLayout.CENTER);
        clientPanel.add(buttonWrapperPanel, BorderLayout.SOUTH);
        clientPanel.setPreferredSize(new Dimension(150, 0));

        //query banned button
        queryBannedPhrasesButton = new JButton("Banned Phrases");
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(queryBannedPhrasesButton, BorderLayout.EAST);

        // message box
        messageListModel = new DefaultListModel<>();
        JList<String> messageList = new JList<>(messageListModel);
        messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane messageScrollPane = new JScrollPane(messageList);


        // input field
        inputField = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && g instanceof Graphics2D) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                }
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                if (g instanceof Graphics2D) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.GRAY);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                }
            }
        };
        inputField.setOpaque(false);
        inputField.setText("Type your message here...");
        inputField.setForeground(Color.LIGHT_GRAY);
        inputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputField.getText().equals("Type your message here...")) {
                    inputField.setText("");
                    inputField.setForeground(Color.DARK_GRAY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputField.getText().isEmpty()) {
                    inputField.setText("Type your message here...");
                    inputField.setForeground(Color.LIGHT_GRAY);
                }
            }
        });

        // send button
        sendButton = new JButton("Send");

        // input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(topPanel, BorderLayout.NORTH);
        rightPanel.add(messageScrollPane, BorderLayout.CENTER);
        rightPanel.add(inputPanel, BorderLayout.SOUTH);


        // layout
        frame.add(clientPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);
        frame.setVisible(true);
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
        JOptionPane.showMessageDialog(frame, message, title,  JOptionPane.PLAIN_MESSAGE);
        inputField.requestFocus();
    }

    public void setChatTitle(String username) {
        frame.setTitle("BibaChat: " + username);
    }

    public void showInstructions(String bannedPhrases, String instructions) {
        String message = "<html><body style='font-family:Arial; font-size:10px; color:#2b2a2a;'>"
                + "<div style='color:#2b2a2a; text-align: center; margin-top: 20px; margin-bottom: 20px;'><b>Hello there! Nice to see you.</b> </div>"
                + "<div style='color:#2b2a2a; text-align: left; margin-bottom: 10px;'>To keep everyone's spirits up, please follow the rules: </div>"
                + "<div style='text-align: left; margin-bottom: 5px;'> 1. Do not use the following phrases: " + bannedPhrases + "</div>"
                + "<div style='color:#2b2a2a; text-align: left;'> 2. Hold off on greeting with " + instructions + "." +"</div>"
                + "<div style='color:#2b2a2a; text-align: left; margin-top: 20px; margin-bottom: 20px;'>How to send messages: </div>"
                + "<div style='color:#2b2a2a; text-align: left; margin-bottom: 10px;'>1. By default your messages are broadcast to all connected users</div>"
                + "<div style='color:#2b2a2a; text-align: left; margin-bottom: 10px;'>2. To send messages to specified users just select them in the list of connected clients (using ⌘ if multiple)</div>"
                + "<div style='color:#2b2a2a; text-align: left; '>3. To skip messaging users you don’t like, just select them under &quot;Exclude Mode&quot;.</div>"
                + "<div style='color:#2b2a2a; text-align: center; margin-top: 20px;'> <b>Enjoy chatting!</b> </div>"
                + "</body></html>";

        showAlertWindow(message, "How to Use the Chat");
    }
}
