package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.hints.SmartHints;

/**
 * Умные подсказки (идея №203 из IDEAS.md): клиент сам говорит, что пригодится прямо сейчас.
 */
public final class SmartHintsModule
extends Module {

    public final ModeSetting mode = this.register(new ModeSetting("Режим",
            "Что показывать: всё, только важное (мало FPS, память, обновление, конфликты) или ничего.",
            SmartHints.MODE_IMPORTANT, SmartHints.MODE_IMPORTANT, SmartHints.MODE_ALL, SmartHints.MODE_OFF));
    public final BooleanSetting toChat = this.register(new BooleanSetting("Писать в чат",
            "Дублировать подсказку строкой в чате — удобно, если уведомления скрыты."));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Подсказки"));
    private final ButtonSetting now = this.register(new ButtonSetting("Подсказать сейчас",
            "Сразу показать подсказку, которая есть на данный момент.").label("Сейчас").onClick(SmartHintsModule::showNow));
    private final ButtonSetting list = this.register(new ButtonSetting("Что доступно",
            "Список подсказок, которые клиент может выдать в этой ситуации.").label("Список").onClick(SmartHintsModule::printList));
    private final ButtonSetting forget = this.register(new ButtonSetting("Забыть историю",
            "Показывать подсказки заново, без трёхдневной паузы.").label("Сбросить").onClick(SmartHintsModule::forgetAll));

    private long lastCheck;

    public SmartHintsModule() {
        super("Smart Hints", "Ненавязчивые подсказки по ситуации: мало FPS, мало памяти, нет копии настроек, вышло обновление.",
                Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        SmartHints.ensure();
        ChatMessage.send("§bSmart Hints: §f" + SmartHints.summary());
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPost()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastCheck < 3000L) {
            return;
        }
        this.lastCheck = now;
        SmartHints.tick();
    }

    private static void showNow() {
        ChatMessage.send("§b" + SmartHints.checkNow());
    }

    private static void printList() {
        java.util.List<SmartHints.Hint> pending = SmartHints.pending();
        if (pending.isEmpty()) {
            ChatMessage.send("§7Сейчас подсказывать нечего — всё в порядке");
            return;
        }
        ChatMessage.send("§bПодсказки, доступные сейчас:");
        for (SmartHints.Hint hint : pending) {
            ChatMessage.send("§7• §f" + hint.title() + " §8— " + hint.text());
        }
    }

    private static void forgetAll() {
        SmartHints.forget();
        ChatMessage.send("§bИстория подсказок сброшена");
    }
}
