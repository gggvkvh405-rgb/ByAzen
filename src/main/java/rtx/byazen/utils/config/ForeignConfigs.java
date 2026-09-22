package rtx.byazen.utils.config;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.utils.key.KeyBind;

/**
 * Импорт настроек других клиентов (идея №153 из IDEAS.md).
 * <p>
 * Переезд на ByAzen больше не начинается с нуля: клиент ищет рядом с игрой папки знакомых
 * клиентов (Lunar, Badlion, Feather, LabyMod, Meteor, Wurst и другие), аккуратно вычитывает
 * клавиши и основные ползунки из их конфигов и переносит к нам. Всё чтение защищено: непонятный
 * файл просто пропускается, ничего не ломается.
 */
public final class ForeignConfigs {

    /** Найденный клиент: имя, папка и что из неё можно взять. */
    public static final class Found {

        public final String name;
        public final Path path;
        public final boolean readable;
        public final int binds;
        public final int sliders;
        public final String note;

        Found(String name, Path path, boolean readable, int binds, int sliders, String note) {
            this.name = name;
            this.path = path;
            this.readable = readable;
            this.binds = binds;
            this.sliders = sliders;
            this.note = note;
        }

        public String summary() {
            if (!this.readable) {
                return this.note;
            }
            return "клавиш: " + this.binds + " · ползунков: " + this.sliders;
        }
    }

    /** Итог переноса: сколько клавиш и настроек применилось. */
    public static final class Report {

        public int binds;
        public int sliders;
        public final List<String> notes = new ArrayList<String>();
    }

    /** Кандидаты: имя клиента и его папки относительно игры. */
    private static final String[][] CLIENTS = {
            {"Lunar Client", ".lunarclient", ".lunarclient/settings"},
            {"Badlion", "Badlion Client", "Badlion Client/settings"},
            {"Feather", "feather", ".feather"},
            {"LabyMod", "labymod", ".minecraft/labymod"},
            {"Meteor Client", "meteor-client", ".meteor"},
            {"Wurst", "wurst", ".wurst"},
            {"Impact", "Impact", "Impact"},
            {"Salwyrr", "Salwyrr", "Salwyrr"},
            {"Ванильная игра", "options.txt", "options.txt"},
    };

    /** Соответствие названий клавиш из других клиентов нашим модулям. */
    private static final String[][] ALIASES = {
            {"freelook", "Freelook"},
            {"perspective", "Freelook"},
            {"sprint", "AutoSprint"},
            {"fullbright", "Night Vision"},
            {"brightness", "Night Vision"},
            {"nametag", "NameTags"},
            {"name tag", "NameTags"},
            {"clickgui", "ClickGui"},
            {"click gui", "ClickGui"},
            {"hud", "HUD Polish"},
            {"music", "Music Player"},
            {"keystrokes", "KeyStrokes"},
            {"key strokes", "KeyStrokes"},
            {"crosshair", "Crosshair"},
            {"chat", "Quick Chat"},
            {"trajectories", "Trajectories"},
            {"waypoint", "Waypoints"},
            {"minimap", "Minimap"},
            {"particles", "Custom Particles"},
            {"china hat", "China Hat"},
            {"hit sound", "Hit Sound"},
            {"hitsound", "Hit Sound"},
            {"potions", "Potions"},
            {"item physics", "Item Physics"},
            {"shulker", "Shulker Preview"},
            {"inventory", "InventoryPlus"},
            {"timer", "Timer Alarms"},
            {"watermark", "Watermark"},
            {"optimization", "Optimization"},
            {"screenshots", "Screenshots"},
    };

    private ForeignConfigs() {
    }

    /** Ищет папки других клиентов рядом с игрой. */
    public static List<ForeignConfigs.Found> detect() {
        ArrayList<ForeignConfigs.Found> list = new ArrayList<ForeignConfigs.Found>();
        Path root = ForeignConfigs.gameRoot();
        if (root == null) {
            return list;
        }
        for (String[] client : CLIENTS) {
            String name = client[0];
            Path path = root.resolve(client[1]);
            if (!Files.exists(path)) {
                continue;
            }
            ForeignConfigs.ImportData data = ForeignConfigs.read(path);
            list.add(new ForeignConfigs.Found(name, path, data.readable, data.binds.size(), data.sliders.size(),
                    data.readable ? data.note : "не удалось прочитать (" + data.note + ")"));
        }
        // папки рядом с игрой, в названии которых есть «client» — тоже интересны
        try (Stream<Path> stream = Files.list(root)) {
            for (Path path : (Iterable<Path>)stream::iterator) {
                if (!Files.isDirectory(path)) {
                    continue;
                }
                String fileName = path.getFileName().toString();
                String lower = fileName.toLowerCase(Locale.ROOT);
                if (!lower.contains("client") || lower.contains("byazen")) {
                    continue;
                }
                boolean known = false;
                for (ForeignConfigs.Found found : list) {
                    if (found.path.equals(path)) {
                        known = true;
                        break;
                    }
                }
                if (known) {
                    continue;
                }
                ForeignConfigs.ImportData data = ForeignConfigs.read(path);
                if (data.readable && (data.binds.size() > 0 || data.sliders.size() > 0)) {
                    list.add(new ForeignConfigs.Found(fileName, path, true, data.binds.size(), data.sliders.size(), data.note));
                }
            }
        }
        catch (Throwable ignored) {
            // нет доступа к папке — просто меньше найдём
        }
        return list;
    }

    /** Переносит клавиши и основные ползунки из найденного клиента. */
    public static ForeignConfigs.Report apply(ForeignConfigs.Found found) {
        ForeignConfigs.Report report = new ForeignConfigs.Report();
        if (found == null) {
            return report;
        }
        ForeignConfigs.ImportData data = ForeignConfigs.read(found.path);
        if (!data.readable) {
            report.notes.add("конфиг не читается: " + data.note);
            return report;
        }
        for (Map.Entry<String, Integer> pair : data.binds.entrySet()) {
            String moduleName = ForeignConfigs.moduleFor(pair.getKey());
            if (moduleName == null) {
                continue;
            }
            Module module = ModuleManager.get().findByName(moduleName);
            if (module == null) {
                continue;
            }
            module.setBind(new KeyBind(pair.getValue()));
            ++report.binds;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            for (Map.Entry<String, Integer> pair : data.sliders.entrySet()) {
                try {
                    switch (pair.getKey()) {
                        case "fov":
                            client.options.getFov().setValue(pair.getValue());
                            ++report.sliders;
                            break;
                        case "renderDistance":
                            client.options.getViewDistance().setValue(pair.getValue());
                            ++report.sliders;
                            break;
                        default:
                            break;
                    }
                }
                catch (Throwable throwable) {
                    report.notes.add("ползунок " + pair.getKey() + " не применился");
                }
            }
        }
        rtx.byazen.api.config.ConfigManager.markDirty();
        if (report.binds == 0 && report.sliders == 0) {
            report.notes.add("нечего переносить: в конфиге нет знакомых клавиш");
        }
        return report;
    }

    private static String moduleFor(String key) {
        String lower = key.toLowerCase(Locale.ROOT).replace('_', ' ').replace('-', ' ');
        for (String[] alias : ALIASES) {
            if (lower.contains(alias[0])) {
                return alias[1];
            }
        }
        return null;
    }

    private static final class ImportData {

        final Map<String, Integer> binds = new LinkedHashMap<String, Integer>();
        final Map<String, Integer> sliders = new LinkedHashMap<String, Integer>();
        boolean readable;
        String note = "пусто";
    }

    private static ForeignConfigs.ImportData read(Path path) {
        ForeignConfigs.ImportData data = new ForeignConfigs.ImportData();
        List<Path> files = new ArrayList<Path>();
        if (Files.isRegularFile(path)) {
            files.add(path);
        }
        else {
            ForeignConfigs.collect(path, 0, files);
        }
        for (Path file : files) {
            try {
                if (file.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".json")) {
                    ForeignConfigs.readJson(new String(Files.readAllBytes(file), StandardCharsets.UTF_8), data);
                }
                else {
                    ForeignConfigs.readLines(Files.readAllLines(file, StandardCharsets.UTF_8), data);
                }
                data.readable = true;
            }
            catch (Throwable ignored) {
                // отдельный файл не читается — не беда
            }
        }
        if (data.readable) {
            data.note = "файлов: " + files.size();
        }
        return data;
    }

    private static void collect(Path directory, int depth, List<Path> files) {
        if (depth > 3 || files.size() > 60) {
            return;
        }
        try (Stream<Path> stream = Files.list(directory)) {
            for (Path path : (Iterable<Path>)stream::iterator) {
                if (Files.isDirectory(path)) {
                    ForeignConfigs.collect(path, depth + 1, files);
                    continue;
                }
                String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
                if (name.endsWith(".json") || name.endsWith(".txt") || name.endsWith(".cfg") || name.endsWith(".properties")) {
                    files.add(path);
                }
            }
        }
        catch (Throwable ignored) {
            // недоступная папка
        }
    }

    private static void readJson(String text, ForeignConfigs.ImportData data) {
        try {
            com.google.gson.JsonElement root = com.google.gson.JsonParser.parseString(text);
            ForeignConfigs.walk(root, "", data);
        }
        catch (Throwable ignored) {
            // не JSON — не страшно
        }
    }

    private static void walk(com.google.gson.JsonElement element, String key, ForeignConfigs.ImportData data) {
        if (element.isJsonObject()) {
            for (Map.Entry<String, com.google.gson.JsonElement> pair : element.getAsJsonObject().entrySet()) {
                ForeignConfigs.walk(pair.getValue(), key.isEmpty() ? pair.getKey() : key + "." + pair.getKey(), data);
            }
            return;
        }
        if (element.isJsonArray()) {
            for (com.google.gson.JsonElement child : element.getAsJsonArray()) {
                ForeignConfigs.walk(child, key, data);
            }
            return;
        }
        if (!element.isJsonPrimitive()) {
            return;
        }
        String lower = key.toLowerCase(Locale.ROOT);
        if (element.getAsJsonPrimitive().isNumber()) {
            int value = (int)element.getAsInt();
            if (ForeignConfigs.looksLikeBind(lower) && value >= 0 && value < 512) {
                data.binds.put(ForeignConfigs.shortKey(key), value);
            }
            else if (lower.contains("fov")) {
                data.sliders.put("fov", Math.max(30, Math.min(110, value)));
            }
            else if (lower.contains("render") && lower.contains("distance")) {
                data.sliders.put("renderDistance", Math.max(2, Math.min(32, value)));
            }
            return;
        }
        String text = element.getAsString();
        int code = ForeignConfigs.codeFromText(text);
        if (ForeignConfigs.looksLikeBind(lower) && code >= 0) {
            data.binds.put(ForeignConfigs.shortKey(key), code);
        }
    }

    private static void readLines(List<String> lines, ForeignConfigs.ImportData data) {
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("//")) {
                continue;
            }
            int separator = trimmed.indexOf(':');
            if (separator <= 0) {
                separator = trimmed.indexOf('=');
            }
            if (separator <= 0) {
                continue;
            }
            String key = trimmed.substring(0, separator).trim();
            String value = trimmed.substring(separator + 1).trim();
            String lower = key.toLowerCase(Locale.ROOT);
            int code = ForeignConfigs.codeFromText(value);
            if (lower.startsWith("key_") && code >= 0) {
                data.binds.put(ForeignConfigs.shortKey(key), code);
                continue;
            }
            if (lower.equals("fov") || lower.equals("gamma") || lower.contains("renderdistance")) {
                try {
                    int number = (int)Double.parseDouble(value);
                    if (lower.equals("fov")) {
                        data.sliders.put("fov", Math.max(30, Math.min(110, number)));
                    }
                    else if (lower.contains("renderdistance")) {
                        data.sliders.put("renderDistance", Math.max(2, Math.min(32, number)));
                    }
                }
                catch (Throwable ignored) {
                    // не число
                }
            }
        }
    }

    private static boolean looksLikeBind(String key) {
        return key.startsWith("key_") || key.startsWith("key.") || key.contains("keybind")
                || key.contains("bind.") || key.endsWith(".key") || key.contains("hotkey");
    }

    private static String shortKey(String key) {
        int separator = key.lastIndexOf('.');
        String value = separator >= 0 ? key.substring(separator + 1) : key;
        return value.replace("key_", "").trim();
    }

    /** Распознаёт код клавиши: GLFW-имя, «key.mouse.left» или обычное имя. */
    static int codeFromText(String text) {
        if (text == null || text.isBlank()) {
            return -1;
        }
        String value = text.trim().toLowerCase(Locale.ROOT);
        if (value.chars().allMatch(Character::isDigit)) {
            try {
                return Integer.parseInt(value);
            }
            catch (Throwable ignored) {
                return -1;
            }
        }
        if (value.contains("mouse.left")) {
            return 0;
        }
        if (value.contains("mouse.right")) {
            return 1;
        }
        if (value.contains("mouse.middle")) {
            return 2;
        }
        if (value.contains("space")) {
            return 32;
        }
        if (value.contains("shift")) {
            return 340;
        }
        if (value.contains("control") || value.contains("ctrl")) {
            return 341;
        }
        if (value.contains("alt")) {
            return 342;
        }
        if (value.contains("tab")) {
            return 258;
        }
        if (value.contains("enter")) {
            return 257;
        }
        if (value.contains("escape")) {
            return 256;
        }
        String letters = value.replace("key.keyboard.", "").replace("key.", "");
        if (letters.length() == 1 && Character.isLetterOrDigit(letters.charAt(0))) {
            return 65 + Character.toUpperCase(letters.charAt(0)) - 65;
        }
        if (letters.startsWith("f") && letters.length() <= 3) {
            try {
                int index = Integer.parseInt(letters.substring(1));
                if (index >= 1 && index <= 25) {
                    return 289 + index;
                }
            }
            catch (Throwable ignored) {
                return -1;
            }
        }
        return -1;
    }

    private static Path gameRoot() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.runDirectory == null) {
            return null;
        }
        Path path = client.runDirectory.toPath();
        return Files.isDirectory(path) ? path : null;
    }
}
