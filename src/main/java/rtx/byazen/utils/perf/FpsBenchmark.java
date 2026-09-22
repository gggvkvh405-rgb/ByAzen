package rtx.byazen.utils.perf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Бенчмарк-режим (идея №174 из IDEAS.md): прогон сцены с замером FPS до и после настроек.
 * <p>
 * Запускается кнопкой: клиент собирает кадры несколько секунд, считает средний FPS, «1 % худших»
 * кадров и минимум, а потом сравнивает с прошлым замером — видно, помогли ли настройки.
 */
public final class FpsBenchmark {

    private static final String FILE = "fps_benchmark";
    private static final int MAX_SAMPLES = 4000;

    private static final List<Float> SAMPLES = new ArrayList<Float>();
    private static boolean running;
    private static long startedAt;
    private static int durationSeconds = 10;
    private static Result last;
    private static Result previous;
    private static boolean loaded;

    private FpsBenchmark() {
    }

    /** Итог замера. */
    public static final class Result {

        public final float average;
        public final float low;      // 1 % худших кадров
        public final float min;
        public final float max;
        public final int samples;
        public final long time;

        Result(float average, float low, float min, float max, int samples, long time) {
            this.average = average;
            this.low = low;
            this.min = min;
            this.max = max;
            this.samples = samples;
            this.time = time;
        }

        public String text() {
            return String.format(Locale.ROOT, "средний %.0f, 1%% худших %.0f, минимум %.0f, максимум %.0f FPS (кадров %d)",
                    this.average, this.low, this.min, this.max, this.samples);
        }
    }

    public static void start(int seconds) {
        FpsBenchmark.load();
        durationSeconds = Math.max(3, Math.min(60, seconds));
        SAMPLES.clear();
        startedAt = System.currentTimeMillis();
        running = true;
        ChatMessage.send("§bByAzen: замер FPS начат — " + durationSeconds + " с, постарайтесь не менять настройки");
    }

    public static void stop() {
        if (!running) {
            return;
        }
        running = false;
        Result result = FpsBenchmark.build();
        previous = last;
        last = result;
        FpsBenchmark.save();
        ChatMessage.send("§bByAzen: замер окончен — " + result.text());
        ChatMessage.send(FpsBenchmark.compareText());
        ClientLog.info("бенчмарк FPS: " + result.text());
    }

    public static boolean running() {
        return running;
    }

    public static int remainingSeconds() {
        if (!running) {
            return 0;
        }
        long elapsed = (System.currentTimeMillis() - startedAt) / 1000L;
        return (int)Math.max(0L, (long)durationSeconds - elapsed);
    }

    /** Вызывается каждый кадр HUD, пока идёт замер. */
    public static void sample(MinecraftClient client) {
        if (!running || client == null) {
            return;
        }
        if (SAMPLES.size() < MAX_SAMPLES) {
            SAMPLES.add(Float.valueOf(client.getCurrentFps()));
        }
        if (System.currentTimeMillis() - startedAt >= (long)durationSeconds * 1000L) {
            FpsBenchmark.stop();
        }
    }

    private static Result build() {
        if (SAMPLES.isEmpty()) {
            return new Result(0.0f, 0.0f, 0.0f, 0.0f, 0, System.currentTimeMillis());
        }
        ArrayList<Float> sorted = new ArrayList<Float>(SAMPLES);
        Collections.sort(sorted);
        float sum = 0.0f;
        for (Float value : SAMPLES) {
            sum += value.floatValue();
        }
        float average = sum / (float)SAMPLES.size();
        int lowIndex = Math.max(0, (int)((float)sorted.size() * 0.01f));
        float low = 0.0f;
        int lowCount = Math.max(1, lowIndex);
        for (int i = 0; i < lowCount && i < sorted.size(); ++i) {
            low += sorted.get(i).floatValue();
        }
        low /= (float)lowCount;
        return new Result(average, low, sorted.getFirst().floatValue(), sorted.getLast().floatValue(),
                SAMPLES.size(), System.currentTimeMillis());
    }

    public static Result last() {
        FpsBenchmark.load();
        return last;
    }

    public static Result previous() {
        FpsBenchmark.load();
        return previous;
    }

    /** Текст сравнения с прошлым замером: понятно, стало лучше или хуже. */
    public static String compareText() {
        FpsBenchmark.load();
        if (last == null || previous == null) {
            return "прошлого замера нет — сделайте второй и сравните";
        }
        float delta = last.average - previous.average;
        float percent = previous.average <= 0.0f ? 0.0f : delta / previous.average * 100.0f;
        float lowDelta = last.low - previous.low;
        String verdict = delta >= 0.0f ? "лучше" : "хуже";
        return String.format(Locale.ROOT, "было %.0f → стало %.0f FPS (%+.1f%%, %s), 1%% худших %+.0f",
                previous.average, last.average, percent, verdict, lowDelta);
    }

    public static String summary() {
        FpsBenchmark.load();
        if (running) {
            return "идёт замер, осталось " + FpsBenchmark.remainingSeconds() + " с";
        }
        if (last == null) {
            return "замеров ещё не было";
        }
        return last.text();
    }

    public static List<String> report() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Бенчмарк FPS: " + FpsBenchmark.summary());
        list.add("Сравнение: " + FpsBenchmark.compareText());
        if (last != null) {
            list.add(String.format(Locale.ROOT, "Последний: средний %.1f, 1%% худших %.1f, минимум %.1f, максимум %.1f",
                    last.average, last.low, last.min, last.max));
        }
        return list;
    }

    /** Сбрасывает историю замеров, чтобы сравнивать «с чистого листа». */
    public static void clear() {
        last = null;
        previous = null;
        SAMPLES.clear();
        RepositoryStorage.write(FILE, new com.google.gson.JsonObject());
        ChatMessage.send("§bByAzen: замеры FPS сброшены");
    }

    private static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        try {
            com.google.gson.JsonObject root = RepositoryStorage.readObject(FILE);
            last = FpsBenchmark.read(root, "last");
            previous = FpsBenchmark.read(root, "previous");
        }
        catch (Throwable throwable) {
            last = null;
            previous = null;
        }
    }

    private static Result read(com.google.gson.JsonObject root, String key) {
        if (root == null || !root.has(key) || !root.get(key).isJsonObject()) {
            return null;
        }
        com.google.gson.JsonObject object = root.getAsJsonObject(key);
        if (!object.has("average")) {
            return null;
        }
        return new Result(object.get("average").getAsFloat(), object.has("low") ? object.get("low").getAsFloat() : 0.0f,
                object.has("min") ? object.get("min").getAsFloat() : 0.0f,
                object.has("max") ? object.get("max").getAsFloat() : 0.0f,
                object.has("samples") ? object.get("samples").getAsInt() : 0,
                object.has("time") ? object.get("time").getAsLong() : System.currentTimeMillis());
    }

    private static void save() {
        com.google.gson.JsonObject root = new com.google.gson.JsonObject();
        if (previous != null) {
            root.add("previous", FpsBenchmark.write(previous));
        }
        if (last != null) {
            root.add("last", FpsBenchmark.write(last));
        }
        RepositoryStorage.write(FILE, root);
    }

    private static com.google.gson.JsonObject write(Result result) {
        com.google.gson.JsonObject object = new com.google.gson.JsonObject();
        object.addProperty("average", (Number)Float.valueOf(result.average));
        object.addProperty("low", (Number)Float.valueOf(result.low));
        object.addProperty("min", (Number)Float.valueOf(result.min));
        object.addProperty("max", (Number)Float.valueOf(result.max));
        object.addProperty("samples", (Number)result.samples);
        object.addProperty("time", (Number)result.time);
        return object;
    }
}
