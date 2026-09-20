package rtx.kimiko.api.events.funtime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import rtx.kimiko.api.events.funtime.FunTimeApi;
import rtx.kimiko.api.events.funtime.FunTimeApiException;
import rtx.kimiko.api.events.funtime.FunTimeEvent;
import rtx.kimiko.api.events.funtime.FunTimeEventsSnapshot;
import rtx.kimiko.api.events.funtime.FunTimeMine;
import rtx.kimiko.api.events.funtime.FunTimeMinesSnapshot;

public final class FunTimeEventsClient {
    public static final FunTimeEventsClient INSTANCE = new FunTimeEventsClient();
    private static final int POLL_SECONDS = 12;
    private final AtomicReference<FunTimeEventsSnapshot> events = new AtomicReference<FunTimeEventsSnapshot>(FunTimeEventsSnapshot.offline("\u041d\u0435\u0442 \u0441\u0432\u044f\u0437\u0438 \u0441 API HolyWorld"));
    private final AtomicReference<FunTimeMinesSnapshot> mines = new AtomicReference<FunTimeMinesSnapshot>(FunTimeMinesSnapshot.offline("\u041d\u0435\u0442 \u0441\u0432\u044f\u0437\u0438 \u0441 API HolyWorld"));
    private ScheduledExecutorService executor;

    private FunTimeEventsClient() {
    }

    public synchronized void shutdown() {
        if (this.executor == null) {
            return;
        }
        this.executor.shutdownNow();
        this.executor = null;
    }

    public synchronized void start() {
        if (this.executor != null) {
            return;
        }
        this.executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "kimiko-holyworld-events");
            thread.setDaemon(true);
            return thread;
        });
        this.executor.scheduleWithFixedDelay(this::refresh, 0L, 12L, TimeUnit.SECONDS);
    }

    public FunTimeEventsSnapshot snapshot() {
        return this.events.get();
    }

    private void refresh() {
        try {
            long l = System.currentTimeMillis();
            List<FunTimeEvent> list = FunTimeApi.INSTANCE.events();
            list.sort(Comparator.comparingInt((FunTimeEvent funTimeEvent) -> -FunTimeEventsClient.rarityWeight(funTimeEvent.rarity())).thenComparingInt(FunTimeEvent::anarchy));
            this.events.set(new FunTimeEventsSnapshot(List.copyOf(list), List.of(), l, true, ""));
            List<FunTimeMine> list2 = FunTimeApi.INSTANCE.mines().stream().filter(funTimeMine -> FunTimeEventsClient.isNotableRarity(funTimeMine.rarity())).toList();
            this.mines.set(new FunTimeMinesSnapshot(list2, l, true, ""));
        }
        catch (FunTimeApiException funTimeApiException) {
            this.setOffline(funTimeApiException.getMessage());
        }
        catch (Exception exception) {
            this.setOffline(exception.getClass().getSimpleName());
        }
    }

    public FunTimeMinesSnapshot minesSnapshot() {
        return this.mines.get();
    }

    private static boolean isNotableRarity(String string) {
        if (string == null) {
            return false;
        }
        String string2 = string.toLowerCase(Locale.ROOT);
        return string2.contains("\u043b\u0435\u0433\u0435\u043d\u0434\u0430\u0440\u043d") || string2.contains("\u043c\u0438\u0444\u0438\u0447\u0435\u0441\u043a") || string2.contains("\u044d\u043f\u0438\u0447\u0435\u0441\u043a") || string2.contains("\u0440\u0435\u0434\u043a");
    }

    public long remainingSeconds(FunTimeMine funTimeMine) {
        if (funTimeMine.refillAtMs() <= 0L) {
            return 0L;
        }
        return Math.max(0L, (funTimeMine.refillAtMs() - System.currentTimeMillis()) / 1000L);
    }

    public int remainingSeconds(FunTimeEvent funTimeEvent) {
        return 0;
    }

    private static int rarityWeight(String string) {
        if (string == null) {
            return 0;
        }
        return switch (string.toUpperCase(Locale.ROOT)) {
            case "MYTHICAL" -> 5;
            case "LEGENDARY" -> 4;
            case "EPIC" -> 3;
            case "RARE" -> 2;
            case "NORMAL" -> 1;
            default -> 0;
        };
    }

    private void setOffline(String string) {
        FunTimeEventsSnapshot funTimeEventsSnapshot = this.events.get();
        this.events.set(new FunTimeEventsSnapshot(funTimeEventsSnapshot.current(), funTimeEventsSnapshot.nearest(), funTimeEventsSnapshot.generatedAt(), false, string));
        FunTimeMinesSnapshot funTimeMinesSnapshot = this.mines.get();
        this.mines.set(new FunTimeMinesSnapshot(funTimeMinesSnapshot.mines(), funTimeMinesSnapshot.generatedAt(), false, string));
    }
}

