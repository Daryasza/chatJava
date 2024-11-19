package client.commands;

import client.GUIManager;
import client.messages.ExcludeRecipientsMessage;

public class ExcludedMessageCommand implements Command {
    private final ExcludeRecipientsMessage message;
    private final GUIManager guiManager;

    public ExcludedMessageCommand(ExcludeRecipientsMessage message, GUIManager guiManager) {
        this.message = message;
        this.guiManager = guiManager;
    }

    @Override
    public void execute() {
        String currentUsername = guiManager.getCurrentUsername();
        System.out.println("ExcludedMessageCommand");
        if (!message.getExcludedRecipients().contains(currentUsername)) {
            guiManager.addMessageToChat(message.getSender(), message.getContent());
        }
    }
}
