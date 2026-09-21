package rtx.byazen.utils.render.render2d.font;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.ByAzen;

/**
 * Свои шрифты TTF/OTF (идея №49 из IDEAS.md).
 * <p>
 * Файлы кладутся в {@code .minecraft/byazen/fonts}. Каждый шрифт проверяется на сглаженность:
 * если у букв почти нет полупрозрачных пикселей (то есть шрифт пиксельный), он не подключается —
 * правило проекта «никаких пикселей» соблюдается автоматически.
 */
public final class CustomFonts {

    public static final String NONE = "По умолчанию";
    private static final String FOLDER = "byazen/fonts";
    private static final String SAMPLE = "ByAzen Wq 123";
    private static final float SAMPLE_SIZE = 12.0f;
    private static final float MIN_SOFT_RATIO = 0.24f;
    private static final float BASELINE_SHARE = 0.62f;

    private static final Map<String, String> LOADED = new LinkedHashMap<String, String>();
    private static final Map<String, String> REJECTED = new LinkedHashMap<String, String>();
    private static volatile String override = null;
    private static boolean scanned;

    private CustomFonts() {
    }

    public static Path folder() {
        MinecraftClient client = MinecraftClient.getInstance();
        Path root = client == null ? Path.of(FOLDER) : client.runDirectory.toPath().resolve(FOLDER);
        return root;
    }

    /** Имена подключённых шрифтов (первое — «По умолчанию»). */
    public static String[] names() {
        List<String> list = new ArrayList<String>();
        list.add(NONE);
        list.addAll(LOADED.keySet());
        return list.toArray(new String[0]);
    }

    public static String[] rejected() {
        return REJECTED.keySet().toArray(new String[0]);
    }

    public static boolean isCustom(String fontName) {
        return fontName != null && LOADED.containsKey(fontName);
    }

    public static String override() {
        return override;
    }

    public static void setOverride(String name) {
        override = name == null || name.isBlank() || NONE.equals(name) ? null : name;
    }

    /** Семейство, которым надо заменить запрошенный шрифт (или null, если замены нет). */
    public static String substitute(String fontName) {
        String current = override;
        if (current == null || fontName == null) {
            return null;
        }
        String lower = fontName.toLowerCase(Locale.ROOT);
        if (lower.contains("icon") || lower.equals("byazen")) {
            return null;
        }
        return CustomFonts.familyId(current);
    }

    /** Пересобирает список шрифтов из папки (вызывает кнопка в настройках или команда). */
    public static synchronized int refresh() {
        LOADED.clear();
        REJECTED.clear();
        scanned = true;
        Path directory = CustomFonts.folder();
        if (!Files.isDirectory(directory)) {
            try {
                Files.createDirectories(directory);
            }
            catch (IOException exception) {
                ByAzen.LOGGER.debug("[ByAzen] Не удалось создать папку шрифтов {}", directory, exception);
            }
            return 0;
        }
        try (Stream<Path> stream = Files.list(directory)) {
            for (Path path : stream.toList()) {
                String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
                if (!fileName.endsWith(".ttf") && !fileName.endsWith(".otf")) {
                    continue;
                }
                CustomFonts.register(path);
            }
        }
        catch (IOException exception) {
            ByAzen.LOGGER.warn("[ByAzen] Не удалось прочитать папку шрифтов: {}", exception.toString());
        }
        return LOADED.size();
    }

    private static void register(Path path) {
        String display = CustomFonts.displayName(path);
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, path.toFile()).deriveFont(Font.PLAIN, 12.0f);
            float soft = CustomFonts.softRatio(font);
            float baseline = CustomFonts.baselineRatio();
            if (soft < MIN_SOFT_RATIO || soft < baseline * BASELINE_SHARE) {
                REJECTED.put(display, "пиксельный шрифт (сглаженность " + CustomFonts.percent(soft) + ")");
                ByAzen.LOGGER.info("[ByAzen] Шрифт {} отклонён: сглаженность {} против {}", display, CustomFonts.percent(soft), CustomFonts.percent(baseline));
                return;
            }
            String id = "custom_" + display.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9а-яё]+", "_");
            File file = path.toFile();
            TextRenderer.getInstance().registerFont(id, () -> {
                try {
                    return Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(Font.PLAIN, 12.0f);
                }
                catch (Exception exception) {
                    return new Font("SansSerif", Font.PLAIN, 12);
                }
            });
            LOADED.put(display, id + "|" + CustomFonts.percent(soft));
        }
        catch (Exception exception) {
            REJECTED.put(display, "не удалось прочитать (" + exception.getClass().getSimpleName() + ")");
        }
    }

    private static String displayName(Path path) {
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf(46);
        return dot > 0 ? name.substring(0, dot) : name;
    }

    /** Идентификатор семейства для Render2D (внутренний id шрифта). */
    public static String familyId(String displayName) {
        String value = LOADED.get(displayName);
        return value == null ? null : value.split("\\|", 2)[0];
    }

    /** Комментарий к шрифту: «сглаженность 38%». */
    public static String note(String displayName) {
        String value = LOADED.get(displayName);
        return value == null ? "" : "сглаженность " + value.split("\\|", 2)[1];
    }

    public static boolean scanned() {
        return scanned;
    }

    /**
     * Доля полупрозрачных пикселей среди «чернильных»: у сглаженных шрифтов края букв
     * размытые, у пиксельных — резкие, почти бинарные.
     */
    private static float softRatio(Font font) {
        BufferedImage image = new BufferedImage(220, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setFont(font.deriveFont(SAMPLE_SIZE));
            graphics.setColor(java.awt.Color.WHITE);
            graphics.drawString(SAMPLE, 4, 26);
        }
        finally {
            graphics.dispose();
        }
        int soft = 0;
        int solid = 0;
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                int alpha = image.getRGB(x, y) >>> 24 & 0xFF;
                if (alpha == 0) {
                    continue;
                }
                if (alpha == 255) {
                    ++solid;
                }
                else {
                    ++soft;
                }
            }
        }
        int total = soft + solid;
        return total == 0 ? 0.0f : (float) soft / (float) total;
    }

    private static float baselineRatio() {
        return CustomFonts.softRatio(new Font("SansSerif", Font.PLAIN, 12));
    }

    private static String percent(float ratio) {
        return Math.round(ratio * 100.0f) + "%";
    }

}
