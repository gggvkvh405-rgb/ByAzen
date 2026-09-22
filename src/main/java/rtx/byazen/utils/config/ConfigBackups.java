package rtx.byazen.utils.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import rtx.byazen.ByAzen;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Резервные копии конфигурации (идея №102 из IDEAS.md).
 * <p>
 * Копии складываются в отдельную папку рядом с настройками: каждая — снимок всех файлов конфигурации
 * с отметкой времени и причиной. Копию можно поставить обратно одним нажатием, старые обрезаются,
 * чтобы папка не росла бесконечно.
 */
public final class ConfigBackups {

    private static final String FOLDER = "backups";
    private static final String STAMP = "yyyy-MM-dd_HH-mm-ss";

    private ConfigBackups() {
    }

    /** Одна резервная копия. */
    public static final class Backup {

        public final String name;
        public final Path path;
        public final long createdAt;
        public final int files;
        public final long bytes;
        public final String reason;

        Backup(String name, Path path, long createdAt, int files, long bytes, String reason) {
            this.name = name;
            this.path = path;
            this.createdAt = createdAt;
            this.files = files;
            this.bytes = bytes;
            this.reason = reason;
        }

        public String sizeText() {
            if (this.bytes < 1024L) {
                return this.bytes + " Б";
            }
            if (this.bytes < 1048576L) {
                return Math.max(1L, this.bytes / 1024L) + " КБ";
            }
            return String.format(Locale.ROOT, "%.1f МБ", (double)this.bytes / 1048576.0);
        }

        public String dateText() {
            return new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ROOT).format(new Date(this.createdAt));
        }

        public String reasonText() {
            if (this.reason == null || this.reason.isEmpty()) {
                return "без пометки";
            }
            switch (this.reason) {
                case "start": {
                    return "при запуске";
                }
                case "timer": {
                    return "по таймеру";
                }
                case "profile": {
                    return "перед сменой профиля";
                }
                case "import": {
                    return "перед импортом";
                }
                case "manual": {
                    return "вручную";
                }
                default: {
                    return this.reason;
                }
            }
        }
    }

    /** Папка резервных копий. */
    public static Path directory() {
        return RepositoryStorage.configRoot().resolve(FOLDER);
    }

    /** Все копии, свежие сверху. */
    public static List<Backup> list() {
        ArrayList<Backup> result = new ArrayList<Backup>();
        Path directory = ConfigBackups.directory();
        if (!Files.isDirectory(directory)) {
            return result;
        }
        try (Stream<Path> stream = Files.list(directory)) {
            for (Path path : stream.toList()) {
                if (!Files.isDirectory(path)) {
                    continue;
                }
                String name = path.getFileName().toString();
                int files = 0;
                long bytes = 0L;
                try (Stream<Path> inner = Files.walk(path)) {
                    for (Path file : inner.filter(Files::isRegularFile).toList()) {
                        ++files;
                        try {
                            bytes += Files.size(file);
                        }
                        catch (Throwable ignored) {
                            // размер отдельного файла недоступен
                        }
                    }
                }
                long created = 0L;
                try {
                    created = Files.getLastModifiedTime(path).toMillis();
                }
                catch (Throwable ignored) {
                    // время недоступно
                }
                String reason = name.contains("_") ? name.substring(name.lastIndexOf(95) + 1) : "";
                result.add(new Backup(name, path, created, files, bytes, reason));
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Резервные копии: {}", throwable.toString());
        }
        result.sort(Comparator.comparingLong(backup -> -backup.createdAt));
        return result;
    }

    /** Делает копию всех файлов конфигурации. Возвращает имя копии или пустую строку. */
    public static String create(String reason) {
        Path source = RepositoryStorage.configRoot();
        String clean = reason == null ? "manual" : reason.trim().toLowerCase(Locale.ROOT);
        if (clean.isEmpty()) {
            clean = "manual";
        }
        if (clean.length() > 24) {
            clean = clean.substring(0, 24);
        }
        String stamp = new SimpleDateFormat(STAMP, Locale.ROOT).format(new Date());
        String name = stamp + "_" + clean;
        Path target = ConfigBackups.directory().resolve(name);
        try {
            Files.createDirectories(target);
            int copied = ConfigBackups.copyTree(source, target, 0);
            if (copied == 0) {
                Files.deleteIfExists(target);
                return "";
            }
            return name;
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Не удалось сделать копию настроек: {}", throwable.toString());
            return "";
        }
    }

    private static int copyTree(Path source, Path target, int depth) throws Exception {
        if (depth > 6 || !Files.isDirectory(source)) {
            return 0;
        }
        int copied = 0;
        try (Stream<Path> stream = Files.list(source)) {
            for (Path path : stream.toList()) {
                String name = path.getFileName().toString();
                if (name.equals(FOLDER)) {
                    continue;
                }
                if (Files.isDirectory(path)) {
                    copied += ConfigBackups.copyTree(path, target.resolve(name), depth + 1);
                    continue;
                }
                try {
                    Path destination = target.resolve(name);
                    if (destination.getParent() != null) {
                        Files.createDirectories(destination.getParent());
                    }
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                    ++copied;
                }
                catch (Throwable ignored) {
                    // отдельный файл пропускаем
                }
            }
        }
        return copied;
    }

    /** Возвращает копию на место. */
    public static boolean restore(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        Path source = ConfigBackups.directory().resolve(name);
        if (!Files.isDirectory(source)) {
            return false;
        }
        try {
            ConfigBackups.copyTree(source, RepositoryStorage.configRoot(), 0);
            return true;
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Не удалось восстановить настройки: {}", throwable.toString());
            return false;
        }
    }

    /** Ставит на место самую свежую копию. */
    public static boolean restoreLatest() {
        List<Backup> all = ConfigBackups.list();
        return !all.isEmpty() && ConfigBackups.restore(all.get(0).name);
    }

    public static boolean delete(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        Path target = ConfigBackups.directory().resolve(name);
        try {
            if (!Files.isDirectory(target)) {
                return false;
            }
            try (Stream<Path> stream = Files.walk(target)) {
                for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
            return true;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    /** Оставляет только {@code keep} свежих копий. */
    public static int prune(int keep) {
        List<Backup> all = ConfigBackups.list();
        int removed = 0;
        for (int i = keep; i < all.size(); ++i) {
            if (ConfigBackups.delete(all.get(i).name)) {
                ++removed;
            }
        }
        return removed;
    }

    /** Сколько времени прошло с последней копии, в минутах; -1 — копий нет. */
    public static long minutesSinceLast() {
        List<Backup> all = ConfigBackups.list();
        if (all.isEmpty()) {
            return -1L;
        }
        return (System.currentTimeMillis() - all.get(0).createdAt) / 60000L;
    }

    public static String summary() {
        List<Backup> all = ConfigBackups.list();
        if (all.isEmpty()) {
            return "резервных копий пока нет";
        }
        return "копий: " + all.size() + " · последняя " + all.get(0).dateText() + " (" + all.get(0).reasonText() + ")";
    }
}
