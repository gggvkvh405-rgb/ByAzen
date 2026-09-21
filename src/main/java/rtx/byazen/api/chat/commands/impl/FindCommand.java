package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import java.util.List;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.api.ui.UI;
import rtx.byazen.api.ui.settings.SettingsSearch;

/**
 * «Где это найти» (идея №189 из IDEAS.md): {@code /byazen find прыжок} открывает ClickGui,
 * переходит в нужную категорию и подсвечивает модуль, а если совпала настройка — открывает её.
 */
public final class FindCommand
extends Command {

    public FindCommand() {
        super("find", "Найти модуль или настройку и показать, где это", "где", "search");
    }

    @Override
    public void execute(String label, String[] args) {
        if (args.length == 0) {
            this.usage();
            this.logDirect("Пример: find прицел", Formatting.GRAY);
            return;
        }
        String query = String.join(" ", args);
        List<SettingsSearch.Entry> results = SettingsSearch.search(query, 5);
        if (results.isEmpty()) {
            this.logDirect("Ничего не нашлось по запросу «" + query + "».", Formatting.RED);
            return;
        }
        SettingsSearch.Entry best = results.get(0);
        this.logDirect("Нашёл: " + best.module().getDisplayName()
                + (best.settingName() == null ? "" : " → " + best.settingName()), Formatting.GRAY);
        if (results.size() > 1) {
            StringBuilder others = new StringBuilder("Ещё: ");
            for (int i = 1; i < results.size(); ++i) {
                if (i > 1) {
                    others.append(", ");
                }
                others.append(results.get(i).module().getDisplayName());
            }
            this.logDirect(others.toString(), Formatting.DARK_GRAY);
        }
        UI.focusModuleInGui(best.module());
        if (best.settingName() != null) {
            UI.openModuleSettings(best.module(), best.settingName());
        }
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Ищет модуль или настройку по названию и открывает её прямо в ClickGui,",
                "подсвечивая нужную строку. Удобно, когда нужно быстро что-то выключить, но лень искать.",
                "", "Использование:",
                "  find <текст>   — найти модуль или настройку");
    }
}
