package client.commands;
import client.GUIManager;
import client.messages.SentToSpecificMessage;

public class SpecificUsersMessageCommand implements Command {
    private final SentToSpecificMessage message;
    private final GUIManager guiManager;

    public SpecificUsersMessageCommand(SentToSpecificMessage message, GUIManager guiManager) {
        this.message = message;
        this.guiManager = guiManager;
    }

    @Override
    public void execute() {
        String currentUsername = guiManager.getCurrentUsername();
        if (message.getRecipients().contains(currentUsername)) {
            guiManager.addMessageToChat(message.getSender(), message.getContent());
        }
    }
}
