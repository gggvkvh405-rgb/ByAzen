package rtx.byazen.utils.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import rtx.byazen.ByAzen;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Перенос конфигурации одним файлом (идея №101 из IDEAS.md).
 * <p>
 * Собирает все настройки клиента в один код: файлы упаковываются, сжимаются и кодируются Base64 с
 * заголовком версии. Из этого кода можно сделать файл, а можно показать QR — так профиль переносится
 * на другое устройство или передаётся другу. Импорт восстанавливает файлы и сразу применяет настройки.
 */
public final class ConfigTransfer {

    private static final String HEADER = "BZCFG1:";
    private static final String EXPORT_DIR = "exports";
    private static final String SKIP_DIR = "backups";

    private ConfigTransfer() {
    }

    /** Результат операции обмена. */
    public static final class Result {

        public final boolean ok;
        public final String message;
        public final String code;
        public final Path file;
        public final int files;

        Result(boolean ok, String message, String code, Path file, int files) {
            this.ok = ok;
            this.message = message;
            this.code = code == null ? "" : code;
            this.file = file;
            this.files = files;
        }
    }

    /** Папка с подготовленными файлами конфигурации. */
    public static Path directory() {
        return RepositoryStorage.configRoot().resolve(EXPORT_DIR);
    }

    /** Собирает код всей конфигурации. */
    public static String currentCode() {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("byazen", 1);
            root.addProperty("created", System.currentTimeMillis());
            JsonObject files = new JsonObject();
            Path base = RepositoryStorage.configRoot();
            int count = 0;
            try (Stream<Path> stream = Files.walk(base)) {
                List<Path> list = stream.filter(Files::isRegularFile)
                        .filter(path -> !ConfigTransfer.skipped(base, path))
                        .sorted()
                        .toList();
                for (Path path : list) {
                    String relative = base.relativize(path).toString().replace('\\', '/');
                    JsonObject entry = new JsonObject();
                    entry.addProperty("b64", Base64.getEncoder().encodeToString(Files.readAllBytes(path)));
                    entry.addProperty("size", Files.size(path));
                    files.add(relative, entry);
                    ++count;
                }
            }
            root.addProperty("count", count);
            root.add("files", files);
            byte[] packed = ConfigTransfer.pack(root.toString().getBytes(StandardCharsets.UTF_8));
            return HEADER + Base64.getUrlEncoder().withoutPadding().encodeToString(packed);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Не удалось собрать конфигурацию: {}", throwable.toString());
            return "";
        }
    }

    private static boolean skipped(Path base, Path path) {
        String relative = base.relativize(path).toString().replace('\\', '/');
        return relative.startsWith(SKIP_DIR + "/") || relative.startsWith(EXPORT_DIR + "/");
    }

    private static byte[] pack(byte[] raw) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(buffer)) {
            gzip.write(raw);
        }
        return buffer.toByteArray();
    }

    private static byte[] unpack(byte[] packed) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(packed))) {
            byte[] chunk = new byte[8192];
            int read;
            while ((read = gzip.read(chunk)) > 0) {
                buffer.write(chunk, 0, read);
            }
        }
        return buffer.toByteArray();
    }

    /** Сохраняет код в файл в папке экспорта. */
    public static Result exportToFile() {
        String code = ConfigTransfer.currentCode();
        if (code.isEmpty()) {
            return new Result(false, "Не удалось собрать конфигурацию: папка настроек недоступна.", "", null, 0);
        }
        try {
            Files.createDirectories(ConfigTransfer.directory());
            String name = "byazen-config-"
                    + new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ROOT).format(new java.util.Date())
                    + ".bycfg";
            Path file = ConfigTransfer.directory().resolve(name);
            Files.writeString(file, code, StandardCharsets.UTF_8);
            return new Result(true, "Файл сохранён: " + name, code, file, ConfigTransfer.countFiles(code));
        }
        catch (Throwable throwable) {
            return new Result(false, "Ошибка записи файла: " + throwable.getClass().getSimpleName(), code, null, 0);
        }
    }

    /** Импортирует конфигурацию из кода. */
    public static Result importCode(String code) {
        String clean = ConfigTransfer.normalize(code);
        if (clean.isEmpty()) {
            return new Result(false, "Код пустой или повреждён.", "", null, 0);
        }
        try {
            byte[] packed = Base64.getUrlDecoder().decode(clean.substring(HEADER.length()));
            byte[] raw = ConfigTransfer.unpack(packed);
            JsonElement element = JsonParser.parseString(new String(raw, StandardCharsets.UTF_8));
            if (element == null || !element.isJsonObject()) {
                return new Result(false, "В коде нет данных конфигурации.", "", null, 0);
            }
            JsonObject root = element.getAsJsonObject();
            if (!root.has("files") || !root.get("files").isJsonObject()) {
                return new Result(false, "Код не содержит файлов настроек.", "", null, 0);
            }
            Path base = RepositoryStorage.configRoot();
            Files.createDirectories(base);
            int count = 0;
            for (var pair : root.getAsJsonObject("files").entrySet()) {
                JsonElement value = pair.getValue();
                if (value == null || !value.isJsonObject()) {
                    continue;
                }
                JsonObject entry = value.getAsJsonObject();
                String encoded = entry.has("b64") ? entry.get("b64").getAsString() : "";
                if (encoded.isEmpty()) {
                    continue;
                }
                Path target = base.resolve(pair.getKey()).normalize();
                if (!target.startsWith(base)) {
                    continue;
                }
                Path parent = target.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                Files.write(target, Base64.getDecoder().decode(encoded));
                ++count;
            }
            if (count == 0) {
                return new Result(false, "В коде не нашлось ни одного файла настроек.", "", null, 0);
            }
            rtx.byazen.api.config.ConfigManager.loadAll();
            return new Result(true, "Конфигурация применена: файлов " + count + ".", clean, null, count);
        }
        catch (Throwable throwable) {
            return new Result(false, "Не удалось прочитать код: " + throwable.getClass().getSimpleName(), "", null, 0);
        }
    }

    /** Импортирует самый свежий файл из папки экспорта. */
    public static Result importLatest() {
        List<Path> files = ConfigTransfer.files();
        if (files.isEmpty()) {
            return new Result(false, "Файлов экспорта пока нет — сначала сохраните конфигурацию.", "", null, 0);
        }
        Path latest = files.get(0);
        try {
            Result result = ConfigTransfer.importCode(Files.readString(latest, StandardCharsets.UTF_8));
            if (!result.ok) {
                return result;
            }
            return new Result(true, "Импорт из файла " + latest.getFileName() + ": " + result.message, result.code, latest, result.files);
        }
        catch (Throwable throwable) {
            return new Result(false, "Файл не читается: " + throwable.getClass().getSimpleName(), "", latest, 0);
        }
    }

    /** Файлы экспорта, свежие сверху. */
    public static List<Path> files() {
        ArrayList<Path> list = new ArrayList<Path>();
        Path directory = ConfigTransfer.directory();
        if (!Files.isDirectory(directory)) {
            return list;
        }
        try (Stream<Path> stream = Files.list(directory)) {
            list.addAll(stream.filter(path -> path.getFileName().toString().endsWith(".bycfg")).toList());
        }
        catch (Throwable throwable) {
            return list;
        }
        list.sort(Comparator.comparingLong((Path path) -> {
            try {
                return -Files.getLastModifiedTime(path).toMillis();
            }
            catch (Throwable throwable) {
                return 0L;
            }
        }));
        return list;
    }

    /** Проверяет, похож ли текст на код конфигурации. */
    public static boolean looksLikeCode(String text) {
        return text != null && ConfigTransfer.normalize(text).startsWith(HEADER);
    }

    /** Приводит вставленный текст к чистому коду. */
    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String clean = text.replaceAll("\\s+", "").trim();
        int start = clean.indexOf(HEADER);
        if (start < 0) {
            return "";
        }
        return clean.substring(start);
    }

    /** Сколько байт занимает код: подсказка, поместится ли он в QR. */
    public static int codeBytes(String code) {
        return code == null ? 0 : code.getBytes(StandardCharsets.UTF_8).length;
    }

    /** Размер кода в удобном виде. */
    public static String sizeText(int bytes) {
        if (bytes < 1024) {
            return bytes + " Б";
        }
        return String.format(Locale.ROOT, "%.1f КБ", (double)bytes / 1024.0);
    }

    private static int countFiles(String code) {
        try {
            byte[] packed = Base64.getUrlDecoder().decode(ConfigTransfer.normalize(code).substring(HEADER.length()));
            JsonElement element = JsonParser.parseString(new String(ConfigTransfer.unpack(packed), StandardCharsets.UTF_8));
            if (element != null && element.isJsonObject() && element.getAsJsonObject().has("count")) {
                return element.getAsJsonObject().get("count").getAsInt();
            }
        }
        catch (Throwable ignored) {
            // количество не определить
        }
        return 0;
    }
}
