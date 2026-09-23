package rtx.byazen.utils.perf;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.impl.Utils.GraphicsPresets;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.profiles.BuildTier;
import rtx.byazen.utils.render.others.RenderCompatibility;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Авто-настройки под железо (идея №193 из IDEAS.md).
 * <p>
 * Клиент смотрит, сколько ядер и памяти даёт компьютер, что за видеокарта (через OpenGL) и как
 * устроена система, после чего предлагает готовый набор настроек: графический пресет и, если
 * железо слабое, облегчённый профиль Lite. Применяется только по кнопке, запоминается в конфиге,
 * чтобы не спрашивать каждый запуск.
 */
public final class HardwareProfile {

    /** Строка «характеристика — значение — вывод». */
    public record Row(String name, String value, String verdict) {
    }

    private static String gpu;
    private static boolean asked;

    private HardwareProfile() {
    }

    public static List<Row> rows() {
        ArrayList<Row> rows = new ArrayList<Row>();
        int cores = Runtime.getRuntime().availableProcessors();
        long ram = Runtime.getRuntime().maxMemory() / 1048576L;
        rows.add(new Row("Процессор", cores + " ядер", HardwareProfile.verdictCores(cores)));
        rows.add(new Row("Память для игры", ram + " МБ", HardwareProfile.verdictRam(ram)));
        String card = HardwareProfile.gpuName();
        rows.add(new Row("Видеокарта", card, HardwareProfile.verdictGpu(card)));
        rows.add(new Row("Система", System.getProperty("os.name") + " · Java "
                + System.getProperty("java.version"), "клиент требует Java 21+"));
        return rows;
    }

    private static String verdictCores(int cores) {
        if (cores >= 8) {
            return "хватит на тяжёлые визуалы";
        }
        if (cores >= 4) {
            return "уверенно для средних настроек";
        }
        return "лучше облегчённый профиль";
    }

    private static String verdictRam(long ram) {
        if (ram >= 4096L) {
            return "можно ставить высокие настройки и моды";
        }
        if (ram >= 2048L) {
            return "средние настройки без тяжёлых модов";
        }
        return "мало памяти: уберите лишние моды";
    }

    private static String verdictGpu(String card) {
        String lower = card.toLowerCase(Locale.ROOT);
        if (lower.contains("rtx") || lower.contains("radeon rx") || lower.contains("arc a")) {
            return "можно включать эффекты и шейдеры";
        }
        if (lower.contains("intel") || lower.contains("uhd") || lower.contains("vega")) {
            return "встроенная графика: лучше средний пресет";
        }
        return "подбирайте по FPS";
    }

    /** Название видеокарты: читается через OpenGL, поэтому вызывать только в игре. */
    public static String gpuName() {
        if (gpu != null) {
            return gpu;
        }
        // Пока Minecraft не создал GL-устройство, любой вызов OpenGL — нативное падение драйвера,
        // поэтому до готовности контекста ничего не читаем и не запоминаем.
        if (!RenderCompatibility.glReady()) {
            ClientLog.debug("железо: OpenGL ещё не готов — видеокарту посмотрим позже");
            return "не определена";
        }
        gpu = "не определена";
        try {
            String renderer = org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_RENDERER);
            String vendor = org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_VENDOR);
            if (renderer != null && !renderer.isBlank()) {
                gpu = renderer + (vendor == null || renderer.contains(vendor) ? "" : " (" + vendor + ")");
            }
        }
        catch (Throwable throwable) {
            ClientLog.debug("железо: OpenGL недоступен вне игры");
        }
        return gpu;
    }

    public static String recommendedPreset() {
        int cores = Runtime.getRuntime().availableProcessors();
        long ram = Runtime.getRuntime().maxMemory() / 1048576L;
        String card = HardwareProfile.gpuName().toLowerCase(Locale.ROOT);
        boolean weakGpu = card.contains("intel") || card.contains("uhd") || card.contains("vega")
                || card.contains("не определена");
        if (cores >= 8 && ram >= 4096L && !weakGpu) {
            return "Кино 60 fps";
        }
        if (cores >= 4 && ram >= 2048L) {
            return "Баланс";
        }
        return "PvP 240 fps";
    }

    public static boolean liteRecommended() {
        return Runtime.getRuntime().availableProcessors() <= 4 || Runtime.getRuntime().maxMemory() < 2147483648L;
    }

    public static List<String> plan() {
        ArrayList<String> plan = new ArrayList<String>();
        plan.add("графический пресет: «" + HardwareProfile.recommendedPreset() + "»");
        if (HardwareProfile.liteRecommended()) {
            plan.add("облегчённый профиль: тяжёлые визуалы выключены, кэши урезаны");
        }
        plan.add("масштаб интерфейса: 100 % (меняется в UiScale)");
        plan.add("сглаживание и тени — по вкусу, FPS покажет бенчмарк");
        return plan;
    }

    /** Применяет рекомендованное и рассказывает, что сделано. */
    public static String apply() {
        String preset = HardwareProfile.recommendedPreset();
        GraphicsPresets.apply(preset, null);
        int disabled = 0;
        if (HardwareProfile.liteRecommended()) {
            disabled = BuildTier.applyLite();
        }
        HardwareProfile.markAsked();
        String text = "Настройки под ваше железо: пресет «" + preset + "»"
                + (disabled > 0 ? ", выключено тяжёлых модулей: " + disabled : "")
                + ". Проверить FPS — модуль FPS Benchmark.";
        ChatMessage.send("§b" + text);
        NotificationsModule.notify("§bНастройки подобраны под ваше железо", 4000L);
        return text;
    }

    public static String summary() {
        StringBuilder builder = new StringBuilder();
        for (Row row : HardwareProfile.rows()) {
            if (builder.length() > 0) {
                builder.append(" · ");
            }
            builder.append(row.name()).append(": ").append(row.value());
        }
        builder.append(" · рекомендуем «").append(HardwareProfile.recommendedPreset()).append("»");
        return builder.toString();
    }

    /** Спрашивали ли уже подбор настроек. */
    public static boolean wasAsked() {
        HardwareProfile.load();
        return asked;
    }

    public static void markAsked() {
        asked = true;
        HardwareProfile.save();
    }

    private static void load() {
        if (HardwareProfile.loaded) {
            return;
        }
        HardwareProfile.loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject("hardware_profile");
            asked = root.has("asked") && root.get("asked").getAsBoolean();
        }
        catch (Throwable throwable) {
            ClientLog.debug("железо: профиль ещё не сохранялся");
        }
    }

    private static void save() {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("asked", asked);
            root.addProperty("gpu", HardwareProfile.gpuName());
            root.addProperty("preset", HardwareProfile.recommendedPreset());
            RepositoryStorage.write("hardware_profile", root);
        }
        catch (Throwable throwable) {
            ClientLog.warn("железо: не удалось сохранить профиль");
        }
    }

    private static boolean loaded;
}

