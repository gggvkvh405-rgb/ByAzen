package rtx.byazen.utils.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import rtx.byazen.ByAzen;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Профили клавиш под конкретный сервер (идея №97 из IDEAS.md).
 * <p>
 * Для каждого адреса хранится набор «модуль → клавиша», плюс общий профиль для незнакомых серверов.
 * Локальные адреса профилями не считаются: на одиночной игре клавиши остаются как есть.
 */
public final class ServerBindProfiles {

    private static final String FILE = "binds.json";
    private static final String DEFAULT_KEY = "servers/default";

    private ServerBindProfiles() {
    }

    private static Path file() {
        return RepositoryStorage.configRoot().resolve(FILE);
    }

    /** Все сохранённые профили: ключ — адрес сервера или «servers/default». */
    public static Map<String, Map<String, Integer>> all() {
        LinkedHashMap<String, Map<String, Integer>> result = new LinkedHashMap<String, Map<String, Integer>>();
        Path path = ServerBindProfiles.file();
        if (!Files.isRegularFile(path)) {
            return result;
        }
        try {
            JsonElement element = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
            if (element == null || !element.isJsonObject()) {
                return result;
            }
            JsonObject root = element.getAsJsonObject();
            for (var pair : root.entrySet()) {
                JsonElement value = pair.getValue();
                if (value == null || !value.isJsonObject()) {
                    continue;
                }
                LinkedHashMap<String, Integer> profile = new LinkedHashMap<String, Integer>();
                for (var bind : value.getAsJsonObject().entrySet()) {
                    JsonElement code = bind.getValue();
                    if (code == null || !code.isJsonPrimitive()) {
                        continue;
                    }
                    try {
                        profile.put(bind.getKey(), code.getAsInt());
                    }
                    catch (Throwable ignored) {
                        // значение не число — пропускаем
                    }
                }
                result.put(pair.getKey(), profile);
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Профили клавиш: {}", throwable.toString());
        }
        return result;
    }

    private static void saveAll(Map<String, Map<String, Integer>> profiles) {
        try {
            JsonObject root = new JsonObject();
            for (var pair : profiles.entrySet()) {
                JsonObject profile = new JsonObject();
                for (var bind : pair.getValue().entrySet()) {
                    profile.addProperty(bind.getKey(), bind.getValue());
                }
                root.add(pair.getKey(), profile);
            }
            Path path = ServerBindProfiles.file();
            Files.createDirectories(path.getParent());
            Files.writeString(path, root.toString(), StandardCharsets.UTF_8);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Не удалось сохранить профили клавиш: {}", throwable.toString());
        }
    }

    /** Ключ профиля для адреса. */
    public static String keyFor(String address) {
        String clean = ServerPinger.cleanAddress(address);
        return clean.isEmpty() ? "" : "servers/" + clean;
    }

    /** Профиль для адреса: сначала свой, потом общий. Пустая карта — профиля нет. */
    public static Map<String, Integer> profileFor(String address) {
        Map<String, Map<String, Integer>> profiles = ServerBindProfiles.all();
        String key = ServerBindProfiles.keyFor(address);
        if (!key.isEmpty()) {
            Map<String, Integer> own = profiles.get(key);
            if (own != null && !own.isEmpty()) {
                return own;
            }
        }
        Map<String, Integer> fallback = profiles.get(DEFAULT_KEY);
        return fallback == null ? new LinkedHashMap<String, Integer>() : fallback;
    }

    /** Есть ли у адреса собственный профиль (без учёта общего). */
    public static boolean hasOwnProfile(String address) {
        String key = ServerBindProfiles.keyFor(address);
        return !key.isEmpty() && ServerBindProfiles.all().containsKey(key);
    }

    /** Сохраняет профиль конкретного сервера. */
    public static void saveProfile(String address, Map<String, Integer> binds) {
        String key = ServerBindProfiles.keyFor(address);
        if (key.isEmpty() || binds == null) {
            return;
        }
        Map<String, Map<String, Integer>> profiles = ServerBindProfiles.all();
        profiles.put(key, new LinkedHashMap<String, Integer>(binds));
        ServerBindProfiles.saveAll(profiles);
    }

    /** Сохраняет общий профиль для незнакомых серверов. */
    public static void saveDefault(Map<String, Integer> binds) {
        if (binds == null) {
            return;
        }
        Map<String, Map<String, Integer>> profiles = ServerBindProfiles.all();
        profiles.put(DEFAULT_KEY, new LinkedHashMap<String, Integer>(binds));
        ServerBindProfiles.saveAll(profiles);
    }

    /** Забывает профиль сервера. */
    public static boolean forget(String address) {
        String key = ServerBindProfiles.keyFor(address);
        Map<String, Map<String, Integer>> profiles = ServerBindProfiles.all();
        if (key.isEmpty() || profiles.remove(key) == null) {
            return false;
        }
        ServerBindProfiles.saveAll(profiles);
        return true;
    }

    /** Сколько клавиш сохранено в профиле адреса. */
    public static int size(String address) {
        return ServerBindProfiles.profileFor(address).size();
    }

    /** Локальный ли адрес: одиночная игра и локальная сеть профилями не пользуются. */
    public static boolean isRemote(String address) {
        String host = ServerPinger.hostOf(address).toLowerCase(Locale.ROOT);
        if (host.isEmpty()) {
            return false;
        }
        if (host.equals("localhost") || host.equals("127.0.0.1") || host.equals("::1") || host.equals("0.0.0.0")) {
            return false;
        }
        if (host.startsWith("192.168.") || host.startsWith("10.") || host.startsWith("172.16.") || host.startsWith("172.17.")
                || host.startsWith("172.18.") || host.startsWith("172.19.") || host.startsWith("172.2")
                || host.startsWith("172.30.") || host.startsWith("172.31.") || host.endsWith(".local")) {
            return false;
        }
        return true;
    }

    /** Все адреса, у которых есть свой профиль. */
    public static java.util.List<String> addresses() {
        java.util.ArrayList<String> list = new java.util.ArrayList<String>();
        for (String key : ServerBindProfiles.all().keySet()) {
            if (key.startsWith("servers/") && !key.equals(DEFAULT_KEY)) {
                list.add(key.substring("servers/".length()));
            }
        }
        return list;
    }

    public static String summary() {
        int own = ServerBindProfiles.addresses().size();
        int fallback = ServerBindProfiles.all().containsKey(DEFAULT_KEY) ? ServerBindProfiles.all().get(DEFAULT_KEY).size() : 0;
        return "профилей серверов: " + own + " · общий профиль: " + (fallback == 0 ? "не задан" : fallback + " клавиш");
    }
}
