package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Utils.RemindersModule;

/**
 * Планировщик напоминаний (идея №103 из IDEAS.md):
 * {@code remind 30 выпить воды}, {@code remind list}, {@code remind clear}.
 */
public final class RemindCommand
extends Command {

    public RemindCommand() {
        super("remind", "Напоминания через N минут", "timer");
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
        String action = args[0].toLowerCase(Locale.ROOT);
        if (action.equals("list") || action.equals("список")) {
            List<RemindersModule.Entry> entries = module.entries();
            if (entries.isEmpty()) {
                this.logDirect("Активных напоминаний нет.", Formatting.GRAY);
                return;
            }
            for (RemindersModule.Entry entry : entries) {
                this.logDirect("через " + entry.minutesLeft() + " мин — " + entry.text + (entry.minutes > 0 ? " (поставлено на " + entry.minutes + " мин)" : ""), Formatting.GRAY);
            }
            return;
        }
        if (action.equals("clear") || action.equals("очистить")) {
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
        if (args.length < 2) {
            this.logDirect("Добавьте текст напоминания: remind " + minutes + " выпить воды", Formatting.RED);
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; ++i) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(args[i]);
        }
        module.add(minutes, builder.toString());
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Напоминает о чём угодно через заданное число минут: вода, разминка, проверка AFK.",
                "", "Использование:",
                "> remind 30 выпить воды",
                "> remind 60 размяться",
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
