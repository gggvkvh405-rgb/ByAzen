package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.ByAzenHubScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.profiles.BuildTier;

/**
 * Сборки Lite и Full (идея №187 из IDEAS.md): облегчённый профиль в один клик.
 */
public final class BuildTierModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Сборка"));
    private final ButtonSetting lite = this.register(new ButtonSetting("Профиль Lite", "Выключить тяжёлые визуалы и урезать кэши — для слабых ПК.").label("Lite").onClick(BuildTierModule::applyLite));
    private final ButtonSetting full = this.register(new ButtonSetting("Профиль Full", "Вернуть обычные кэши; визуалы включайте по вкусу.").label("Full").onClick(BuildTierModule::applyFull));
    private final ButtonSetting list = this.register(new ButtonSetting("Тяжёлые модули", "Что именно отключает Lite и почему.").label("Список").onClick(BuildTierModule::printRows));
    private final ButtonSetting hub = this.register(new ButtonSetting("Открыть хаб", "Профиль и статистика клиента.").label("Хаб").onClick(() -> ByAzenHubScreen.open(4)));

    public BuildTierModule() {
        super("Build Tier", "Lite-профиль для слабых ПК: тяжёлые визуалы выключены, кэши урезаны.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        BuildTier.hintIfLite();
        ChatMessage.send("§b" + BuildTier.summary());
    }

    private static void applyLite() {
        BuildTier.applyLite();
    }

    private static void applyFull() {
        ChatMessage.send("§b" + BuildTier.applyFull());
    }

    private static void printRows() {
        for (String line : BuildTier.rows()) {
            ChatMessage.send(line.startsWith("§") ? line : "§7" + line);
        }
    }
}
