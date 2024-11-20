package client.commands;

import client.GUIManager;

public abstract sealed class GUICommand permits BroadcastMessageGUICommand, ErrorGUICommand, ExcludedMessageGUICommand,
        ShowBannedPhrasesGUICommand, SpecificUsersMessageGUICommand, UpdateUserListGUICommand, ServerConnectedCommand {
    protected final GUIManager guiManager;

    public GUICommand(GUIManager guiManager) {
        this.guiManager = guiManager;
    }
    public abstract void execute();
}
