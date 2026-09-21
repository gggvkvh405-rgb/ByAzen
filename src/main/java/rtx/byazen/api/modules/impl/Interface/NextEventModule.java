package rtx.byazen.api.modules.impl.Interface;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.liteapi.LiteApiClient;
import rtx.byazen.api.liteapi.LiteApiEvents;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;

/**
 * Виджет «следующий ивент сервера» через LiteApi (идея №37 из IDEAS.md).
 * Пока сервер не ответил, виджет молчит или пишет «нет данных» - настраивается.
 */
public final class NextEventModule
extends InterfaceComponentModule {

    public final SeparatorSetting main = this.register(new SeparatorSetting("Следующий ивент"));
    public final BooleanSetting showWidget = this.register(new BooleanSetting("Виджет в HUD", "Показывать плашку с ближайшим ивентом сервера.", false));
    public final BooleanSetting showTime = this.register(new BooleanSetting("Обратный отсчёт", "Показывать, сколько осталось до ивента.", true).visible(this.showWidget::getValue));
    public final BooleanSetting showDetail = this.register(new BooleanSetting("Подробности", "Показывать описание ивента второй строкой.", true).visible(this.showWidget::getValue));
    public final BooleanSetting hideUnknown = this.register(new BooleanSetting("Прятать без данных", "Не показывать виджет, если сервер не сообщил об ивенте.", true).visible(this.showWidget::getValue));
    public final SliderSetting refreshSeconds = this.register(new SliderSetting("Обновление, сек", "Как часто запрашивать у сервера ближайший ивент.")
            .range(15.0f, 300.0f).increment(5.0f).setValue(60.0f).visible(this.showWidget::getValue));

    private long lastRequest;
    private boolean requestedOnEnable;

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    public NextEventModule() {
        super("Next Event", "Плашка с ближайшим ивентом сервера: данные приходят по LiteApi.");
    }

    @Override
    protected void onEnable() {
        this.requestedOnEnable = false;
        this.lastRequest = 0L;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre() || !this.isEnabled() || !this.showWidget.getValue()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) {
            return;
        }
        long now = System.currentTimeMillis();
        long period = (long)(this.refreshSeconds.getValue() * 1000.0f);
        if (!this.requestedOnEnable) {
            this.requestedOnEnable = true;
            this.lastRequest = now;
            LiteApiClient.INSTANCE.requestNextEvent();
            return;
        }
        if (now - this.lastRequest < period) {
            return;
        }
        this.lastRequest = now;
        LiteApiClient.INSTANCE.requestNextEvent();
    }

    /** Сегодняшний ивент (или null, если сервер ничего не прислал). */
    public LiteApiEvents.Snapshot event() {
        return LiteApiEvents.current();
    }
}
