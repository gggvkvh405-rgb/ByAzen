package rtx.byazen.utils.profile;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Анимированные обложки профиля (идея №131 из IDEAS.md).
 * <p>
 * У каждого игрока в карточке есть живая обложка: мягкий градиент с плавающими бликами, который
 * действительно движется — это настоящий GIF, а не статичная картинка. Здесь хранится каталог
 * обложек и выбор игрока (свой профиль и обложки друзей), всё сохраняется рядом с настройками.
 */
public final class ProfileCovers {

    /** Обложка: имя, файл, оттенки для подписи и сезон. */
    public static final class Cover {

        public final String id;
        public final String name;
        public final String texture;
        public final int accent;
        public final String season;

        Cover(String id, String name, String texture, int accent, String season) {
            this.id = id;
            this.name = name;
            this.texture = texture;
            this.accent = accent;
            this.season = season;
        }

        public String path() {
            return "byazen:gif/covers/" + this.texture;
        }
    }

    private static final String FILE = "profilecovers";
    private static final Map<String, Cover> COVERS = new LinkedHashMap<String, Cover>();
    private static final Map<String, String> PLAYERS = new LinkedHashMap<String, String>();

    private static String own = "neon";
    private static boolean loaded;

    private ProfileCovers() {
    }

    static {
        ProfileCovers.register(new Cover("neon", "Неон", "neon.gif", 0x7FF3C8, "neon"));
        ProfileCovers.register(new Cover("winter", "Зима", "winter.gif", 0xBFE8FF, "snow"));
        ProfileCovers.register(new Cover("space", "Космос", "space.gif", 0xA98BFF, "space"));
        ProfileCovers.register(new Cover("candy", "Карамель", "candy.gif", 0xFF9EC4, ""));
    }

    private static void register(Cover cover) {
        COVERS.put(cover.id, cover);
    }

    public static List<Cover> all() {
        return new ArrayList<Cover>(COVERS.values());
    }

    public static Cover get(String id) {
        return COVERS.get(id);
    }

    public static Cover own() {
        ProfileCovers.ensureLoaded();
        Cover cover = COVERS.get(ProfileCovers.own);
        return cover != null ? cover : COVERS.values().iterator().next();
    }

    public static void setOwn(String id) {
        if (!COVERS.containsKey(id)) {
            return;
        }
        ProfileCovers.ensureLoaded();
        ProfileCovers.own = id;
        ProfileCovers.save();
    }

    /** Обложка конкретного игрока: своя — из настроек, чужая — сезонная или по имени. */
    public static Cover coverFor(String name) {
        ProfileCovers.ensureLoaded();
        if (name == null || name.isBlank()) {
            return ProfileCovers.own();
        }
        String chosen = PLAYERS.get(name.toLowerCase(java.util.Locale.ROOT));
        if (chosen != null && COVERS.containsKey(chosen)) {
            return COVERS.get(chosen);
        }
        return ProfileCovers.seasonal();
    }

    /** Сезонная обложка: зимой — «Зима», иначе выбранная игроком. */
    public static Cover seasonal() {
        if (rtx.byazen.utils.cosmetics.Cosmetics.seasonNow(rtx.byazen.utils.cosmetics.Cosmetic.FLAVOR_SNOW)) {
            Cover winter = COVERS.get("winter");
            if (winter != null) {
                return winter;
            }
        }
        if (rtx.byazen.utils.cosmetics.Cosmetics.seasonNow(rtx.byazen.utils.cosmetics.Cosmetic.FLAVOR_SPACE)) {
            Cover space = COVERS.get("space");
            if (space != null) {
                return space;
            }
        }
        return ProfileCovers.own();
    }

    /** Запомнить обложку для игрока (например, пришедшую от друга). */
    public static void assign(String name, String coverId) {
        if (name == null || name.isBlank() || !COVERS.containsKey(coverId)) {
            return;
        }
        ProfileCovers.ensureLoaded();
        PLAYERS.put(name.toLowerCase(java.util.Locale.ROOT), coverId);
        ProfileCovers.save();
    }

    public static void ensureLoaded() {
        if (!ProfileCovers.loaded) {
            ProfileCovers.load();
        }
    }

    public static synchronized void load() {
        ProfileCovers.loaded = true;
        JsonObject json = RepositoryStorage.readObject(FILE);
        if (json == null) {
            return;
        }
        try {
            if (json.has("own") && COVERS.containsKey(json.get("own").getAsString())) {
                ProfileCovers.own = json.get("own").getAsString();
            }
            if (json.has("players") && json.get("players").isJsonObject()) {
                JsonObject players = json.getAsJsonObject("players");
                for (String key : players.keySet()) {
                    String value = players.get(key).getAsString();
                    if (COVERS.containsKey(value)) {
                        PLAYERS.put(key.toLowerCase(java.util.Locale.ROOT), value);
                    }
                }
            }
        }
        catch (Exception ignored) {
            // повреждённый файл просто игнорируем
        }
    }

    public static synchronized void save() {
        JsonObject json = new JsonObject();
        json.addProperty("own", ProfileCovers.own);
        JsonObject players = new JsonObject();
        for (Map.Entry<String, String> entry : PLAYERS.entrySet()) {
            players.addProperty(entry.getKey(), entry.getValue());
        }
        json.add("players", players);
        RepositoryStorage.write(FILE, json);
    }
}
