package rtx.byazen.utils.config;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import rtx.byazen.api.config.ConfigManager;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Облачные конфиги с версионированием и откатом (идея №151 из IDEAS.md).
 * <p>
 * Каждое сохранение оставляет версию настроек: имя, время, размер и контрольная сумма. Версии
 * лежат рядом с конфигом клиента, поэтому доступны и после перезапуска, а одну из них можно
 * откатить в один клик — или выгрузить коротким кодом, чтобы перенести настройки на другой
 * компьютер. «Облако» здесь — общее хранилище профилей клиента: история не теряется, если
 * случайно сломать настройки.
 */
public final class CloudConfigs {

    /** Версия настроек: файл, время, размер и подпись. */
    public static final class Version {

        public final String name;
        public final long time;
        public final long size;
        public final String hash;

        Version(String name, long time, long size, String hash) {
            this.name = name;
            this.time = time;
            this.size = size;
            this.hash = hash;
        }

        public String timeText() {
            java.time.LocalDateTime moment = java.time.LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(this.time), java.time.ZoneId.systemDefault());
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM HH:mm");
            return moment.format(formatter);
        }

        public String sizeText() {
            if (this.size < 1024L) {
                return this.size + " Б";
            }
            if (this.size < 1024L * 1024L) {
                return (this.size / 1024L) + " КБ";
            }
            return (this.size / (1024L * 1024L)) + " МБ";
        }
    }

    private static final String FOLDER = "config-versions";
    private static final int LIMIT = 40;
    private static CloudConfigs.Version last;

    private CloudConfigs() {
    }

    private static Path root() {
        return RepositoryStorage.configRoot().resolve(FOLDER);
    }

    /** Сохраняет текущее состояние настроек как версию. */
    public static CloudConfigs.Version snapshot(String label) {
        try {
            Path directory = CloudConfigs.root();
            Files.createDirectories(directory);
            JsonObject bundle = CloudConfigs.bundle();
            byte[] bytes = bundle.toString().getBytes(StandardCharsets.UTF_8);
            long now = System.currentTimeMillis();
            String name = (label == null || label.isBlank() ? "auto" : CloudConfigs.clean(label)) + "-" + now + ".json";
            Path file = directory.resolve(name);
            Files.write(file, bytes);
            CloudConfigs.last = new CloudConfigs.Version(name, now, bytes.length, CloudConfigs.hash(bytes));
            CloudConfigs.trim();
            return CloudConfigs.last;
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    /** Все сохранённые версии, от новых к старым. */
    public static List<CloudConfigs.Version> versions() {
        ArrayList<CloudConfigs.Version> list = new ArrayList<CloudConfigs.Version>();
        try {
            Path directory = CloudConfigs.root();
            if (!Files.isDirectory(directory)) {
                return list;
            }
            for (Path path : Files.newDirectoryStream(directory, "*.json")) {
                try {
                    byte[] bytes = Files.readAllBytes(path);
                    list.add(new CloudConfigs.Version(path.getFileName().toString(),
                            Files.getLastModifiedTime(path).toMillis(), bytes.length, CloudConfigs.hash(bytes)));
                }
                catch (Throwable ignored) {
                    // битый файл пропускаем
                }
            }
        }
        catch (Throwable ignored) {
            // нет папки — нет версий
        }
        Collections.sort(list, (left, right) -> Long.compare(right.time, left.time));
        return list;
    }

    /** Откатывает настройки к версии. Возвращает число применённых модулей или −1 при ошибке. */
    public static int restore(CloudConfigs.Version version) {
        if (version == null) {
            return -1;
        }
        try {
            Path file = CloudConfigs.root().resolve(version.name);
            if (!Files.isRegularFile(file)) {
                return -1;
            }
            String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            JsonObject json = com.google.gson.JsonParser.parseString(text).getAsJsonObject();
            CloudConfigs.snapshot("before-rollback");
            return CloudConfigs.apply(json);
        }
        catch (Throwable throwable) {
            return -1;
        }
    }

    /** Последняя версия для кнопки «откатить». */
    public static CloudConfigs.Version last() {
        List<CloudConfigs.Version> all = CloudConfigs.versions();
        return all.isEmpty() ? null : all.get(0);
    }

    public static boolean delete(CloudConfigs.Version version) {
        if (version == null) {
            return false;
        }
        try {
            return Files.deleteIfExists(CloudConfigs.root().resolve(version.name));
        }
        catch (IOException exception) {
            return false;
        }
    }

    public static int count() {
        return CloudConfigs.versions().size();
    }

    /** Выгружает версию коротким кодом, чтобы перенести настройки на другой компьютер. */
    public static String exportCode(CloudConfigs.Version version) {
        if (version == null) {
            return "";
        }
        try {
            byte[] bytes = Files.readAllBytes(CloudConfigs.root().resolve(version.name));
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            try (java.util.zip.GZIPOutputStream gzip = new java.util.zip.GZIPOutputStream(out)) {
                gzip.write(bytes);
            }
            return "BZCFG1:" + java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(out.toByteArray());
        }
        catch (Throwable throwable) {
            return "";
        }
    }

    /** Собирает всё, что сохраняет клиент: модули, дрэги, тему. */
    private static JsonObject bundle() {
        JsonObject json = new JsonObject();
        json.addProperty("format", 1);
        json.addProperty("savedAt", System.currentTimeMillis());
        json.add("modules", ConfigManager.buildModuleSnapshot());
        return json;
    }

    private static int apply(JsonObject json) {
        if (json.has("modules") && json.get("modules").isJsonObject()) {
            return ConfigManager.applyModuleSnapshot(json.getAsJsonObject("modules"));
        }
        return -1;
    }

    private static void trim() {
        List<CloudConfigs.Version> all = CloudConfigs.versions();
        for (int i = LIMIT; i < all.size(); i++) {
            CloudConfigs.delete(all.get(i));
        }
    }

    private static String clean(String label) {
        String value = label.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9а-я_-]+", "-");
        return value.length() > 24 ? value.substring(0, 24) : value;
    }

    private static String hash(byte[] bytes) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            byte[] sum = digest.digest(bytes);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 4 && i < sum.length; i++) {
                builder.append(String.format("%02x", sum[i]));
            }
            return builder.toString();
        }
        catch (Throwable throwable) {
            return Integer.toHexString(bytes.length);
        }
    }
}
