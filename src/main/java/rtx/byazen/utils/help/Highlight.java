package rtx.byazen.utils.help;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import rtx.byazen.api.modules.Module;

/**
 * Подсветка «где найти» (идея №189 из IDEAS.md).
 * <p>
 * Когда игрок ищет модуль через команду {@code find}/{@code где} или через туториал, найденный
 * модуль отмечается мягким пульсирующим свечением в списке клиента на несколько секунд — видно,
 * куда именно переключиться. Подсветка живёт только в памяти и гаснет сама.
 */
public final class Highlight {

    /** Сколько держится подсветка. */
    public static final long DURATION = 4500L;
    private static final Map<String, Long> PULSES = new LinkedHashMap<String, Long>();

    private Highlight() {
    }

    public static void pulse(Module module) {
        Highlight.pulse(module == null ? null : module.getName());
    }

    public static void pulse(String moduleName) {
        if (moduleName == null || moduleName.isBlank()) {
            return;
        }
        synchronized (PULSES) {
            Highlight.expire();
            PULSES.put(moduleName.toLowerCase(Locale.ROOT), System.currentTimeMillis() + DURATION);
        }
    }

    public static boolean active(String moduleName) {
        return Highlight.factor(moduleName) > 0.0f;
    }

    /** Насколько ярко светить: 1.0 в начале, 0.0 когда подсветка кончилась. */
    public static float factor(String moduleName) {
        if (moduleName == null) {
            return 0.0f;
        }
        Long until;
        synchronized (PULSES) {
            Highlight.expire();
            until = PULSES.get(moduleName.toLowerCase(Locale.ROOT));
        }
        if (until == null) {
            return 0.0f;
        }
        long left = until - System.currentTimeMillis();
        if (left <= 0L) {
            return 0.0f;
        }
        float progress = (float)((double)left / (double)DURATION);
        return 0.35f + 0.65f * progress;
    }

    public static int activeCount() {
        synchronized (PULSES) {
            Highlight.expire();
            return PULSES.size();
        }
    }

    public static void clear() {
        synchronized (PULSES) {
            PULSES.clear();
        }
    }

    public static String summary() {
        int count = Highlight.activeCount();
        return count == 0 ? "подсветка не активна" : "подсвечено модулей: " + count;
    }

    private static void expire() {
        long now = System.currentTimeMillis();
        PULSES.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}

