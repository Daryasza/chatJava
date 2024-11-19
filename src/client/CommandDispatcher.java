package client;

import client.commands.*;
import client.messages.*;

public class CommandDispatcher {
    private final GUIManager guiManager;

    public CommandDispatcher(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public void dispatchCommand(Message message) {
        Command cmd;

        System.out.println(message);

        cmd = switch (message) {
            case ExcludeRecipientsMessage ERMessage -> new ExcludedMessageCommand(ERMessage, guiManager);
            case ChatMessage chatMessage -> new BroadcastMessageCommand(chatMessage, guiManager);
            case SentToSpecificMessage SpecMessage -> new SpecificUsersMessageCommand(SpecMessage, guiManager);
            case UserListMessage userListMessage -> new UpdateUserListCommand(userListMessage, guiManager);
            case ErrorMessage errorMessage -> new ErrorCommand(errorMessage, guiManager);
            case BannedPhrasesMessage bannedPhrases -> new ShowBannedPhrasesCommand(bannedPhrases, guiManager);
            default -> throw new IllegalArgumentException("Unknown message type: " + message);
        };

        cmd.execute();
    }
}
