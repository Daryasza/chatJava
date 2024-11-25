package server;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class UsernameValidator {

    public static Optional<String> getError(String username, Set<String> bannedPhrases) {
        username = username.trim();

        //empty check
        if (username.isEmpty()) {
            return Optional.of("ERROR: Username cannot be empty.");
        }

        if (username.contains(" ")) {
            return Optional.of("ERROR: Username contains spaces.");
        }

        return bannedPhrases.stream().anyMatch(username.toLowerCase()::contains)
                ? Optional.of("ERROR: Username consists of a banned phrase!")
                : Optional.empty();

    }
}
