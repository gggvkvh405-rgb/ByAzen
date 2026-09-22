package rtx.byazen.utils.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import rtx.byazen.utils.chat.ChatMessage;

/**
 * Локальный веб-сервер клиента (идеи №175 и №176 из IDEAS.md).
 * <p>
 * Поднимает обычный HTTP-сервер на JDK ({@code com.sun.net.httpserver}) — без сторонних библиотек.
 * Наружу отдаются только страницы компаньона и дашборда, доступ закрыт токеном в ссылке; сам сервер
 * слушает либо только этот компьютер, либо домашнюю сеть (чтобы открыть страницу с телефона).
 * Команды со страницы (плеер, модули) выполняются в игровом потоке через {@link WebBridge}.
 */
public final class LocalHttp {

    public static final int DEFAULT_PORT = 8765;

    private static final LocalHttp INSTANCE = new LocalHttp();
    private static final AtomicInteger handled = new AtomicInteger();

    private HttpServer server;
    private ExecutorService pool;
    private int port = DEFAULT_PORT;
    private boolean lan;
    private String token = LocalHttp.newToken();
    private boolean dashboard;
    private long startedAt;

    private LocalHttp() {
    }

    public static LocalHttp get() {
        return INSTANCE;
    }

    public synchronized boolean running() {
        return this.server != null;
    }

    public synchronized int port() {
        return this.port;
    }

    public synchronized boolean lan() {
        return this.lan;
    }

    public synchronized String token() {
        return this.token;
    }

    public synchronized long uptimeMs() {
        return this.server == null ? 0L : System.currentTimeMillis() - this.startedAt;
    }

    public synchronized int requests() {
        return handled.get();
    }

    /** Адрес для телефона: этот компьютер либо домашняя сеть. */
    public synchronized String baseUrl() {
        return "http://" + (this.lan ? LocalHttp.lanAddress() : "127.0.0.1") + ":" + this.port;
    }

    /** Ссылка с токеном — её и показывает QR-код. */
    public synchronized String link() {
        return this.baseUrl() + "/?t=" + this.token;
    }

    /**
     * Запускает сервер. {@code dashboard} — открывать ли страницу дашборда (идея №176),
     * {@code lan} — слушать ли домашнюю сеть (иначе только этот компьютер).
     */
    public synchronized String start(int port, boolean lan, boolean dashboard) {
        this.dashboard = dashboard;
        if (this.server != null) {
            if (this.port == port && this.lan == lan && this.dashboard == dashboard) {
                return "Веб-сервер уже работает: " + this.link();
            }
            this.stop();
        }
        try {
            this.port = Math.max(1024, Math.min(65535, port));
            this.lan = lan;
            this.token = LocalHttp.newToken();
            InetAddress address = lan ? new InetSocketAddress(this.port).getAddress() : InetAddress.getLoopbackAddress();
            this.server = HttpServer.create(new InetSocketAddress(address, this.port), 0);
            this.server.createContext("/", this::handle);
            this.pool = Executors.newFixedThreadPool(4, runnable -> {
                Thread thread = new Thread(runnable, "ByAzen web");
                thread.setDaemon(true);
                return thread;
            });
            this.server.setExecutor(this.pool);
            this.server.start();
            this.startedAt = System.currentTimeMillis();
            handled.set(0);
            return "Веб-сервер запущен: " + this.link();
        }
        catch (Throwable throwable) {
            this.server = null;
            return "Не удалось запустить веб-сервер (порт " + this.port + " занят?): " + throwable.getClass().getSimpleName();
        }
    }

    public synchronized void stop() {
        if (this.server != null) {
            try {
                this.server.stop(0);
            }
            catch (Throwable ignored) {
            }
            this.server = null;
        }
        if (this.pool != null) {
            this.pool.shutdownNow();
            this.pool = null;
        }
    }

    /** Меняет токен: все старые ссылки перестают работать. */
    public synchronized String rotateToken() {
        this.token = LocalHttp.newToken();
        return this.token;
    }

    private void handle(HttpExchange exchange) {
        try {
            handled.incrementAndGet();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            if (path.equals("/health")) {
                LocalHttp.send(exchange, 200, "text/plain", "ok");
                return;
            }
            if (!LocalHttp.authorized(query)) {
                LocalHttp.send(exchange, 403, "text/html; charset=utf-8", WebPages.denied());
                return;
            }
            if (path.equals("/") || path.equals("/index.html")) {
                LocalHttp.send(exchange, 200, "text/html; charset=utf-8", WebPages.companion(query));
                return;
            }
            if (path.equals("/dashboard")) {
                if (!this.dashboard) {
                    LocalHttp.send(exchange, 404, "text/html; charset=utf-8", WebPages.off());
                    return;
                }
                LocalHttp.send(exchange, 200, "text/html; charset=utf-8", WebPages.dashboard(query));
                return;
            }
            if (path.equals("/api/state")) {
                LocalHttp.send(exchange, 200, "application/json; charset=utf-8", WebBridge.state().toString());
                return;
            }
            if (path.equals("/api/stats")) {
                LocalHttp.send(exchange, 200, "application/json; charset=utf-8", WebBridge.stats().toString());
                return;
            }
            if (path.equals("/api/events")) {
                LocalHttp.send(exchange, 200, "application/json; charset=utf-8", WebBridge.eventsJson().toString());
                return;
            }
            if (path.startsWith("/api/action/")) {
                String action = path.substring("/api/action/".length());
                LocalHttp.send(exchange, 200, "application/json; charset=utf-8", WebBridge.request(action, query).toString());
                return;
            }
            if (path.equals("/favicon.ico")) {
                LocalHttp.send(exchange, 204, "image/x-icon", "");
                return;
            }
            LocalHttp.send(exchange, 404, "text/html; charset=utf-8", WebPages.off());
        }
        catch (Throwable throwable) {
            try {
                LocalHttp.send(exchange, 500, "text/plain; charset=utf-8", "ошибка: " + throwable.getClass().getSimpleName());
            }
            catch (Throwable ignored) {
            }
        }
        finally {
            exchange.close();
        }
    }

    private static boolean authorized(String query) {
        if (query == null) {
            return false;
        }
        for (String part : query.split("&")) {
            int index = part.indexOf(61);
            if (index <= 0) {
                continue;
            }
            if (part.substring(0, index).equals("t") && part.substring(index + 1).equals(LocalHttp.INSTANCE.token)) {
                return true;
            }
        }
        return false;
    }

    static void send(HttpExchange exchange, int status, String type, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", type);
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream stream = exchange.getResponseBody()) {
            stream.write(bytes);
        }
    }

    private static String newToken() {
        String alphabet = "abcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder builder = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 10; ++i) {
            builder.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return builder.toString();
    }

    /** Первый адрес в домашней сети — по нему телефон и попадёт на страницу. */
    public static String lanAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            List<String> found = new ArrayList<String>();
            while (interfaces.hasMoreElements()) {
                NetworkInterface network = interfaces.nextElement();
                if (!network.isUp() || network.isLoopback()) {
                    continue;
                }
                for (InetAddress address : Collections.list(network.getInetAddresses())) {
                    if (!(address instanceof Inet4Address) || address.isLoopbackAddress() || address.isLinkLocalAddress()) {
                        continue;
                    }
                    found.add(address.getHostAddress());
                }
            }
            if (!found.isEmpty()) {
                return found.get(0);
            }
        }
        catch (Throwable ignored) {
        }
        return "127.0.0.1";
    }

    /** Короткая сводка для настроек и чата. */
    public synchronized String status() {
        if (this.server == null) {
            return "веб-сервер выключен";
        }
        return String.format(Locale.ROOT, "веб-сервер %s:%d · %s · запросов %d · %d с",
                this.lan ? LocalHttp.lanAddress() : "127.0.0.1", this.port, this.dashboard ? "компаньон + дашборд" : "компаньон",
                handled.get(), (System.currentTimeMillis() - this.startedAt) / 1000L);
    }

    public static void say(String text) {
        ChatMessage.send(text);
    }
}
