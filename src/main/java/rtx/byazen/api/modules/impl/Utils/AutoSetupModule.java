package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.DiagnosticsScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.perf.HardwareProfile;

/**
 * Авто-настройки под железо (идея №193 из IDEAS.md).
 */
public final class AutoSetupModule
extends Module {

    public final BooleanSetting offerOnStart = this.register(new BooleanSetting("Предлагать при запуске",
            "Если профиль ещё не применяли — подсказать в чате.", true));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Подбор"));
    private final ButtonSetting show = this.register(new ButtonSetting("Показать железо",
            "Процессор, память, видеокарта и вывод по каждому пункту.").label("Железо")
            .onClick(AutoSetupModule::printHardware));
    private final ButtonSetting apply = this.register(new ButtonSetting("Подобрать настройки",
            "Применить графический пресет и (если железо слабое) облегчённый профиль Lite.")
            .label("Подобрать").onClick(AutoSetupModule::applyNow));
    private final ButtonSetting diag = this.register(new ButtonSetting("Открыть диагностику",
            "Подробно: версии, память, моды, кэши.").label("Диагностика")
            .onClick(() -> DiagnosticsScreen.open(0)));

    public AutoSetupModule() {
        super("Auto Setup", "Авто-настройки под железо: подбирает графику и профиль под ваш ПК.",
                Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        if (this.offerOnStart.getValue() && !HardwareProfile.wasAsked()) {
            ChatMessage.send("§bНастройки под ваше железо: §7" + HardwareProfile.summary());
            ChatMessage.send("§7Применить — модуль Auto Setup → кнопка «Подобрать настройки»");
        }
    }

    private static void printHardware() {
        ChatMessage.send("§bВаш компьютер:");
        for (HardwareProfile.Row row : HardwareProfile.rows()) {
            ChatMessage.send("§7" + row.name() + ": §f" + row.value() + " §8— " + row.verdict());
        }
        ChatMessage.send("§7План: " + String.join("; ", HardwareProfile.plan()));
    }

    private static void applyNow() {
        HardwareProfile.apply();
    }
}

