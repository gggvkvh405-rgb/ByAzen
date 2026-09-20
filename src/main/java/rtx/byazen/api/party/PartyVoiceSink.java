package rtx.byazen.api.party;

@FunctionalInterface
public interface PartyVoiceSink {
    void accept(String username, int seq, byte[] opusData);
}