package rtx.byazen.utils.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Профили под сервер (идея №152 из IDEAS.md).
 * <p>
 * На каждом сервере нужен свой набор модулей и оформления: где-то важен HUD для PvP, где-то —
 * косметика и музыка. Профиль запоминает включённые модули и тему, а при входе на знакомый адрес
 * расставляет всё сам. Клавиши ведёт отдельный профиль клавиш (идея №97), здесь — модули и тема.
 */
public final class ServerProfiles {

    /** Профиль: какие модули включены и какая тема стояла. */
    public static final class Profile {

        public final Set<String> modules = new LinkedHashSet<String>();
        public String theme = "";
        public long time;
    }

    private static final String FILE = "serverprofiles";
    private static final java.util.Map<String, ServerProfiles.Profile> PROFILES =
            new java.util.LinkedHashMap<String, ServerProfiles.Profile>();
    private static boolean loaded;

    private ServerProfiles() {
    }

    public static void ensureLoaded() {
        if (!ServerProfiles.loaded) {
            ServerProfiles.load();
        }
    }

    public static synchronized void load() {
        ServerProfiles.loaded = true;
        JsonObject json = RepositoryStorage.readObject(FILE);
        if (json == null || !json.has("servers")) {
            return;
        }
        try {
            JsonObject servers = json.getAsJsonObject("servers");
            for (String address : servers.keySet()) {
                JsonObject entry = servers.getAsJsonObject(address);
                ServerProfiles.Profile profile = new ServerProfiles.Profile();
                profile.theme = entry.has("theme") ? entry.get("theme").getAsString() : "";
                profile.time = entry.has("time") ? entry.get("time").getAsLong() : 0L;
                if (entry.has("modules")) {
                    for (com.google.gson.JsonElement element : entry.getAsJsonArray("modules")) {
                        profile.modules.add(element.getAsString());
                    }
                }
                PROFILES.put(address.toLowerCase(Locale.ROOT), profile);
            }
        }
        catch (Throwable ignored) {
            // повреждённый файл просто пропускаем
        }
    }

    public static synchronized void save() {
        JsonObject json = new JsonObject();
        JsonObject servers = new JsonObject();
        for (Map.Entry<String, ServerProfiles.Profile> pair : PROFILES.entrySet()) {
            JsonObject entry = new JsonObject();
            entry.addProperty("theme", pair.getValue().theme);
            entry.addProperty("time", pair.getValue().time);
            JsonArray array = new JsonArray();
            for (String name : pair.getValue().modules) {
                array.add(name);
            }
            entry.add("modules", array);
            servers.add(pair.getKey(), entry);
        }
        json.add("servers", servers);
        RepositoryStorage.write(FILE, json);
    }

    public static ServerProfiles.Profile profileFor(String address) {
        ServerProfiles.ensureLoaded();
        if (address == null || address.isBlank()) {
            return null;
        }
        return PROFILES.get(address.toLowerCase(Locale.ROOT));
    }

    public static boolean has(String address) {
        return ServerProfiles.profileFor(address) != null;
    }

    public static int size() {
        ServerProfiles.ensureLoaded();
        return PROFILES.size();
    }

    /** Запоминает текущее состояние: включённые модули и тему. */
    public static void remember(String address, String theme) {
        if (address == null || address.isBlank()) {
            return;
        }
        ServerProfiles.ensureLoaded();
        ServerProfiles.Profile profile = new ServerProfiles.Profile();
        for (Module module : ModuleManager.get().getAll()) {
            if (module.isEnabled()) {
                profile.modules.add(module.getName());
            }
        }
        profile.theme = theme == null ? "" : theme;
        profile.time = System.currentTimeMillis();
        PROFILES.put(address.toLowerCase(Locale.ROOT), profile);
        ServerProfiles.save();
    }

    /** Расставляет модули и тему профиля. Возвращает строку-отчёт для чата. */
    public static String apply(String address) {
        ServerProfiles.Profile profile = ServerProfiles.profileFor(address);
        if (profile == null) {
            return "";
        }
        int enabled = 0;
        int disabled = 0;
        for (Module module : ModuleManager.get().getAll()) {
            boolean wanted = profile.modules.contains(module.getName());
            if (wanted == module.isEnabled()) {
                continue;
            }
            if (wanted) {
                module.enable();
                ++enabled;
            }
            else {
                module.disable();
                ++disabled;
            }
        }
        if (!profile.theme.isBlank()) {
            try {
                rtx.byazen.api.ui.theme.ThemeManager.set(
                        rtx.byazen.api.ui.theme.Theme.valueOf(profile.theme));
            }
            catch (Throwable ignored) {
                // название темы могло устареть
            }
        }
        return enabled + " вкл / " + disabled + " выкл";
    }

    public static boolean forget(String address) {
        ServerProfiles.ensureLoaded();
        if (address == null || PROFILES.remove(address.toLowerCase(Locale.ROOT)) == null) {
            return false;
        }
        ServerProfiles.save();
        return true;
    }

    public static List<String> addresses() {
        ServerProfiles.ensureLoaded();
        return new ArrayList<String>(PROFILES.keySet());
    }

    public static String summary() {
        ServerProfiles.ensureLoaded();
        if (PROFILES.isEmpty()) {
            return "профилей серверов пока нет";
        }
        return "профилей серверов: " + PROFILES.size();
    }
}
