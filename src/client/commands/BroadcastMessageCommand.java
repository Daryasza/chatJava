package client.commands;

import client.GUIManager;
import client.messages.ChatMessage;

public class BroadcastMessageCommand implements Command {
    private final ChatMessage message;
    private final GUIManager guiManager;

    public BroadcastMessageCommand(ChatMessage message, GUIManager guiManager) {
        this.message = message;
        this.guiManager = guiManager;
    }

    @Override
    public void execute() {
        guiManager.addMessageToChat(message.getSender(), message.getContent());
    }
}
