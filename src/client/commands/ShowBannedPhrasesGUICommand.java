package client.commands;

import client.GUIManager;
import client.messages.BannedPhrasesMessage;

public class ShowBannedPhrasesGUICommand extends GUICommand {
    private final BannedPhrasesMessage bannedPhrases;

    public ShowBannedPhrasesGUICommand(BannedPhrasesMessage bannedPhrases, GUIManager guiManager) {
        super(guiManager);
        this.bannedPhrases = bannedPhrases;
    }

    @Override
    public void execute() {
        guiManager.showAlertWindow(bannedPhrases.getContent(), "Restricted phrases");
    }
}
