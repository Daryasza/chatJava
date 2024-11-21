package client.commands;

import client.GUIManager;
import client.messages.ServerConnectedMessage;
import server.Server;

public class ServerConnectedCommand extends GUICommand {
    private final ServerConnectedMessage message;

    public ServerConnectedCommand(ServerConnectedMessage message, GUIManager guiManager) {
        super(guiManager);
        this.message = message;
    }

    @Override
    public void execute() {
        guiManager.setChatTitle(message.getUsername());
        guiManager.showInstructions(message.getContent());
    }

}
