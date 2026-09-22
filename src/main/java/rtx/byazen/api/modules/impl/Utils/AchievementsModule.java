package rtx.byazen.api.modules.impl.Utils;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.ByAzenHubScreen;
import rtx.byazen.utils.achievements.Achievements;
import rtx.byazen.utils.chat.ChatMessage;

/**
 * Достижения клиента (идея №185 из IDEAS.md): что вы уже сделали в ByAzen.
 */
public final class AchievementsModule
extends Module {

    private final BooleanSetting notify = this.register(new BooleanSetting("Уведомлять о новых", "Всплывающее уведомление и строка в чате при получении достижения."));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Достижения"));
    private final ButtonSetting hub = this.register(new ButtonSetting("Открыть хаб", "Профиль, прогресс и список достижений.").label("Хаб").onClick(() -> ByAzenHubScreen.open(1)));
    private final ButtonSetting chat = this.register(new ButtonSetting("Список в чат", "Все достижения с прогрессом.").label("В чат").onClick(AchievementsModule::printRows));
    private final ButtonSetting copy = this.register(new ButtonSetting("Скопировать профиль", "Уровень, очки и счёт по категориям.").label("Профиль").onClick(AchievementsModule::copyProfile));
    private final ButtonSetting reset = this.register(new ButtonSetting("Сбросить", "Начать собирать достижения заново.").label("Сбросить").onClick(AchievementsModule::resetAll));

    public AchievementsModule() {
        super("Achievements", "Достижения за игру и функции клиента: 30 целей, очки, уровень, косметика в награду.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        Achievements.ensure();
    }

    public boolean wantsNotify() {
        return this.notify.getValue();
    }

    private static void printRows() {
        Achievements.ensure();
        ChatMessage.send("§bДостижения ByAzen: §f" + Achievements.profile());
        for (String line : Achievements.rows()) {
            ChatMessage.send("§7" + line);
        }
    }

    private static void copyProfile() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.keyboard.setClipboard(Achievements.shareText());
        }
        ChatMessage.send("§bПрофиль скопирован в буфер");
    }

    private static void resetAll() {
        Achievements.reset();
        ChatMessage.send("§bДостижения сброшены — можно заработать заново");
    }
}
