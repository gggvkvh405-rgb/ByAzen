package rtx.byazen.utils.config;

import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import rtx.byazen.api.modules.impl.Interface.InterfaceModule;
import rtx.byazen.api.ui.theme.ThemeManager;

/**
 * Шаринг темы коротким кодом (идея №154 из IDEAS.md).
 * <p>
 * «Вставь код — получи мою тему»: цвета, второй цвет, радуга и режим подбираются в один клик, а
 * сам код короткий и помещается в чат. Внутри — та же упаковка, что у переноса конфигов, поэтому
 * код можно хранить и пересылать без искажений.
 */
public final class ThemeCodes {

    private static final String PREFIX = "BZTHM1:";

    private ThemeCodes() {
    }

    /** Собирает код текущей темы. */
    public static String export() {
        try {
            InterfaceModule module = InterfaceModule.getInstance();
            JsonObject json = new JsonObject();
            json.addProperty("format", 1);
            json.addProperty("name", ThemeManager.current().name());
            if (module != null) {
                json.addProperty("mode", module.clientColorMode.getSelected());
                json.addProperty("color", module.rectColor.getColor());
                json.addProperty("second", module.rectSecondColor.getColor());
                json.addProperty("useSecond", module.rectUseSecondColor.getValue());
                json.addProperty("movement", module.rectColorMovement.getValue());
                json.addProperty("gradient", module.gradientStyle.getSelected());
            }
            return ThemeCodes.pack(json);
        }
        catch (Throwable throwable) {
            return "";
        }
    }

    /** Применяет код темы. Возвращает название темы или пустую строку при ошибке. */
    public static String apply(String code) {
        JsonObject json = ThemeCodes.unpack(code);
        if (json == null) {
            return "";
        }
        String name = "";
        try {
            if (json.has("name")) {
                name = json.get("name").getAsString();
                try {
                    ThemeManager.set(rtx.byazen.api.ui.theme.Theme.valueOf(name));
                }
                catch (Throwable ignored) {
                    // тема могла быть переименована
                }
            }
            InterfaceModule module = InterfaceModule.getInstance();
            if (module != null) {
                if (json.has("mode")) {
                    module.clientColorMode.selected(json.get("mode").getAsString());
                }
                if (json.has("color")) {
                    module.rectColor.setColor(json.get("color").getAsInt());
                }
                if (json.has("second")) {
                    module.rectSecondColor.setColor(json.get("second").getAsInt());
                }
                if (json.has("useSecond")) {
                    module.rectUseSecondColor.setValue(json.get("useSecond").getAsBoolean());
                }
                if (json.has("movement")) {
                    module.rectColorMovement.setValue(json.get("movement").getAsBoolean());
                }
                if (json.has("gradient")) {
                    module.gradientStyle.selected(json.get("gradient").getAsString());
                }
            }
            rtx.byazen.api.config.ConfigManager.markDirty();
        }
        catch (Throwable throwable) {
            return "";
        }
        return name;
    }

    /** Применяет только два цвета — для палитр из обложек трека (идея №155). */
    public static void applyColors(int primary, int secondary) {
        try {
            InterfaceModule module = InterfaceModule.getInstance();
            if (module == null) {
                return;
            }
            module.clientColorMode.selected(InterfaceModule.CLIENT_COLOR_CUSTOM);
            module.rectColor.setColor(primary & 0xFFFFFF);
            module.rectSecondColor.setColor(secondary & 0xFFFFFF);
            module.rectUseSecondColor.setValue(true);
            rtx.byazen.api.config.ConfigManager.markDirty();
        }
        catch (Throwable ignored) {
            // модуль интерфейса ещё не создан
        }
    }

    public static boolean looksLikeCode(String text) {
        return text != null && text.trim().startsWith(PREFIX);
    }

    private static String pack(JsonObject json) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
                gzip.write(json.toString().getBytes(StandardCharsets.UTF_8));
            }
            return PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(out.toByteArray());
        }
        catch (Throwable throwable) {
            return "";
        }
    }

    private static JsonObject unpack(String code) {
        if (!ThemeCodes.looksLikeCode(code)) {
            return null;
        }
        try {
            String body = code.trim().substring(PREFIX.length());
            byte[] bytes = Base64.getUrlDecoder().decode(body);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (GZIPInputStream gzip = new GZIPInputStream(in)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = gzip.read(buffer)) > 0) {
                    out.write(buffer, 0, read);
                }
            }
            JsonObject json = com.google.gson.JsonParser.parseString(out.toString(StandardCharsets.UTF_8)).getAsJsonObject();
            return json.has("format") ? json : null;
        }
        catch (Throwable throwable) {
            return null;
        }
    }
}
