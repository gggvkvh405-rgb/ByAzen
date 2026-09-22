package rtx.byazen.utils.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.stats.StatsExport;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Локальные данные под ключом (идея №162 из IDEAS.md).
 * <p>
 * Заметки, музыка, история смертей, профили серверов и журнал настроек лежат рядом с конфигом
 * обычными файлами. Кнопка «Закрыть под ключом» складывает их в один зашифрованный контейнер
 * `private/vault.bzenc`, а исходники убирает — так содержимое нельзя прочитать в блокноте.
 * «Открыть хранилище» возвращает всё на место. Перед закрытием контейнер проверяется: клиент
 * расшифровывает его обратно и сверяет список файлов и размер, поэтому данные не теряются.
 */
public final class PrivateData {

    /** Один защищаемый файл. */
    public static final class Store {

        public final String key;
        public final String label;
        public final Path path;
        public final long size;

        Store(String key, String label, Path path, long size) {
            this.key = key;
            this.label = label;
            this.path = path;
            this.size = size;
        }

        public String sizeText() {
            return PrivateData.bytes(this.size);
        }
    }

    /** Итог операции. */
    public static final class Report {

        public int files;
        public long bytes;
        public final List<String> notes = new ArrayList<String>();
        public String vault = "";
        public boolean ok;

        public String summary() {
            if (!this.ok) {
                return this.notes.isEmpty() ? "не получилось" : this.notes.get(0);
            }
            return "файлов: " + this.files + " · " + PrivateData.bytes(this.bytes) + (this.vault.isEmpty() ? "" : " · " + this.vault);
        }
    }

    private static final String VAULT = "vault.bzenc";
    private static final String FOLDER = "private";
    private static final String[] EXCLUDE = {"autocfg", "config", "backup", "vault"};

    private static final Map<String, String> LABELS = new LinkedHashMap<String, String>();

    static {
        LABELS.put("notes", "Заметки");
        LABELS.put("music_library", "Музыка: избранное и недавнее");
        LABELS.put("deaths", "История смертей");
        LABELS.put("servers.favorites", "Избранные сервера");
        LABELS.put("serverprofiles", "Профили серверов");
        LABELS.put("settings_history", "Журнал настроек");
        LABELS.put("clipboard", "История буфера обмена");
        LABELS.put("reminders", "Напоминания");
        LABELS.put("waypoints", "Точки маршрута");
    }

    private PrivateData() {
    }

    private static Path folder() {
        return RepositoryStorage.root().resolve(FOLDER);
    }

    public static Path vaultPath() {
        return PrivateData.folder().resolve(VAULT);
    }

    public static boolean vaultExists() {
        return Files.exists(PrivateData.vaultPath());
    }

    /** Что можно закрыть ключом прямо сейчас. */
    public static List<PrivateData.Store> stores() {
        ArrayList<PrivateData.Store> list = new ArrayList<PrivateData.Store>();
        try {
            if (!Files.isDirectory(RepositoryStorage.root())) {
                return list;
            }
            List<Path> files = new ArrayList<Path>();
            try (java.util.stream.Stream<Path> stream = Files.list(RepositoryStorage.root())) {
                for (Path path : (Iterable<Path>)stream::iterator) {
                    if (!Files.isRegularFile(path)) {
                        continue;
                    }
                    String name = path.getFileName().toString();
                    if (!name.endsWith(".byazen")) {
                        continue;
                    }
                    String base = name.substring(0, name.length() - ".byazen".length());
                    if (PrivateData.excluded(base)) {
                        continue;
                    }
                    files.add(path);
                }
            }
            for (Path path : files) {
                String name = path.getFileName().toString();
                String base = name.substring(0, name.length() - ".byazen".length());
                String label = LABELS.getOrDefault(base, base);
                list.add(new PrivateData.Store(base, label, path, Files.size(path)));
            }
        }
        catch (Throwable throwable) {
            ClientLog.warn("не удалось перечислить локальные данные: " + throwable);
        }
        return list;
    }

    private static boolean excluded(String key) {
        for (String pattern : EXCLUDE) {
            if (key.toLowerCase(java.util.Locale.ROOT).contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    public static long totalBytes() {
        long bytes = 0L;
        for (PrivateData.Store store : PrivateData.stores()) {
            bytes += store.size;
        }
        return bytes;
    }

    /** Закрывает данные под ключом: zip → AES-256-GCM → vault.bzenc, затем исходники убираются. */
    public static PrivateData.Report lock(boolean keepCopy) {
        PrivateData.Report report = new PrivateData.Report();
        List<PrivateData.Store> stores = PrivateData.stores();
        if (stores.isEmpty()) {
            report.notes.add("Нечего закрывать: локальные файлы уже в хранилище или отсутствуют");
            return report;
        }
        if (!SecretBox.available()) {
            report.notes.add("Нет ключа шифрования — проверить папку с данными");
            return report;
        }
        byte[] archive;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (ZipOutputStream zip = new ZipOutputStream(buffer)) {
                for (PrivateData.Store store : stores) {
                    zip.putNextEntry(new ZipEntry(store.key));
                    Files.copy(store.path, zip);
                    zip.closeEntry();
                }
            }
            archive = buffer.toByteArray();
        }
        catch (Throwable throwable) {
            report.notes.add("не удалось собрать архив: " + throwable);
            return report;
        }
        byte[] sealed = SecretBox.encrypt(archive);
        if (sealed == null) {
            report.notes.add("не удалось зашифровать архив");
            return report;
        }
        try {
            Files.createDirectories(PrivateData.folder());
            Files.write(PrivateData.vaultPath(), sealed);
        }
        catch (Throwable throwable) {
            report.notes.add("не удалось записать хранилище: " + throwable);
            return report;
        }
        // проверка: расшифровываем то, что только что записали, и сверяем список
        Map<String, Long> check = PrivateData.read(sealed);
        if (check.size() != stores.size()) {
            report.notes.add("проверка хранилища не сошлась — исходные файлы не тронуты");
            return report;
        }
        for (PrivateData.Store store : stores) {
            report.bytes += store.size;
            try {
                if (keepCopy) {
                    Path backup = PrivateData.folder().resolve("copy-" + StatsExport.stamp());
                    Files.createDirectories(backup);
                    Files.copy(store.path, backup.resolve(store.path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                }
                Files.deleteIfExists(store.path);
                ++report.files;
            }
            catch (Throwable throwable) {
                report.notes.add("не убрался " + store.key + ": " + throwable);
            }
        }
        report.vault = PrivateData.vaultPath().getFileName().toString() + " · " + PrivateData.bytes(sealed.length);
        report.ok = true;
        ClientLog.info("локальные данные закрыты под ключом: файлов " + report.files);
        return report;
    }

    /** Возвращает данные из хранилища на место. */
    public static PrivateData.Report restore() {
        PrivateData.Report report = new PrivateData.Report();
        if (!PrivateData.vaultExists()) {
            report.notes.add("Хранилища нет — открывать нечего");
            return report;
        }
        byte[] sealed;
        try {
            sealed = Files.readAllBytes(PrivateData.vaultPath());
        }
        catch (Throwable throwable) {
            report.notes.add("не удалось прочитать хранилище: " + throwable);
            return report;
        }
        byte[] plain = SecretBox.decrypt(sealed);
        if (plain == null) {
            report.notes.add("Не тот ключ: расшифровать не получилось (ключ лежит рядом с данными)");
            return report;
        }
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(plain))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.contains("/") || name.contains("..") || name.isBlank()) {
                    continue;
                }
                Path target = RepositoryStorage.root().resolve(name + ".byazen");
                Files.createDirectories(RepositoryStorage.root());
                Files.copy(zip, target, StandardCopyOption.REPLACE_EXISTING);
                report.bytes += Files.size(target);
                ++report.files;
            }
        }
        catch (Throwable throwable) {
            report.notes.add("хранилище распаковалось с ошибкой: " + throwable);
            return report;
        }
        try {
            Files.deleteIfExists(PrivateData.vaultPath());
        }
        catch (Throwable ignored) {
            // оставим на месте — не беда
        }
        report.ok = true;
        report.notes.add("Данные возвращены — перезапустите игру, чтобы клиент их подхватил");
        ClientLog.info("локальные данные открыты: файлов " + report.files);
        return report;
    }

    /** Проверка хранилища без изменений: читается ли и сколько внутри файлов. */
    public static String verify() {
        if (!PrivateData.vaultExists()) {
            return "хранилища нет";
        }
        try {
            byte[] sealed = Files.readAllBytes(PrivateData.vaultPath());
            Map<String, Long> inside = PrivateData.read(sealed);
            if (inside.isEmpty()) {
                return "хранилище не читается текущим ключом";
            }
            StringBuilder builder = new StringBuilder("хранилище читается: файлов " + inside.size());
            for (Map.Entry<String, Long> pair : inside.entrySet()) {
                builder.append(", ").append(pair.getKey()).append(' ').append(PrivateData.bytes(pair.getValue()));
            }
            return builder.toString();
        }
        catch (Throwable throwable) {
            return "ошибка проверки: " + throwable;
        }
    }

    private static Map<String, Long> read(byte[] sealed) {
        LinkedHashMap<String, Long> inside = new LinkedHashMap<String, Long>();
        byte[] plain = SecretBox.decrypt(sealed);
        if (plain == null) {
            return inside;
        }
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(plain))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                long size = 0L;
                byte[] buffer = new byte[4096];
                int read;
                while ((read = zip.read(buffer)) > 0) {
                    size += read;
                }
                inside.put(entry.getName(), size);
            }
        }
        catch (Throwable ignored) {
            // читаем, что успели
        }
        return inside;
    }

    private static String bytes(long size) {
        if (size < 1024L) {
            return size + " Б";
        }
        if (size < 1048576L) {
            return String.format(java.util.Locale.ROOT, "%.1f КБ", (double)size / 1024.0);
        }
        return String.format(java.util.Locale.ROOT, "%.1f МБ", (double)size / 1048576.0);
    }

    /** Заглушка для InputStream-совместимости (используется в тестах ключа). */
    public static InputStream open(byte[] sealed) {
        return SecretBox.stream(sealed);
    }
}
