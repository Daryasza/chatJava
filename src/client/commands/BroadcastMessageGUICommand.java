package client.commands;

import client.GUIManager;
import client.messages.ChatMessage;

public final class BroadcastMessageGUICommand extends GUICommand {
    private final ChatMessage message;

    public BroadcastMessageGUICommand(ChatMessage message, GUIManager guiManager) {
        super(guiManager);
        this.message = message;
    }

    @Override
    public void execute() {
        guiManager.addMessageToChat(message.getSender(), message.getContent(), message.getTimestamp());
    }
}
