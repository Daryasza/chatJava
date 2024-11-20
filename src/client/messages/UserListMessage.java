package client.messages;

public final class UserListMessage extends Message {
    private final String users;

    public UserListMessage(String users) {
        this.users = users;
    }

    @Override
    public String getContent() {
        return users;
    }
}
