package rtx.byazen.utils.cosmetics;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Выдача косметики (идея №121 из IDEAS.md).
 * <p>
 * Один и тот же формат приходит двумя путями: пушем от LiteApi по каналу {@code liteapi:feature-control}
 * ({@code {"cosmetics":{"set":"winter","source":"ивент"}}}) и строкой-кодом «BZGS1:», которую игрок
 * может переслать другому — например, чтобы забрать свои предметы на новом устройстве. Код сжат и
 * закодирован без «опасных» символов, поэтому его можно копировать из чата.
 */
public final class CosmeticGrants {

    public static final String CODE_PREFIX = "BZGS1:";

    private CosmeticGrants() {
    }

    /** Обрабатывает пуш LiteApi: если в полезной нагрузке есть блок косметики, он применяется. */
    public static int applyFromLiteApi(JsonObject payload) {
        if (payload == null || !payload.has("cosmetics") || !payload.get("cosmetics").isJsonObject()) {
            return 0;
        }
        return CosmeticGrants.apply(payload.getAsJsonObject("cosmetics"));
    }

    /** Применяет блок выдачи: набор, список выданных предметов и отзыв. */
    public static int apply(JsonObject block) {
        if (block == null) {
            return 0;
        }
        int before = Cosmetics.ownedCount();
        Cosmetics.applyGrant(block);
        return Math.max(0, Cosmetics.ownedCount() - before);
    }

    /** Разбирает код «BZGS1:…» или обычный JSON с выдачей. */
    public static int applyCode(String code) {
        if (code == null || code.isBlank()) {
            return 0;
        }
        String trimmed = code.trim();
        if (trimmed.startsWith(CODE_PREFIX)) {
            JsonObject decoded = CosmeticGrants.decode(trimmed.substring(CODE_PREFIX.length()));
            return decoded == null ? 0 : CosmeticGrants.apply(decoded);
        }
        if (trimmed.startsWith("{")) {
            try {
                JsonObject parsed = JsonParser.parseString(trimmed).getAsJsonObject();
                return CosmeticGrants.apply(parsed.has("cosmetics") && parsed.get("cosmetics").isJsonObject()
                        ? parsed.getAsJsonObject("cosmetics") : parsed);
            }
            catch (Throwable ignored) {
                return 0;
            }
        }
        return 0;
    }

    /** Код со своим списком предметов: можно перенести косметику на другое устройство. */
    public static String exportCode() {
        JsonObject root = new JsonObject();
        JsonArray owned = new JsonArray();
        for (Cosmetic cosmetic : CosmeticRegistry.all()) {
            if (Cosmetics.granted(cosmetic)) {
                owned.add(cosmetic.id);
            }
        }
        root.addProperty("byazen", "cosmetics");
        root.addProperty("count", owned.size());
        root.add("grant", owned);
        return CODE_PREFIX + CosmeticGrants.encode(root.toString());
    }

    private static String encode(String json) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(buffer);
                 Writer writer = new OutputStreamWriter(gzip, StandardCharsets.UTF_8)) {
                writer.write(json);
            }
            return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.toByteArray());
        }
        catch (Throwable throwable) {
            return "";
        }
    }

    private static JsonObject decode(String payload) {
        try {
            byte[] raw = Base64.getUrlDecoder().decode(payload);
            try (InputStream input = new GZIPInputStream(new ByteArrayInputStream(raw));
                 Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                return JsonParser.parseReader(reader).getAsJsonObject();
            }
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    /** Пример описания выдачи — используется в подсказке каталога. */
    public static String example() {
        return "{\"set\":\"winter\",\"source\":\"ивент\"}";
    }
}
