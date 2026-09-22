package rtx.byazen.utils.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import rtx.byazen.api.config.ConfigManager;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Сравнение версий настроек (идея №204 из IDEAS.md).
 * <p>
 * Версии настроек ({@link CloudConfigs}) уже сохраняются при обновлениях и вручную. Здесь можно
 * посмотреть, что именно изменилось: какие модули включились или выключились, какие настройки
 * поменяли значение и куда, кто переставил клавишу. Это ответ на вопрос «после обновления клиента
 * всё поехало — что случилось?»: сравнение с прошлой версией показывает разницу человеческим текстом.
 */
public final class ConfigDiff {

    /** Что изменилось в одной настройке. */
    public record Change(String module, String setting, String before, String after) {
        public String text() {
            if (this.before.isEmpty()) {
                return "§b" + this.module + " §8· §f" + this.setting + " §7: было пусто → §f" + this.after;
            }
            if (this.after.isEmpty()) {
                return "§b" + this.module + " §8· §f" + this.setting + " §7: §f" + this.before + " §7→ пусто";
            }
            return "§b" + this.module + " §8· §f" + this.setting + " §7: §f" + this.before + " §7→ §f" + this.after;
        }
    }

    private static final String FOLDER = "config-versions";
    private static final int MAX_ROWS = 60;

    private ConfigDiff() {
    }

    private static Path folder() {
        return RepositoryStorage.configRoot().resolve(FOLDER);
    }

    /** Читает снимок настроек из файла версии. */
    public static JsonObject snapshotOf(CloudConfigs.Version version) {
        if (version == null) {
            return null;
        }
        try {
            Path file = ConfigDiff.folder().resolve(version.name);
            if (!Files.exists(file)) {
                return null;
            }
            String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            JsonElement parsed = JsonParser.parseString(text);
            if (!parsed.isJsonObject()) {
                return null;
            }
            JsonObject root = parsed.getAsJsonObject();
            return root.has("modules") && root.get("modules").isJsonObject() ? root.getAsJsonObject("modules") : null;
        }
        catch (Throwable throwable) {
            ClientLog.warn("сравнение конфигов: не удалось прочитать версию " + version.name);
            return null;
        }
    }

    public static JsonObject currentSnapshot() {
        try {
            return ConfigManager.buildModuleSnapshot();
        }
        catch (Throwable throwable) {
            ClientLog.warn("сравнение конфигов: не удалось снять текущий снимок");
            return null;
        }
    }

    /** Разница между версией и тем, что настроено сейчас. */
    public static List<String> againstCurrent(CloudConfigs.Version version) {
        return ConfigDiff.describe(ConfigDiff.snapshotOf(version), ConfigDiff.currentSnapshot(), "версия → сейчас");
    }

    /** Разница между двумя сохранёнными версиями. */
    public static List<String> between(CloudConfigs.Version older, CloudConfigs.Version newer) {
        return ConfigDiff.describe(ConfigDiff.snapshotOf(older), ConfigDiff.snapshotOf(newer), "старая → новая");
    }

    /** Общий список изменений в виде строк для чата. */
    public static List<String> describe(JsonObject left, JsonObject right, String direction) {
        ArrayList<String> rows = new ArrayList<String>();
        if (left == null || right == null) {
            rows.add("§7Не получилось прочитать одну из версий настроек");
            return rows;
        }
        List<Change> changes = ConfigDiff.changes(left, right);
        List<String> toggles = ConfigDiff.toggles(left, right);
        List<String> binds = ConfigDiff.binds(left, right);
        int total = changes.size() + toggles.size() + binds.size();
        if (total == 0) {
            rows.add("§aРазницы нет — настройки совпадают (" + direction + ")");
            return rows;
        }
        rows.add("§bИзменений: §f" + total + " §7(" + direction + ")");
        for (String line : toggles) {
            ConfigDiff.addRow(rows, "§7• " + line);
        }
        for (String line : binds) {
            ConfigDiff.addRow(rows, "§7• " + line);
        }
        ArrayList<Change> sorted = new ArrayList<Change>(changes);
        sorted.sort((first, second) -> {
            int byModule = first.module().compareToIgnoreCase(second.module());
            return byModule != 0 ? byModule : first.setting().compareToIgnoreCase(second.setting());
        });
        for (Change change : sorted) {
            ConfigDiff.addRow(rows, "§7• " + change.text());
        }
        return rows;
    }

    private static void addRow(List<String> rows, String line) {
        if (rows.size() < MAX_ROWS) {
            rows.add(line);
        }
        else if (rows.size() == MAX_ROWS) {
            rows.add("§8… остальные изменения пропущены");
        }
    }

    /** Все изменения значений настроек. */
    public static List<Change> changes(JsonObject left, JsonObject right) {
        ArrayList<Change> list = new ArrayList<Change>();
        for (String module : ConfigDiff.moduleNames(left, right)) {
            JsonObject leftSettings = ConfigDiff.settingsOf(left, module);
            JsonObject rightSettings = ConfigDiff.settingsOf(right, module);
            Set<String> keys = new LinkedHashSet<String>();
            keys.addAll(leftSettings.keySet());
            keys.addAll(rightSettings.keySet());
            for (String key : keys) {
                String before = ConfigDiff.shortValue(leftSettings.get(key));
                String after = ConfigDiff.shortValue(rightSettings.get(key));
                if (!before.equals(after)) {
                    list.add(new Change(module, key, before, after));
                }
            }
        }
        return list;
    }

    /** Модули, которые включили или выключили. */
    public static List<String> toggles(JsonObject left, JsonObject right) {
        ArrayList<String> rows = new ArrayList<String>();
        for (String module : ConfigDiff.moduleNames(left, right)) {
            boolean before = ConfigDiff.flagOf(left, module);
            boolean after = ConfigDiff.flagOf(right, module);
            if (before == after) {
                continue;
            }
            rows.add("§f" + module + (after ? " §aвключён" : " §cвыключен"));
        }
        return rows;
    }

    /** Переставленные клавиши. */
    public static List<String> binds(JsonObject left, JsonObject right) {
        ArrayList<String> rows = new ArrayList<String>();
        for (String module : ConfigDiff.moduleNames(left, right)) {
            String before = ConfigDiff.bindOf(left, module);
            String after = ConfigDiff.bindOf(right, module);
            if (!before.equals(after)) {
                rows.add("§f" + module + " §7: клавиша §f" + before + " §7→ §f" + after);
            }
        }
        return rows;
    }

    /** Сводка: сколько версий и когда снята последняя. */
    public static String summary() {
        List<CloudConfigs.Version> versions = CloudConfigs.versions();
        if (versions.isEmpty()) {
            return "версий настроек пока нет — они снимаются перед каждым обновлением";
        }
        CloudConfigs.Version latest = versions.getFirst();
        return "версий: " + versions.size() + ", последняя — " + latest.timeText()
                + " («" + latest.name.replace(".json", "") + "»), " + latest.sizeText();
    }

    /** Строки для окна диагностики и чата: разница последней версии с текущим состоянием. */
    public static List<String> latestAgainstCurrent() {
        List<CloudConfigs.Version> versions = CloudConfigs.versions();
        if (versions.isEmpty()) {
            ArrayList<String> rows = new ArrayList<String>();
            rows.add("§7Версий настроек нет — сравнить не с чем");
            return rows;
        }
        return ConfigDiff.againstCurrent(versions.getFirst());
    }

    /** Сколько всего изменений в последней версии — для бейджа или строки статуса. */
    public static int changedCount() {
        List<CloudConfigs.Version> versions = CloudConfigs.versions();
        if (versions.isEmpty()) {
            return 0;
        }
        JsonObject old = ConfigDiff.snapshotOf(versions.getFirst());
        JsonObject now = ConfigDiff.currentSnapshot();
        if (old == null || now == null) {
            return 0;
        }
        return ConfigDiff.changes(old, now).size() + ConfigDiff.toggles(old, now).size() + ConfigDiff.binds(old, now).size();
    }

    private static List<String> moduleNames(JsonObject left, JsonObject right) {
        LinkedHashSet<String> names = new LinkedHashSet<String>();
        if (left != null) {
            names.addAll(left.keySet());
        }
        if (right != null) {
            names.addAll(right.keySet());
        }
        return new ArrayList<String>(names);
    }

    private static JsonObject settingsOf(JsonObject snapshot, String module) {
        if (snapshot == null || !snapshot.has(module) || !snapshot.get(module).isJsonObject()) {
            return new JsonObject();
        }
        JsonObject entry = snapshot.getAsJsonObject(module);
        return entry.has("settings") && entry.get("settings").isJsonObject() ? entry.getAsJsonObject("settings") : new JsonObject();
    }

    private static boolean flagOf(JsonObject snapshot, String module) {
        if (snapshot == null || !snapshot.has(module) || !snapshot.get(module).isJsonObject()) {
            return false;
        }
        JsonObject entry = snapshot.getAsJsonObject(module);
        return entry.has("enabled") && entry.get("enabled").getAsBoolean();
    }

    private static String bindOf(JsonObject snapshot, String module) {
        if (snapshot == null || !snapshot.has(module) || !snapshot.get(module).isJsonObject()) {
            return "нет";
        }
        JsonObject entry = snapshot.getAsJsonObject(module);
        if (!entry.has("bind")) {
            return "нет";
        }
        int code = entry.get("bind").getAsInt();
        return code <= 0 ? "нет" : String.valueOf(code);
    }

    private static String shortValue(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return "";
        }
        String text = element.isJsonPrimitive() ? element.getAsString() : element.toString();
        text = text.replace('\n', ' ').trim();
        if (text.length() > 42) {
            text = text.substring(0, 41) + "…";
        }
        return text;
    }

    /** Название версии без расширения — удобно для команд. */
    public static String label(CloudConfigs.Version version) {
        return version == null ? "?" : version.name.replace(".json", "");
    }

    /** Ищет версию по началу имени (как в командах). */
    public static CloudConfigs.Version find(String needle) {
        if (needle == null || needle.isBlank()) {
            return null;
        }
        String lower = needle.toLowerCase(Locale.ROOT);
        for (CloudConfigs.Version version : CloudConfigs.versions()) {
            if (ConfigDiff.label(version).toLowerCase(Locale.ROOT).contains(lower)) {
                return version;
            }
        }
        return null;
    }
}
