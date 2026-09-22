package rtx.byazen.utils.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.UiScaleModule;

/**
 * Проверка раскладки окон (идея №167 из IDEAS.md): влезают ли панели клиента в доступное место
 * при текущем масштабе интерфейса Minecraft и при масштабах 1x–4x на типовых разрешениях.
 * <p>
 * Окна ByAzen рисуются в своём пространстве размером «кадр / масштаб клиента» (см. {@link UiScale}),
 * поэтому при 4x на 1080p места остаётся 480×270 — крупные панели должны уметь уменьшаться.
 * Здесь это считается честно: для каждой панели видно, влезает она или во сколько её нужно ужать,
 * а кнопка «Подогнать масштаб» выставляет подходящий коэффициент в настройках модуля «Масштаб UI».
 */
public final class LayoutAudit {

    /** Размеры окон клиента в собственных пикселях (то, что нарисовано в коде). */
    private static final Screen[] SCREENS = new Screen[]{
            new Screen("Диагностика", 640, 392),
            new Screen("Редактор темы", 620, 340),
            new Screen("Облачные конфиги", 520, 300),
            new Screen("Импорт конфигов", 520, 286),
            new Screen("Каталог косметики", 560, 320),
            new Screen("Примерка питомцев", 540, 320),
            new Screen("Обложки профиля", 520, 292),
            new Screen("Плеер музыки", 500, 300),
            new Screen("Скриншоты", 520, 300)
    };

    /** Типовые разрешения, на которых проверяем (ширина, высота кадра). */
    private static final int[][] RESOLUTIONS = new int[][]{{1920, 1080}, {2560, 1440}, {3840, 2160}};

    private LayoutAudit() {
    }

    public record Screen(String name, int width, int height) {
    }

    public record Row(String name, int width, int height, float availableW, float availableH, float zoom, boolean fits) {
        public String note() {
            if (this.fits) {
                return "влезает (запас " + LayoutAudit.intText(this.availableW - (float)this.width) + " px)";
            }
            return "нужно ужать до " + Math.round(this.zoom * 100.0f) + " % (есть " + LayoutAudit.intText(this.availableW)
                    + "×" + LayoutAudit.intText(this.availableH) + ")";
        }

        public String text() {
            return this.name + " " + this.width + "×" + this.height + " — " + this.note();
        }
    }

    /** Текущее доступное место с учётом масштаба клиента: «кадр / действующий масштаб». */
    public static float[] available() {
        return new float[]{Position.screenWidth(), Position.screenHeight()};
    }

    /** Какое ужатие нужно панели, чтобы влезть: 1.0 — влезает без изменений. */
    public static float zoomFor(int width, int height) {
        float rawW = Position.screenWidthRaw();
        float rawH = Position.screenHeightRaw();
        float zoom = Math.min(1.0f, Math.min(rawW / (float)width, rawH / (float)height));
        return Math.max(0.5f, zoom);
    }

    public static List<Row> rows() {
        ArrayList<Row> list = new ArrayList<Row>();
        float current = Math.max(0.5f, UiScale.screenScale());
        float rawW = Position.screenWidthRaw();
        float rawH = Position.screenHeightRaw();
        for (Screen screen : SCREENS) {
            float zoom = LayoutAudit.zoomFor(screen.width(), screen.height());
            boolean fits = zoom >= 0.999f;
            float spaceW = rawW / (fits ? current : zoom);
            float spaceH = rawH / (fits ? current : zoom);
            list.add(new Row(screen.name(), screen.width(), screen.height(), spaceW, spaceH, zoom, fits));
        }
        return list;
    }

    /** Самый маленький коэффициент, при котором влезают все панели (не больше 1.0). */
    public static float fitZoom() {
        float rawW = Position.screenWidthRaw();
        float rawH = Position.screenHeightRaw();
        float zoom = 1.0f;
        for (Screen screen : SCREENS) {
            zoom = Math.min(zoom, Math.min(rawW / (float)screen.width(), rawH / (float)screen.height()));
        }
        return Math.max(0.5f, Math.min(1.0f, zoom));
    }

    /** Ужимает окна клиента так, чтобы влезали все панели. Возвращает новый коэффициент. */
    public static float applyFitZoom() {
        float zoom = LayoutAudit.fitZoom();
        UiScaleModule module = ModuleManager.get().get(UiScaleModule.class);
        if (module == null) {
            return UiScale.value();
        }
        float current = UiScale.value();
        if (zoom < current) {
            module.scale.setValue(zoom);
            UiScale.invalidate();
        }
        return zoom;
    }

    /** Текстовая сводка: сколько панелей не влезает прямо сейчас. */
    public static String summary() {
        int bad = 0;
        for (Row row : LayoutAudit.rows()) {
            if (!row.fits()) {
                ++bad;
            }
        }
        float[] space = LayoutAudit.available();
        String where = "окно " + LayoutAudit.intText(space[0]) + "×" + LayoutAudit.intText(space[1])
                + ", общий масштаб окон " + Math.round(UiScale.value() * 100.0f) + " %";
        return bad == 0 ? "раскладка в порядке (" + where + ")" : "не влезает панелей: " + bad + " из " + SCREENS.length + " (" + where + ")";
    }

    /** Полный отчёт: все панели плюс проверка типовых разрешений при масштабах 1x–4x. */
    public static String report() {
        StringBuilder builder = new StringBuilder();
        builder.append("Проверка раскладки ByAzen\n");
        builder.append("Сейчас: ").append(LayoutAudit.summary()).append('\n');
        for (Row row : LayoutAudit.rows()) {
            builder.append(row.fits() ? "  [ок] " : "  [!!] ").append(row.text()).append('\n');
        }
        builder.append("Типовые разрешения (место под окно при масштабе интерфейса 1x–4x):\n");
        for (int[] resolution : RESOLUTIONS) {
            builder.append("  ").append(resolution[0]).append('×').append(resolution[1]).append(": ");
            StringBuilder line = new StringBuilder();
            for (int guiScale = 1; guiScale <= 4; ++guiScale) {
                float spaceW = (float)resolution[0] / (float)guiScale;
                float spaceH = (float)resolution[1] / (float)guiScale;
                int bad = 0;
                for (Screen screen : SCREENS) {
                    if (spaceW >= (float)screen.width() && spaceH >= (float)screen.height()) {
                        continue;
                    }
                    ++bad;
                }
                if (line.length() > 0) {
                    line.append(", ");
                }
                line.append(guiScale).append("x — ").append(bad == 0 ? "все окна" : "не влезает " + bad);
            }
            builder.append(line).append('\n');
        }
        float zoom = LayoutAudit.fitZoom();
        builder.append("Кнопка «Подогнать масштаб» выставляет ").append(Math.round(zoom * 100.0f)).append(" %.\n");
        return builder.toString();
    }

    private static String intText(float value) {
        return String.format(Locale.ROOT, "%.0f", value);
    }
}
