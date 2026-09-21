package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Utils.RemindersModule;
import rtx.byazen.utils.chat.ChatMessage;

/**
 * Планировщик напоминаний (идея №103 из IDEAS.md).
 * <p>
 * {@code remind 30 выпить воды} — напомнить через 30 минут, {@code remind list} — список,
 * {@code remind clear} — удалить все.
 */
public final class RemindCommand
extends Command {

    public RemindCommand() {
        super("remind", "Напоминания через N минут", "timer", "напомни");
    }

    private RemindersModule module() {
        return ModuleManager.get().get(RemindersModule.class);
    }

    @Override
    public void execute(String label, String[] args) {
        RemindersModule module = this.module();
        if (module == null) {
            this.logDirect("Модуль Reminders не найден.", Formatting.RED);
            return;
        }
        if (args.length == 0) {
            this.usage();
            this.logDirect("Пример: remind 30 выпить воды", Formatting.GRAY);
            return;
        }
        String first = args[0].toLowerCase(Locale.ROOT);
        if (first.equals("list") || first.equals("список")) {
            List<RemindersModule.Entry> entries = module.entries();
            if (entries.isEmpty()) {
                ChatMessage.brandmessage("Активных напоминаний нет.");
                return;
            }
            ChatMessage.brandmessage("Напоминания (" + entries.size() + "):");
            for (int i = 0; i < entries.size(); ++i) {
                RemindersModule.Entry entry = entries.get(i);
                ChatMessage.brandmessage(net.minecraft.text.Text.literal("  " + (i + 1) + ". через " + entry.minutesLeft() + " мин — " + entry.text)
                        .formatted(Formatting.GRAY));
            }
            return;
        }
        if (first.equals("clear") || first.equals("очистить")) {
            module.clear();
            return;
        }
        int minutes;
        try {
            minutes = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException numberFormatException) {
            this.logDirect("Первым аргументом должно быть число минут: remind 30 выпить воды", Formatting.RED);
            return;
        }
        StringBuilder text = new StringBuilder();
        for (int i = 1; i < args.length; ++i) {
            if (text.length() > 0) {
                text.append(' ');
            }
            text.append(args[i]);
        }
        if (text.length() == 0) {
            this.logDirect("Добавьте текст напоминания: remind " + minutes + " выпить воды", Formatting.RED);
            return;
        }
        module.add(minutes, text.toString());
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Напоминает о чём угодно через заданное число минут.",
                "", "Использование:",
                "> remind 30 выпить воды",
                "> remind 5 проверить рынок",
                "> remind list",
                "> remind clear");
    }

    @Override
    public Stream<String> tabComplete(String label, String[] args) {
        if (args.length == 1) {
            return Stream.of("15", "30", "60", "list", "clear").filter(value -> value.startsWith(args[0].toLowerCase(Locale.ROOT)));
        }
        return Stream.empty();
    }
}
