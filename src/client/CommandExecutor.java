package client;

import client.commands.*;
import client.messages.*;

public class CommandExecutor {
    private final GUIManager guiManager;

    public CommandExecutor(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    protected void dispatchCommand(MessageBase messageBase) {
        GUICommand cmd = switch (messageBase) {
            case ServerConnectedMessage scMessage -> new ServerConnectedGUICommand(scMessage, guiManager);
            case ChatMessage chatMessage -> new ChatMessageGUICommand(chatMessage, guiManager);
            case UserListMessage userListMessage -> new UpdateUserListGUICommand(userListMessage, guiManager);
            case ErrorMessage errorMessage -> new ErrorGUICommand(errorMessage, guiManager);
            case BannedPhrasesMessage bannedPhrases -> new ShowBannedPhrasesGUICommand(bannedPhrases, guiManager);
            case InstructionsMessage instructionsMessage -> new ShowInstructionsCommand(instructionsMessage, guiManager);
        };

        cmd.execute();
    }
}
