package rtx.byazen.api.modules.impl.Utils;

import java.nio.file.Files;
import java.util.Locale;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.scripts.ScriptCatalog;
import rtx.byazen.utils.scripts.ScriptEngine;
import rtx.byazen.utils.scripts.ScriptStore;

/**
 * Правила и мини-скрипты клиента (идея №177 из IDEAS.md).
 * <p>
 * Вместо встраивания сторонних интерпретаторов — собственный безопасный язык правил: условия из
 * состояния игры (HP, голод, FPS, бой, огонь, ночь, дождь) и действия из белого списка
 * (уведомление, звук, сообщение, модуль, музыка). Файлы правил лежат в {@code ByAzen/scripts},
 * «магазин» из десяти готовых наборов встроен в клиент и работает без интернета.
 */
public final class ScriptsModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Правила"));
    private final ButtonSetting reload = this.register(new ButtonSetting("Перечитать папку", "Загрузить правила из ByAzen/scripts заново.").label("Перечитать").onClick(ScriptsModule::reload));
    private final ButtonSetting shop = this.register(new ButtonSetting("Установить наборы", "Положить в папку все готовые правила из магазина.").label("Магазин").onClick(ScriptsModule::installAll));
    private final ButtonSetting list = this.register(new ButtonSetting("Список правил в чат", "Показать загруженные правила и их условия.").label("Список").onClick(ScriptsModule::printList));
    private final ButtonSetting errors = this.register(new ButtonSetting("Ошибки разбора", "Показать файлы, которые не удалось прочитать.").label("Ошибки").onClick(ScriptsModule::printErrors));
    private final ButtonSetting folder = this.register(new ButtonSetting("Открыть папку правил", "Проводник с файлами правил.").label("Папка").onClick(ScriptsModule::openFolder));

    public ScriptsModule() {
        super("Scripts", "Свои правила: «если HP ниже 8 — напомни». Безопасный мини-язык и магазин наборов.", Category.UTILS);
    }

    @Override
    protected void onEnable() {
        int loaded = ScriptStore.reload();
        ClientLog.info("правила: " + ScriptStore.summary());
        ChatMessage.send("§bПравила загружены: §f" + loaded + " §7(" + ScriptStore.folder() + ")");
    }

    @Override
    protected void onDisable() {
        ScriptEngine.get().clear();
    }

    private static void reload() {
        int loaded = ScriptStore.reload();
        ChatMessage.send("§bПравила: §f" + loaded + " §7загружено · " + ScriptStore.summary());
    }

    private static void installAll() {
        int installed = ScriptCatalog.installMissing();
        ChatMessage.send(installed == 0 ? "§7Все готовые наборы уже на месте" : "§bУстановлено наборов: §f" + installed);
    }

    private static void printList() {
        ScriptStore.reload();
        if (ScriptEngine.get().rules().isEmpty()) {
            ChatMessage.send("§7Правил нет — нажмите «Магазин», чтобы поставить готовые");
            return;
        }
        ChatMessage.send("§bПравила клиента (" + ScriptEngine.get().rules().size() + "):");
        for (ScriptEngine.Rule rule : ScriptEngine.get().rules()) {
            ChatMessage.send("§7• " + (rule.enabled ? "§aвкл §7" : "§cвыкл §7") + "§f" + rule.name
                    + " §7— " + rule.describe() + " §8(откат " + rule.cooldownMs / 1000L + " с)");
        }
    }

    private static void printErrors() {
        if (ScriptStore.errors().isEmpty()) {
            ChatMessage.send("§aОшибок разбора нет");
            return;
        }
        for (String error : ScriptStore.errors()) {
            ChatMessage.send("§c" + error);
        }
    }

    private static void openFolder() {
        try {
            Files.createDirectories(ScriptStore.folder());
            String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
            ProcessBuilder builder = os.contains("win")
                    ? new ProcessBuilder("explorer.exe", ScriptStore.folder().toAbsolutePath().toString())
                    : os.contains("mac")
                            ? new ProcessBuilder("open", ScriptStore.folder().toAbsolutePath().toString())
                            : new ProcessBuilder("xdg-open", ScriptStore.folder().toAbsolutePath().toString());
            builder.start();
            return;
        }
        catch (Throwable ignored) {
        }
        ChatMessage.send("§7Папка правил: §f" + ScriptStore.folder());
    }
}
