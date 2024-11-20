package client.commands;

import client.messages.ErrorMessage;
import client.GUIManager;

public final class ErrorGUICommand extends GUICommand {
    private final ErrorMessage errorMessage;

    public ErrorGUICommand(ErrorMessage errorMessage, GUIManager guiManager) {
        super(guiManager);
        this.errorMessage = errorMessage;
    }
    @Override
    public void execute() {
        guiManager.showAlertWindow(errorMessage.getContent(), "Error");
    }
}
