package rtx.byazen.utils.mods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.Person;
import net.fabricmc.loader.api.metadata.ModMetadata;
import rtx.byazen.ByAzen;

/**
 * Каталог загруженных модов (идея №100 из IDEAS.md).
 * <p>
 * Собирает список модов с идентификаторами, версиями и путями к файлам, ищет по ним (в том числе по
 * языковым файлам внутри jar — так видно, какой мод добавил надпись или кнопку) и находит конфликты:
 * повторяющиеся идентификаторы и совпадающие сочетания клавиш.
 */
public final class ModsIndex {

    private ModsIndex() {
    }

    /** Один загруженный мод. */
    public static final class Mod {

        public final String id;
        public final String name;
        public final String version;
        public final Path path;
        public final String authors;
        public final String description;

        Mod(String id, String name, String version, Path path, String authors, String description) {
            this.id = id;
            this.name = name;
            this.version = version;
            this.path = path;
            this.authors = authors;
            this.description = description;
        }

        public String display() {
            return this.name == null || this.name.isEmpty() ? this.id : this.name;
        }

        public String sizeText() {
            try {
                long bytes = Files.size(this.path);
                return bytes < 1048576L
                        ? Math.max(1L, bytes / 1024L) + " КБ"
                        : String.format(Locale.ROOT, "%.1f МБ", (double)bytes / 1048576.0);
            }
            catch (Throwable throwable) {
                return "размер неизвестен";
            }
        }
    }

    /** Совпадение поиска: мод и то, что именно совпало. */
    public static final class Hit {

        public final Mod mod;
        public final String where;

        Hit(Mod mod, String where) {
            this.mod = mod;
            this.where = where;
        }
    }

    /** Конфликт: либо общий идентификатор, либо общая клавиша. */
    public static final class Conflict {

        public final String what;
        public final String who;

        Conflict(String what, String who) {
            this.what = what;
            this.who = who;
        }
    }

    /** Первый автор мода: в лоадере это коллекция, а не список. */
    private static String firstAuthor(ModMetadata metadata) {
        try {
            for (Person person : metadata.getAuthors()) {
                if (person != null && person.getName() != null) {
                    return person.getName();
                }
            }
        }
        catch (Throwable throwable) {
            return "";
        }
        return "";
    }

    /** Список всех модов, отсортированный по названию. */
    public static List<Mod> list() {
        ArrayList<Mod> list = new ArrayList<Mod>();
        try {
            for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
                ModMetadata metadata = container.getMetadata();
                Path path = ModsIndex.pathOf(container);
                String authors = ModsIndex.firstAuthor(metadata);
                String description = metadata.getDescription() == null ? "" : metadata.getDescription();
                list.add(new Mod(metadata.getId(), metadata.getName(), metadata.getVersion().getFriendlyString(),
                        path, authors, description));
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Менеджер модов: {}", throwable.toString());
        }
        list.sort(Comparator.comparing(mod -> mod.display().toLowerCase(Locale.ROOT)));
        return list;
    }

    private static Path pathOf(ModContainer container) {
        try {
            Optional<Path> root = container.getRootPaths().isEmpty() ? Optional.empty() : Optional.of(container.getRootPaths().get(0));
            return root.orElse(null);
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    /** Поиск по названию, идентификатору и описанию, с заглядыванием внутрь файлов. */
    public static List<Hit> search(String query, boolean lookInside) {
        ArrayList<Hit> hits = new ArrayList<Hit>();
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) {
            for (Mod mod : ModsIndex.list()) {
                hits.add(new Hit(mod, "мод"));
            }
            return hits;
        }
        for (Mod mod : ModsIndex.list()) {
            if (mod.id.toLowerCase(Locale.ROOT).contains(needle)) {
                hits.add(new Hit(mod, "идентификатор: " + mod.id));
                continue;
            }
            if (mod.display().toLowerCase(Locale.ROOT).contains(needle)) {
                hits.add(new Hit(mod, "название мода"));
                continue;
            }
            if (mod.authors != null && mod.authors.toLowerCase(Locale.ROOT).contains(needle)) {
                hits.add(new Hit(mod, "автор: " + mod.authors));
                continue;
            }
            if (mod.description != null && mod.description.toLowerCase(Locale.ROOT).contains(needle)) {
                hits.add(new Hit(mod, "описание мода"));
                continue;
            }
            if (lookInside) {
                String inside = ModsIndex.lookInside(mod, needle);
                if (!inside.isEmpty()) {
                    hits.add(new Hit(mod, inside));
                }
            }
        }
        return hits;
    }

    /** Ищет строку в языковых файлах и в описаниях внутри jar-файла мода. */
    private static String lookInside(Mod mod, String needle) {
        if (mod.path == null) {
            return "";
        }
        try {
            if (Files.isDirectory(mod.path)) {
                Path lang = mod.path.resolve("assets");
                if (!Files.isDirectory(lang)) {
                    return "";
                }
                try (var stream = Files.walk(lang)) {
                    for (Path file : stream.filter(Files::isRegularFile).toList()) {
                        if (!file.getFileName().toString().endsWith(".json")) {
                            continue;
                        }
                        String text = Files.readString(file, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
                        if (text.contains(needle)) {
                            return "файл " + mod.path.relativize(file);
                        }
                    }
                }
                return "";
            }
            try (ZipFile zip = new ZipFile(mod.path.toFile())) {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String name = entry.getName().toLowerCase(Locale.ROOT);
                    if (entry.isDirectory() || !name.endsWith(".json") || !name.contains("lang")) {
                        continue;
                    }
                    try (InputStream input = zip.getInputStream(entry)) {
                        String text = new String(input.readAllBytes(), StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
                        if (text.contains(needle)) {
                            return "языковой файл " + entry.getName();
                        }
                    }
                }
            }
        }
        catch (Throwable throwable) {
            return "";
        }
        return "";
    }

    /** Все надписи, в которых встречается строка запроса: подсказка «какой мод добавил эту кнопку». */
    public static List<String> labelsFor(String query) {
        ArrayList<String> result = new ArrayList<String>();
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) {
            return result;
        }
        for (Mod mod : ModsIndex.list()) {
            String inside = ModsIndex.lookInside(mod, needle);
            if (!inside.isEmpty()) {
                result.add(mod.display() + " — " + inside);
            }
        }
        return result;
    }

    /** Конфликты: одинаковые идентификаторы и неоднозначные описания. */
    public static List<Conflict> conflicts() {
        ArrayList<Conflict> conflicts = new ArrayList<Conflict>();
        HashMap<String, StringBuilder> ids = new HashMap<String, StringBuilder>();
        for (Mod mod : ModsIndex.list()) {
            ids.computeIfAbsent(mod.id.toLowerCase(Locale.ROOT), key -> new StringBuilder()).append(mod.display()).append(", ");
        }
        for (Map.Entry<String, StringBuilder> entry : ids.entrySet()) {
            if (entry.getValue().toString().split(", ").length > 1) {
                conflicts.add(new Conflict("идентификатор " + entry.getKey(), entry.getValue().toString()));
            }
        }
        return conflicts;
    }

    /** Группировка модов по автору: удобно, когда модов много. */
    public static Map<String, Integer> byAuthor() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
        for (Mod mod : ModsIndex.list()) {
            String author = mod.authors == null || mod.authors.isEmpty() ? "без автора" : mod.authors;
            map.merge(author, 1, Integer::sum);
        }
        return map;
    }

    public static String summary() {
        List<Mod> list = ModsIndex.list();
        return "загружено модов: " + list.size() + " · конфликтов идентификаторов: " + ModsIndex.conflicts().size();
    }

    /** Разбор строки вида {"name": "..."} — небольшой помощник для языковых файлов. */
    public static String firstValue(String json, String key) {
        try {
            JsonElement element = JsonParser.parseString(json);
            if (element != null && element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                if (object.has(key)) {
                    return object.get(key).getAsString();
                }
            }
        }
        catch (Throwable ignored) {
            // не разобралось — не страшно
        }
        return "";
    }

    /** Клавиша игры вместе с модом, который её объявил. */
    public static final class KeyEntry {

        public final String name;
        public final String category;
        public final String bound;
        public final String mod;

        KeyEntry(String name, String category, String bound, String mod) {
            this.name = name;
            this.category = category;
            this.bound = bound;
            this.mod = mod;
        }

        public String display() {
            return this.mod.isEmpty() ? this.name : this.name + "  ·  " + this.mod;
        }
    }

    /** Все клавиши клиента и моды-владельцы: так сразу видно, «какой мод добавил эту кнопку». */
    public static List<KeyEntry> keys() {
        ArrayList<KeyEntry> result = new ArrayList<KeyEntry>();
        try {
            Object client = net.minecraft.client.MinecraftClient.getInstance();
            if (client == null) {
                return result;
            }
            Object options = ModsIndex.field(client, "options");
            if (options == null) {
                return result;
            }
            Object all = ModsIndex.field(options, "allKeys");
            if (!(all instanceof Object[])) {
                return result;
            }
            Map<String, String> owners = new HashMap<String, String>();
            for (Mod mod : ModsIndex.list()) {
                owners.put(mod.id.toLowerCase(Locale.ROOT), mod.display());
            }
            for (Object binding : (Object[])all) {
                if (binding == null) {
                    continue;
                }
                String translation = String.valueOf(ModsIndex.call(binding, "getTranslationKey"));
                String category = String.valueOf(ModsIndex.call(binding, "getCategory"));
                Object localized = ModsIndex.call(binding, "getBoundKeyLocalizedText");
                String bound = ModsIndex.text(localized);
                if (bound.isEmpty()) {
                    bound = String.valueOf(ModsIndex.call(binding, "getBoundKeyLocalized"));
                }
                String name = ModsIndex.translationOf(translation);
                String mod = "";
                if (translation.startsWith("key.")) {
                    String guess = translation.substring(4);
                    int dot = guess.indexOf(46);
                    if (dot > 0) {
                        guess = guess.substring(0, dot).toLowerCase(Locale.ROOT);
                        mod = owners.getOrDefault(guess, "");
                    }
                }
                if (mod.isEmpty() && !category.isEmpty()) {
                    mod = owners.getOrDefault(category.toLowerCase(Locale.ROOT), "");
                }
                result.add(new KeyEntry(name, category, bound, mod));
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Список клавиш: {}", throwable.toString());
        }
        result.sort(Comparator.comparing(entry -> entry.name.toLowerCase(Locale.ROOT)));
        return result;
    }

    /** Совпадающие сочетания клавиш: их видно в отдельной вкладке окна модов. */
    public static List<String> keyConflicts() {
        ArrayList<String> result = new ArrayList<String>();
        HashMap<String, List<String>> byBound = new HashMap<String, List<String>>();
        for (KeyEntry entry : ModsIndex.keys()) {
            if (entry.bound == null || entry.bound.isEmpty() || entry.bound.equals("не назначено")) {
                continue;
            }
            byBound.computeIfAbsent(entry.bound, key -> new ArrayList<String>()).add(entry.name);
        }
        for (Map.Entry<String, List<String>> pair : byBound.entrySet()) {
            if (pair.getValue().size() > 1) {
                result.add(pair.getKey() + " — " + String.join(", ", pair.getValue()));
            }
        }
        return result;
    }

    private static String translationOf(String translation) {
        String name = translation == null ? "" : translation;
        if (name.startsWith("key.")) {
            int dot = name.lastIndexOf(46);
            return dot > 0 ? name.substring(dot + 1).replace('_', ' ') : name.substring(4);
        }
        return name;
    }

    private static String text(Object value) {
        if (value == null) {
            return "";
        }
        try {
            Object string = ModsIndex.call(value, "getString");
            return string == null ? String.valueOf(value) : String.valueOf(string);
        }
        catch (Throwable throwable) {
            return String.valueOf(value);
        }
    }

    private static Object field(Object target, String name) {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                var declared = type.getDeclaredField(name);
                declared.setAccessible(true);
                return declared.get(target);
            }
            catch (Throwable ignored) {
                type = type.getSuperclass();
            }
        }
        return null;
    }

    private static Object call(Object target, String name) {
        Class<?> type = target.getClass();
        while (type != null) {
            for (var method : type.getDeclaredMethods()) {
                if (!method.getName().equals(name) || method.getParameterCount() != 0) {
                    continue;
                }
                try {
                    method.setAccessible(true);
                    return method.invoke(target);
                }
                catch (Throwable ignored) {
                    // пробуем следующий вариант
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }
}
