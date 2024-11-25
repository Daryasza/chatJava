package client.commands;

import client.GUIManager;
import client.messages.InstructionsMessage;

public class ShowInstructionsCommand extends GUICommand {
    InstructionsMessage message;

    public ShowInstructionsCommand(InstructionsMessage message, GUIManager guiManager) {
        super(guiManager);
        this.message = message;
    }

    @Override
    public void execute() {
        guiManager.showInstructions(message.getBannedPhrases(), message.getInstructions());
    }
}
