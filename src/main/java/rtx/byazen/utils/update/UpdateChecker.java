package rtx.byazen.utils.update;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.web.WebBridge;

/**
 * Автообновление клиента (идея №192 из IDEAS.md).
 * <p>
 * Клиент раз в сутки сам смотрит файл {@code update.json} в репозитории проекта: если там версия
 * новее — в чат приходит подсказка. Кнопки в модуле Updater умеют скачать новый jar прямо в папку
 * {@code mods} (с проверкой, что это действительно сборка ByAzen), убрать старый и закрыть игру,
 * чтобы лаунчер запустил свежую версию. Ничего не ставится без нажатия кнопки.
 */
public final class UpdateChecker {

    /** Файл-манифест обновлений в репозитории проекта. */
    public static final String MANIFEST = "https://raw.githubusercontent.com/gggvkvh405-rgb/ByAzen/main/update.json";
    public static final String PAGE = "https://github.com/gggvkvh405-rgb/ByAzen";
    private static final long CHECK_PERIOD = 86400000L;

    /** Что вернула проверка. */
    public record Result(boolean ok, String current, String latest, String url, String note) {
        public boolean newer() {
            return this.ok && UpdateChecker.compare(this.latest, this.current) > 0;
        }
    }

    private static volatile Result result;
    private static volatile boolean checking;
    private static volatile long checkedAt;
    private static volatile String status = "обновление ещё не проверялось";
    private static volatile String downloadNote = "";

    private UpdateChecker() {
    }

    public static Result last() {
        return result;
    }

    public static boolean checking() {
        return checking;
    }

    public static String status() {
        return status;
    }

    public static String downloadNote() {
        return downloadNote;
    }

    private static void setStatus(String text) {
        status = text;
        ClientLog.info("обновление: " + text);
    }

    public static String current() {
        return WebBridge.version();
    }

    public static String summary() {
        Result last = result;
        if (checking) {
            return "проверяю обновление…";
        }
        if (last == null) {
            return "версия " + UpdateChecker.current() + " · " + status;
        }
        if (last.newer()) {
            return "версия " + last.current() + ", доступна " + last.latest() + " — можно обновить";
        }
        return "версия " + last.current() + " — самая свежая (" + last.note() + ")";
    }

    /** Сравнение версий вида 1.8.1 и 1.9.0: больше нуля, если left новее. */
    public static int compare(String left, String right) {
        String[] a = (left == null ? "" : left).split("\\.");
        String[] b = (right == null ? "" : right).split("\\.");
        int size = Math.max(a.length, b.length);
        for (int i = 0; i < size; ++i) {
            int x = UpdateChecker.number(i < a.length ? a[i] : "0");
            int y = UpdateChecker.number(i < b.length ? b[i] : "0");
            if (x != y) {
                return Integer.compare(x, y);
            }
        }
        return 0;
    }

    private static int number(String text) {
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            char symbol = text.charAt(i);
            if (Character.isDigit(symbol)) {
                digits.append(symbol);
            }
            else {
                break;
            }
        }
        if (digits.length() == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(digits.toString());
        }
        catch (Throwable throwable) {
            return 0;
        }
    }

    /** Проверка в фоне: страница не должна тормозить игру. */
    public static void check(boolean louder) {
        long now = System.currentTimeMillis();
        if (checking || now - checkedAt < CHECK_PERIOD && !louder) {
            return;
        }
        checking = true;
        checkedAt = now;
        UpdateChecker.setStatus("проверяю…");
        Thread worker = new Thread(() -> UpdateChecker.work(louder), "byazen-update-check");
        worker.setDaemon(true);
        worker.start();
    }

    private static void work(boolean louder) {
        try {
            JsonObject root = JsonParser.parseString(UpdateChecker.get(UpdateChecker.MANIFEST)).getAsJsonObject();
            String latest = root.has("version") ? root.get("version").getAsString() : "";
            String url = root.has("url") ? root.get("url").getAsString()
                    : UpdateChecker.getUrl(root, latest);
            if (latest.isBlank()) {
                result = new Result(false, UpdateChecker.current(), "", "", "в манифесте нет версии");
                UpdateChecker.setStatus("в манифесте нет версии");
                return;
            }
            result = new Result(true, UpdateChecker.current(), latest, url, "манифест прочитан");
            UpdateChecker.setStatus("проверено: доступна " + latest);
            if (UpdateChecker.compare(latest, UpdateChecker.current()) > 0) {
                NotificationsModule.notify("§bЕсть обновление ByAzen: §f" + latest, 5000L);
                if (louder) {
                    ChatMessage.send("§bОбновление ByAzen: §fверсия " + latest + "§7 (сейчас " + UpdateChecker.current()
                            + "). Модуль Updater → «Скачать обновление».");
                }
            }
            else if (louder) {
                ChatMessage.send("§7ByAzen обновлён: версия " + UpdateChecker.current() + " — самая свежая");
            }
        }
        catch (Throwable throwable) {
            result = new Result(false, UpdateChecker.current(), "", "", "нет связи с репозиторием");
            UpdateChecker.setStatus("не удалось проверить: " + throwable.getClass().getSimpleName());
            if (louder) {
                ChatMessage.send("§7Обновление проверить не удалось (нет сети или репозиторий недоступен)");
            }
        }
        finally {
            checking = false;
        }
    }

    private static String getUrl(JsonObject root, String latest) {
        String jar = root.has("jar") ? root.get("jar").getAsString() : ("ByAzen-" + latest + ".jar");
        return "https://raw.githubusercontent.com/gggvkvh405-rgb/ByAzen/main/" + jar;
    }

    private static String get(String address) throws Exception {
        HttpURLConnection connection = (HttpURLConnection)new URL(address).openConnection();
        connection.setConnectTimeout(6000);
        connection.setReadTimeout(8000);
        connection.setRequestProperty("User-Agent", "ByAzen/" + UpdateChecker.current());
        connection.setInstanceFollowRedirects(true);
        try (InputStream stream = connection.getInputStream()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int read;
            while ((read = stream.read(chunk)) > 0) {
                buffer.write(chunk, 0, read);
            }
            return buffer.toString(StandardCharsets.UTF_8.name());
        }
    }

    /** Скачивает jar и кладёт его в mods, убирая старые сборки. */
    public static String download() {
        Result last = result;
        if (last == null || !last.ok()) {
            return "Сначала проверьте обновления (кнопка «Проверить обновление»)";
        }
        if (!last.newer()) {
            return "Уже стоит самая свежая версия " + last.current();
        }
        if (last.url().isBlank()) {
            return "В манифесте нет ссылки на файл — скачайте вручную со страницы проекта";
        }
        try {
            byte[] data = UpdateChecker.bytes(last.url());
            if (data.length < 10000) {
                return "Файл обновления подозрительно маленький — скачивание отменено";
            }
            Path mods = FabricLoader.getInstance().getGameDir().resolve("mods");
            Files.createDirectories(mods);
            Path target = mods.resolve("ByAzen-" + last.latest() + ".jar");
            Path temp = mods.resolve("ByAzen-update.tmp");
            Files.write(temp, data);
            if (!UpdateChecker.isByAzenJar(temp)) {
                Files.deleteIfExists(temp);
                downloadNote = "файл не похож на сборку ByAzen";
                return "Скачанный файл не похож на сборку ByAzen — ничего не менял";
            }
            UpdateChecker.removeOld(mods, target);
            Files.move(temp, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            downloadNote = "скачан " + target.getFileName();
            UpdateChecker.setStatus("обновление скачано: " + target.getFileName());
            NotificationsModule.notify("§bОбновление скачано: §f" + target.getFileName(), 5000L);
            ChatMessage.send("§bОбновление скачано: §f" + target.getFileName() + "§7. Перезапустите игру — версия "
                    + last.latest() + " начнёт работать.");
            return "Скачано: " + target.getFileName() + " (перезапустите игру)";
        }
        catch (Throwable throwable) {
            downloadNote = "ошибка: " + throwable.getClass().getSimpleName();
            return "Скачать не удалось: " + throwable.getClass().getSimpleName();
        }
    }

    /** «Обновить и перезапустить»: скачивает и закрывает игру, чтобы лаунчер поднял новую версию. */
    public static String updateAndRestart() {
        String note = UpdateChecker.download();
        if (!note.startsWith("Скачано")) {
            return note;
        }
        ChatMessage.send("§bЗакрываю игру для обновления — откройте её заново");
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.scheduleStop();
        }
        return "Обновление установлено, игра закрывается";
    }

    private static byte[] bytes(String address) throws Exception {
        HttpURLConnection connection = (HttpURLConnection)new URL(address).openConnection();
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(20000);
        connection.setRequestProperty("User-Agent", "ByAzen/" + UpdateChecker.current());
        connection.setInstanceFollowRedirects(true);
        try (InputStream stream = connection.getInputStream()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[16384];
            int read;
            while ((read = stream.read(chunk)) > 0) {
                buffer.write(chunk, 0, read);
            }
            return buffer.toByteArray();
        }
    }

    private static boolean isByAzenJar(Path file) {
        try (ZipFile zip = new ZipFile(file.toFile())) {
            String modJson = null;
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if ("fabric.mod.json".equals(entry.getName())) {
                    modJson = new String(zip.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8);
                    break;
                }
            }
            return modJson != null && modJson.contains("\"byazen\"");
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private static void removeOld(Path mods, Path keep) {
        try {
            Files.list(mods).filter(path -> {
                String name = path.getFileName().toString();
                return name.startsWith("ByAzen-") && name.endsWith(".jar") && !path.equals(keep);
            }).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                }
                catch (Throwable throwable) {
                    ClientLog.warn("обновление: старый jar не удалить — " + path.getFileName());
                }
            });
        }
        catch (Throwable throwable) {
            ClientLog.warn("обновление: не удалось перебрать mods");
        }
    }

    public static void openPage() {
        try {
            Util.getOperatingSystem().open(new URI(UpdateChecker.PAGE));
        }
        catch (Throwable throwable) {
            ClientLog.warn("обновление: не удалось открыть страницу проекта");
        }
    }
}

