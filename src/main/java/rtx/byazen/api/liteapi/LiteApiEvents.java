package rtx.byazen.api.liteapi;

import com.google.gson.JsonObject;

/**
 * Последний известный ивент сервера, полученный по LiteApi (идея №37 из IDEAS.md).
 * Хранилище статическое: виджет HUD читает его из отрисовки, запись - из сетевого потока.
 */
public final class LiteApiEvents {

    private static volatile Snapshot snapshot = null;

    private LiteApiEvents() {
    }

    /** Ответ сервера: пусто, пока данных не было. */
    public record Snapshot(String name, String detail, long atMs, boolean recurring, long receivedAt) {
    }

    public static void update(JsonObject payload) {
        if (payload == null) {
            return;
        }
        LiteApiCodec.Event event = LiteApiCodec.parseEvent(payload);
        if (event == null) {
            return;
        }
        snapshot = new Snapshot(event.name(), event.detail(), event.atMs(), event.recurring(), System.currentTimeMillis());
    }

    public static Snapshot current() {
        return snapshot;
    }

    public static void clear() {
        snapshot = null;
    }

    /** Сколько секунд осталось до ивента (или -1, если время неизвестно). */
    public static long secondsLeft() {
        Snapshot snapshot = LiteApiEvents.snapshot;
        if (snapshot == null || snapshot.atMs() <= 0L) {
            return -1L;
        }
        return Math.max(0L, (snapshot.atMs() - System.currentTimeMillis()) / 1000L);
    }
}
