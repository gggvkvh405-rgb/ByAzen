package rtx.byazen.utils.perf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Профайлер рендера по модулям (идея №163 из IDEAS.md).
 * <p>
 * Клиент замеряет, сколько времени каждый модуль тратит внутри своих обработчиков событий (это и есть
 * отрисовка HUD, визуалы мира, оверлеи). Раз в несколько секунд окно сбрасывается, поэтому в отчёте
 * видно не общий счётчик, а «сколько сейчас»: время на вызов и долю от всей работы модулей. Модули,
 * которые едят кадр, можно выключать автоматически — с уведомлением в чат.
 */
public final class ModuleProfiler {

    /** Строка отчёта по одному модулю. */
    public static final class Row {

        public final String name;
        public final long calls;
        public final double microsPerCall;
        public final double share;

        Row(String name, long calls, double microsPerCall, double share) {
            this.name = name;
            this.calls = calls;
            this.microsPerCall = microsPerCall;
            this.share = share;
        }

        public String callsText() {
            return String.format(java.util.Locale.ROOT, "%d", this.calls);
        }

        public String microsText() {
            return String.format(java.util.Locale.ROOT, "%.2f мс", this.microsPerCall / 1000.0);
        }

        public String shareText() {
            return String.format(java.util.Locale.ROOT, "%.1f%%", this.share * 100.0);
        }
    }

    private static final class Counter {

        long calls;
        long nanos;
    }

    private static final Map<Object, Counter> WINDOW = new ConcurrentHashMap<Object, Counter>();
    private static final Map<Object, Counter> TOTAL = new ConcurrentHashMap<Object, Counter>();
    private static volatile boolean enabled;
    private static volatile long windowNanos;
    private static volatile long windowStart = System.currentTimeMillis();
    private static volatile int windowFrames;

    private ModuleProfiler() {
    }

    public static void setEnabled(boolean value) {
        ModuleProfiler.enabled = value;
        if (!value) {
            ModuleProfiler.reset();
        }
    }

    public static boolean enabled() {
        return ModuleProfiler.enabled;
    }

    /** Замер одного вызова обработчика. Вызывается из шины событий, поэтому максимально дешёвый. */
    public static void record(Object owner, long nanos) {
        if (!ModuleProfiler.enabled || owner == null) {
            return;
        }
        ModuleProfiler.add(WINDOW, owner, nanos);
        ModuleProfiler.add(TOTAL, owner, nanos);
        ModuleProfiler.windowNanos += nanos;
    }

    private static void add(Map<Object, Counter> map, Object owner, long nanos) {
        Counter counter = map.get(owner);
        if (counter == null) {
            counter = new Counter();
            Counter existing = map.putIfAbsent(owner, counter);
            if (existing != null) {
                counter = existing;
            }
        }
        synchronized (counter) {
            ++counter.calls;
            counter.nanos += nanos;
        }
    }

    /** Отмечает кадр: по кадрам считается доля модулей в кадре. */
    public static void frame() {
        if (ModuleProfiler.enabled) {
            ++ModuleProfiler.windowFrames;
        }
    }

    /**
     * Закрывает окно замера. Вызывается раз в пару секунд: данные переезжают в отчёт, а счётчики
     * окна начинают считать заново — так в отчёте видно текущую нагрузку, а не историю за час.
     */
    public static void rollWindow() {
        ModuleProfiler.windowStart = System.currentTimeMillis();
        ModuleProfiler.windowNanos = 0L;
        ModuleProfiler.WINDOW.clear();
        ModuleProfiler.windowFrames = 0;
    }

    public static int frames() {
        return ModuleProfiler.windowFrames;
    }

    public static long windowAgeMs() {
        return System.currentTimeMillis() - ModuleProfiler.windowStart;
    }

    /** Отчёт: модули по убыванию съеденного времени. */
    public static List<ModuleProfiler.Row> snapshot() {
        ArrayList<ModuleProfiler.Row> rows = new ArrayList<ModuleProfiler.Row>();
        long total = Math.max(1L, ModuleProfiler.windowNanos);
        for (Map.Entry<Object, Counter> pair : WINDOW.entrySet()) {
            long calls;
            long nanos;
            synchronized (pair.getValue()) {
                calls = pair.getValue().calls;
                nanos = pair.getValue().nanos;
            }
            if (calls <= 0L) {
                continue;
            }
            rows.add(new ModuleProfiler.Row(ModuleProfiler.name(pair.getKey()), calls,
                    (double)nanos / (double)calls, (double)nanos / (double)total));
        }
        Collections.sort(rows, Comparator.comparingDouble(row -> -row.share));
        return rows;
    }

    /** Модули, которые явно объедают кадр: время на вызов выше порога и вызовов достаточно. */
    public static List<ModuleProfiler.Row> heavy(double thresholdMicros, long minCalls) {
        ArrayList<ModuleProfiler.Row> heavy = new ArrayList<ModuleProfiler.Row>();
        for (ModuleProfiler.Row row : ModuleProfiler.snapshot()) {
            if (row.microsPerCall >= thresholdMicros && row.calls >= minCalls) {
                heavy.add(row);
            }
        }
        return heavy;
    }

    /** Общее время модулей в окне, мс. */
    public static double windowMillis() {
        return (double)ModuleProfiler.windowNanos / 1.0E6;
    }

    public static void reset() {
        ModuleProfiler.WINDOW.clear();
        ModuleProfiler.windowNanos = 0L;
        ModuleProfiler.windowFrames = 0;
        ModuleProfiler.windowStart = System.currentTimeMillis();
    }

    private static String name(Object owner) {
        if (owner instanceof rtx.byazen.api.modules.Module) {
            return ((rtx.byazen.api.modules.Module)owner).getName();
        }
        String full = owner.getClass().getName();
        int separator = full.lastIndexOf('.');
        return separator >= 0 ? full.substring(separator + 1) : full;
    }

    /** Сколько модулей уже попало в замер за текущее окно. */
    public static int measured() {
        return ModuleProfiler.WINDOW.size();
    }

    /** Полный список модулей, попавших в замер, по алфавиту — для отчёта. */
    public static List<String> measuredNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (Object owner : TOTAL.keySet()) {
            names.add(ModuleProfiler.name(owner));
        }
        Collections.sort(names);
        return names;
    }

    /** Живая статистика окна для экрана «почему лагает». */
    public static Map<String, Double> shareMap() {
        LinkedHashMap<String, Double> shares = new LinkedHashMap<String, Double>();
        for (ModuleProfiler.Row row : ModuleProfiler.snapshot()) {
            shares.put(row.name, row.share);
        }
        return shares;
    }
}
