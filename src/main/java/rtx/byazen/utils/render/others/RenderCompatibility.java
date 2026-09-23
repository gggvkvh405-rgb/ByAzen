package rtx.byazen.utils.render.others;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public final class RenderCompatibility {
    private static Boolean safeWorldEffects;
    private static String safeWorldEffectsReason;
    private static final String SAFE_WORLD_EFFECTS_PROPERTY = "byazen_safeWorldEffects";
    private static final String SAFE_WORLD_EFFECTS_ENV = "BYAZEN_SAFE_WORLD_EFFECTS";
    private static Boolean disableFragEffectScanShader;
    private static String disableFragEffectScanShaderReason;
    private static final String DISABLE_FRAG_EFFECT_SCAN_PROPERTY = "byazen_disableFragEffectScanShader";
    private static final String DISABLE_FRAG_EFFECT_SCAN_ENV = "BYAZEN_DISABLE_FRAG_EFFECT_SCAN_SHADER";

    private RenderCompatibility() {
    }

    static {
        safeWorldEffectsReason = "unresolved";
        disableFragEffectScanShaderReason = "unresolved";
    }

    private static String normalize(String string) {
        return string == null ? "" : string.trim().toLowerCase();
    }

    private static boolean detectFragEffectScanShaderBlocklist() {
        if (!RenderSystem.isOnRenderThread()) {
            disableFragEffectScanShaderReason = "render thread unavailable";
            return false;
        }
        String string = RenderCompatibility.readGlString(7936);
        String string2 = RenderCompatibility.readGlString(7937);
        String string3 = RenderCompatibility.readGlString(7938);
        disableFragEffectScanShaderReason = "safe depth-copy shader path enabled: vendor=" + string + ", renderer=" + string2 + ", version=" + string3;
        return false;
    }

    public static String getDisableFragEffectScanShaderReason() {
        return disableFragEffectScanShaderReason;
    }

    public static boolean shouldDisableFragEffectScanShader() {
        Boolean bl = RenderCompatibility.readManualOverride(System.getProperty(DISABLE_FRAG_EFFECT_SCAN_PROPERTY));
        if (bl == null) {
            bl = RenderCompatibility.readManualOverride(System.getenv(DISABLE_FRAG_EFFECT_SCAN_ENV));
        }
        if (bl != null) {
            disableFragEffectScanShader = bl;
            disableFragEffectScanShaderReason = "manual override";
            return bl;
        }
        if (disableFragEffectScanShader == null) {
            disableFragEffectScanShader = RenderCompatibility.detectFragEffectScanShaderBlocklist();
        }
        return disableFragEffectScanShader;
    }

    public static void primeFromCurrentContext() {
        RenderCompatibility.useSafeWorldEffects();
        RenderCompatibility.shouldDisableFragEffectScanShader();
    }

    /**
     * Готов ли OpenGL-контекст именно в этом потоке.
     * <p>
     * Важная защита: клиент инициализируется раньше, чем Minecraft создаёт GL-устройство, и вызов
     * любой GL-функции в этот момент — это нативное падение драйвера
     * ({@code EXCEPTION_ACCESS_VIOLATION [lwjgl_opengl.dll+…]}), которое невозможно поймать
     * на стороне Java. Поэтому перед каждым обращением к OpenGL проверяем, что мы на потоке
     * отрисовки и что возможности GL созданы ({@code GL.createCapabilities()} уже вызывался).
     */
    public static boolean glReady() {
        try {
            return RenderSystem.isOnRenderThread() && GL.getCapabilities() != null;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    /** Короткая строка про OpenGL для логов: производитель, модель, версия. */
    public static String glSummary() {
        if (!RenderCompatibility.glReady()) {
            return "контекст OpenGL ещё не создан";
        }
        return "vendor=" + RenderCompatibility.readGlString(7936)
                + " · renderer=" + RenderCompatibility.readGlString(7937)
                + " · version=" + RenderCompatibility.readGlString(7938);
    }

    private static String readGlString(int n) {
        if (!RenderCompatibility.glReady()) {
            return "недоступно";
        }
        try {
            return RenderCompatibility.normalize(GL11.glGetString((int)n));
        }
        catch (Throwable throwable) {
            return "";
        }
    }

    public static boolean useSafeWorldEffects() {
        Boolean bl = RenderCompatibility.readManualOverride(System.getProperty(SAFE_WORLD_EFFECTS_PROPERTY));
        if (bl == null) {
            bl = RenderCompatibility.readManualOverride(System.getenv(SAFE_WORLD_EFFECTS_ENV));
        }
        if (bl != null) {
            safeWorldEffects = bl;
            safeWorldEffectsReason = "manual override";
            return bl;
        }
        if (safeWorldEffects == null) {
            safeWorldEffects = false;
            safeWorldEffectsReason = "default-path";
        }
        return safeWorldEffects;
    }

    private static Boolean readManualOverride(String string) {
        if (string == null) {
            return null;
        }
        String string2 = string.trim().toLowerCase();
        if (string2.equals("1") || string2.equals("true") || string2.equals("yes") || string2.equals("on")) {
            return true;
        }
        if (string2.equals("0") || string2.equals("false") || string2.equals("no") || string2.equals("off")) {
            return false;
        }
        return null;
    }

    public static String getSafeWorldEffectsReason() {
        return safeWorldEffectsReason;
    }
}

