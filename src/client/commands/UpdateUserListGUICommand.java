package client.commands;

import client.GUIManager;
import client.messages.UserListMessage;

import javax.swing.*;

public class UpdateUserListGUICommand extends GUICommand {
    private final UserListMessage userListMessage;

    public UpdateUserListGUICommand(UserListMessage userListMessage, GUIManager guiManager) {
        super(guiManager);
        this.userListMessage = userListMessage;
    }

    @Override
    public void execute() {
        guiManager.updateClientList(userListMessage.getContent());
    }
}
