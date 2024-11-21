package client.commands;

import client.GUIManager;

public abstract class GUICommand {
    protected final GUIManager guiManager;

    public GUICommand(GUIManager guiManager) {
        this.guiManager = guiManager;
    }
    public abstract void execute();
}
