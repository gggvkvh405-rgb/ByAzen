package rtx.byazen.utils.safety;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.logs.ClientLog;

/**
 * Анти-краш (идея №169 из IDEAS.md).
 * <p>
 * Шина событий уже ловит исключения обработчиков, но раньше модуль продолжал падать каждый кадр,
 * заливая лог. Теперь сбои считаются: после {@code N} ошибок модуль отключается сам, в чат уходит
 * понятное сообщение с причиной и подсказкой, а запись попадает в лог клиента.
 */
public final class ModuleGuard {

    private static final Map<String, Integer> FAILS = new LinkedHashMap<String, Integer>();
    private static final Map<String, String> REASONS = new LinkedHashMap<String, String>();
    private static final List<String> HISTORY = new ArrayList<String>();
    private static int limit = 3;

    private ModuleGuard() {
    }

    /** Порог сбоев берётся из настроек модуля «Защита модулей» (1–10, по умолчанию 3). */
    public static void setLimit(int value) {
        limit = Math.max(1, Math.min(10, value));
    }

    public static int limit() {
        return limit;
    }

    /**
     * Вызывается шиной событий при исключении в обработчике. Возвращает {@code true}, если модуль
     * был отключён прямо сейчас.
     */
    public static boolean failed(Object owner, Throwable throwable) {
        if (!(owner instanceof Module)) {
            return false;
        }
        Module module = (Module)owner;
        String name = module.getName();
        String reason = ModuleGuard.describe(throwable);
        int count = ModuleGuard.countOf(name) + 1;
        FAILS.put(name, count);
        REASONS.put(name, reason);
        HISTORY.add(module.getDisplayName() + " — " + reason);
        while (HISTORY.size() > 40) {
            HISTORY.remove(0);
        }
        ClientLog.error("сбой модуля " + module.getDisplayName() + " (№" + count + "): " + reason);
        if (count < limit) {
            return false;
        }
        FAILS.put(name, 0);
        try {
            if (module.isEnabled()) {
                module.setEnabled(false);
            }
        }
        catch (Throwable throwable2) {
            ClientLog.error("не удалось отключить модуль " + name + ": " + ModuleGuard.describe(throwable2));
        }
        ClientLog.error("модуль " + module.getDisplayName() + " отключён после " + count + " ошибок — " + reason);
        ChatMessage.send("§cByAzen: модуль " + module.getDisplayName() + " отключён после " + count
                + " ошибок (" + reason + "). Запустите игру заново, если это повторяется");
        return true;
    }

    private static int countOf(String name) {
        Integer value = FAILS.get(name);
        return value == null ? 0 : value;
    }

    /** Человеческая причина: тип плюс сообщение, коротко. */
    public static String describe(Throwable throwable) {
        if (throwable == null) {
            return "неизвестная ошибка";
        }
        Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
        String message = cause.getMessage() == null ? "" : cause.getMessage().trim();
        String text = cause.getClass().getSimpleName() + (message.isEmpty() ? "" : ": " + message);
        return text.length() <= 120 ? text : text.substring(0, 119) + "…";
    }

    public static boolean hasProblems() {
        return !HISTORY.isEmpty();
    }

    /** Список модулей, у которых уже были сбои (для окна диагностики). */
    public static List<String> problems() {
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : FAILS.entrySet()) {
            if (entry.getValue() <= 0) {
                continue;
            }
            String reason = REASONS.get(entry.getKey());
            list.add(entry.getKey() + ": ошибок " + entry.getValue() + (reason == null ? "" : " (" + reason + ")"));
        }
        return list;
    }

    public static List<String> history() {
        return new ArrayList<String>(HISTORY);
    }

    public static String summary() {
        int failing = ModuleGuard.problems().size();
        return failing == 0 ? "сбоев модулей нет (порог отключения " + limit + ")" : "модулей со сбоями: " + failing;
    }

    /** Обнуляет счётчики: после исправления причины модуль можно включать заново. */
    public static void reset() {
        FAILS.clear();
        REASONS.clear();
        HISTORY.clear();
    }

    /** Отключает все модули, у которых есть сбои — «аварийный покой». */
    public static int disableBroken() {
        int disabled = 0;
        for (String name : new ArrayList<String>(FAILS.keySet())) {
            Module module = ModuleManager.get().findByName(name);
            if (module == null || !module.isEnabled()) {
                continue;
            }
            module.setEnabled(false);
            ++disabled;
        }
        if (disabled > 0) {
            ClientLog.warn("защита модулей отключила модулей: " + disabled);
        }
        return disabled;
    }

    public static String statusText() {
        return String.format(Locale.ROOT, "%s · порог %d", ModuleGuard.summary(), limit);
    }
}
