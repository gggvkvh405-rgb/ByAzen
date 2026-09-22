package rtx.byazen.utils.cosmetics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Состояние косметики игрока (идеи №121 и №122 из IDEAS.md).
 * <p>
 * Хранит три вещи: что уже открыто, что надето сейчас (по одному предмету на слот) и что добавлено в
 * избранное. Сезонный набор в свой сезон доступен целиком — его можно забрать одной кнопкой, а в
 * остальное время предметы приходят выдачей: от LiteApi, с ивента или от администрации сервера.
 */
public final class Cosmetics {

    private static final String FILE = "cosmetics";

    private static final Set<String> owned = new LinkedHashSet<String>();
    private static final Map<String, String> equipped = new LinkedHashMap<String, String>();
    private static final Set<String> favorites = new LinkedHashSet<String>();
    private static final Map<String, String> sources = new LinkedHashMap<String, String>();
    private static boolean loaded;

    private Cosmetics() {
    }

    private static void ensureLoaded() {
        if (!loaded) {
            Cosmetics.load();
        }
    }

    /** Читает состояние с диска: испорченные записи пропускаются. */
    public static synchronized void load() {
        owned.clear();
        equipped.clear();
        favorites.clear();
        sources.clear();
        loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject(FILE);
            for (JsonElement element : Cosmetics.array(root, "owned")) {
                if (element.isJsonPrimitive()) {
                    String id = element.getAsString();
                    if (CosmeticRegistry.byId(id) != null) {
                        owned.add(id.toLowerCase(Locale.ROOT));
                    }
                }
            }
            if (root.has("equipped") && root.get("equipped").isJsonObject()) {
                for (Map.Entry<String, JsonElement> pair : root.getAsJsonObject("equipped").entrySet()) {
                    String id = pair.getValue().getAsString();
                    Cosmetic cosmetic = CosmeticRegistry.byId(id);
                    if (cosmetic != null && cosmetic.slot.equals(pair.getKey())) {
                        equipped.put(pair.getKey(), cosmetic.id);
                    }
                }
            }
            for (JsonElement element : Cosmetics.array(root, "favorites")) {
                if (element.isJsonPrimitive()) {
                    favorites.add(element.getAsString().toLowerCase(Locale.ROOT));
                }
            }
            if (root.has("sources") && root.get("sources").isJsonObject()) {
                for (Map.Entry<String, JsonElement> pair : root.getAsJsonObject("sources").entrySet()) {
                    sources.put(pair.getKey().toLowerCase(Locale.ROOT), pair.getValue().getAsString());
                }
            }
        }
        catch (Throwable ignored) {
            // пустое состояние лучше, чем падение при входе
        }
    }

    private static JsonArray array(JsonObject root, String key) {
        return root.has(key) && root.get(key).isJsonArray() ? root.getAsJsonArray(key) : new JsonArray();
    }

    /** Сохраняет состояние на диск. */
    public static synchronized void save() {
        try {
            JsonObject root = new JsonObject();
            JsonArray ownedArray = new JsonArray();
            for (String id : owned) {
                ownedArray.add(id);
            }
            root.add("owned", ownedArray);
            JsonObject equippedObject = new JsonObject();
            for (Map.Entry<String, String> pair : equipped.entrySet()) {
                equippedObject.addProperty(pair.getKey(), pair.getValue());
            }
            root.add("equipped", equippedObject);
            JsonArray favoriteArray = new JsonArray();
            for (String id : favorites) {
                favoriteArray.add(id);
            }
            root.add("favorites", favoriteArray);
            JsonObject sourceObject = new JsonObject();
            for (Map.Entry<String, String> pair : sources.entrySet()) {
                sourceObject.addProperty(pair.getKey(), pair.getValue());
            }
            root.add("sources", sourceObject);
            RepositoryStorage.write(FILE, root);
        }
        catch (Throwable ignored) {
            // запись не критична: состояние останется в памяти до следующего сохранения
        }
    }

    /** Открыт ли предмет: базовый, выданный или из набора, чей сезон идёт сейчас. */
    public static boolean available(Cosmetic cosmetic) {
        if (cosmetic == null) {
            return false;
        }
        if (cosmetic.free) {
            return true;
        }
        Cosmetics.ensureLoaded();
        if (owned.contains(cosmetic.id.toLowerCase(Locale.ROOT))) {
            return true;
        }
        return cosmetic.inSet() && CosmeticRegistry.seasonNow(cosmetic.flavor, Cosmetics.month());
    }

    private static int month() {
        return java.time.LocalDate.now().getMonthValue();
    }

    /** Идёт ли сезон набора прямо сейчас. */
    public static boolean seasonNow(String flavor) {
        return CosmeticRegistry.seasonNow(flavor, Cosmetics.month());
    }

    public static boolean granted(Cosmetic cosmetic) {
        return cosmetic != null && Cosmetics.granted(cosmetic.id);
    }

    public static boolean granted(String id) {
        Cosmetics.ensureLoaded();
        return owned.contains(id == null ? "" : id.toLowerCase(Locale.ROOT));
    }

    /** Откуда предмет взялся: «сезон», «ивент», «LiteApi» и так далее. */
    public static String source(String id) {
        Cosmetics.ensureLoaded();
        String value = sources.get(id == null ? "" : id.toLowerCase(Locale.ROOT));
        return value == null ? "" : value;
    }

    /** Выдаёт предмет игроку. */
    public static boolean grant(String id, String source) {
        Cosmetic cosmetic = CosmeticRegistry.byId(id);
        if (cosmetic == null) {
            return false;
        }
        Cosmetics.ensureLoaded();
        String key = cosmetic.id.toLowerCase(Locale.ROOT);
        boolean added = owned.add(key);
        if (source != null && !source.isEmpty()) {
            sources.put(key, source);
        }
        if (added) {
            Cosmetics.save();
        }
        return added;
    }

    /** Забирает все предметы набора: используется в сезон и при выдаче набора целиком. */
    public static int grantSet(String flavor) {
        int granted = 0;
        for (Cosmetic cosmetic : CosmeticRegistry.ofFlavor(flavor)) {
            if (Cosmetics.grant(cosmetic.id, "сезон")) {
                ++granted;
            }
        }
        return granted;
    }

    /** Забирает весь набор, который сейчас доступен целиком. */
    public static int claimSet(String flavor) {
        if (!Cosmetics.seasonNow(flavor)) {
            return 0;
        }
        return Cosmetics.grantSet(flavor);
    }

    public static boolean revoke(String id) {
        Cosmetic cosmetic = CosmeticRegistry.byId(id);
        if (cosmetic == null) {
            return false;
        }
        Cosmetics.ensureLoaded();
        boolean removed = owned.remove(cosmetic.id.toLowerCase(Locale.ROOT));
        sources.remove(cosmetic.id.toLowerCase(Locale.ROOT));
        if (equipped.containsValue(cosmetic.id)) {
            equipped.remove(cosmetic.slot);
        }
        if (removed) {
            Cosmetics.save();
        }
        return removed;
    }

    public static boolean equipped(Cosmetic cosmetic) {
        if (cosmetic == null) {
            return false;
        }
        Cosmetics.ensureLoaded();
        return cosmetic.id.equals(equipped.get(cosmetic.slot));
    }

    public static String equippedIn(String slot) {
        Cosmetics.ensureLoaded();
        return equipped.getOrDefault(slot, "");
    }

    public static Cosmetic equippedCosmetic(String slot) {
        return CosmeticRegistry.byId(Cosmetics.equippedIn(slot));
    }

    /** Надел предмет или снял его, если он уже был надет. Возвращает новое состояние. */
    public static boolean toggle(Cosmetic cosmetic) {
        if (cosmetic == null) {
            return false;
        }
        if (Cosmetics.equipped(cosmetic)) {
            Cosmetics.unequip(cosmetic.slot);
            return false;
        }
        return Cosmetics.equip(cosmetic);
    }

    public static boolean equip(Cosmetic cosmetic) {
        if (cosmetic == null || !Cosmetics.available(cosmetic)) {
            return false;
        }
        Cosmetics.ensureLoaded();
        equipped.put(cosmetic.slot, cosmetic.id);
        Cosmetics.save();
        return true;
    }

    public static void unequip(String slot) {
        Cosmetics.ensureLoaded();
        if (equipped.remove(slot) != null) {
            Cosmetics.save();
        }
    }

    /** Надевает все предметы набора, которые уже открыты. Возвращает число надетых. */
    public static int equipSet(String flavor) {
        int count = 0;
        for (Cosmetic cosmetic : CosmeticRegistry.ofFlavor(flavor)) {
            if (!Cosmetics.available(cosmetic) || Cosmetics.equipped(cosmetic)) {
                continue;
            }
            if (Cosmetics.equip(cosmetic)) {
                ++count;
            }
        }
        return count;
    }

    /** Снимает всё, что надето. */
    public static int clearAll() {
        Cosmetics.ensureLoaded();
        int count = equipped.size();
        equipped.clear();
        if (count > 0) {
            Cosmetics.save();
        }
        return count;
    }

    public static boolean favorite(Cosmetic cosmetic) {
        return cosmetic != null && Cosmetics.favorite(cosmetic.id);
    }

    public static boolean favorite(String id) {
        Cosmetics.ensureLoaded();
        return favorites.contains(id == null ? "" : id.toLowerCase(Locale.ROOT));
    }

    public static boolean toggleFavorite(Cosmetic cosmetic) {
        if (cosmetic == null) {
            return false;
        }
        Cosmetics.ensureLoaded();
        String key = cosmetic.id.toLowerCase(Locale.ROOT);
        boolean added = favorites.add(key);
        if (!added) {
            favorites.remove(key);
        }
        Cosmetics.save();
        return added;
    }

    public static Set<String> favorites() {
        Cosmetics.ensureLoaded();
        return java.util.Collections.unmodifiableSet(favorites);
    }

    /** Сколько предметов набора уже открыто. */
    public static int ownedInSet(String flavor) {
        CosmeticRegistry.Set set = CosmeticRegistry.setOf(flavor);
        if (set == null) {
            return 0;
        }
        Cosmetics.ensureLoaded();
        int count = 0;
        for (Cosmetic cosmetic : set.items) {
            if (Cosmetics.available(cosmetic)) {
                ++count;
            }
        }
        return count;
    }

    public static int ownedCount() {
        Cosmetics.ensureLoaded();
        return owned.size();
    }

    /** Строка состояния набора для каталога. */
    public static String setState(String flavor) {
        CosmeticRegistry.Set set = CosmeticRegistry.setOf(flavor);
        if (set == null) {
            return "";
        }
        int ownedInSet = Cosmetics.ownedInSet(flavor);
        String season = CosmeticRegistry.seasonText(flavor);
        return ownedInSet + "/" + set.items.size() + " · " + season
                + (Cosmetics.seasonNow(flavor) ? " (идёт сейчас)" : "");
    }

    /** Общее число предметов: для подписи в каталоге. */
    public static int totalCount() {
        return CosmeticRegistry.all().size();
    }

    public static List<Cosmetic> equippedList() {
        Cosmetics.ensureLoaded();
        ArrayList<Cosmetic> list = new ArrayList<Cosmetic>();
        for (String id : equipped.values()) {
            Cosmetic cosmetic = CosmeticRegistry.byId(id);
            if (cosmetic != null) {
                list.add(cosmetic);
            }
        }
        return list;
    }

    /**
     * Выдача от LiteApi (идея №121): сервер присылает список предметов, набор или отзыв.
     * <pre>
     * {"grant":["winter_hat"], "revoke":["neon_mask"], "set":"space", "source":"ивент"}
     * </pre>
     */
    public static void applyGrant(JsonObject json) {
        if (json == null) {
            return;
        }
        String source = json.has("source") ? json.get("source").getAsString() : "LiteApi";
        CosmeticRegistry.Set set = json.has("set") ? CosmeticRegistry.setOf(json.get("set").getAsString()) : null;
        if (set != null) {
            for (Cosmetic cosmetic : set.items) {
                Cosmetics.grant(cosmetic.id, source);
            }
        }
        if (json.has("grant")) {
            for (JsonElement element : Cosmetics.asArray(json.get("grant"))) {
                if (element.isJsonPrimitive()) {
                    Cosmetics.grant(element.getAsString(), source);
                }
            }
        }
        if (json.has("revoke")) {
            for (JsonElement element : Cosmetics.asArray(json.get("revoke"))) {
                if (element.isJsonPrimitive()) {
                    Cosmetics.revoke(element.getAsString());
                }
            }
        }
    }

    private static JsonArray asArray(JsonElement element) {
        if (element == null) {
            return new JsonArray();
        }
        if (element.isJsonArray()) {
            return element.getAsJsonArray();
        }
        JsonArray array = new JsonArray();
        if (element.isJsonPrimitive()) {
            array.add(element.getAsString());
        }
        return array;
    }
}
