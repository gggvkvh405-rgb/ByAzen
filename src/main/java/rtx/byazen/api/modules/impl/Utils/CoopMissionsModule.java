package rtx.byazen.api.modules.impl.Utils;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.ByAzenHubScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.missions.CoopMissions;

/**
 * Кооп-миссии (идея №183 из IDEAS.md): челленджи для игры с друзьями.
 */
public final class CoopMissionsModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Миссии"));
    private final ButtonSetting hub = this.register(new ButtonSetting("Открыть хаб", "Миссии, достижения, события и голосование в одном окне.").label("Хаб").onClick(() -> ByAzenHubScreen.open(0)));
    private final ButtonSetting code = this.register(new ButtonSetting("Код для друзей", "Скопировать прогресс, чтобы сложить его с друзьями.").label("Код").onClick(CoopMissionsModule::copyCode));
    private final ButtonSetting accept = this.register(new ButtonSetting("Принять код друга", "Взять код из буфера обмена и сложить прогресс.").label("Принять").onClick(CoopMissionsModule::acceptCode));
    private final ButtonSetting chat = this.register(new ButtonSetting("Список в чат", "Показать миссии и прогресс.").label("В чат").onClick(CoopMissionsModule::printRows));
    private final ButtonSetting reset = this.register(new ButtonSetting("Сбросить прогресс", "Начать миссии заново.").label("Сбросить").onClick(CoopMissionsModule::resetAll));

    public CoopMissionsModule() {
        super("Coop Missions", "Кооп-миссии: вместе пройти, добыть, победить — с наградой косметикой.", Category.UTILS);
    }

    @Override
    protected void onEnable() {
        CoopMissions.ensure();
        ChatMessage.send("§bКооп-миссии: §f" + CoopMissions.summary());
    }

    private static void copyCode() {
        CoopMissions.ensure();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.keyboard.setClipboard(CoopMissions.code());
        }
        ChatMessage.send("§bКод миссий скопирован — отправьте его другу");
    }

    private static void acceptCode() {
        MinecraftClient client = MinecraftClient.getInstance();
        String code = client == null ? "" : client.keyboard.getClipboard();
        ChatMessage.send("§b" + CoopMissions.applyCode(code));
    }

    private static void printRows() {
        CoopMissions.ensure();
        ChatMessage.send("§bКооп-миссии ByAzen:");
        for (String line : CoopMissions.rows()) {
            ChatMessage.send("§7" + line);
        }
    }

    private static void resetAll() {
        CoopMissions.reset();
        ChatMessage.send("§bПрогресс миссий сброшен");
    }
}
