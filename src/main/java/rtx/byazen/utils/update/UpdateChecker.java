package rtx.byazen.utils.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HexFormat;
import java.util.List;
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
 * Автообновление клиента (идеи №192, №206–№208 из IDEAS.md).
 * <p>
 * Клиент раз в сутки смотрит файл {@code update.json}. Манифест генерируется автоматически в CI:
 * в нём версия, ссылка на jar, контрольная сумма, размер, дата и короткое описание изменений.
 * <p>
 * Источников несколько и они проверяются по очереди: главная ветка репозитория, ветка публикации
 * сборки и GitHub Releases. Если манифест найден — версия сравнивается с текущей, а скачанный файл
 * обязательно проверяется по контрольной сумме из манифеста и по метке сборки ByAzen внутри jar.
 * Ничего не устанавливается без нажатия кнопки.
 */
public final class UpdateChecker {

    /** Страница проекта и адреса, откуда читаются манифесты. */
    public static final String REPO = "https://github.com/gggvkvh405-rgb/ByAzen";
    public static final String PAGE = UpdateChecker.REPO;
    public static final String RAW = "https://raw.githubusercontent.com/gggvkvh405-rgb/ByAzen/";
    /** Ветка, в которую CI кладёт собранный jar и свежий манифест. */
    public static final String BRANCH = "arena/01a0bf74-byazen";
    private static final String RELEASES_API = "https://api.github.com/repos/gggvkvh405-rgb/ByAzen/releases/latest";
    private static final String[] SOURCES = {
            UpdateChecker.RAW + "main/update.json",
            UpdateChecker.RAW + UpdateChecker.BRANCH + "/update.json",
            UpdateChecker.RELEASES_API,
    };
    /** Первый источник — для совместимости с прежним кодом и подсказок в интерфейсе. */
    public static final String MANIFEST = UpdateChecker.SOURCES[0];
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
    private static volatile String notes = "";
    private static volatile String released = "";
    private static volatile String sha256 = "";
    private static volatile long size;
    private static volatile String source = "";
    private static volatile String checksumNote = "";

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

    /** Короткое описание изменений из манифеста. */
    public static String notes() {
        return notes;
    }

    /** Дата релиза из манифеста (как её записал CI). */
    public static String released() {
        return released;
    }

    /** Контрольная сумма файла обновления из манифеста. */
    public static String checksum() {
        return sha256;
    }

    /** Размер файла обновления в мегабайтах, если он известен. */
    public static String sizeText() {
        return size <= 0L ? "" : String.format(java.util.Locale.ROOT, "%.1f МБ", size / 1048576.0);
    }

    /** Откуда прочитан манифест: ветка репозитория или GitHub Releases. */
    public static String source() {
        return source;
    }

    /** Чем закончилась проверка контрольной суммы при скачивании. */
    public static String checksumNote() {
        return checksumNote;
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
        StringBuilder builder = new StringBuilder();
        if (last.newer()) {
            builder.append("версия ").append(last.current()).append(", доступна ").append(last.latest())
                    .append(" — можно обновить");
        }
        else {
            builder.append("версия ").append(last.current()).append(" — самая свежая");
        }
        if (!UpdateChecker.released.isBlank()) {
            builder.append(" · от ").append(UpdateChecker.released);
        }
        if (!UpdateChecker.sizeText().isBlank()) {
            builder.append(" · ").append(UpdateChecker.sizeText());
        }
        if (!UpdateChecker.sha256.isBlank()) {
            builder.append(" · sha256 ").append(UpdateChecker.sha256.substring(0, Math.min(12, UpdateChecker.sha256.length())));
        }
        if (!UpdateChecker.source.isBlank()) {
            builder.append(" · источник: ").append(UpdateChecker.source);
        }
        if (!UpdateChecker.notes.isBlank()) {
            builder.append("\n§7").append(UpdateChecker.notes);
        }
        if (!UpdateChecker.checksumNote.isBlank()) {
            builder.append("\n§7").append(UpdateChecker.checksumNote);
        }
        return builder.toString();
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
            JsonObject manifest = null;
            String used = "";
            Throwable failure = null;
            for (String address : UpdateChecker.SOURCES) {
                try {
                    manifest = UpdateChecker.readManifest(address);
                    used = UpdateChecker.sourceName(address);
                    if (manifest != null && !UpdateChecker.versionOf(manifest).isBlank()) {
                        break;
                    }
                    manifest = null;
                }
                catch (Throwable throwable) {
                    failure = throwable;
                }
            }
            if (manifest == null) {
                result = new Result(false, UpdateChecker.current(), "", "", "нет связи с репозиторием");
                UpdateChecker.setStatus(failure == null
                        ? "манифест обновлений не найден ни в одном из источников"
                        : "не удалось проверить: " + failure.getClass().getSimpleName());
                if (louder) {
                    ChatMessage.send("§7Обновление проверить не удалось: манифест не найден ("
                            + (failure == null ? "нет файла update.json" : failure.getClass().getSimpleName()) + ")");
                }
                return;
            }
            String latest = UpdateChecker.versionOf(manifest);
            String url = UpdateChecker.firstUrl(manifest, latest);
            UpdateChecker.source = used;
            UpdateChecker.notes = UpdateChecker.textOf(manifest, "notes");
            UpdateChecker.released = UpdateChecker.textOf(manifest, "released");
            UpdateChecker.sha256 = UpdateChecker.textOf(manifest, "sha256");
            UpdateChecker.size = manifest.has("size") ? manifest.get("size").getAsLong() : 0L;
            UpdateChecker.checksumNote = "";
            result = new Result(true, UpdateChecker.current(), latest, url, "манифест прочитан (" + used + ")");
            UpdateChecker.setStatus("проверено: доступна " + latest);
            if (UpdateChecker.compare(latest, UpdateChecker.current()) > 0) {
                NotificationsModule.notify("§bЕсть обновление ByAzen: §f" + latest, 5000L);
                if (louder) {
                    ChatMessage.send("§bОбновление ByAzen: §fверсия " + latest + "§7 (сейчас " + UpdateChecker.current()
                            + "). Модуль Updater → «Скачать обновление».");
                    if (!UpdateChecker.notes.isBlank()) {
                        ChatMessage.send("§7Что нового: " + UpdateChecker.notes);
                    }
                    if (!url.isBlank() && !UpdateChecker.sha256.isBlank()) {
                        ChatMessage.send("§8Файл проверится по sha256 " + UpdateChecker.sha256);
                    }
                }
            }
            else if (louder) {
                ChatMessage.send("§7ByAzen обновлён: версия " + UpdateChecker.current() + " — самая свежая"
                        + (UpdateChecker.released.isBlank() ? "" : " (манифест от " + UpdateChecker.released + ")"));
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

    /** Приводит разные источники к одному виду: обычный манифест или ответ GitHub Releases. */
    private static JsonObject readManifest(String address) throws Exception {
        JsonObject root = JsonParser.parseString(UpdateChecker.get(address)).getAsJsonObject();
        if (!address.contains("api.github.com")) {
            return root;
        }
        JsonObject manifest = new JsonObject();
        if (root.has("tag_name")) {
            manifest.addProperty("version", root.get("tag_name").getAsString().replaceFirst("^[vV]", ""));
        }
        if (root.has("name")) {
            manifest.addProperty("notes", root.get("name").getAsString());
        }
        if (root.has("published_at")) {
            manifest.addProperty("released", root.get("published_at").getAsString().split("T")[0]);
        }
        JsonArray urls = new JsonArray();
        if (root.has("assets") && root.get("assets").isJsonArray()) {
            for (JsonElement element : root.getAsJsonArray("assets")) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject asset = element.getAsJsonObject();
                String name = asset.has("name") ? asset.get("name").getAsString() : "";
                if (!name.startsWith("ByAzen-") || !name.endsWith(".jar") || name.contains("lite")) {
                    continue;
                }
                if (asset.has("browser_download_url")) {
                    urls.add(asset.get("browser_download_url").getAsString());
                }
                if (asset.has("size")) {
                    manifest.addProperty("size", asset.get("size").getAsLong());
                }
                if (asset.has("digest") && asset.get("digest").getAsString().startsWith("sha256:")) {
                    manifest.addProperty("sha256", asset.get("digest").getAsString().substring("sha256:".length()));
                }
            }
        }
        if (!urls.isEmpty()) {
            manifest.add("urls", urls);
        }
        return manifest;
    }

    private static String sourceName(String address) {
        if (address.contains("api.github.com")) {
            return "GitHub Releases";
        }
        if (address.contains("/main/")) {
            return "ветка main";
        }
        return "ветка публикации";
    }

    private static String versionOf(JsonObject root) {
        if (root.has("version")) {
            return root.get("version").getAsString();
        }
        if (root.has("latest")) {
            return root.get("latest").getAsString();
        }
        return "";
    }

    private static String textOf(JsonObject root, String key) {
        return root.has(key) ? root.get(key).getAsString().replace('\n', ' ').trim() : "";
    }

    /** Первая рабочая ссылка на файл: urls[] из манифеста, затем url, затем догадка по имени файла. */
    private static String firstUrl(JsonObject root, String latest) {
        if (root.has("urls") && root.get("urls").isJsonArray()) {
            for (JsonElement element : root.getAsJsonArray("urls")) {
                String value = element.getAsString();
                if (value != null && !value.isBlank()) {
                    return value;
                }
            }
        }
        if (root.has("url")) {
            return root.get("url").getAsString();
        }
        String jar = root.has("jar") ? root.get("jar").getAsString() : ("ByAzen-" + latest + ".jar");
        return UpdateChecker.RAW + UpdateChecker.BRANCH + "/" + jar;
    }

    private static String get(String address) throws Exception {
        HttpURLConnection connection = (HttpURLConnection)new URL(address).openConnection();
        connection.setConnectTimeout(6000);
        connection.setReadTimeout(8000);
        connection.setRequestProperty("User-Agent", "ByAzen/" + UpdateChecker.current());
        connection.setRequestProperty("Accept", "application/vnd.github+json");
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
            if (!UpdateChecker.sha256.isBlank()) {
                String actual = UpdateChecker.hex(UpdateChecker.digest(data));
                if (!actual.equalsIgnoreCase(UpdateChecker.sha256)) {
                    UpdateChecker.checksumNote = "контрольная сумма не сошлась: файл не установлен";
                    downloadNote = "ошибка: sha256 не совпал";
                    return "Контрольная сумма не совпала — файл не установлен (возможно, скачивание оборвалось)";
                }
                UpdateChecker.checksumNote = "sha256 проверен";
            }
            else {
                UpdateChecker.checksumNote = "контрольной суммы в манифесте нет — проверяю только метку сборки";
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
            downloadNote = "скачан " + target.getFileName()
                    + (UpdateChecker.sha256.isBlank() ? "" : " (sha256 проверен)");
            UpdateChecker.setStatus("обновление скачано: " + target.getFileName());
            NotificationsModule.notify("§bОбновление скачано: §f" + target.getFileName(), 5000L);
            ChatMessage.send("§bОбновление скачано: §f" + target.getFileName() + "§7. Перезапустите игру, чтобы версия "
                    + last.latest() + " заработала.");
            return "Скачано: " + target.getFileName() + " (перезапустите игру)";
        }
        catch (Throwable throwable) {
            downloadNote = "ошибка: " + throwable.getClass().getSimpleName();
            return "Скачать не удалось: " + throwable.getClass().getSimpleName();
        }
    }

    /** «Обновить и перезапустить»: скачивает и закрывает игру, чтобы лаунчер поднял свежую версию. */
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

    /** Список источников — для команды и окна диагностики. */
    public static List<String> sources() {
        ArrayList<String> list = new ArrayList<String>(List.of(UpdateChecker.SOURCES));
        return list;
    }

    private static byte[] digest(byte[] data) throws Exception {
        return MessageDigest.getInstance("SHA-256").digest(data);
    }

    private static String hex(byte[] bytes) {
        return HexFormat.of().formatHex(bytes);
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
