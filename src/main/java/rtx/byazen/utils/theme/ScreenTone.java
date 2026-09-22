package rtx.byazen.utils.theme;

import java.time.LocalTime;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.ScreenToneModule;
import rtx.byazen.utils.access.Accessibility;

/**
 * Тёмный и светлый вариант окон клиента (идея №194 из IDEAS.md).
 * <p>
 * Цвета окон, подложек и текста берутся отсюда: в тёмном режиме — привычная тёмная палитра, в
 * светлом — молочные панели с тёмным текстом, а в режиме «Авто» светлая тема включается днём.
 * Отдельно учитывается режим для слабовидящих: он поднимает контраст и делает подложки плотнее.
 */
public final class ScreenTone {

    private ScreenTone() {
    }

    private static ScreenToneModule module() {
        if (ModuleManager.get() == null) {
            return null;
        }
        return ModuleManager.get().get(ScreenToneModule.class);
    }

    /** Светлая тема сейчас? */
    public static boolean light() {
        ScreenToneModule module = ScreenTone.module();
        if (module == null || !module.isEnabled()) {
            return false;
        }
        String mode = module.mode.getValue();
        if ("Светлая".equals(mode)) {
            return true;
        }
        if ("Авто".equals(mode)) {
            int hour = LocalTime.now().getHour();
            return hour >= 7 && hour < 19;
        }
        return false;
    }

    /** Плотность подложек: в светлой теме и при высоком контрасте панели почти непрозрачные. */
    public static float opacity() {
        float base = ScreenTone.light() ? 0.97f : 0.55f;
        if (Accessibility.highContrast()) {
            base = Math.min(1.0f, base + 0.2f);
        }
        return base;
    }

    /** Цвет панели окна. */
    public static int panel(float alpha) {
        if (ScreenTone.light()) {
            return ScreenTone.rgba(246, 248, 252, 250.0f * alpha);
        }
        return ScreenTone.rgba(16, 18, 26, 205.0f * alpha);
    }

    /** Основной текст. */
    public static int ink(float alpha) {
        if (ScreenTone.light()) {
            return ScreenTone.rgba(22, 26, 36, 245.0f * alpha);
        }
        return ScreenTone.rgba(233, 238, 248, 240.0f * alpha);
    }

    /** Подписи и второстепенный текст. */
    public static int sub(float alpha) {
        if (ScreenTone.light()) {
            return ScreenTone.rgba(88, 96, 112, 235.0f * alpha);
        }
        return ScreenTone.rgba(168, 176, 194, 230.0f * alpha);
    }

    /** Поля, плашки, строки списка. */
    public static int field(float alpha) {
        if (ScreenTone.light()) {
            return ScreenTone.rgba(20, 26, 40, 14.0f * alpha);
        }
        return ScreenTone.rgba(255, 255, 255, 14.0f * alpha);
    }

    /** Подсветка под курсором. */
    public static int hover(float alpha) {
        if (ScreenTone.light()) {
            return ScreenTone.rgba(20, 26, 40, 26.0f * alpha);
        }
        return ScreenTone.rgba(255, 255, 255, 24.0f * alpha);
    }

    public static String summary() {
        ScreenToneModule module = ScreenTone.module();
        String mode = module == null ? "Тёмная" : module.mode.getValue();
        return "тема окон: " + (ScreenTone.light() ? "светлая" : "тёмная") + " (режим " + mode + ")"
                + (Accessibility.highContrast() ? " · высокий контраст" : "");
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, (int)alpha));
        return a << 24 | r << 16 | g << 8 | b;
    }
}

