package client.commands;

import client.messages.ErrorMessage;
import client.GUIManager;

public class ErrorCommand implements Command {
    private final ErrorMessage errorMessage;
    private final GUIManager guiManager;

    public ErrorCommand(ErrorMessage errorMessage, GUIManager guiManager) {
        this.errorMessage = errorMessage;
        this.guiManager = guiManager;
    }
    @Override
    public void execute() {
        guiManager.showAlertWindow(errorMessage.getContent(), "Error");
    }
}
