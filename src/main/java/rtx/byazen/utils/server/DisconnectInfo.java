package rtx.byazen.utils.server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Разбор экрана отключения (идея №119 из IDEAS.md).
 * <p>
 * Клиент не подменяет экран отключения, а читает с него причину: это позволяет вежливо повести себя
 * с очередью — запомнить место в ней, показать его игроку и не устраивать переподключение после
 * бана или кик-а. Всё через обращения по именам, поэтому разбор не ломается от мелких правок игры.
 */
public final class DisconnectInfo {

    private static final Pattern QUEUE_RU = Pattern.compile("(?:место|позиция)[^0-9]{0,20}(\\d{1,6})");
    private static final Pattern QUEUE_EN = Pattern.compile("(?:queue|position|place)[^0-9]{0,20}(\\d{1,6})", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOTAL_EN = Pattern.compile("of\\s+(\\d{1,6})", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOTAL_RU = Pattern.compile("из\\s+(\\d{1,6})");

    private static final String[] PUNISH_MARKERS = {
        "бан", "забан", "заблокирован", "кик", "кикнут", "нарушение", "правил",
        "banned", "ban", "kicked", "kick", "blacklist", "mut"
    };

    private DisconnectInfo() {
    }

    /** Класс экрана отключения, найденный по имени: он менял имена между версиями. */
    private static Class<?> disconnectScreenClass() {
        String[] names = {"net.minecraft.client.gui.screen.DisconnectedScreen", "net.minecraft.client.gui.screen.DisconnectScreen"};
        for (String name : names) {
            try {
                return Class.forName(name);
            }
            catch (Throwable ignored) {
                // проверяем следующее имя
            }
        }
        return null;
    }

    /** Текущий экран игры, если он относится к отключению. */
    public static Screen disconnectScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return null;
        }
        Screen screen = client.currentScreen;
        if (screen == null) {
            return null;
        }
        Class<?> type = DisconnectInfo.disconnectScreenClass();
        if (type != null && type.isInstance(screen)) {
            return screen;
        }
        String simple = screen.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        return simple.contains("disconnect") ? screen : null;
    }

    /** Текст причины отключения: собирается из заголовка и строк экрана. */
    public static String reason(Screen screen) {
        if (screen == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        String title = DisconnectInfo.textOf(screen, "title");
        if (!title.isEmpty()) {
            builder.append(title);
        }
        String message = DisconnectInfo.textOf(screen, "reason");
        if (message.isEmpty()) {
            message = DisconnectInfo.textOf(screen, "message");
        }
        if (!message.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(message);
        }
        return builder.toString().replace('\n', ' ').trim();
    }

    private static String textOf(Object target, String fieldName) {
        if (target == null) {
            return "";
        }
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(target);
                if (value instanceof Text) {
                    return ((Text)value).getString();
                }
                if (value instanceof String) {
                    return (String)value;
                }
            }
            catch (Throwable ignored) {
                // поля может не быть — идём к родителю
            }
            type = type.getSuperclass();
        }
        return "";
    }

    /** Короткая причина для чата и HUD. */
    public static String shortReason(String reason) {
        String clean = reason == null ? "" : reason.replaceAll("\\s+", " ").trim();
        if (clean.isEmpty()) {
            return "связь с сервером потеряна";
        }
        return clean.length() <= 70 ? clean : clean.substring(0, 69) + "…";
    }

    /** Место в очереди с экрана отключения; 0 — очередь не найдена. */
    public static int queuePosition(Screen screen) {
        String text = DisconnectInfo.reason(screen);
        if (text.isEmpty()) {
            return 0;
        }
        Matcher russian = QUEUE_RU.matcher(text);
        if (russian.find()) {
            return DisconnectInfo.parse(russian.group(1));
        }
        Matcher english = QUEUE_EN.matcher(text);
        if (english.find()) {
            return DisconnectInfo.parse(english.group(1));
        }
        return 0;
    }

    /** Всего мест в очереди; 0 — не указано. */
    public static int queueTotal(Screen screen) {
        String text = DisconnectInfo.reason(screen);
        if (text.isEmpty()) {
            return 0;
        }
        Matcher russian = TOTAL_RU.matcher(text);
        if (russian.find()) {
            return DisconnectInfo.parse(russian.group(1));
        }
        Matcher english = TOTAL_EN.matcher(text);
        if (english.find()) {
            return DisconnectInfo.parse(english.group(1));
        }
        return 0;
    }

    private static int parse(String value) {
        try {
            return Integer.parseInt(value.trim());
        }
        catch (Throwable ignored) {
            return 0;
        }
    }

    /** Наказание ли это: после бана или кика переподключаться не нужно. */
    public static boolean looksLikePunishment(String reason) {
        if (reason == null || reason.isEmpty()) {
            return false;
        }
        String lower = reason.toLowerCase(Locale.ROOT);
        for (String marker : PUNISH_MARKERS) {
            if (lower.contains(marker)) {
                return true;
            }
        }
        return false;
    }

    /** Ждет ли сервер решения человека: при пароле или капче автопереподключение бессмысленно. */
    public static boolean needsAttention(String reason) {
        if (reason == null || reason.isEmpty()) {
            return false;
        }
        String lower = reason.toLowerCase(Locale.ROOT);
        return lower.contains("пароль") || lower.contains("password") || lower.contains("капч")
                || lower.contains("captcha") || lower.contains("дважды") || lower.contains("двухфактор")
                || lower.contains("2fa") || lower.contains("авториза");
    }
}
