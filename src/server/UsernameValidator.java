package server;

public class UsernameValidator {

    public static String getError(String username, Server server) {
        // client disconnected or invalid input
        if (username == null) {
            return "ERROR: Username is null.";
        }

        username = username.trim();

        //empty check
        if (username.isEmpty()) {
            return "ERROR: Username cannot be empty.";
        }
        //duplicates check
        else if (server.getConnectedUsernames().contains(username)) {
            return "ERROR: The username is already taken. Use a different name.";
        }
        //
        return null;
    }
}
