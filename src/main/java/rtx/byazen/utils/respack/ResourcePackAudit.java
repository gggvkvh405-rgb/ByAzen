package rtx.byazen.utils.respack;

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
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.fabricmc.loader.api.FabricLoader;
import rtx.byazen.ByAzen;

/**
 * Ревизор ресурспаков (идея №115 из IDEAS.md).
 * <p>
 * Просматривает наборы в папке resourcepacks: сколько файлов, какого размера, какая версия упаковки,
 * какие текстуры самые крупные, нет ли ошибок упаковки и где наборы перекрывают друг друга. Картинки
 * читаются только по заголовку — поэтому проверка десятков наборов занимает мгновения и не ест память.
 */
public final class ResourcePackAudit {

    private static final String FOLDER = "resourcepacks";
    private static final int TEXTURE_LIMIT = 400;
    private static final int BIG_TEXTURE = 1024;
    private static final int HUGE_TEXTURE = 2048;

    private ResourcePackAudit() {
    }

    /** Один набор ресурсов. */
    public static final class Pack {

        public final String name;
        public final Path path;
        public final boolean archive;
        public boolean enabled;
        public int files;
        public long bytes;
        public int format = -1;
        public int maxTexture;
        public int oversized;
        public final List<String> problems = new ArrayList<String>();

        Pack(String name, Path path, boolean archive) {
            this.name = name;
            this.path = path;
            this.archive = archive;
        }

        public String sizeText() {
            if (this.bytes < 1048576L) {
                return Math.max(1L, this.bytes / 1024L) + " КБ";
            }
            return String.format(Locale.ROOT, "%.1f МБ", (double)this.bytes / 1048576.0);
        }

        public String formatText() {
            if (this.format < 0) {
                return "версия не указана";
            }
            return "формат " + this.format;
        }

        public String state() {
            if (!this.problems.isEmpty()) {
                return this.problems.get(0);
            }
            return this.enabled ? "включён" : "выключен";
        }
    }

    /** Папка с наборами ресурсов. */
    public static Path directory() {
        try {
            return FabricLoader.getInstance().getGameDir().resolve(FOLDER);
        }
        catch (Throwable throwable) {
            return Path.of(FOLDER);
        }
    }

    /** Пересчитывает все наборы, крупные сверху. */
    public static List<Pack> scan() {
        ArrayList<Pack> packs = new ArrayList<Pack>();
        Path directory = ResourcePackAudit.directory();
        if (!Files.isDirectory(directory)) {
            return packs;
        }
        try (Stream<Path> paths = Files.list(directory)) {
            List<Path> found = paths.sorted().toList();
            for (Path path : found) {
                String name = path.getFileName().toString();
                if (Files.isDirectory(path)) {
                    Pack pack = new Pack(name, path, false);
                    ResourcePackAudit.inspect(pack);
                    packs.add(pack);
                }
                else if (name.toLowerCase(Locale.ROOT).endsWith(".zip")) {
                    Pack pack = new Pack(name, path, true);
                    ResourcePackAudit.inspect(pack);
                    packs.add(pack);
                }
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Ревизор ресурспаков: {}", throwable.toString());
        }
        Map<String, Boolean> enabled = ResourcePackAudit.enabledPacks();
        for (Pack pack : packs) {
            String key = pack.name.toLowerCase(Locale.ROOT);
            Boolean state = enabled.get(key);
            if (state != null) {
                pack.enabled = state;
            }
        }
        packs.sort(Comparator.comparingLong(pack -> -pack.bytes));
        return packs;
    }

    private static void inspect(Pack pack) {
        try {
            if (pack.archive) {
                try (ZipFile zip = new ZipFile(pack.path.toFile())) {
                    Enumeration<? extends ZipEntry> entries = zip.entries();
                    int textures = 0;
                    boolean assets = false;
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (entry.isDirectory()) {
                            continue;
                        }
                        ++pack.files;
                        pack.bytes += entry.getSize() > 0L ? entry.getSize() : entry.getCompressedSize();
                        String name = entry.getName();
                        if (name.startsWith("assets/")) {
                            assets = true;
                        }
                        if (name.equals("pack.mcmeta")) {
                            try (InputStream stream = zip.getInputStream(entry)) {
                                pack.format = ResourcePackAudit.format(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
                            }
                        }
                        if (textures < TEXTURE_LIMIT && name.toLowerCase(Locale.ROOT).endsWith(".png")) {
                            ++textures;
                            ResourcePackAudit.measure(pack, zip.getInputStream(entry));
                        }
                    }
                    if (!assets) {
                        pack.problems.add("нет папки assets — набор ничего не заменяет");
                    }
                    if (pack.format < 0) {
                        pack.problems.add("нет pack.mcmeta — версия не объявлена");
                    }
                }
            }
            else {
                try (Stream<Path> paths = Files.walk(pack.path)) {
                    for (Path file : paths.toList()) {
                        if (!Files.isRegularFile(file)) {
                            continue;
                        }
                        ++pack.files;
                        try {
                            pack.bytes += Files.size(file);
                        }
                        catch (Throwable ignored) {
                            // размер недоступен
                        }
                        String relative = pack.path.relativize(file).toString().replace('\\', '/');
                        if (relative.equals("pack.mcmeta")) {
                            pack.format = ResourcePackAudit.format(Files.readString(file, StandardCharsets.UTF_8));
                        }
                        if (pack.maxTexture < HUGE_TEXTURE && relative.toLowerCase(Locale.ROOT).endsWith(".png")) {
                            try (InputStream stream = Files.newInputStream(file)) {
                                ResourcePackAudit.measure(pack, stream);
                            }
                        }
                    }
                }
                catch (Throwable throwable) {
                    pack.problems.add("набор повреждён: " + throwable.getClass().getSimpleName());
                }
            }
        }
        catch (Throwable throwable) {
            pack.problems.add("не читается: " + throwable.getClass().getSimpleName());
        }
        if (pack.format >= 0 && (pack.format < 20 || pack.format > 70)) {
            pack.problems.add("формат " + pack.format + " может не подойти этой версии игры");
        }
    }

    /** Размер картинки читается по заголовку — без полной загрузки в память. */
    private static void measure(Pack pack, InputStream stream) {
        try (ImageInputStream input = ImageIO.createImageInputStream(stream)) {
            if (input == null) {
                return;
            }
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                return;
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                int size = Math.max(width, height);
                if (size > pack.maxTexture) {
                    pack.maxTexture = size;
                }
                if (size > BIG_TEXTURE) {
                    ++pack.oversized;
                }
            }
            finally {
                reader.dispose();
            }
        }
        catch (Throwable ignored) {
            // картинка повреждена — набор всё равно показываем
        }
    }

    /** Версия упаковки из pack.mcmeta: поддерживаются обе формы записи. */
    public static int format(String json) {
        try {
            JsonElement element = JsonParser.parseString(json);
            if (element == null || !element.isJsonObject()) {
                return -1;
            }
            JsonObject root = element.getAsJsonObject();
            JsonElement pack = root.has("pack") ? root.get("pack") : root;
            if (pack == null || !pack.isJsonObject()) {
                return -1;
            }
            JsonObject object = pack.getAsJsonObject();
            if (!object.has("pack_format")) {
                return -1;
            }
            JsonElement value = object.get("pack_format");
            if (value.isJsonPrimitive()) {
                return value.getAsInt();
            }
            if (value.isJsonObject() && value.getAsJsonObject().has("pack_format")) {
                return value.getAsJsonObject().get("pack_format").getAsInt();
            }
            return -1;
        }
        catch (Throwable throwable) {
            return -1;
        }
    }

    /** Какие наборы включены в игре: читается через настройки, если они доступны. */
    public static Map<String, Boolean> enabledPacks() {
        HashMap<String, Boolean> result = new HashMap<String, Boolean>();
        try {
            Object client = net.minecraft.client.MinecraftClient.getInstance();
            if (client == null) {
                return result;
            }
            Object manager = ResourcePackAudit.reflectField(client, "resourcePackManager");
            if (manager == null) {
                return result;
            }
            Object profiles = ResourcePackAudit.reflectCall(manager, "getEnabledProfiles", "getEnabledIds", "getEnabledNames");
            if (profiles instanceof Iterable<?>) {
                for (Object entry : (Iterable<?>)profiles) {
                    String name = String.valueOf(entry).toLowerCase(Locale.ROOT);
                    result.put(name, true);
                    int slash = name.lastIndexOf(47);
                    if (slash > 0) {
                        result.put(name.substring(slash + 1), true);
                    }
                }
            }
        }
        catch (Throwable ignored) {
            // настройки недоступны — просто покажем наборы без пометки
        }
        return result;
    }

    private static Object reflectField(Object target, String name) {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                var field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(target);
            }
            catch (Throwable ignored) {
                type = type.getSuperclass();
            }
        }
        return null;
    }

    private static Object reflectCall(Object target, String... names) {
        for (String name : names) {
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
        }
        return null;
    }

    /** Перекрытия: файлы, которые заменяют сразу несколько наборов. */
    public static List<String> overlaps(List<Pack> packs) {
        LinkedHashMap<String, List<String>> owners = new LinkedHashMap<String, List<String>>();
        for (Pack pack : packs) {
            if (pack.files == 0) {
                continue;
            }
            for (String file : ResourcePackAudit.files(pack, 60)) {
                owners.computeIfAbsent(file, key -> new ArrayList<String>()).add(pack.name);
            }
        }
        ArrayList<String> result = new ArrayList<String>();
        for (Map.Entry<String, List<String>> entry : owners.entrySet()) {
            if (entry.getValue().size() > 1) {
                result.add(entry.getKey() + " — " + String.join(", ", entry.getValue()));
                if (result.size() >= 12) {
                    break;
                }
            }
        }
        return result;
    }

    /** Первые файлы набора: нужны для поиска перекрытий. */
    public static List<String> files(Pack pack, int limit) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            if (pack.archive) {
                try (ZipFile zip = new ZipFile(pack.path.toFile())) {
                    Enumeration<? extends ZipEntry> entries = zip.entries();
                    while (entries.hasMoreElements() && result.size() < limit) {
                        ZipEntry entry = entries.nextElement();
                        if (!entry.isDirectory() && entry.getName().startsWith("assets/")) {
                            result.add(entry.getName());
                        }
                    }
                }
            }
            else {
                try (Stream<Path> paths = Files.walk(pack.path)) {
                    List<Path> all = paths.filter(Files::isRegularFile).sorted().toList();
                    for (Path file : all) {
                        if (result.size() >= limit) {
                            break;
                        }
                        result.add(pack.path.relativize(file).toString().replace('\\', '/'));
                    }
                }
            }
        }
        catch (Throwable ignored) {
            // не удалось перечислить файлы
        }
        return result;
    }

    /** Полный отчёт для окна и чата. */
    public static List<String> report() {
        ArrayList<String> lines = new ArrayList<String>();
        List<Pack> packs = ResourcePackAudit.scan();
        if (packs.isEmpty()) {
            lines.add("Наборов ресурсов нет — папка resourcepacks пустая.");
            return lines;
        }
        lines.add("Наборов: " + packs.size() + ", из них включено: " + packs.stream().filter(pack -> pack.enabled).count());
        for (Pack pack : packs) {
            lines.add(pack.name + " — " + pack.sizeText() + ", файлов " + pack.files + ", " + pack.formatText()
                    + (pack.maxTexture > 0 ? ", крупнейшая текстура " + pack.maxTexture + " px" : ""));
            for (String problem : pack.problems) {
                lines.add("    ! " + problem);
            }
        }
        List<String> overlaps = ResourcePackAudit.overlaps(packs);
        if (!overlaps.isEmpty()) {
            lines.add("Перекрытия:");
            lines.addAll(overlaps);
        }
        return lines;
    }

    /** Краткий итог: одна строка для уведомления при входе в игру. */
    public static String summary() {
        List<Pack> packs = ResourcePackAudit.scan();
        if (packs.isEmpty()) {
            return "ресурспаков нет";
        }
        int problems = 0;
        int oversized = 0;
        for (Pack pack : packs) {
            problems += pack.problems.size();
            oversized += pack.oversized;
        }
        String state = problems == 0 ? "в порядке" : "замечаний: " + problems;
        return "ресурспаков: " + packs.size() + " · " + state + (oversized > 0 ? " · крупных текстур: " + oversized : "");
    }

    /** Быстрая проверка одной строкой. */
    public static String quick() {
        List<Pack> packs = ResourcePackAudit.scan();
        if (packs.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Pack pack : packs) {
            if (!pack.problems.isEmpty()) {
                if (builder.length() > 0) {
                    builder.append("; ");
                }
                builder.append(pack.name).append(": ").append(pack.problems.get(0));
            }
        }
        return builder.toString();
    }
}
