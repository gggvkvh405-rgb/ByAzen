package rtx.byazen.utils.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;

/**
 * Миграция конфигов между версиями (идея №160 из IDEAS.md).
 * <p>
 * Старый файл настроек не должен «терять» то, что человек настраивал годами. Перед применением
 * клиент приводит файл к текущему формату: переименовывает модули, которые меняли название, чинит
 * отсутствующие поля, записывает номер формата. Всё, что не удалось понять, остаётся в файле и
 * попадает в отчёт «Доктора настроек», а не удаляется молча.
 */
public final class ConfigMigrations {

    /** Номер текущего формата настроек (1 — первые версии ByAzen, 2 — версия с журналом). */
    public static final int CURRENT_FORMAT = 2;

    /** Новое имя модуля для старых конфигов: только если старого имени больше нет. */
    private static final String[][] RENAMES = {
            {"Cosmetics", "Cosmetics 3D"},
            {"Kill Effect", "Kill Effect 3D"},
            {"Trails 2", "Trails 2.0"},
            {"Server Binds", "Server Binds"},
            {"Config Backups", "ConfigBackups"},
            {"HUD Groups", "HUD Groups"},
    };

    /** Итог миграции. */
    public static final class Report {

        public final List<String> notes = new ArrayList<String>();
        public int from = ConfigMigrations.CURRENT_FORMAT;
        public int renamed;
        public int repaired;
        public int dropped;

        public boolean changed() {
            return this.renamed > 0 || this.repaired > 0 || this.dropped > 0;
        }

        public String summary() {
            if (!this.changed()) {
                return "формат " + this.from + " → " + ConfigMigrations.CURRENT_FORMAT + ": переделок не нужно";
            }
            return "формат " + this.from + " → " + ConfigMigrations.CURRENT_FORMAT + ": переименовано " + this.renamed
                    + ", починено " + this.repaired + ", убрано устаревшего " + this.dropped;
        }
    }

    private ConfigMigrations() {
    }

    /** Приводит корень настроек к текущему формату. */
    public static ConfigMigrations.Report migrate(JsonObject root) {
        ConfigMigrations.Report report = new ConfigMigrations.Report();
        if (root == null) {
            return report;
        }
        report.from = root.has("format") && root.get("format").isJsonPrimitive()
                ? root.get("format").getAsInt() : (root.has("version") ? root.get("version").getAsInt() : 1);
        JsonObject modules = root.has("modules") && root.get("modules").isJsonObject()
                ? root.getAsJsonObject("modules") : new JsonObject();
        LinkedHashMap<String, String> known = new LinkedHashMap<String, String>();
        for (Module module : ModuleManager.get().getAll()) {
            known.put(module.getName().toLowerCase(java.util.Locale.ROOT), module.getName());
        }
        // 1. модули, которые переименовали между версиями
        for (String[] rename : RENAMES) {
            String from = rename[0];
            String to = rename[1];
            if (from.equalsIgnoreCase(to)) {
                continue;
            }
            String existing = known.get(from.toLowerCase(java.util.Locale.ROOT));
            String target = known.get(to.toLowerCase(java.util.Locale.ROOT));
            if (existing != null || target == null || !modules.has(from) || modules.has(to)) {
                continue;
            }
            JsonElement value = modules.remove(from);
            modules.add(to, value);
            ++report.renamed;
            report.notes.add("модуль «" + from + "» переименован в «" + to + "»");
        }
        // 2. записи модулей, которых в клиенте больше нет
        for (String name : new ArrayList<String>(modules.keySet())) {
            if (known.containsKey(name.toLowerCase(java.util.Locale.ROOT)) || !modules.get(name).isJsonObject()) {
                continue;
            }
            JsonObject entry = modules.getAsJsonObject(name);
            if (entry.entrySet().isEmpty()) {
                modules.remove(name);
                ++report.dropped;
                continue;
            }
            report.notes.add("модуль «" + name + "» больше не существует — настройки оставлены и попадут в отчёт доктора");
        }
        // 3. недостающие поля внутри записи модуля
        for (Map.Entry<String, JsonElement> pair : modules.entrySet()) {
            if (!pair.getValue().isJsonObject()) {
                continue;
            }
            JsonObject entry = pair.getValue().getAsJsonObject();
            if (!entry.has("bindMode")) {
                entry.addProperty("bindMode", Module.BindMode.TOGGLE.name());
                ++report.repaired;
            }
            if (!entry.has("enabled")) {
                entry.addProperty("enabled", true);
                ++report.repaired;
            }
            if (!entry.has("settings") || !entry.get("settings").isJsonObject()) {
                entry.add("settings", new JsonObject());
                ++report.repaired;
            }
        }
        if (!root.has("drags") || !root.get("drags").isJsonObject()) {
            root.add("drags", new JsonObject());
            ++report.repaired;
        }
        if (report.from != ConfigMigrations.CURRENT_FORMAT || !root.has("format")) {
            root.addProperty("format", ConfigMigrations.CURRENT_FORMAT);
            root.remove("version");
        }
        return report;
    }

    /** Проверяет файл, ничего не меняя: для кнопки «проверить миграцию». */
    public static ConfigMigrations.Report inspect(JsonObject root) {
        return ConfigMigrations.migrate(root == null ? new JsonObject() : root.deepCopy());
    }
}
