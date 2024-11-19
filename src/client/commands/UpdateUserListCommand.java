package client.commands;

import client.GUIManager;
import client.messages.UserListMessage;

public class UpdateUserListCommand implements Command {

    private final UserListMessage userListMessage;
    private final GUIManager guiManager;

    public UpdateUserListCommand(UserListMessage userListMessage, GUIManager guiManager) {
        this.userListMessage = userListMessage;
        this.guiManager = guiManager;
    }

    @Override
    public void execute() {
        guiManager.updateClientList(userListMessage.getUsers());
    }
}
