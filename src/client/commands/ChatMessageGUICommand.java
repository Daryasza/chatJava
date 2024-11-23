package client.commands;

import client.GUIManager;
import client.messages.ChatMessage;

public class ChatMessageGUICommand extends GUICommand {
    private final ChatMessage message;

    public ChatMessageGUICommand(ChatMessage message, GUIManager guiManager) {
        super(guiManager);
        this.message = message;
    }

    @Override
    public void execute() {
        guiManager.addMessageToChat(message.getSender(), message.getContent(), message.date);
    }
}
