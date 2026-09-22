package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import java.util.List;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.api.ui.TutorialScreen;
import rtx.byazen.utils.help.TutorialState;

/**
 * Тур по клиенту (идея №188 из IDEAS.md) одной командой.
 */
public final class TutorialCommand
extends Command {

    public TutorialCommand() {
        super("tutorial", "Тур по клиенту: короткие шаги с кнопками", "тур", "tour");
    }

    @Override
    public void execute(String label, String[] args) {
        if (args.length > 0 && (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("сброс"))) {
            TutorialState.reset();
            this.logDirect("Тур сброшен: покажу с первого шага", Formatting.GREEN);
            return;
        }
        TutorialScreen.open();
        this.logDirect("Тур: " + TutorialState.progressText(), Formatting.GRAY);
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Открывает интерактивный тур: знакомство, модули, поиск «где найти»,",
                "хаб, диагностика, обновления.", "", "Использование:",
                "  tutorial        — открыть тур", "  tutorial reset  — начать тур заново");
    }
}

