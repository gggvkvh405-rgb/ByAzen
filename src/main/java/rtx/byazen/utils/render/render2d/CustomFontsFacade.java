package rtx.byazen.utils.render.render2d;

/**
 * Мелкие вопросы читаемости интерфейса к системе шрифтов (идея №195 из IDEAS.md).
 */
public final class CustomFontsFacade {

    private static volatile boolean highContrast = false;

    private CustomFontsFacade() {
    }

    /** Повышенная контрастность надписей (включается пользователем). */
    public static boolean highContrast() {
        return highContrast;
    }

    public static void setHighContrast(boolean value) {
        highContrast = value;
    }
}
