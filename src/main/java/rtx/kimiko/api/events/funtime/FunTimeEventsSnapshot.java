package rtx.kimiko.api.events.funtime;
import java.util.List;
import rtx.kimiko.api.events.funtime.FunTimeEvent;

public record FunTimeEventsSnapshot(List<FunTimeEvent> current, List<FunTimeEvent> nearest, long generatedAt, boolean online, String error) {
    public static FunTimeEventsSnapshot offline(String string) {
        return new FunTimeEventsSnapshot(List.of(), List.of(), 0L, false, string);
    }
}

