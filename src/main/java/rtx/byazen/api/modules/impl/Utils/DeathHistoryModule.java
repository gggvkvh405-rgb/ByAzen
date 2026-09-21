package rtx.byazen.api.modules.impl.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.network.PacketEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * История смертей (идея №111 из IDEAS.md): где, когда и в каком мире умер игрок.
 * Записи хранятся в конфиге ByAzen и переживают перезапуск игры.
 */
public final class DeathHistoryModule
extends Module {

    private static final String FILE = "death_history";
    private static final int LIMIT = 60;
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd.MM HH:mm:ss", Locale.ROOT);

    public final SeparatorSetting behaviour = this.register(new SeparatorSetting("Поведение"));
    public final BooleanSetting notify = this.register(new BooleanSetting("Уведомление", "Показывать тост с координатами смерти.", true));
    public final BooleanSetting chat = this.register(new BooleanSetting("Писать в чат", "Дополнительно выводить координаты в чат (клик — копировать).", false));
    public final ButtonSetting clear = this.register(new ButtonSetting("Очистить историю", "Удалить все сохранённые смерти.").label("Очистить").onClick(this::clearHistory));

    private final List<DeathHistoryModule.Entry> entries = new ArrayList<DeathHistoryModule.Entry>();
    private boolean loaded;

    public DeathHistoryModule() {
        super("Death History", "История смертей: координаты, мир и время — с сохранением в конфиг.", Category.UTILS);
    }

    /** Последние смерти (новые первыми). */
    public List<DeathHistoryModule.Entry> entries() {
        this.ensureLoaded();
        return new ArrayList<DeathHistoryModule.Entry>(this.entries);
    }

    public void clearHistory() {
        this.ensureLoaded();
        this.entries.clear();
        this.save();
    }

    @EventHandler
    public void onPacket(PacketEvent packetEvent) {
        if (!packetEvent.isReceive() || this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (!(packetEvent.getPacket() instanceof DeathMessageS2CPacket)) {
            return;
        }
        this.ensureLoaded();
        double x = this.mc.player.getX();
        double y = this.mc.player.getY();
        double z = this.mc.player.getZ();
        String world = this.mc.world.getRegistryKey().getValue().getPath();
        String coords = Math.round(x) + " " + Math.round(y) + " " + Math.round(z);
        DeathHistoryModule.Entry entry = new DeathHistoryModule.Entry(Math.round(x), Math.round(y), Math.round(z), world, System.currentTimeMillis());
        this.entries.add(0, entry);
        while (this.entries.size() > LIMIT) {
            this.entries.remove(this.entries.size() - 1);
        }
        this.save();

        String label = coords + " (" + world + ")";
        if (this.notify.getValue()) {
            NotificationsModule.notify("Смерть: " + label, 3500L);
        }
        if (this.chat.getValue()) {
            MutableText message = Text.literal("[ByAzen] Смерть " + label + " — " + DeathHistoryModule.TIME_FORMAT.format(new Date(entry.time)))
                    .styled(style -> style.withClickEvent(new ClickEvent.CopyToClipboard(coords)));
            this.mc.player.sendMessage(message, false);
        }
    }

    // ------------------------------------------------------------------ хранение

    private void ensureLoaded() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject(FILE);
            JsonArray array = root.has("deaths") && root.get("deaths").isJsonArray() ? root.getAsJsonArray("deaths") : new JsonArray();
            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject object = element.getAsJsonObject();
                this.entries.add(new DeathHistoryModule.Entry(
                        intValue(object, "x"),
                        intValue(object, "y"),
                        intValue(object, "z"),
                        string(object, "world"),
                        longValue(object, "time")));
            }
        }
        catch (Throwable throwable) {
            rtx.byazen.ByAzen.LOGGER.warn("[ByAzen] Death history could not be loaded: {}", throwable.toString());
        }
    }

    private void save() {
        try {
            JsonObject root = new JsonObject();
            JsonArray array = new JsonArray();
            for (DeathHistoryModule.Entry entry : this.entries) {
                JsonObject object = new JsonObject();
                object.addProperty("x", entry.x);
                object.addProperty("y", entry.y);
                object.addProperty("z", entry.z);
                object.addProperty("world", entry.world);
                object.addProperty("time", entry.time);
                array.add(object);
            }
            root.add("deaths", array);
            RepositoryStorage.write(FILE, root);
        }
        catch (Throwable throwable) {
            rtx.byazen.ByAzen.LOGGER.warn("[ByAzen] Death history could not be saved: {}", throwable.toString());
        }
    }

    private static int intValue(JsonObject object, String key) {
        try {
            return object.has(key) ? object.get(key).getAsInt() : 0;
        }
        catch (Throwable throwable) {
            return 0;
        }
    }

    private static long longValue(JsonObject object, String key) {
        try {
            return object.has(key) ? object.get(key).getAsLong() : 0L;
        }
        catch (Throwable throwable) {
            return 0L;
        }
    }

    private static String string(JsonObject object, String key) {
        try {
            return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString() : "";
        }
        catch (Throwable throwable) {
            return "";
        }
    }

    /** Одна запись истории смертей. */
    public static final class Entry {
        public final int x;
        public final int y;
        public final int z;
        public final String world;
        public final long time;

        public Entry(int x, int y, int z, String world, long time) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = world == null ? "" : world;
            this.time = time;
        }
    }
}
