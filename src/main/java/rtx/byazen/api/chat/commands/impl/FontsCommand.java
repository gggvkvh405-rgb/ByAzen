package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.FontsModule;
import rtx.byazen.utils.render.render2d.font.CustomFonts;

/**
 * Свои шрифты из чата (идея №49 из IDEAS.md):
 * {@code /byazen fonts}, {@code /byazen fonts reload}, {@code /byazen fonts use MyFont}.
 */
public final class FontsCommand
extends Command {

    public FontsCommand() {
        super("fonts", "Свои шрифты: список, перезагрузка папки, выбор шрифта", "шрифты", "font");
    }

    @Override
    public void execute(String label, String[] args) {
        FontsModule module = ModuleManager.get().get(FontsModule.class);
        if (args.length == 0) {
            this.print();
            if (module != null && !module.isEnabled()) {
                this.logDirect("Модуль Fonts выключен — включите его, чтобы шрифт применился.", Formatting.GRAY);
            }
            return;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("reload") || sub.equals("обновить")) {
            if (module == null) {
                this.logDirect("Модуль Fonts не найден.", Formatting.RED);
                return;
            }
            module.reload();
            this.print();
            return;
        }
        if (sub.equals("use") || sub.equals("выбрать")) {
            if (module == null) {
                this.logDirect("Модуль Fonts не найден.", Formatting.RED);
                return;
            }
            if (args.length < 2) {
                this.usage();
                return;
            }
            String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            String[] names = CustomFonts.names();
            boolean found = false;
            for (String candidate : names) {
                if (candidate.equalsIgnoreCase(name)) {
                    name = candidate;
                    found = true;
                    break;
                }
            }
            if (!found) {
                this.logDirect("Такого шрифта нет. Доступные: " + String.join(", ", names), Formatting.RED);
                return;
            }
            module.font.value(names).selected(name);
            CustomFonts.setOverride(name);
            this.logDirect("Шрифт интерфейса: " + name, Formatting.GREEN);
            return;
        }
        this.usage();
    }

    private void print() {
        String[] names = CustomFonts.names();
        this.logDirect("Шрифты из .minecraft/byazen/fonts:", Formatting.GRAY);
        for (int i = 0; i < names.length; ++i) {
            String note = i == 0 ? "как в клиенте" : CustomFonts.note(names[i]);
            this.logDirect(" • " + names[i] + (note.isBlank() ? "" : " (" + note + ")"), Formatting.GRAY);
        }
        String[] rejected = CustomFonts.rejected();
        if (rejected.length > 0) {
            this.logDirect("Отклонены (пиксельные или битые): " + String.join(", ", rejected), Formatting.DARK_GRAY);
        }
    }
}
