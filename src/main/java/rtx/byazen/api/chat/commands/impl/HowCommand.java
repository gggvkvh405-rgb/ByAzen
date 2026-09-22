package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.utils.help.ModuleHelp;

/**
 * Кнопка «как это работает» из чата (идея №197 из IDEAS.md).
 * <p>
 * {@code .how <модуль>} рассказывает, что модуль делает и как им пользоваться, {@code .how}
 * без аргумента показывает список модулей по разделам.
 */
public final class HowCommand
extends Command {

    public HowCommand() {
        super("how", "Как это работает: инструкция по модулю", "как", "help-module");
    }

    @Override
    public void execute(String label, String[] args) {
        if (args.length == 0) {
            this.logDirect("Как это работает: напишите .how <часть названия модуля>", Formatting.GRAY);
            for (String line : ModuleHelp.allModules()) {
                this.logDirect(line);
            }
            return;
        }
        String query = String.join(" ", args);
        Module module = ModuleManager.get().findByName(query);
        if (module == null) {
            module = ModuleHelp.find(query);
        }
        if (module == null) {
            this.logDirect("Модуль «" + query + "» не найден. Попробуйте .find " + query, Formatting.RED);
            return;
        }
        for (String line : ModuleHelp.full(module)) {
            this.logDirect(line);
        }
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Показывает, как работает модуль: что делает, где искать, что настроить.",
                "", "Использование:", "  how            — список модулей по разделам",
                "  how <модуль>   — инструкция по модулю");
    }

    @Override
    public Stream<String> tabComplete(String label, String[] args) {
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            return ModuleManager.get().getAll().stream()
                    .map(Module::getDisplayName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(prefix));
        }
        return Stream.empty();
    }
}

