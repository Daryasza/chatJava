package server.messageFilters;

import java.util.Optional;

public interface MessageFilter {
    Optional<String> validate(String message);
}
