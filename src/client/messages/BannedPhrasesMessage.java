package client.messages;

import java.util.Set;

public final class BannedPhrasesMessage extends MessageBase {
    private final String bannedPhrases;

    public BannedPhrasesMessage(String bannedPhrases) {
        this.bannedPhrases = bannedPhrases;
    }

    public String getBannedPhrases() {
        return bannedPhrases;
    }

}
