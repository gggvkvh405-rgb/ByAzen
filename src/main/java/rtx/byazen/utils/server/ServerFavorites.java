package rtx.byazen.utils.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import rtx.byazen.ByAzen;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Избранные сервера для проверки состояния (идея №99 из IDEAS.md).
 * <p>
 * Список хранится рядом с настройками клиента: адрес, короткая пометка и время добавления. Из окна
 * проверки сервер можно добавить в один клик, а из списка — сразу подключиться.
 */
public final class ServerFavorites {

    private static final String FILE = "servers.json";

    private ServerFavorites() {
    }

    /** Одна запись списка. */
    public static final class Entry {

        public final String address;
        public String note;
        public long addedAt;

        public Entry(String address, String note, long addedAt) {
            this.address = ServerPinger.cleanAddress(address);
            this.note = note == null ? "" : note;
            this.addedAt = addedAt;
        }

        public String display() {
            return ServerPinger.prettyAddress(this.address);
        }
    }

    private static Path file() {
        return RepositoryStorage.configRoot().resolve(FILE);
    }

    /** Загружает список, отбрасывая повреждённые записи. */
    public static List<Entry> load() {
        ArrayList<Entry> list = new ArrayList<Entry>();
        Path path = ServerFavorites.file();
        if (!Files.isRegularFile(path)) {
            return list;
        }
        try {
            String text = Files.readString(path, StandardCharsets.UTF_8);
            JsonElement element = JsonParser.parseString(text);
            if (element == null || !element.isJsonObject()) {
                return list;
            }
            JsonElement servers = element.getAsJsonObject().get("servers");
            if (servers == null || !servers.isJsonArray()) {
                return list;
            }
            JsonArray array = servers.getAsJsonArray();
            for (JsonElement child : array) {
                if (child == null || !child.isJsonObject()) {
                    continue;
                }
                JsonObject object = child.getAsJsonObject();
                String address = object.has("address") ? object.get("address").getAsString() : "";
                if (address.isEmpty()) {
                    continue;
                }
                String note = object.has("note") ? object.get("note").getAsString() : "";
                long added = object.has("added") ? object.get("added").getAsLong() : 0L;
                list.add(new Entry(address, note, added));
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Избранные сервера: {}", throwable.toString());
        }
        return list;
    }

    /** Сохраняет список на диск. */
    public static void save(List<Entry> entries) {
        try {
            JsonObject root = new JsonObject();
            JsonArray array = new JsonArray();
            for (Entry entry : entries) {
                JsonObject object = new JsonObject();
                object.addProperty("address", entry.address);
                object.addProperty("note", entry.note);
                object.addProperty("added", entry.addedAt);
                array.add(object);
            }
            root.add("servers", array);
            Path path = ServerFavorites.file();
            Files.createDirectories(path.getParent());
            Files.writeString(path, root.toString(), StandardCharsets.UTF_8);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Не удалось сохранить избранные сервера: {}", throwable.toString());
        }
    }

    /** Добавляет адрес. Возвращает {@code false}, если он уже в списке. */
    public static boolean add(String address, String note) {
        String clean = ServerPinger.cleanAddress(address);
        if (clean.isEmpty()) {
            return false;
        }
        List<Entry> list = ServerFavorites.load();
        for (Entry entry : list) {
            if (entry.address.equals(clean)) {
                entry.note = note == null || note.isEmpty() ? entry.note : note;
                ServerFavorites.save(list);
                return false;
            }
        }
        list.add(new Entry(clean, note, System.currentTimeMillis()));
        ServerFavorites.save(list);
        return true;
    }

    public static boolean remove(String address) {
        String clean = ServerPinger.cleanAddress(address);
        List<Entry> list = ServerFavorites.load();
        boolean removed = list.removeIf(entry -> entry.address.equals(clean));
        if (removed) {
            ServerFavorites.save(list);
        }
        return removed;
    }

    public static boolean contains(String address) {
        String clean = ServerPinger.cleanAddress(address);
        for (Entry entry : ServerFavorites.load()) {
            if (entry.address.equals(clean)) {
                return true;
            }
        }
        return false;
    }

    /** Переставляет запись на первое место — так свежие адреса всегда сверху. */
    public static void touch(String address) {
        String clean = ServerPinger.cleanAddress(address);
        List<Entry> list = ServerFavorites.load();
        for (Entry entry : list) {
            if (entry.address.equals(clean)) {
                entry.addedAt = System.currentTimeMillis();
                ServerFavorites.save(list);
                return;
            }
        }
    }

    public static String summary() {
        int size = ServerFavorites.load().size();
        if (size == 0) {
            return "избранных серверов нет";
        }
        return "избранных серверов: " + size;
    }
}
