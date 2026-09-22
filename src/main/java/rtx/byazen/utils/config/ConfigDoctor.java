package rtx.byazen.utils.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import rtx.byazen.api.config.ConfigManager;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.Setting;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Читаемый JSON с валидатором (идея №157 из IDEAS.md).
 * <p>
 * Конфиг клиента можно прочитать глазами: файл переписывается с отступами и понятными ключами, а
 * проверка объясняет, что в нём не так — модуль, которого больше нет, неизвестная настройка или
 * значение вне допустимых границ. Всё, что можно починить, чинится на месте; остальное попадает в
 * отчёт простым текстом.
 */
public final class ConfigDoctor {

    /** Найденная проблема: где и что делать. */
    public static final class Issue {

        public final String where;
        public final String what;
        public final String fix;

        Issue(String where, String what, String fix) {
            this.where = where;
            this.what = what;
            this.fix = fix;
        }
    }

    /** Итог проверки. */
    public static final class Report {

        public final List<ConfigDoctor.Issue> issues = new ArrayList<ConfigDoctor.Issue>();
        public int modules;
        public int settings;
        public int fixed;
        public int removed;
        public String path = "";

        public String summary() {
            if (this.issues.isEmpty()) {
                return "настройки в порядке: " + this.modules + " модулей, " + this.settings + " настроек";
            }
            return "проблем: " + this.issues.size() + " · исправлено " + this.fixed + " · убрано устаревшего " + this.removed;
        }
    }

    private ConfigDoctor() {
    }

    /** Проверяет и чинит настройки, затем пишет читаемый JSON. */
    public static ConfigDoctor.Report check(boolean repair) {
        ConfigDoctor.Report report = new ConfigDoctor.Report();
        JsonObject root = ConfigManager.buildModuleSnapshot();
        JsonObject modules = root.has("modules") && root.get("modules").isJsonObject()
                ? root.getAsJsonObject("modules") : new JsonObject();
        report.modules = modules.entrySet().size();
        List<String> known = new ArrayList<String>();
        for (Module module : ModuleManager.get().getAll()) {
            known.add(module.getName());
        }
        // неизвестные модули: остались от старой версии клиента
        for (String name : new ArrayList<String>(modules.keySet())) {
            String match = ConfigDoctor.findIgnoreCase(known, name);
            if (match == null) {
                report.issues.add(new ConfigDoctor.Issue("модуль «" + name + "»",
                        "такого модуля больше нет в клиенте", repair ? "запись убрана" : "будет убрана"));
                if (repair) {
                    modules.remove(name);
                    ++report.removed;
                }
                continue;
            }
            if (!match.equals(name) && repair) {
                JsonElement value = modules.remove(name);
                modules.add(match, value);
                report.issues.add(new ConfigDoctor.Issue("модуль «" + name + "»", "другое написание имени", "исправлено на «" + match + "»"));
                ++report.fixed;
            }
        }
        for (Module module : ModuleManager.get().getAll()) {
            JsonObject entry = ConfigDoctor.moduleObject(modules, module.getName());
            if (entry == null || !entry.has("settings") || !entry.getAsJsonObject("settings").isJsonObject()) {
                continue;
            }
            JsonObject values = entry.getAsJsonObject("settings");
            Map<String, Setting> settings = new LinkedHashMap<String, Setting>();
            for (Setting setting : module.getSettings().all()) {
                settings.put(setting.getName(), setting);
            }
            report.settings += values.entrySet().size();
            for (String key : new ArrayList<String>(values.keySet())) {
                String plain = key.contains("#") ? key.substring(0, key.indexOf('#')) : key;
                Setting setting = settings.get(plain);
                if (setting == null) {
                    report.issues.add(new ConfigDoctor.Issue(module.getName() + " → «" + plain + "»",
                            "настройки с таким названием нет", repair ? "запись убрана" : "будет убрана"));
                    if (repair) {
                        values.remove(key);
                        ++report.removed;
                    }
                    continue;
                }
                JsonElement value = values.get(key);
                if (!ConfigDoctor.compatible(setting, value)) {
                    report.issues.add(new ConfigDoctor.Issue(module.getName() + " → «" + plain + "»",
                            "значение не подходит по типу", repair ? "вернули значение по умолчанию" : "будет сброшено"));
                    if (repair) {
                        values.remove(key);
                        ++report.fixed;
                    }
                }
            }
        }
        if (repair) {
            report.path = ConfigDoctor.writeReadable(root);
            ConfigManager.applyModuleSnapshot(root);
        }
        return report;
    }

    /** Пишет читаемый JSON рядом с конфигом и возвращает путь. */
    public static String writeReadable(JsonObject root) {
        try {
            Path directory = RepositoryStorage.configRoot().resolve("readable");
            Files.createDirectories(directory);
            Path file = directory.resolve("settings.json");
            String text = new com.google.gson.GsonBuilder().setPrettyPrinting().serializeNulls().create().toJson(root);
            Files.write(file, text.getBytes(StandardCharsets.UTF_8));
            return file.toString();
        }
        catch (Throwable throwable) {
            return "";
        }
    }

    private static JsonObject moduleObject(JsonObject modules, String name) {
        if (modules.has(name) && modules.get(name).isJsonObject()) {
            return modules.getAsJsonObject(name);
        }
        for (String key : modules.keySet()) {
            if (key.equalsIgnoreCase(name) && modules.get(key).isJsonObject()) {
                return modules.getAsJsonObject(key);
            }
        }
        return null;
    }

    private static String findIgnoreCase(List<String> values, String name) {
        for (String value : values) {
            if (value.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    private static boolean compatible(Setting setting, JsonElement value) {
        if (value == null || value.isJsonNull()) {
            return true;
        }
        if (setting instanceof rtx.byazen.api.modules.settings.impl.BooleanSetting) {
            return value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean();
        }
        if (setting instanceof rtx.byazen.api.modules.settings.impl.SliderSetting
                || setting instanceof rtx.byazen.api.modules.settings.impl.NumberSetting) {
            return value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber();
        }
        return true;
    }
}
