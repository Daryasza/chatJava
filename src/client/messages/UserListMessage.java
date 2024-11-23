package client.messages;

import java.util.Set;

public final class UserListMessage extends MessageBase {
    private final Set<String> users;

    public UserListMessage(Set<String> users) {
        this.users = users;
    }

    public Set<String> getUserList() {
        return users;
    }
}
