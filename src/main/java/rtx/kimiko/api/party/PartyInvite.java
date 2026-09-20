package rtx.kimiko.api.party;

public record PartyInvite(String id, String leader, String partyName, long expiresAt) {
}