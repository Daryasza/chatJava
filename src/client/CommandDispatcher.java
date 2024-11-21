package client;

import client.commands.*;
import client.messages.*;

public class CommandDispatcher {
    private final GUIManager guiManager;

    public CommandDispatcher(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public void dispatchCommand(Message message) {
        GUICommand cmd;

        cmd = switch (message) {
            case ServerConnectedMessage scMessage -> new ServerConnectedCommand(scMessage, guiManager);
            case ExcludeRecipientsMessage ERMessage -> new ExcludedMessageGUICommand(ERMessage, guiManager);
            case ChatMessage chatMessage -> new BroadcastMessageGUICommand(chatMessage, guiManager);
            case SentToSpecificMessage SpecMessage -> new SpecificUsersMessageGUICommand(SpecMessage, guiManager);
            case UserListMessage userListMessage -> new UpdateUserListGUICommand(userListMessage, guiManager);
            case ErrorMessage errorMessage -> new ErrorGUICommand(errorMessage, guiManager);
            case BannedPhrasesMessage bannedPhrases -> new ShowBannedPhrasesGUICommand(bannedPhrases, guiManager);
        };

        cmd.execute();
    }
}
