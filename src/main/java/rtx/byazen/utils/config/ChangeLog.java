package rtx.byazen.utils.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.Setting;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Журнал изменений настроек (идеи №159 и №41 из IDEAS.md): «что я поменял и когда».
 * Пишется в конфиг ByAzen, читается командой {@code /byazen history}.
 */
public final class ChangeLog {

    private static final String FILE = "settings_history";
    private static final int LIMIT = 200;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("dd.MM HH:mm").withZone(ZoneId.systemDefault());

    private static final List<Entry> ENTRIES = new ArrayList<Entry>();
    private static boolean loaded;
    private static boolean dirty;

    private ChangeLog() {
    }

    public record Entry(long time, String module, String setting, String value) {

        public String timeText() {
            return TIME.format(Instant.ofEpochMilli(this.time));
        }
    }

    /** Записывает изменение настройки (вызывается слушателем изменения настроек). */
    public static void record(Module module, Setting setting) {
        if (module == null || setting == null || rtx.byazen.api.config.ConfigManager.isLoading()) {
            return;
        }
        ChangeLog.load();
        String value;
        try {
            JsonElement json = rtx.byazen.api.config.ConfigManager.serializeSetting(setting);
            value = json == null ? "" : json.toString();
        }
        catch (Throwable throwable) {
            value = "";
        }
        ENTRIES.add(0, new Entry(System.currentTimeMillis(), module.getName(), setting.getName(), value));
        while (ENTRIES.size() > LIMIT) {
            ENTRIES.remove(ENTRIES.size() - 1);
        }
        dirty = true;
        ChangeLog.save();
    }

    public static List<Entry> entries() {
        ChangeLog.load();
        return Collections.unmodifiableList(new ArrayList<Entry>(ENTRIES));
    }

    public static List<Entry> entriesFor(String moduleName) {
        ArrayList<Entry> list = new ArrayList<Entry>();
        for (Entry entry : ChangeLog.entries()) {
            if (entry.module().equalsIgnoreCase(moduleName)) {
                list.add(entry);
            }
        }
        return list;
    }

    public static void clear() {
        ChangeLog.load();
        ENTRIES.clear();
        dirty = true;
        ChangeLog.save();
    }

    private static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject(FILE);
            JsonArray array = root.getAsJsonArray("entries");
            if (array == null) {
                return;
            }
            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject object = element.getAsJsonObject();
                long time = object.has("t") ? object.get("t").getAsLong() : System.currentTimeMillis();
                String module = object.has("m") ? object.get("m").getAsString() : "";
                String setting = object.has("s") ? object.get("s").getAsString() : "";
                String value = object.has("v") ? object.get("v").getAsString() : "";
                ENTRIES.add(new Entry(time, module, setting, value));
            }
        }
        catch (Throwable ignored) {
        }
    }

    private static void save() {
        if (!dirty) {
            return;
        }
        dirty = false;
        JsonObject root = new JsonObject();
        JsonArray array = new JsonArray();
        for (Entry entry : ENTRIES) {
            JsonObject object = new JsonObject();
            object.addProperty("t", entry.time());
            object.addProperty("m", entry.module());
            object.addProperty("s", entry.setting());
            object.addProperty("v", entry.value());
            array.add(object);
        }
        root.add("entries", array);
        RepositoryStorage.write(FILE, root);
    }
}
