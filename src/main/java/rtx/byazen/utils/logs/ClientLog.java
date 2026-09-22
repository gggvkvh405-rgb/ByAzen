package rtx.byazen.utils.logs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Клиентский лог с уровнями (идея №172 из IDEAS.md).
 * <p>
 * Внутри игры нужен короткий и понятный лог: что включили, что выключили, что не сработало. Записи
 * держатся в памяти кольцом (без роста), делятся по уровням, копируются одной кнопкой и выгружаются в
 * файл. Тяжёлая диагностика по-прежнему уходит в общий лог Forge/Fabric.
 */
public final class ClientLog {

    /** Уровень записи. */
    public enum Level {

        DEBUG("Отладка"),
        INFO("Инфо"),
        WARN("Внимание"),
        ERROR("Ошибка");

        private final String label;

        Level(String label) {
            this.label = label;
        }

        public String label() {
            return this.label;
        }
    }

    /** Одна строка лога. */
    public static final class Line {

        public final long time;
        public final Level level;
        public final String text;

        Line(long time, Level level, String text) {
            this.time = time;
            this.level = level;
            this.text = text;
        }

        public String timeText() {
            return rtx.byazen.utils.stats.StatsExport.formatTime(this.time);
        }

        public String full() {
            return "[" + this.timeText() + "] " + this.level.name() + " " + this.text;
        }
    }

    private static final int LIMIT = 500;
    private static final List<ClientLog.Line> LINES = new ArrayList<ClientLog.Line>();

    private ClientLog() {
    }

    public static void debug(String text) {
        ClientLog.add(ClientLog.Level.DEBUG, text);
    }

    public static void info(String text) {
        ClientLog.add(ClientLog.Level.INFO, text);
    }

    public static void warn(String text) {
        ClientLog.add(ClientLog.Level.WARN, text);
    }

    public static void error(String text) {
        ClientLog.add(ClientLog.Level.ERROR, text);
    }

    public static synchronized void add(ClientLog.Level level, String text) {
        LINES.add(new ClientLog.Line(System.currentTimeMillis(), level, text == null ? "" : text));
        if (LINES.size() > LIMIT) {
            LINES.subList(0, LINES.size() - LIMIT).clear();
        }
    }

    public static synchronized List<ClientLog.Line> lines() {
        ArrayList<ClientLog.Line> copy = new ArrayList<ClientLog.Line>(LINES);
        Collections.reverse(copy);
        return copy;
    }

    public static synchronized List<ClientLog.Line> lines(ClientLog.Level minimum) {
        ArrayList<ClientLog.Line> filtered = new ArrayList<ClientLog.Line>();
        for (ClientLog.Line line : ClientLog.lines()) {
            if (line.level.ordinal() >= minimum.ordinal()) {
                filtered.add(line);
            }
        }
        return filtered;
    }

    public static synchronized int count(ClientLog.Level level) {
        int count = 0;
        for (ClientLog.Line line : LINES) {
            if (line.level == level) {
                ++count;
            }
        }
        return count;
    }

    public static synchronized void clear() {
        LINES.clear();
    }

    /** Текст всего лога — для кнопки «скопировать». */
    public static String dump() {
        StringBuilder builder = new StringBuilder();
        for (ClientLog.Line line : ClientLog.lines()) {
            builder.append(line.full()).append('\n');
        }
        return builder.toString();
    }
}
