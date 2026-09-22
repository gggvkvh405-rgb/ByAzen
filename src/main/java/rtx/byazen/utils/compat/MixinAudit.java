package rtx.byazen.utils.compat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.logs.ClientLog;

/**
 * Проверка конфликтов миксинов (идея №173 из IDEAS.md).
 * <p>
 * Миксины применяются на старте, и если другой мод трогает те же классы, в логе остаются
 * предупреждения Mixin. Клиент читает свой последний лог, вытаскивает такие строки, связывает их
 * с известными модами (Iris, Sodium, OptiFine и т.д.) и говорит об этом простым текстом.
 */
public final class MixinAudit {

    private static final int MAX_LINES = 6000;
    private static final int MAX_ROWS = 20;
    private static boolean warned;

    private MixinAudit() {
    }

    public record Row(String text, String mod, String advice) {
    }

    /** Читает последний лог клиента и собирает строки про миксины, похожие на проблему. */
    public static List<Row> scan() {
        ArrayList<Row> rows = new ArrayList<Row>();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return rows;
        }
        Path log = client.runDirectory.toPath().resolve("logs").resolve("latest.log");
        if (!Files.isReadable(log)) {
            return rows;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(log, StandardCharsets.UTF_8);
        }
        catch (IOException exception) {
            return rows;
        }
        int from = Math.max(0, lines.size() - MAX_LINES);
        for (int i = from; i < lines.size() && rows.size() < MAX_ROWS; ++i) {
            String line = lines.get(i);
            String lower = line.toLowerCase(Locale.ROOT);
            if (!lower.contains("mixin")) {
                continue;
            }
            if (!(lower.contains("error") || lower.contains("warn") || lower.contains("conflict")
                    || lower.contains("failed") || lower.contains("already") || lower.contains("overwrit"))) {
                continue;
            }
            rows.add(new Row(MixinAudit.trim(line), MixinAudit.guessMod(lower), MixinAudit.advice(lower)));
        }
        return rows;
    }

    private static String guessMod(String lower) {
        String[] known = {"iris", "sodium", "embeddium", "rubidium", "optifine", "optifabric", "distanthorizons",
                "vulkanmod", "nvidium", "lithium", "ferritecore", "indium", "flywheel", "entityculling"};
        for (String id : known) {
            if (lower.contains(id)) {
                return id;
            }
        }
        return "";
    }

    private static String advice(String lower) {
        if (lower.contains("already") || lower.contains("overwrit")) {
            return "два мода правят один и тот же класс: оставьте один из модов или выключите их функцию";
        }
        if (lower.contains("failed")) {
            return "модуль миксина не применился: проверьте версию мода под 1.21 и наличие Fabric API";
        }
        if (lower.contains("conflict")) {
            return "явный конфликт: отключите подозрительный мод и перезапустите игру";
        }
        return "посмотрите строку лога целиком — там указан класс и мод";
    }

    public static String summary() {
        int problems = MixinAudit.scan().size();
        return problems == 0 ? "конфликтов миксинов в логе не найдено" : "подозрительных строк про миксины: " + problems;
    }

    public static List<String> report() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Проверка конфликтов миксинов: " + MixinAudit.summary());
        for (Row row : MixinAudit.scan()) {
            list.add("  " + (row.mod().isEmpty() ? "" : "[" + row.mod() + "] ") + row.text());
            list.add("    → " + row.advice());
        }
        list.add("Известные соседи по рендеру:");
        for (ModEnv.Row row2 : ModEnv.presentRows()) {
            list.add("  " + row2.label + (row2.version.isEmpty() ? "" : " " + row2.version) + " — " + row2.note);
        }
        return list;
    }

    /** Один раз за сессию предупреждает в чате, если в логе есть проблемы с миксинами. */
    public static void warnIfNeeded() {
        if (warned) {
            return;
        }
        warned = true;
        List<Row> rows = MixinAudit.scan();
        if (rows.isEmpty()) {
            ClientLog.info("миксины: конфликтов не найдено");
            return;
        }
        Map<String, Integer> byMod = new LinkedHashMap<String, Integer>();
        for (Row row : rows) {
            String key = row.mod().isEmpty() ? "неизвестный мод" : row.mod();
            byMod.merge(key, 1, Integer::sum);
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : byMod.entrySet()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(entry.getKey()).append(" ×").append(entry.getValue());
        }
        ClientLog.warn("миксины: " + builder);
        ChatMessage.send("§eByAzen: в логе есть предупреждения миксинов — " + builder + ". Откройте «Диагностика» → «Моды»");
    }

    private static String trim(String line) {
        String text = line.replace('\t', ' ').trim();
        return text.length() <= 160 ? text : text.substring(0, 159) + "…";
    }
}
