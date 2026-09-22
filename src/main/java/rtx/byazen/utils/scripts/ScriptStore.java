package rtx.byazen.utils.scripts;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Хранилище правил (идея №177 из IDEAS.md): папка {@code ByAzen/scripts/*.az}.
 * <p>
 * Правила — обычные текстовые файлы: их можно писать в блокноте, делиться ими и складывать в
 * «магазин» из готовых наборов. Клиент читает папку при включении модуля и по кнопке «Перечитать».
 */
public final class ScriptStore {

    private static final List<String> ERRORS = new ArrayList<String>();

    private ScriptStore() {
    }

    /** Папка с правилами: ByAzen/scripts. */
    public static Path folder() {
        return RepositoryStorage.configRoot().resolve("scripts");
    }

    public static List<String> files() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            Files.createDirectories(ScriptStore.folder());
            try (var stream = Files.list(ScriptStore.folder())) {
                stream.filter(item -> item.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".az"))
                        .sorted()
                        .forEach(item -> list.add(item.getFileName().toString()));
            }
        }
        catch (IOException exception) {
            ClientLog.error("не удалось прочитать папку правил: " + exception.getMessage());
        }
        return list;
    }

    public static String read(String file) {
        try {
            Path path = ScriptStore.folder().resolve(file);
            return Files.exists(path) ? Files.readString(path, StandardCharsets.UTF_8) : "";
        }
        catch (IOException exception) {
            return "";
        }
    }

    public static boolean write(String file, String text) {
        try {
            String name = file.endsWith(".az") ? file : file + ".az";
            Files.createDirectories(ScriptStore.folder());
            Files.writeString(ScriptStore.folder().resolve(name), text, StandardCharsets.UTF_8);
            return true;
        }
        catch (IOException exception) {
            ClientLog.error("не удалось записать правило " + file + ": " + exception.getMessage());
            return false;
        }
    }

    public static boolean delete(String file) {
        try {
            return Files.deleteIfExists(ScriptStore.folder().resolve(file.endsWith(".az") ? file : file + ".az"));
        }
        catch (IOException exception) {
            return false;
        }
    }

    /** Имя файла из названия правила: «Мало HP» → malo-hp.az */
    public static String fileNameFor(String ruleName) {
        StringBuilder builder = new StringBuilder();
        for (char symbol : ruleName.toLowerCase(Locale.ROOT).toCharArray()) {
            if (symbol >= 'а' && symbol <= 'я' || symbol >= 'a' && symbol <= 'z' || symbol >= '0' && symbol <= '9') {
                builder.append(symbol);
            }
            else if (builder.length() > 0 && builder.charAt(builder.length() - 1) != '-') {
                builder.append('-');
            }
        }
        String name = builder.toString().replaceAll("-+$", "");
        return (name.isEmpty() ? "rule" : name) + ".az";
    }

    /** Перечитывает все файлы в движок. Возвращает число загруженных правил. */
    public static int reload() {
        ERRORS.clear();
        ScriptEngine engine = ScriptEngine.get();
        engine.clear();
        for (String file : ScriptStore.files()) {
            ScriptEngine.ParseResult result = ScriptEngine.parse(ScriptStore.read(file));
            if (!result.ok()) {
                ERRORS.add(file + ": " + result.error());
                continue;
            }
            engine.add(result.rule());
        }
        engine.ensureSubscribed();
        return engine.rules().size();
    }

    public static List<String> errors() {
        return new ArrayList<String>(ERRORS);
    }

    /** Отчёт для чата: что загрузилось и что не разобралось. */
    public static String summary() {
        int rules = ScriptEngine.get().rules().size();
        StringBuilder builder = new StringBuilder();
        builder.append("правил ").append(rules).append(" из ").append(ScriptStore.files().size()).append(" файлов");
        if (!ERRORS.isEmpty()) {
            builder.append(", ошибок ").append(ERRORS.size()).append(": ").append(ERRORS.get(0));
        }
        return builder.toString();
    }
}
