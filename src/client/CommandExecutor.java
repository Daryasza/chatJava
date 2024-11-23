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
            case ServerConnectedMessage scMessage -> new ServerConnectedCommand(scMessage, guiManager);
            case ExcludeRecipientsMessage ERMessage -> new ExcludedMessageGUICommand(ERMessage, guiManager);
            case BroadcastMessage chatMessage -> new BroadcastMessageGUICommand(chatMessage, guiManager);
            case SentToSpecificMessage SpecMessage -> new SpecificUsersMessageGUICommand(SpecMessage, guiManager);
            case UserListMessage userListMessage -> new UpdateUserListGUICommand(userListMessage, guiManager);
            case ErrorMessage errorMessage -> new ErrorGUICommand(errorMessage, guiManager);
            case BannedPhrasesMessage bannedPhrases -> new ShowBannedPhrasesGUICommand(bannedPhrases, guiManager);
        };

        cmd.execute();
    }
}
