package rtx.byazen.api.modules.impl.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.ByAzen;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.chat.ChatMessage;

/**
 * Логи клиента: понятные уровни, кнопка «скопировать лог» для багрепортов и открытие папки логов
 * (идея №172 из IDEAS.md).
 */
public final class LogsModule
extends Module {

    public final SeparatorSetting behaviour = this.register(new SeparatorSetting("Багрепорты"));
    public final SliderSetting tailLines = this.register(new SliderSetting("Строк для копирования", "Сколько последних строк лога копировать в буфер.").range(50, 500).increment(50).setValue(200.0f));
    public final BooleanSetting chatSummary = this.register(new BooleanSetting("Сводка в чат", "Писать в чат, где лежит лог и сколько в нём строк.", true));
    public final ButtonSetting copy = this.register(new ButtonSetting("Скопировать лог", "Копирует путь к latest.log и последние строки — можно вставлять в багрепорт.")
            .label("Скопировать").onClick(this::copyLog));
    public final ButtonSetting path = this.register(new ButtonSetting("Путь к логам", "Показать путь к папке с логами.").label("Показать").onClick(this::sendPath));

    public LogsModule() {
        super("Logs", "Логи клиента: путь к логу, копирование последних строк для багрепорта.", Category.UTILS);
    }

    public File logFile() {
        MinecraftClient client = MinecraftClient.getInstance();
        File run = client == null ? null : client.runDirectory;
        return run == null ? null : new File(new File(run, "logs"), "latest.log");
    }

    private void copyLog() {
        File file = this.logFile();
        if (file == null || !file.exists()) {
            ChatMessage.brandmessage("Файл лога ещё не создан.");
            return;
        }
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            int count = Math.min(lines.size(), (int) this.tailLines.getValue());
            StringBuilder builder = new StringBuilder();
            builder.append("ByAzen log: ").append(file.getAbsolutePath()).append('\n');
            for (int i = lines.size() - count; i < lines.size(); ++i) {
                builder.append(lines.get(i)).append('\n');
            }
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.keyboard != null) {
                client.keyboard.setClipboard(builder.toString());
            }
            ChatMessage.brandmessage("Скопировано строк лога: " + count + " — просто вставьте в багрепорт.");
        }
        catch (IOException ioException) {
            ByAzen.LOGGER.warn("[ByAzen] Log copy failed: {}", ioException.toString());
            ChatMessage.brandmessage("Не удалось прочитать лог: " + ioException.getMessage());
        }
    }

    private void sendPath() {
        File file = this.logFile();
        if (file == null) {
            return;
        }
        String path = file.getAbsolutePath();
        if (this.chatSummary.getValue()) {
            ChatMessage.brandmessage("Логи клиента: " + path);
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.keyboard != null) {
            client.keyboard.setClipboard(path);
            NotificationsModule.notify("Путь к логу скопирован", 1800L);
        }
    }
}
