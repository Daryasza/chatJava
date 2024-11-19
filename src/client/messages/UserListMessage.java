package client.messages;

public final class UserListMessage extends Message {
    private final String[] users;

    public UserListMessage(String[] users) {
        this.users = users;
    }

    public String[] getUsers() {
        return users;
    }

    public String getContent() {
        StringBuilder content = new StringBuilder();
        for (String user : users) {
            content.append(user);
        }
        return content.toString();
    }
}
