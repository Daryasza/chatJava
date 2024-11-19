package client.commands;

import client.GUIManager;
import client.messages.BannedPhrasesMessage;

import java.util.Arrays;

public class ShowBannedPhrasesCommand implements Command {
    private final BannedPhrasesMessage bannedPhrases;
    private final GUIManager guiManager;

    public ShowBannedPhrasesCommand(BannedPhrasesMessage bannedPhrases, GUIManager guiManager) {
        this.bannedPhrases = bannedPhrases;
        this.guiManager = guiManager;
    }

    @Override
    public void execute() {
        guiManager.showAlertWindow(bannedPhrases.getContent(), "Please do not use these phrases");
    }
}
