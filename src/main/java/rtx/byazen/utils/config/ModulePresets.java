package rtx.byazen.utils.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import rtx.byazen.ByAzen;
import rtx.byazen.api.config.ConfigManager;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.Setting;

/**
 * Пресеты настроек модуля (идея №42 из IDEAS.md): набор настроек модуля можно выгрузить в короткий
 * код вида {@code BZ1.eyJ...}, отправить другу и применить одним нажатием.
 * <p>
 * Сериализация переиспользует {@link ConfigManager} - тот же формат, что и в конфиге клиента.
 */
public final class ModulePresets {

    private static final String PREFIX = "BZ1.";
    private static final String VERSION_KEY = "byazenPreset";

    private ModulePresets() {
    }

    /** Короткий код со всеми настройками модуля. */
    public static String export(Module module) {
        JsonObject settings = new JsonObject();
        for (Setting setting : module.getSettings().all()) {
            JsonElement value = ConfigManager.serializeSetting(setting);
            if (value != null) {
                settings.add(setting.getName(), value);
            }
        }
        JsonObject root = new JsonObject();
        root.addProperty(VERSION_KEY, 1);
        root.addProperty("module", module.getName());
        root.add("settings", settings);
        String json = root.toString();
        return PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    /** Применяет код к модулю. Возвращает число применённых настроек или -1 при ошибке. */
    public static int apply(Module module, String code) {
        JsonObject root = ModulePresets.decode(code);
        if (root == null) {
            return -1;
        }
        JsonObject settings = root.has("settings") && root.get("settings").isJsonObject()
                ? root.getAsJsonObject("settings")
                : null;
        if (settings == null) {
            return -1;
        }
        int applied = 0;
        for (Map.Entry<String, JsonElement> entry : settings.entrySet()) {
            Setting setting = module.getSettings().get(entry.getKey());
            if (setting == null) {
                continue;
            }
            ConfigManager.applySetting(setting, entry.getValue());
            ++applied;
        }
        ConfigManager.markDirty();
        return applied;
    }

    public static boolean looksLikeCode(String text) {
        return text != null && text.trim().startsWith(PREFIX);
    }

    private static JsonObject decode(String code) {
        if (code == null) {
            return null;
        }
        String trimmed = code.trim();
        if (trimmed.startsWith(PREFIX)) {
            trimmed = trimmed.substring(PREFIX.length());
        }
        try {
            String json = new String(Base64.getUrlDecoder().decode(trimmed), StandardCharsets.UTF_8);
            JsonElement parsed = JsonParser.parseString(json);
            return parsed.isJsonObject() ? parsed.getAsJsonObject() : null;
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Preset code could not be read: {}", throwable.toString());
            return null;
        }
    }

    /** Сбрасывает все настройки модуля к значениям по умолчанию. */
    public static int reset(Module module) {
        int count = 0;
        for (Setting setting : module.getSettings().all()) {
            ConfigManager.resetSetting(setting);
            ++count;
        }
        ConfigManager.markDirty();
        return count;
    }
}
