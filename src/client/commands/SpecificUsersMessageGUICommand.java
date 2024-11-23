package client.commands;
import client.GUIManager;
import client.messages.SentToSpecificMessage;

public class SpecificUsersMessageGUICommand extends GUICommand {
    private final SentToSpecificMessage message;

    public SpecificUsersMessageGUICommand(SentToSpecificMessage message, GUIManager guiManager) {
        super(guiManager);
        this.message = message;
    }

    @Override
    public void execute() {
        String currentUsername = guiManager.getCurrentUsername();

        if (message.getRecipients().contains(currentUsername)) {
            guiManager.addMessageToChat(message.getSender(), message.getContent(), message.date);
        }
    }
}
