package rtx.kimiko.api.party;

@FunctionalInterface
public interface PartySpitSink {
    void accept(String from, double x, double y, double z, double dx, double dy, double dz, String dim);
}