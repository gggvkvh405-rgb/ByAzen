package rtx.byazen.api.nav;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.ByAzen;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Хранилище путевых точек (идея №72 из IDEAS.md).
 * <p>
 * Точки лежат рядом с конфигом клиента (`waypoints`), их можно добавлять из чата или с клавиши,
 * а показываются они на мини-карте и в HUD-виджете с расстоянием и координатами.
 */
public final class WaypointStore {

    private static final String FILE = "waypoints";
    private static final String ARRAY = "ways";
    private static final WaypointStore INSTANCE = new WaypointStore();

    private final List<Waypoint> points = new ArrayList<Waypoint>();
    private String active = "";
    private boolean loaded;

    private WaypointStore() {
    }

    public static WaypointStore get() {
        return INSTANCE;
    }

    public List<Waypoint> all() {
        this.ensureLoaded();
        return Collections.unmodifiableList(this.points);
    }

    public int size() {
        this.ensureLoaded();
        return this.points.size();
    }

    public String activeName() {
        return this.active;
    }

    public Waypoint active() {
        this.ensureLoaded();
        for (Waypoint point : this.points) {
            if (point.name().equalsIgnoreCase(this.active)) {
                return point;
            }
        }
        return null;
    }

    public void setActive(String name) {
        this.active = name == null ? "" : name;
        this.save();
    }

    public Waypoint find(String name) {
        this.ensureLoaded();
        if (name == null) {
            return null;
        }
        for (Waypoint point : this.points) {
            if (point.name().equalsIgnoreCase(name.trim())) {
                return point;
            }
        }
        return null;
    }

    /** Добавляет точку или обновляет существующую с тем же именем. */
    public Waypoint put(Waypoint waypoint) {
        this.ensureLoaded();
        this.points.removeIf(existing -> existing.name().equalsIgnoreCase(waypoint.name()));
        this.points.add(waypoint);
        this.save();
        return waypoint;
    }

    public boolean remove(String name) {
        this.ensureLoaded();
        boolean removed = this.points.removeIf(point -> point.name().equalsIgnoreCase(name));
        if (removed) {
            if (this.active.equalsIgnoreCase(name)) {
                this.active = "";
            }
            this.save();
        }
        return removed;
    }

    public void clear() {
        this.points.clear();
        this.active = "";
        this.save();
    }

    /** Точки текущего измерения, ближайшие первыми. */
    public List<Waypoint> nearby(int limit) {
        this.ensureLoaded();
        Vec3d here = WaypointStore.playerPos();
        String dimension = WaypointStore.dimensionId();
        List<Waypoint> result = new ArrayList<Waypoint>();
        for (Waypoint point : this.points) {
            if (dimension != null && !point.dimension().equals(dimension)) {
                continue;
            }
            result.add(point);
        }
        result.sort((first, second) -> Double.compare(first.distanceTo(here), second.distanceTo(here)));
        if (limit > 0 && result.size() > limit) {
            return new ArrayList<Waypoint>(result.subList(0, limit));
        }
        return result;
    }

    public List<Waypoint> nearest(int limit) {
        this.ensureLoaded();
        Vec3d here = WaypointStore.playerPos();
        List<Waypoint> result = new ArrayList<Waypoint>(this.points);
        result.sort((first, second) -> Double.compare(first.distanceTo(here), second.distanceTo(here)));
        if (limit > 0 && result.size() > limit) {
            return new ArrayList<Waypoint>(result.subList(0, limit));
        }
        return result;
    }

    private void ensureLoaded() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        this.load();
    }

    private void load() {
        try {
            JsonObject root = RepositoryStorage.readObject(FILE);
            this.points.clear();
            this.active = WaypointStore.string(root, "active", "");
            JsonArray array = root.getAsJsonArray(ARRAY);
            if (array == null) {
                return;
            }
            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject object = element.getAsJsonObject();
                String name = WaypointStore.string(object, "name", "");
                if (name.isBlank()) {
                    continue;
                }
                this.points.add(new Waypoint(
                        name,
                        WaypointStore.number(object, "x", 0.0),
                        WaypointStore.number(object, "y", 0.0),
                        WaypointStore.number(object, "z", 0.0),
                        WaypointStore.string(object, "dimension", "minecraft:overworld"),
                        WaypointStore.integer(object, "color", 0xFF5AA9FF),
                        WaypointStore.string(object, "note", "")));
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Путевые точки не загрузились: {}", throwable.toString());
        }
    }

    public void save() {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("active", this.active);
            JsonArray array = new JsonArray();
            for (Waypoint point : this.points) {
                JsonObject object = new JsonObject();
                object.addProperty("name", point.name());
                object.addProperty("x", point.x());
                object.addProperty("y", point.y());
                object.addProperty("z", point.z());
                object.addProperty("dimension", point.dimension());
                object.addProperty("color", point.color());
                object.addProperty("note", point.note());
                array.add(object);
            }
            root.add(ARRAY, array);
            RepositoryStorage.write(FILE, root);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Путевые точки не сохранились: {}", throwable.toString());
        }
    }

    public static Vec3d playerPos() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client != null && client.player != null
                ? new Vec3d(client.player.getX(), client.player.getY(), client.player.getZ()) : null;
    }

    /** Идентификатор измерения игрока, например minecraft:overworld. */
    public static String dimensionId() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) {
            return null;
        }
        return client.world.getRegistryKey().getValue().toString();
    }

    private static String string(JsonObject object, String key, String fallback) {
        return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString() : fallback;
    }

    private static double number(JsonObject object, String key, double fallback) {
        try {
            return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsDouble() : fallback;
        }
        catch (Throwable throwable) {
            return fallback;
        }
    }

    private static int integer(JsonObject object, String key, int fallback) {
        try {
            return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsInt() : fallback;
        }
        catch (Throwable throwable) {
            return fallback;
        }
    }
}
