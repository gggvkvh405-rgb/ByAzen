package rtx.byazen.api.events.funtime;
import java.util.List;
import rtx.byazen.api.events.funtime.FunTimeMine;

public record FunTimeMinesSnapshot(List<FunTimeMine> mines, long generatedAt, boolean online, String error) {
    public static FunTimeMinesSnapshot offline(String string) {
        return new FunTimeMinesSnapshot(List.of(), 0L, false, string);
    }
}

