package client.messages;

import java.util.Arrays;

public final class BannedPhrasesMessage extends Message {
    private final String[] bannedPhrases;

    public BannedPhrasesMessage(String[] bannedPhrase) {
        this.bannedPhrases = bannedPhrase;
    }

    @Override
    public String getContent() {
        return Arrays.toString(bannedPhrases);
    }
}
