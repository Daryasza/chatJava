package client.messages;

public final class BannedPhrasesMessage extends Message {
    private final String bannedPhrases;

    public BannedPhrasesMessage(String bannedPhrases) {
        this.bannedPhrases = bannedPhrases;
    }

    @Override
    public String getContent() {
        return bannedPhrases;
    }

}
