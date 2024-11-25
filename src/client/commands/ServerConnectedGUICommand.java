package client.commands;

import client.GUIManager;
import client.messages.ServerConnectedMessage;

public class ServerConnectedGUICommand extends GUICommand {
    private final ServerConnectedMessage message;

    public ServerConnectedGUICommand(ServerConnectedMessage message, GUIManager guiManager) {
        super(guiManager);
        this.message = message;
    }

    @Override
    public void execute() {
        guiManager.setChatTitle(message.getUsername());
    }
}
