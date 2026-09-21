package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.utils.config.ChangeLog;

/**
 * Журнал изменений настроек (идея №159 из IDEAS.md): {@code /byazen history} показывает,
 * что и когда менялось, {@code history clear} — очищает журнал.
 */
public final class HistoryCommand
extends Command {

    public HistoryCommand() {
        super("history", "Что я менял в настройках и когда", "log", "история");
    }

    @Override
    public void execute(String label, String[] args) {
        if (args.length > 0 && (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("очистить"))) {
            ChangeLog.clear();
            this.logDirect("Журнал изменений очищен.", Formatting.GRAY);
            return;
        }
        List<ChangeLog.Entry> entries = ChangeLog.entries();
        if (entries.isEmpty()) {
            this.logDirect("Журнал пуст — изменения настроек появятся здесь.", Formatting.GRAY);
            return;
        }
        String filter = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : null;
        int shown = 0;
        for (ChangeLog.Entry entry : entries) {
            if (filter != null && !entry.module().toLowerCase(Locale.ROOT).contains(filter)) {
                continue;
            }
            this.logDirect(entry.timeText() + " • " + entry.module() + " • " + entry.setting()
                    + (entry.value().isBlank() ? "" : " = " + HistoryCommand.shortValue(entry.value())), Formatting.GRAY);
            if (++shown >= 12) {
                break;
            }
        }
        if (shown == 0) {
            this.logDirect("По фильтру «" + filter + "» изменений нет.", Formatting.DARK_GRAY);
        }
    }

    private static String shortValue(String value) {
        String trimmed = value.replace('"', ' ').trim();
        return trimmed.length() > 24 ? trimmed.substring(0, 23) + "…" : trimmed;
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Показывает последние изменения настроек: что, когда и на какое значение.",
                "Журнал хранит 200 последних записей и живёт в конфиге клиента.",
                "", "Использование:",
                "  history            — последние изменения",
                "  history <модуль>   — изменения конкретного модуля",
                "  history clear      — очистить журнал");
    }
}
