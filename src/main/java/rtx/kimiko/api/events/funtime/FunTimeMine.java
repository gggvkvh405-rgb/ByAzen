package rtx.kimiko.api.events.funtime;

public record FunTimeMine(String serverId, String serverRuName, String mineName, String rarity, String nextRarity, long refillAtMs) {
    public static final String RARITY_DEFAULT = "default";
    public static final String RARITY_MYTHICAL = "mythical";
    public static final String RARITY_LEGENDARY = "legendary";
}

