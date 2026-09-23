package rtx.byazen.utils.startup;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;
import rtx.byazen.ByAzen;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Журнал старта клиента и безопасный режим.
 * <p>
 * Игра может упасть не по-джавовски: нативный сбой в драйвере (в логе это
 * {@code EXCEPTION_ACCESS_VIOLATION [lwjgl_opengl.dll+…]}) убивает процесс целиком, и никакой
 * {@code try/catch} его не поймает. Поэтому клиент на каждом шаге запуска пишет «хлебную крошку»:
 * строку в лог и файл {@code byazen/configs/system/last-start.txt}. После падения в файле остаётся
 * последний шаг — сразу видно, на чём именно всё оборвалось.
 * <p>
 * Там же живёт безопасный режим: если рядом с игрой лежит файл {@code byazen-safe.txt} или задан
 * {@code -Dbyazen.safe=true}, клиент пропускает всё, что можно пропустить на старте (прогрев
 * отрисовки, предзагрузка GIF, отложенные тяжёлые интеграции, тёмная полоса заголовка окна).
 * Это позволяет запустить игру даже на «капризном» драйвере и по одному включать вещи обратно.
 */
public final class StartTrace {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ROOT);
    private static final int KEEP_STEPS = 24;

    private StartTrace() {
    }

    private static final List<String> STEPS = new ArrayList<String>();
    private static final long STARTED_AT = System.currentTimeMillis();
    private static String version = "?";
    private static String lastStep = "не начат";

    private static Path flagPath() {
        return FabricLoader.getInstance().getGameDir().resolve("byazen-safe.txt");
    }

    private static Path tracePath() {
        return RepositoryStorage.configRoot().resolve("last-start.txt");
    }

    /** Безопасный режим: файл рядом с игрой, системное свойство или переменная окружения. */
    public static boolean safeMode() {
        try {
            if (Files.exists(StartTrace.flagPath())) {
                return true;
            }
        }
        catch (Throwable ignored) {
        }
        String property = System.getProperty("byazen.safe", "");
        if (property.equalsIgnoreCase("true") || property.equals("1") || property.equalsIgnoreCase("yes")
                || property.equalsIgnoreCase("on")) {
            return true;
        }
        String env = System.getenv("BYAZEN_SAFE");
        return env != null && (env.equalsIgnoreCase("true") || env.equals("1") || env.equalsIgnoreCase("yes"));
    }

    public static String safeModeHint() {
        return "безопасный режим: создай файл " + StartTrace.flagPath() + " или добавь -Dbyazen.safe=true";
    }

    public static Path flagFile() {
        return StartTrace.flagPath();
    }

    public static Path traceFile() {
        return StartTrace.tracePath();
    }

    public static String lastStep() {
        return lastStep;
    }

    /** Начало журнала: пишем версию, время и путь к самому файлу — чтобы было видно в логе. */
    public static void begin() {
        try {
            version = FabricLoader.getInstance().getModContainer("byazen")
                    .map(container -> container.getMetadata().getVersion().getFriendlyString())
                    .orElse(version);
        }
        catch (Throwable ignored) {
        }
        ByAzen.LOGGER.info("[ByAzen] {} стартует · безопасный режим: {} · журнал: {}",
                version, StartTrace.safeMode() ? "ДА" : "нет", StartTrace.tracePath());
        if (StartTrace.safeMode()) {
            ByAzen.LOGGER.warn("[ByAzen] {}", StartTrace.safeModeHint());
        }
        StartTrace.step("начало инициализации");
    }

    /** Очередной шаг старта: короткая строка в лог плюс запись файла-крошки. */
    public static void step(String name) {
        ByAzen.LOGGER.info("[ByAzen] старт: {}", name);
        StartTrace.record(name);
    }

    /** Записывает крошку без строки в лог (когда своя запись уже есть). */
    private static void record(String name) {
        lastStep = name;
        STEPS.add(System.currentTimeMillis() + " — " + name);
        while (STEPS.size() > KEEP_STEPS) {
            STEPS.remove(0);
        }
        StartTrace.write(false);
    }

    /** Клиент дошёл до конца инициализации — крошка больше не нужна как «место падения». */
    public static void finished() {
        lastStep = "инициализация завершена";
        StartTrace.write(true);
    }

    private static void write(boolean done) {
        try {
            Path path = StartTrace.tracePath();
            Files.createDirectories(path.getParent());
            StringBuilder builder = new StringBuilder();
            builder.append("ByAzen ").append(version).append(" — журнал старта\n");
            builder.append("время запуска: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT))).append('\n');
            builder.append("режим: ").append(StartTrace.safeMode() ? "безопасный" : "обычный").append('\n');
            builder.append("последний шаг: ").append(lastStep).append(done ? " (клиент запустился)" : "").append('\n');
            builder.append(String.format(Locale.ROOT, "прошло с начала: %.1f с%n", (System.currentTimeMillis() - STARTED_AT) / 1000.0));
            builder.append('\n').append("шаги (снизу — последние):\n");
            for (String step : STEPS) {
                builder.append("  ").append(step).append('\n');
            }
            builder.append('\n');
            builder.append("если игра упала — в crash-report (или hs_err_pid*.log) рядом лежит причина;\n");
            builder.append("если падение нативное (lwjgl_opengl.dll) — попробуй безопасный режим: ")
                    .append(StartTrace.flagPath()).append('\n');
            Files.writeString(path, builder.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        }
        catch (Throwable ignored) {
            // журнал — вспомогательный: если не смогли записать, игра всё равно должна запускаться
        }
    }

    /** Строка про OpenGL для отчёта (когда контекст уже готов). */
    public static void reportGl(String summary) {
        ByAzen.LOGGER.info("[ByAzen] OpenGL: {}", summary);
        StartTrace.record("первый кадр · OpenGL: " + summary);
    }
}
