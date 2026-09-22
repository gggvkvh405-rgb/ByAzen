package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import java.util.List;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.api.ui.ByAzenHubScreen;
import rtx.byazen.utils.achievements.Achievements;
import rtx.byazen.utils.missions.CoopMissions;
import rtx.byazen.utils.season.SeasonEvents;

/**
 * Хаб ByAzen одной командой: миссии, достижения, события, голосование.
 */
public final class HubCommand
extends Command {

    public HubCommand() {
        super("hub", "Хаб ByAzen: миссии, достижения, события, голосование и профиль", "хаб");
    }

    @Override
    public void execute(String label, String[] args) {
        if (args.length > 0 && (args[0].equalsIgnoreCase("итог") || args[0].equalsIgnoreCase("status"))) {
            this.logDirect("Хаб ByAzen:", Formatting.GRAY);
            this.logDirect("§7" + Achievements.profile());
            this.logDirect("§7" + CoopMissions.summary());
            this.logDirect("§7" + SeasonEvents.summary());
            return;
        }
        ByAzenHubScreen.open(0);
        this.logDirect("Открываю хаб ByAzen", Formatting.GRAY);
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Открывает хаб клиента: кооп-миссии, достижения, сезонные события,",
                "голосование за косметику и профиль.", "", "Использование:",
                "  hub        — открыть хаб", "  hub итог   — краткая сводка в чат");
    }
}

