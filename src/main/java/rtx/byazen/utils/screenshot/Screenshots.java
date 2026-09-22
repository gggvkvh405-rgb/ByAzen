package rtx.byazen.utils.screenshot;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ScreenshotRecorder;
import rtx.byazen.ByAzen;

/**
 * Работа со снимками экрана (идеи №109 и №110 из IDEAS.md).
 * <p>
 * Кадр сохраняется штатным {@link ScreenshotRecorder} игры — так снимок получается ровно таким же,
 * как по F2. Здесь же лежат поиск самой свежей картинки и путь для размеченной копии — на этом
 * строится аннотатор.
 */
public final class Screenshots {

    private static final String ANNOTATED_SUFFIX = "_annotated.png";

    private Screenshots() {
    }

    /** Папка со снимками игры. */
    public static Path directory() {
        try {
            return FabricLoader.getInstance().getGameDir().resolve("screenshots");
        }
        catch (Throwable throwable) {
            return Path.of("screenshots");
        }
    }

    /** Все снимки, свежие сверху. */
    public static List<Path> list() {
        ArrayList<Path> result = new ArrayList<Path>();
        Path directory = Screenshots.directory();
        if (!Files.isDirectory(directory)) {
            return result;
        }
        try (Stream<Path> stream = Files.list(directory)) {
            result.addAll(stream.filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png"))
                    .filter(path -> !path.getFileName().toString().endsWith(ANNOTATED_SUFFIX))
                    .toList());
        }
        catch (Throwable throwable) {
            return result;
        }
        result.sort(Comparator.comparingLong((Path path) -> {
            try {
                return -Files.getLastModifiedTime(path).toMillis();
            }
            catch (Throwable throwable) {
                return 0L;
            }
        }));
        return result;
    }

    /** Самый свежий снимок. */
    public static Path newest() {
        List<Path> all = Screenshots.list();
        return all.isEmpty() ? null : all.get(0);
    }

    /** Путь для размеченной копии: «shot.png» → «shot_annotated.png». */
    public static Path annotated(Path source) {
        if (source == null) {
            return null;
        }
        String name = source.getFileName().toString();
        int dot = name.toLowerCase(Locale.ROOT).lastIndexOf(".png");
        String base = dot > 0 ? name.substring(0, dot) : name;
        return source.resolveSibling(base + ANNOTATED_SUFFIX);
    }

    /** Делает снимок штатным механизмом игры: тот же путь, что и по F2. */
    public static boolean capture() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return false;
        }
        try {
            if (client.runDirectory == null) {
                return false;
            }
            ScreenshotRecorder.saveScreenshot(client.runDirectory, client.getFramebuffer(), text -> {});
            return true;
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Снимок экрана не удался: {}", throwable.toString());
            return false;
        }
    }

    /** Как сохраняются снимки: подпись для интерфейса. */
    public static String backend() {
        return "штатный механизм игры: screenshots/ рядом с игрой";
    }

    /** Имя снимка без пути и расширения: для подписей и уведомлений. */
    public static String name(Path path) {
        if (path == null) {
            return "";
        }
        String name = path.getFileName().toString();
        int dot = name.toLowerCase(Locale.ROOT).lastIndexOf(".png");
        return dot > 0 ? name.substring(0, dot) : name;
    }

    /** Размер в удобном виде. */
    public static String sizeText(Path path) {
        try {
            long bytes = Files.size(path);
            if (bytes < 1024L) {
                return bytes + " Б";
            }
            return bytes < 1048576L
                    ? Math.max(1L, bytes / 1024L) + " КБ"
                    : String.format(Locale.ROOT, "%.1f МБ", (double)bytes / 1048576.0);
        }
        catch (Throwable throwable) {
            return "размер неизвестен";
        }
    }
}
