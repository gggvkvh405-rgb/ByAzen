package rtx.byazen.utils.pets;

import com.google.gson.JsonObject;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Оформление и настроение питомца (идеи №127 и №128 из IDEAS.md).
 * <p>
 * Хранит всё, что игрок подобрал для своего питомца: цвет, акцент, размер, имя, аксессуар, а также
 * уровень и опыт из мини-игр. Данные лежат рядом с остальными настройками клиента, поэтому оформление
 * не слетает между запусками.
 */
public final class PetStyleData {

    private static final String FILE = "petstyle";

    public static final String ACCESSORY_NONE = "Нет";
    public static final String ACCESSORY_BOW = "Бант";
    public static final String ACCESSORY_HAT = "Шляпа";
    public static final String ACCESSORY_SCARF = "Шарф";
    public static final String ACCESSORY_CROWN = "Корона";
    public static final String ACCESSORY_GLASSES = "Очки";
    public static final String[] ACCESSORIES = {ACCESSORY_NONE, ACCESSORY_BOW, ACCESSORY_HAT, ACCESSORY_SCARF, ACCESSORY_CROWN, ACCESSORY_GLASSES};

    private static int color = 0x8FA8FF;
    private static int accent = 0xC7D6FF;
    private static float scale = 1.0f;
    private static String name = "Питомец";
    private static String accessory = ACCESSORY_BOW;
    private static boolean showName = true;
    private static int level = 1;
    private static int xp;
    private static int mood = 60;
    private static long lastPlayMs;
    private static boolean loaded;

    private PetStyleData() {
    }

    /** Опыт до следующего уровня. */
    public static int xpForNextLevel() {
        return 12 + PetStyleData.level * 6;
    }

    public static int getColor() {
        return PetStyleData.color;
    }

    public static int getAccent() {
        return PetStyleData.accent;
    }

    public static float getScale() {
        return PetStyleData.scale;
    }

    public static String getName() {
        return PetStyleData.name;
    }

    public static String getAccessory() {
        return PetStyleData.accessory;
    }

    public static boolean isShowName() {
        return PetStyleData.showName;
    }

    public static int getLevel() {
        return PetStyleData.level;
    }

    public static int getXp() {
        return PetStyleData.xp;
    }

    public static int getMood() {
        return PetStyleData.mood;
    }

    public static long getLastPlayMs() {
        return PetStyleData.lastPlayMs;
    }

    public static void setColor(int value) {
        PetStyleData.color = value & 0xFFFFFF;
    }

    public static void setAccent(int value) {
        PetStyleData.accent = value & 0xFFFFFF;
    }

    public static void setScale(float value) {
        PetStyleData.scale = Math.max(0.5f, Math.min(1.8f, value));
    }

    public static void setName(String value) {
        String clean = value == null ? "" : value.replace("§", "").trim();
        PetStyleData.name = clean.isEmpty() ? "Питомец" : clean.substring(0, Math.min(16, clean.length()));
    }

    public static void setAccessory(String value) {
        PetStyleData.accessory = value == null ? ACCESSORY_BOW : value;
    }

    public static void setShowName(boolean value) {
        PetStyleData.showName = value;
    }

    /** Добавляет опыт и поднимает уровень. Возвращает true, если уровень вырос. */
    public static boolean addXp(int amount) {
        if (amount <= 0) {
            return false;
        }
        PetStyleData.xp += amount;
        boolean leveled = false;
        while (PetStyleData.xp >= PetStyleData.xpForNextLevel() && PetStyleData.level < 50) {
            PetStyleData.xp -= PetStyleData.xpForNextLevel();
            ++PetStyleData.level;
            leveled = true;
        }
        PetStyleData.mood = Math.min(100, PetStyleData.mood + 6);
        PetStyleData.lastPlayMs = System.currentTimeMillis();
        PetStyleData.save();
        return leveled;
    }

    /** Меняет настроение: кормление и игры поднимают, смерть хозяина опускает. */
    public static void changeMood(int delta) {
        PetStyleData.mood = Math.max(0, Math.min(100, PetStyleData.mood + delta));
        PetStyleData.lastPlayMs = System.currentTimeMillis();
        PetStyleData.save();
    }

    public static void ensureLoaded() {
        if (!PetStyleData.loaded) {
            PetStyleData.load();
        }
    }

    public static synchronized void load() {
        PetStyleData.loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject(FILE);
            if (root.has("color")) {
                PetStyleData.color = root.get("color").getAsInt() & 0xFFFFFF;
            }
            if (root.has("accent")) {
                PetStyleData.accent = root.get("accent").getAsInt() & 0xFFFFFF;
            }
            if (root.has("scale")) {
                PetStyleData.setScale(root.get("scale").getAsFloat());
            }
            if (root.has("name")) {
                PetStyleData.setName(root.get("name").getAsString());
            }
            if (root.has("accessory")) {
                PetStyleData.setAccessory(root.get("accessory").getAsString());
            }
            if (root.has("showName")) {
                PetStyleData.showName = root.get("showName").getAsBoolean();
            }
            if (root.has("level")) {
                PetStyleData.level = Math.max(1, Math.min(50, root.get("level").getAsInt()));
            }
            if (root.has("xp")) {
                PetStyleData.xp = Math.max(0, root.get("xp").getAsInt());
            }
            if (root.has("mood")) {
                PetStyleData.mood = Math.max(0, Math.min(100, root.get("mood").getAsInt()));
            }
            if (root.has("lastPlay")) {
                PetStyleData.lastPlayMs = root.get("lastPlay").getAsLong();
            }
        }
        catch (Throwable ignored) {
            // повреждённый файл: остаются значения по умолчанию
        }
    }

    public static synchronized void save() {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("color", PetStyleData.color);
            root.addProperty("accent", PetStyleData.accent);
            root.addProperty("scale", PetStyleData.scale);
            root.addProperty("name", PetStyleData.name);
            root.addProperty("accessory", PetStyleData.accessory);
            root.addProperty("showName", PetStyleData.showName);
            root.addProperty("level", PetStyleData.level);
            root.addProperty("xp", PetStyleData.xp);
            root.addProperty("mood", PetStyleData.mood);
            root.addProperty("lastPlay", PetStyleData.lastPlayMs);
            RepositoryStorage.write(FILE, root);
        }
        catch (Throwable ignored) {
            // запись не критична
        }
    }
}
