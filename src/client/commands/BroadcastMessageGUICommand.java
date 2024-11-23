package client.commands;

import client.GUIManager;
import client.messages.BroadcastMessage;

public class BroadcastMessageGUICommand extends GUICommand {
    private final BroadcastMessage message;

    public BroadcastMessageGUICommand(BroadcastMessage message, GUIManager guiManager) {
        super(guiManager);
        this.message = message;
    }

    @Override
    public void execute() {
        guiManager.addMessageToChat(message.getSender(), message.getContent(), message.date);
    }
}
