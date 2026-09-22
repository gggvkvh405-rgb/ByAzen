package rtx.byazen.utils.access;

import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.AccessibilityModule;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Режим для слабовидящих (идея №195 из IDEAS.md).
 * <p>
 * Крупнее шрифт во всех окнах и HUD, выше контраст (через тему окон), звуковые уведомления на
 * важные события и меньше мигающих анимаций. Всё выключается так же просто, как включается.
 */
public final class Accessibility {

    private static boolean alertPlaying;

    private Accessibility() {
    }

    private static AccessibilityModule module() {
        if (ModuleManager.get() == null) {
            return null;
        }
        return ModuleManager.get().get(AccessibilityModule.class);
    }

    public static boolean active() {
        AccessibilityModule module = Accessibility.module();
        return module != null && module.isEnabled();
    }

    /** Множитель размера шрифта: 1.0 — обычный, до 1.35 — крупный. */
    public static float fontScale() {
        AccessibilityModule module = Accessibility.module();
        if (module == null || !module.isEnabled() || !module.bigFont.getValue()) {
            return 1.0f;
        }
        return Math.max(1.0f, Math.min(1.35f, module.fontSize.getValue()));
    }

    public static boolean highContrast() {
        AccessibilityModule module = Accessibility.module();
        return module != null && module.isEnabled() && module.contrast.getValue();
    }

    public static boolean soundAlerts() {
        AccessibilityModule module = Accessibility.module();
        return module != null && module.isEnabled() && module.soundAlerts.getValue();
    }

    public static boolean reduceAnimations() {
        AccessibilityModule module = Accessibility.module();
        return module != null && module.isEnabled() && module.reduceAnimations.getValue();
    }

    /** Звук важного уведомления: играется только если включён режим. */
    public static void soundAlert() {
        if (!Accessibility.soundAlerts() || Accessibility.alertPlaying) {
            return;
        }
        Accessibility.alertPlaying = true;
        try {
            Sounds.play("gui_open");
        }
        catch (Throwable throwable) {
            // звук может быть недоступен — это не повод ломать уведомление
        }
        finally {
            Accessibility.alertPlaying = false;
        }
    }

    /** Ускорение анимаций: 1.0 — обычная скорость, 1.6 — быстрее и меньше мельтешения. */
    public static float animationSpeed() {
        return Accessibility.reduceAnimations() ? 1.6f : 1.0f;
    }

    public static String summary() {
        if (!Accessibility.active()) {
            return "режим для слабовидящих выключен";
        }
        StringBuilder builder = new StringBuilder("крупный шрифт " + Math.round(Accessibility.fontScale() * 100.0f) + "%");
        if (Accessibility.highContrast()) {
            builder.append(", высокий контраст");
        }
        if (Accessibility.soundAlerts()) {
            builder.append(", звуковые уведомления");
        }
        if (Accessibility.reduceAnimations()) {
            builder.append(", меньше анимаций");
        }
        return builder.toString();
    }
}

