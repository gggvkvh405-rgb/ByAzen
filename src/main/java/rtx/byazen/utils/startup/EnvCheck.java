package rtx.byazen.utils.startup;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.SharedConstants;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.logs.ClientLog;

/**
 * Проверка требований на старте (идея №171 из IDEAS.md): версия Java, Fabric Loader, версия Minecraft,
 * Fabric API и выделенная память. Если чего-то не хватает, клиент говорит об этом простыми словами,
 * а не падает с непонятной ошибкой где-то в рендере.
 */
public final class EnvCheck {

    /** Минимальные требования ByAzen. */
    public static final int MIN_JAVA = 21;
    private static final String MOD_ID = "byazen";
    private static boolean warned;

    private EnvCheck() {
    }

    public record Row(String name, String value, boolean ok, String note) {
    }

    public static List<Row> rows() {
        ArrayList<Row> list = new ArrayList<Row>();
        String javaVersion = System.getProperty("java.version", "?");
        boolean javaOk = EnvCheck.major(javaVersion) >= MIN_JAVA;
        list.add(new Row("Java", javaVersion, javaOk,
                javaOk ? "подходит" : "нужна Java " + MIN_JAVA + "+ (в лаунчере выберите Java 21 или новее)"));

        String loader = EnvCheck.modVersion("fabricloader");
        boolean loaderOk = loader != null && EnvCheck.compare(loader, "0.15.0") >= 0;
        list.add(new Row("Fabric Loader", loader == null ? "не найден" : loader, loaderOk,
                loaderOk ? "подходит" : "обновите Fabric Loader до 0.15.0 или новее"));

        String mc = SharedConstants.getGameVersion().name();
        boolean mcOk = mc.startsWith("1.21");
        list.add(new Row("Minecraft", mc, mcOk,
                mcOk ? "подходит" : "ByAzen собран под 1.21.x — на 1.20 и ниже он не запустится"));

        String api = EnvCheck.modVersion("fabric-api");
        list.add(new Row("Fabric API", api == null ? "не найден" : api, api != null,
                api != null ? "на месте" : "поставьте Fabric API — без него часть функций отключена"));

        long maxMb = Runtime.getRuntime().maxMemory() / 1048576L;
        boolean memoryOk = maxMb >= 1536L;
        list.add(new Row("Память", maxMb + " МБ", memoryOk,
                memoryOk ? "хватает" : "выделите хотя бы 2 ГБ — косметика, GIF и звуки требуют запаса"));

        list.add(new Row("Мод в сборке", EnvCheck.modVersion(MOD_ID) == null ? "не определён" : EnvCheck.modVersion(MOD_ID), true,
                "проверка подписи и версии выполняется лаунчером"));
        return list;
    }

    /** Короткая сводка для чата и журнала. */
    public static String summary() {
        int bad = 0;
        for (Row row : EnvCheck.rows()) {
            if (!row.ok()) {
                ++bad;
            }
        }
        return bad == 0 ? "окружение в порядке (Java, Loader, Minecraft, API, память)" : "проблем окружения: " + bad;
    }

    /** Полный отчёт строками. */
    public static List<String> report() {
        ArrayList<String> list = new ArrayList<String>();
        for (Row row : EnvCheck.rows()) {
            list.add(row.name() + ": " + row.value() + " — " + row.note());
        }
        return list;
    }

    /** Один раз при входе в мир говорит о проблемах, чтобы игрок не искал причину сам. */
    public static void warnIfNeeded() {
        if (warned) {
            return;
        }
        warned = true;
        ArrayList<String> problems = new ArrayList<String>();
        for (Row row : EnvCheck.rows()) {
            if (!row.ok()) {
                problems.add(row.name() + " — " + row.note());
            }
        }
        if (problems.isEmpty()) {
            ClientLog.info("окружение: " + EnvCheck.summary());
            return;
        }
        ClientLog.warn("окружение: " + String.join("; ", problems));
        ChatMessage.send("§cByAzen: проверьте окружение — " + String.join("; ", problems));
    }

    private static String modVersion(String id) {
        try {
            ModContainer container = FabricLoader.getInstance().getModContainer(id).orElse(null);
            return container == null ? null : container.getMetadata().getVersion().getFriendlyString();
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    private static int major(String version) {
        try {
            String text = version.split("[^0-9]")[0];
            return text.isEmpty() ? 0 : Integer.parseInt(text);
        }
        catch (Throwable throwable) {
            return 0;
        }
    }

    /** Сравнение версий вида 0.16.5: отрицательное — слева старее. */
    private static int compare(String left, String right) {
        String[] a = left.split("[^0-9]");
        String[] b = right.split("[^0-9]");
        for (int i = 0; i < Math.max(a.length, b.length); ++i) {
            int av = i < a.length ? EnvCheck.parseInt(a[i]) : 0;
            int bv = i < b.length ? EnvCheck.parseInt(b[i]) : 0;
            if (av != bv) {
                return av - bv;
            }
        }
        return 0;
    }

    private static int parseInt(String text) {
        try {
            return text.isEmpty() ? 0 : Integer.parseInt(text);
        }
        catch (Throwable throwable) {
            return 0;
        }
    }
}
