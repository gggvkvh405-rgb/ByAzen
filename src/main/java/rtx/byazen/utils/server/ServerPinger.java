package rtx.byazen.utils.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import rtx.byazen.ByAzen;

/**
 * Опрос серверов Minecraft (идея №99 из IDEAS.md).
 * <p>
 * Свой разбор протокола состояния: клиент отправляет рукопожатие и запрос статуса, сервер отвечает
 * строкой JSON с версией, числом игроков и описанием. Замеряется время ответа — оно и показывается
 * как пинг. Опрос идёт в отдельном потоке, поэтому игра не подвисает на медленных адресах.
 */
public final class ServerPinger {

    private static final int PROTOCOL = 767;
    private static final int TIMEOUT_MS = 6000;
    private static final ExecutorService POOL = Executors.newFixedThreadPool(4, runnable -> {
        Thread thread = new Thread(runnable, "ByAzen-ServerPing");
        thread.setDaemon(true);
        return thread;
    });

    private ServerPinger() {
    }

    /** Ответ сервера: всё, что нужно показать в списке. */
    public static final class Status {

        public final String address;
        public boolean online;
        public int players = -1;
        public int slots = -1;
        public int ping = -1;
        public String version = "";
        public String motd = "";
        public String error = "";

        public Status(String address) {
            this.address = ServerPinger.cleanAddress(address);
        }

        /** Готовая строка для списка: «12 / 100 · 43 мс». */
        public String summary() {
            if (!this.online) {
                return this.error.isEmpty() ? "не отвечает" : this.error;
            }
            StringBuilder builder = new StringBuilder();
            if (this.players >= 0) {
                builder.append(this.players);
                if (this.slots >= 0) {
                    builder.append(" / ").append(this.slots);
                }
                builder.append(" · ");
            }
            builder.append(this.ping < 0 ? "—" : this.ping + " мс");
            return builder.toString();
        }

        /** Полоски пинга: 0 — нет связи, 5 — отличный отклик. */
        public int bars() {
            if (!this.online || this.ping < 0) {
                return 0;
            }
            if (this.ping < 60) {
                return 5;
            }
            if (this.ping < 120) {
                return 4;
            }
            if (this.ping < 200) {
                return 3;
            }
            return this.ping < 350 ? 2 : 1;
        }

        public String playersText() {
            if (!this.online) {
                return "сервер не ответил";
            }
            if (this.players < 0) {
                return "игроков: неизвестно";
            }
            return this.slots >= 0 ? "игроков: " + this.players + " из " + this.slots : "игроков: " + this.players;
        }

        public String versionText() {
            if (!this.online) {
                return "";
            }
            return this.version.isEmpty() ? "версия не указана" : this.version;
        }
    }

    /** Опрос адреса в вызывающем потоке. */
    public static Status ping(String address) {
        Status status = new Status(address);
        String host = ServerPinger.hostOf(address);
        int port = ServerPinger.portOf(address);
        if (host.isEmpty()) {
            status.error = "адрес пустой";
            return status;
        }
        long started = System.nanoTime();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
            ServerPinger.writeHandshake(output, host, port);
            output.writeByte(1);
            output.writeByte(0);
            output.flush();
            int length = ServerPinger.readVarInt(input);
            if (length <= 0) {
                status.error = "сервер закрыл соединение";
                return status;
            }
            int packetId = ServerPinger.readVarInt(input);
            if (packetId != 0) {
                status.error = "неожиданный ответ сервера";
                return status;
            }
            int jsonLength = ServerPinger.readVarInt(input);
            byte[] payload = new byte[Math.max(0, jsonLength)];
            input.readFully(payload);
            status.ping = (int)Math.min(Integer.MAX_VALUE, (System.nanoTime() - started) / 1000000L);
            ServerPinger.parse(status, new String(payload, StandardCharsets.UTF_8));
            status.online = true;
            return status;
        }
        catch (Throwable throwable) {
            status.online = false;
            status.error = ServerPinger.describe(throwable);
            status.ping = -1;
            return status;
        }
    }

    /** Опрос в отдельном потоке: результат приходит в игровой поток через {@code CompletableFuture}. */
    public static CompletableFuture<Status> pingAsync(String address) {
        return CompletableFuture.supplyAsync(() -> ServerPinger.ping(address), POOL);
    }

    private static void writeHandshake(DataOutputStream output, String host, int port) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream inner = new DataOutputStream(buffer);
        ServerPinger.writeVarInt(inner, 0);
        ServerPinger.writeVarInt(inner, PROTOCOL);
        ServerPinger.writeString(inner, host);
        inner.writeShort(port);
        ServerPinger.writeVarInt(inner, 1);
        inner.flush();
        ServerPinger.writeVarInt(output, buffer.size());
        buffer.writeTo(output);
    }

    private static void parse(Status status, String json) {
        try {
            JsonElement element = JsonParser.parseString(json);
            if (element == null || !element.isJsonObject()) {
                status.error = "сервер ответил странно";
                return;
            }
            JsonObject root = element.getAsJsonObject();
            if (root.has("version") && root.get("version").isJsonObject()) {
                JsonObject version = root.getAsJsonObject("version");
                status.version = version.has("name") ? version.get("name").getAsString() : "";
            }
            if (root.has("players") && root.get("players").isJsonObject()) {
                JsonObject players = root.getAsJsonObject("players");
                status.players = players.has("online") ? players.get("online").getAsInt() : -1;
                status.slots = players.has("max") ? players.get("max").getAsInt() : -1;
            }
            status.motd = root.has("description") ? ServerPinger.describe(root.get("description")) : "";
        }
        catch (Throwable throwable) {
            status.error = "ответ не разобран";
        }
    }

    private static String describe(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return "";
        }
        if (element.isJsonPrimitive()) {
            return element.getAsString();
        }
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            StringBuilder builder = new StringBuilder();
            if (object.has("text")) {
                builder.append(object.get("text").getAsString());
            }
            if (object.has("extra") && object.get("extra").isJsonArray()) {
                JsonArray extra = object.getAsJsonArray("extra");
                for (JsonElement child : extra) {
                    builder.append(ServerPinger.describe(child));
                }
            }
            return builder.toString();
        }
        if (element.isJsonArray()) {
            StringBuilder builder = new StringBuilder();
            for (JsonElement child : element.getAsJsonArray()) {
                builder.append(ServerPinger.describe(child));
            }
            return builder.toString();
        }
        return "";
    }

    private static String describe(Throwable throwable) {
        String name = throwable.getClass().getSimpleName();
        if (name.contains("UnknownHost")) {
            return "адрес не найден";
        }
        if (name.contains("Timeout") || name.contains("SocketTimeout")) {
            return "нет ответа за 6 с";
        }
        if (name.contains("Connect")) {
            return "подключение отклонено";
        }
        return "ошибка связи: " + name;
    }

    /** Хост из адреса: «play.example.net:25565» → «play.example.net». */
    public static String hostOf(String address) {
        String clean = address == null ? "" : address.trim();
        if (clean.isEmpty()) {
            return "";
        }
        if (clean.startsWith("[")) {
            int close = clean.indexOf(93);
            return close > 0 ? clean.substring(1, close) : clean;
        }
        int colon = clean.lastIndexOf(58);
        if (colon > 0 && clean.indexOf(58) == colon) {
            return clean.substring(0, colon);
        }
        return clean;
    }

    /** Порт из адреса, по умолчанию 25565. */
    public static int portOf(String address) {
        String clean = address == null ? "" : address.trim();
        if (clean.isEmpty()) {
            return 25565;
        }
        int colon = clean.lastIndexOf(58);
        if (colon > 0 && colon < clean.length() - 1) {
            String tail = clean.substring(colon + 1);
            try {
                int port = Integer.parseInt(tail.trim());
                return port > 0 && port <= 65535 ? port : 25565;
            }
            catch (NumberFormatException ignored) {
                return 25565;
            }
        }
        return 25565;
    }

    /** Приводит адрес к нижнему регистру без лишних пробелов — ключ профилей и избранного. */
    public static String cleanAddress(String address) {
        if (address == null) {
            return "";
        }
        String clean = address.trim().toLowerCase(Locale.ROOT).replace(" ", "");
        if (clean.isEmpty()) {
            return "";
        }
        if (ServerPinger.portOf(clean) == 25565 && !clean.endsWith(":25565")) {
            return clean;
        }
        return clean;
    }

    /** Адрес без технического порта: «play.example.net:25565» → «play.example.net». */
    public static String prettyAddress(String address) {
        String clean = ServerPinger.cleanAddress(address);
        return clean.endsWith(":25565") ? clean.substring(0, clean.length() - 6) : clean;
    }

    private static void writeVarInt(DataOutputStream output, int value) throws Exception {
        int current = value;
        do {
            int chunk = current & 0x7F;
            current >>>= 7;
            if (current != 0) {
                chunk |= 0x80;
            }
            output.writeByte(chunk);
        } while (current != 0);
    }

    private static void writeString(DataOutputStream output, String text) throws Exception {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        ServerPinger.writeVarInt(output, bytes.length);
        output.write(bytes);
    }

    private static int readVarInt(DataInputStream input) throws Exception {
        int result = 0;
        int shift = 0;
        while (true) {
            int chunk = input.readByte() & 0xFF;
            result |= (chunk & 0x7F) << shift;
            if ((chunk & 0x80) == 0) {
                return result;
            }
            shift += 7;
            if (shift > 35) {
                ByAzen.LOGGER.debug("[ByAzen] Слишком длинный VarInt от сервера {}", shift);
                return -1;
            }
        }
    }
}
