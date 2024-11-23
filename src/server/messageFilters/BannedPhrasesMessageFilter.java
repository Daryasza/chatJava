package server.messageFilters;

import java.util.Optional;
import java.util.Set;

public class BannedPhrasesMessageFilter implements MessageFilter {
    private final Set<String> bannedPhrases;

    public BannedPhrasesMessageFilter(Set<String> bannedPhrases){
        this.bannedPhrases = bannedPhrases;
    }

    @Override
    public Optional<String> validate(String message) {
        return bannedPhrases.stream().anyMatch(message.toLowerCase()::contains)
                ? Optional.of("Message contains banned phrases!")
                : Optional.empty();
    }
}
