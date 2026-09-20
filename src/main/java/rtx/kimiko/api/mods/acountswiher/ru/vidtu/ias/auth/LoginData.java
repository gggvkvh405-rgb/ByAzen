package rtx.kimiko.api.mods.acountswiher.ru.vidtu.ias.auth;

import java.util.UUID;

public record LoginData(String name, UUID uuid, String token, boolean online) {}
