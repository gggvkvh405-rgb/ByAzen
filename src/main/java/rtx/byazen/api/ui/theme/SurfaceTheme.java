package rtx.byazen.api.ui.theme;

import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.ThemeLookModule;

/**
 * Полные темы (идея №50 из IDEAS.md): единая точка, через которую интерфейс узнаёт,
 * тёмная сейчас тема, светлая или неоновая, и как перекрашивать подложки, текст и ванильные экраны.
 * <p>
 * Значение кэшируется на полсекунды: метод вызывается при каждой надписи и не должен тормозить кадр.
 */
public final class SurfaceTheme {

    public static final String DARK = "Тёмная";
    public static final String LIGHT = "Светлая";
    public static final String NEON = "Неоновая";
    private static final int CODE_OFF = 0;
    private static final int CODE_DARK = 1;
    private static final int CODE_LIGHT = 2;
    private static final int CODE_NEON = 3;

    private static volatile int cached = -1;
    private static volatile long cachedAt;

    private SurfaceTheme() {
    }

    private static int code() {
        long now = System.currentTimeMillis();
        int code = SurfaceTheme.cached;
        if (code >= 0 && now - SurfaceTheme.cachedAt < 500L) {
            return code;
        }
        ThemeLookModule module = ModuleManager.get().get(ThemeLookModule.class);
        if (module == null || !module.isEnabled()) {
            code = CODE_OFF;
        }
        else if (module.look.is(LIGHT)) {
            code = CODE_LIGHT;
        }
        else if (module.look.is(NEON)) {
            code = CODE_NEON;
        }
        else {
            code = CODE_DARK;
        }
        SurfaceTheme.cached = code;
        SurfaceTheme.cachedAt = now;
        return code;
    }

    public static boolean isLight() {
        return SurfaceTheme.code() == CODE_LIGHT;
    }

    public static boolean isNeon() {
        return SurfaceTheme.code() == CODE_NEON;
    }

    public static boolean active() {
        return SurfaceTheme.code() > CODE_DARK;
    }

    public static String mode() {
        int code = SurfaceTheme.code();
        if (code == CODE_LIGHT) {
            return LIGHT;
        }
        if (code == CODE_NEON) {
            return NEON;
        }
        return DARK;
    }

    /** Подкрашивает стеклянную подложку панели под тему. */
    public static int tintPanel(int argb) {
        int code = SurfaceTheme.code();
        if (code <= CODE_DARK || !SurfaceTheme.panelFlag()) {
            return argb;
        }
        int alpha = argb >>> 24 & 0xFF;
        if (alpha == 0) {
            return argb;
        }
        int rgb = argb & 0xFFFFFF;
        float lum = SurfaceTheme.luminance(rgb);
        if (code == CODE_LIGHT) {
            float factor = lum < 60.0f ? 0.94f : 0.82f;
            int tinted = SurfaceTheme.lerp(rgb, 0xF1F4FA, factor);
            return SurfaceTheme.withAlpha(tinted, alpha);
        }
        int deep = SurfaceTheme.lerp(rgb, 0x04050B, 0.78f);
        if (SurfaceTheme.neonFlag()) {
            deep = SurfaceTheme.lerp(deep, ClientAccent.accentOpaque(), 0.12f);
        }
        return SurfaceTheme.withAlpha(deep, alpha);
    }

    /** Перекрашивает цвет надписи под тему (светлый текст в светлой теме становится тёмным). */
    public static int ink(int argb) {
        int code = SurfaceTheme.code();
        if (code <= CODE_DARK || !SurfaceTheme.textFlag()) {
            return argb;
        }
        int alpha = argb >>> 24 & 0xFF;
        if (alpha == 0) {
            return argb;
        }
        int rgb = argb & 0xFFFFFF;
        float lum = SurfaceTheme.luminance(rgb);
        if (code == CODE_LIGHT) {
            if (lum < 150.0f) {
                return argb;
            }
            float factor = Math.min(1.0f, 0.62f + (lum - 150.0f) / 105.0f * 0.3f);
            return SurfaceTheme.withAlpha(SurfaceTheme.lerp(rgb, 0x181A22, factor), alpha);
        }
        if (lum >= 210.0f) {
            return SurfaceTheme.withAlpha(SurfaceTheme.lerp(rgb, ClientAccent.accentOpaque(), 0.22f), alpha);
        }
        return argb;
    }

    /** Заливка для ванильных экранов (инвентарь, сундук, меню) или 0, если тема не меняет их вид. */
    public static int vanillaTint() {
        int code = SurfaceTheme.code();
        if (code <= CODE_DARK || !SurfaceTheme.vanillaFlag()) {
            return 0;
        }
        return code == CODE_LIGHT ? 0x74F3F6FB : 0x55120B2A;
    }

    /** Вторая краска ванильного экрана (лёгкий градиент сверху вниз). */
    public static int vanillaTintBottom() {
        int code = SurfaceTheme.code();
        if (code <= CODE_DARK || !SurfaceTheme.vanillaFlag()) {
            return 0;
        }
        return code == CODE_LIGHT ? 0x5CE6EAF2 : 0x66230F3F;
    }

    private static boolean panelFlag() {
        ThemeLookModule module = ModuleManager.get().get(ThemeLookModule.class);
        return module != null && module.panels.getValue();
    }

    private static boolean textFlag() {
        ThemeLookModule module = ModuleManager.get().get(ThemeLookModule.class);
        return module != null && module.recolorText.getValue();
    }

    private static boolean vanillaFlag() {
        ThemeLookModule module = ModuleManager.get().get(ThemeLookModule.class);
        return module != null && module.vanillaScreens.getValue();
    }

    private static boolean neonFlag() {
        ThemeLookModule module = ModuleManager.get().get(ThemeLookModule.class);
        return module != null && module.neonGlow.getValue();
    }

    private static float luminance(int rgb) {
        return 0.299f * (float)(rgb >> 16 & 0xFF) + 0.587f * (float)(rgb >> 8 & 0xFF) + 0.114f * (float)(rgb & 0xFF);
    }

    private static int lerp(int from, int to, float factor) {
        float f = Math.max(0.0f, Math.min(1.0f, factor));
        int r = Math.round((float)(from >> 16 & 0xFF) + (float)((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * f);
        int g = Math.round((float)(from >> 8 & 0xFF) + (float)((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * f);
        int b = Math.round((float)(from & 0xFF) + (float)((to & 0xFF) - (from & 0xFF)) * f);
        return Math.max(0, Math.min(255, r)) << 16 | Math.max(0, Math.min(255, g)) << 8 | Math.max(0, Math.min(255, b));
    }

    private static int withAlpha(int rgb, int alpha) {
        return rgb & 0xFFFFFF | (alpha & 0xFF) << 24;
    }
}
