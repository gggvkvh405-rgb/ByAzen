package rtx.byazen.api.modules.impl.Interface;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.ui.ByAzenHubScreen;
import rtx.byazen.utils.access.Accessibility;
import rtx.byazen.utils.chat.ChatMessage;

/**
 * Режим для слабовидящих (идея №195 из IDEAS.md): крупный шрифт, контраст, звук, меньше анимаций.
 */
public final class AccessibilityModule
extends Module {

    public final BooleanSetting bigFont = this.register(new BooleanSetting("Крупный шрифт",
            "Увеличить текст во всех окнах и HUD клиента.", true));
    public final SliderSetting fontSize = this.register(new SliderSetting("Размер шрифта",
            "Насколько крупнее рисовать текст: 100 % — как обычно.")
            .range(1.0f, 1.35f).increment(0.05f).setValue(1.15f).visible(() -> this.bigFont.getValue()));
    public final BooleanSetting contrast = this.register(new BooleanSetting("Высокий контраст",
            "Панели плотнее, текст ярче — легче читать на любом фоне.", true));
    public final BooleanSetting soundAlerts = this.register(new BooleanSetting("Звуковые уведомления",
            "Короткий звук на важные события: обновления, достижения, пресеты.", false));
    public final BooleanSetting reduceAnimations = this.register(new BooleanSetting("Меньше анимаций",
            "Быстрее открываются окна и меньше мельтешения.", true));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Проверить"));
    private final ButtonSetting show = this.register(new ButtonSetting("Что включено",
            "Короткая сводка режима в чат.").label("Сводка").onClick(AccessibilityModule::report));
    private final ButtonSetting hub = this.register(new ButtonSetting("Открыть хаб",
            "Проверить, как читается интерфейс.").label("Открыть хаб")
            .onClick(() -> ByAzenHubScreen.open(0)));

    public AccessibilityModule() {
        super("Accessibility", "Режим для слабовидящих: крупный шрифт, высокий контраст, звуковые уведомления.",
                Category.DISPLAY);
    }

    @Override
    protected void onEnable() {
        ChatMessage.send("§b" + Accessibility.summary());
    }

    private static void report() {
        ChatMessage.send("§bРежим для слабовидящих: §f" + Accessibility.summary());
        ChatMessage.send("§7Крупный шрифт действует в окнах и HUD; отключить — снять галочку в модуле");
    }
}

