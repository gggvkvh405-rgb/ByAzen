package rtx.byazen.utils.graphics;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.ByAzen;

/**
 * Доступ к настройкам графики игры (идея №116 из IDEAS.md).
 * <p>
 * Пресеты меняют настройки самой игры, а не только модулей клиента, поэтому здесь собран аккуратный
 * доступ к ним: значения читаются и записываются через те же объекты, что и в меню, а после смены
 * настройки сохраняются в файл. Все обращения — по именам, поэтому пресеты не ломаются от мелких
 * переименований внутри игры.
 */
public final class GraphicsSettings {

    private GraphicsSettings() {
    }

    /** Объект настроек игры. */
    private static Object gameOptions() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) {
                return null;
            }
            return GraphicsSettings.field(client, "options");
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    /** Находит настройку по любому из имён. */
    public static Object option(String... names) {
        Object options = GraphicsSettings.gameOptions();
        if (options == null) {
            return null;
        }
        for (String name : names) {
            Object value = GraphicsSettings.field(options, name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static boolean exists(String... names) {
        return GraphicsSettings.option(names) != null;
    }

    /** Текущее значение настройки в виде строки. */
    public static String current(String... names) {
        Object option = GraphicsSettings.option(names);
        if (option == null) {
            return "недоступно";
        }
        Object value = GraphicsSettings.call(option, "getValue");
        if (value == null) {
            return "неизвестно";
        }
        String text = String.valueOf(value);
        int dot = text.lastIndexOf(46);
        if (dot >= 0 && value.getClass().isEnum()) {
            return text.substring(dot + 1);
        }
        return text;
    }

    /** Числовое значение настройки. */
    public static double number(String... names) {
        Object option = GraphicsSettings.option(names);
        if (option == null) {
            return -1.0;
        }
        Object value = GraphicsSettings.call(option, "getValue");
        if (value instanceof Number) {
            return ((Number)value).doubleValue();
        }
        return -1.0;
    }

    /** Ставит число, подбирая тип под текущее значение. */
    public static boolean setNumber(double value, String... names) {
        Object option = GraphicsSettings.option(names);
        if (option == null) {
            return false;
        }
        Object current = GraphicsSettings.call(option, "getValue");
        Object converted;
        if (current instanceof Integer) {
            converted = Integer.valueOf((int)Math.round(value));
        }
        else if (current instanceof Double) {
            converted = Double.valueOf(value);
        }
        else if (current instanceof Float) {
            converted = Float.valueOf((float)value);
        }
        else {
            converted = Integer.valueOf((int)Math.round(value));
        }
        return GraphicsSettings.set(option, converted);
    }

    /** Ставит логическое значение. */
    public static boolean setBoolean(boolean value, String... names) {
        Object option = GraphicsSettings.option(names);
        if (option == null) {
            return false;
        }
        return GraphicsSettings.set(option, Boolean.valueOf(value));
    }

    /** Ставит вариант перечисления по имени константы (например, «DECREASED» или «MINIMAL»). */
    public static boolean setEnum(String constant, String... names) {
        Object option = GraphicsSettings.option(names);
        if (option == null) {
            return false;
        }
        Object current = GraphicsSettings.call(option, "getValue");
        if (current == null) {
            return false;
        }
        Class<?> type = current.getClass();
        if (!type.isEnum()) {
            return false;
        }
        String needle = constant.toUpperCase(Locale.ROOT);
        Object[] constants = type.getEnumConstants();
        if (constants == null) {
            return false;
        }
        for (Object candidate : constants) {
            String name = ((Enum<?>)candidate).name().toUpperCase(Locale.ROOT);
            if (name.equals(needle) || name.startsWith(needle)) {
                return GraphicsSettings.set(option, candidate);
            }
        }
        return false;
    }

    /** Устанавливает значение настройки. */
    private static boolean set(Object option, Object value) {
        return GraphicsSettings.invoke(option, "setValue", value);
    }

    /** Сохраняет настройки в файл игры. */
    public static void save() {
        Object options = GraphicsSettings.gameOptions();
        if (options == null) {
            return;
        }
        GraphicsSettings.call(options, "write");
    }

    /** Текст о доступности настройки: для подписи в интерфейсе. */
    public static String describe(String... names) {
        Object option = GraphicsSettings.option(names);
        if (option == null) {
            return "нет в этой версии";
        }
        return GraphicsSettings.current(names);
    }

    private static Object field(Object target, String name) {
        if (target == null) {
            return null;
        }
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(target);
            }
            catch (Throwable ignored) {
                type = type.getSuperclass();
            }
        }
        return null;
    }

    /** Вызывает метод и сообщает, получилось ли это. */
    private static boolean invoke(Object target, String name, Object... arguments) {
        if (target == null) {
            return false;
        }
        Class<?> type = target.getClass();
        while (type != null) {
            for (Method method : type.getDeclaredMethods()) {
                if (!method.getName().equals(name) || method.getParameterCount() != arguments.length) {
                    continue;
                }
                try {
                    method.setAccessible(true);
                    method.invoke(target, arguments);
                    return true;
                }
                catch (Throwable ignored) {
                    // пробуем следующий вариант
                }
            }
            type = type.getSuperclass();
        }
        return false;
    }

    private static Object call(Object target, String name, Object... arguments) {
        if (target == null) {
            return null;
        }
        Class<?> type = target.getClass();
        while (type != null) {
            for (Method method : type.getDeclaredMethods()) {
                if (!method.getName().equals(name) || method.getParameterCount() != arguments.length) {
                    continue;
                }
                try {
                    method.setAccessible(true);
                    return method.invoke(target, arguments);
                }
                catch (Throwable throwable) {
                    // пробуем следующий вариант
                }
            }
            type = type.getSuperclass();
        }
        ByAzen.LOGGER.debug("[ByAzen] Настройка графики {} недоступна", name);
        return null;
    }
}
