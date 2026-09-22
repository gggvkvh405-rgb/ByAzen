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

    public static int count() {
        return ChangeLog.entries().size();
    }

    /** Сколько записей появилось с указанного времени (например, за сутки). */
    public static int countSince(long time) {
        int count = 0;
        for (ChangeLog.Entry entry : ChangeLog.entries()) {
            if (entry.time() >= time) {
                ++count;
            }
        }
        return count;
    }

    /** Текст журнала для буфера обмена и выгрузки: «время — модуль → настройка = значение». */
    public static String markdown(int max) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Журнал настроек ByAzen\n\n");
        builder.append("Записей: ").append(ChangeLog.count()).append("\n\n");
        int shown = 0;
        for (ChangeLog.Entry entry : ChangeLog.entries()) {
            if (shown++ >= max) {
                builder.append("\n…и ещё ").append(ChangeLog.count() - max).append(" записей\n");
                break;
            }
            builder.append("* ").append(entry.timeText()).append(" — ").append(entry.module())
                    .append(" → ").append(entry.setting()).append(" = ").append(ChangeLog.shortValue(entry.value())).append('\n');
        }
        return builder.toString();
    }

    /** Значение в читаемом виде: без кавычек и лишней длины. */
    public static String shortValue(String value) {
        String text = value == null ? "" : value.replace("\"", "");
        return text.length() <= 48 ? text : text.substring(0, 47) + "…";
    }

    /** Записывает событие миграции настроек: журнал знает не только про настройки. */
    public static void recordNote(String module, String note) {
        ChangeLog.load();
        ENTRIES.add(0, new Entry(System.currentTimeMillis(), module, "миграция", note));
        while (ENTRIES.size() > LIMIT) {
            ENTRIES.remove(ENTRIES.size() - 1);
        }
        dirty = true;
        ChangeLog.save();
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
