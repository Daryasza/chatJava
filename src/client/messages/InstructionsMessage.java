package client.messages;

public final class InstructionsMessage extends MessageBase {
    private final String bannedPhrases;
    private final String instructions;

    public InstructionsMessage(String bannedPhrases, String instructions) {
        this.bannedPhrases = bannedPhrases;
        this.instructions = instructions;
    }

    public String getBannedPhrases() {
        return bannedPhrases;
    }

    public String getInstructions() {
        return instructions;
    }
}
