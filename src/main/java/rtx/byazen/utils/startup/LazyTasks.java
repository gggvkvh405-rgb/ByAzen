package rtx.byazen.utils.startup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;

/**
 * Ленивая загрузка тяжёлых модулей (идея №165 из IDEAS.md).
 * <p>
 * Часть клиента не нужна в первые секунды: косые плащи, просмотр шалкеров, анимации чата и головы в
 * чате работают только внутри мира. Их подготовка откладывается до входа в мир (или до истечения
 * таймера), а запуск игры становится быстрее. Отложенные задачи можно выполнить и вручную — кнопкой
 * в настройках или из окна «Диагностика».
 */
public final class LazyTasks {

    /** Отложенная задача. */
    public static final class Task {

        public final String name;
        final Runnable runnable;
        boolean done;
        long tookMs;

        Task(String name, Runnable runnable) {
            this.name = name;
            this.runnable = runnable;
        }

        public boolean done() {
            return this.done;
        }

        public long tookMs() {
            return this.tookMs;
        }
    }

    private static final Map<String, LazyTasks.Task> TASKS = new LinkedHashMap<String, LazyTasks.Task>();
    private static final long TIMEOUT_MS = 10000L;
    private static final long startedAt = System.currentTimeMillis();
    private static boolean deferring = true;
    private static boolean autoRun = true;

    private LazyTasks() {
    }

    /** Ставить ли тяжесть в очередь (настройка модуля «Fast Start»). */
    public static void setDeferring(boolean value) {
        LazyTasks.deferring = value;
        if (!value) {
            LazyTasks.runAll();
        }
    }

    public static boolean deferring() {
        return LazyTasks.deferring;
    }

    public static void setAutoRun(boolean value) {
        LazyTasks.autoRun = value;
    }

    /** Добавляет задачу: если откладывать нечего — выполняет сразу. */
    public static void submit(String name, Runnable runnable) {
        if (runnable == null) {
            return;
        }
        LazyTasks.Task task = new LazyTasks.Task(name, runnable);
        TASKS.put(name, task);
        if (!LazyTasks.deferring) {
            LazyTasks.run(task);
        }
    }

    /** Выполняет одну задачу по имени. Возвращает true, если она реально выполнилась сейчас. */
    public static boolean run(String name) {
        LazyTasks.Task task = TASKS.get(name);
        return task != null && LazyTasks.run(task);
    }

    private static boolean run(LazyTasks.Task task) {
        if (task.done) {
            return false;
        }
        long begin = System.nanoTime();
        try {
            task.runnable.run();
        }
        catch (Throwable throwable) {
            rtx.byazen.utils.logs.ClientLog.warn("отложенная задача «" + task.name + "» не выполнилась: " + throwable);
        }
        task.tookMs = (System.nanoTime() - begin) / 1000000L;
        task.done = true;
        return true;
    }

    /** Выполняет всё, что накопилось. Возвращает число выполненных задач и их время в мс. */
    public static long[] runAll() {
        int done = 0;
        long millis = 0L;
        for (LazyTasks.Task task : TASKS.values()) {
            if (LazyTasks.run(task)) {
                ++done;
                millis += task.tookMs;
            }
        }
        return new long[]{done, millis};
    }

    /** Тик: задачи дожидаются входа в мир или таймаута, чтобы клиент не «недогрузился» навсегда. */
    public static void tick(MinecraftClient client) {
        if (!LazyTasks.autoRun || TASKS.isEmpty()) {
            return;
        }
        boolean inWorld = client != null && client.world != null && client.player != null;
        if (inWorld) {
            LazyTasks.runAll();
            return;
        }
        if (LazyTasks.autoRun && System.currentTimeMillis() - startedAt > TIMEOUT_MS) {
            LazyTasks.runAll();
        }
    }

    public static List<LazyTasks.Task> tasks() {
        return new ArrayList<LazyTasks.Task>(TASKS.values());
    }

    public static int pending() {
        int count = 0;
        for (LazyTasks.Task task : TASKS.values()) {
            if (!task.done) {
                ++count;
            }
        }
        return count;
    }

    public static long totalMillis() {
        long millis = 0L;
        for (LazyTasks.Task task : TASKS.values()) {
            millis += task.tookMs;
        }
        return millis;
    }

    /** Итог для чата: что отложено и что сэкономлено на старте. */
    public static String report() {
        int pending = LazyTasks.pending();
        if (TASKS.isEmpty()) {
            return "отложенных задач нет";
        }
        return "задач: " + TASKS.size() + " · ждут своего часа: " + pending
                + String.format(java.util.Locale.ROOT, " · их работа заняла бы %.0f мс", (double)LazyTasks.totalMillis());
    }
}
