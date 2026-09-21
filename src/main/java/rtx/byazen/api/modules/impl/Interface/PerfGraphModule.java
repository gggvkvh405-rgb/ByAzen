package rtx.byazen.api.modules.impl.Interface;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.impl.Interface.InterfaceComponentModule;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;

/**
 * Мониторинг FPS и статтеров: график за последние минуты, средний FPS и «1% low»
 * (идеи №117 и №118 из IDEAS.md). График показывается отдельным HUD-виджетом.
 */
public final class PerfGraphModule
extends InterfaceComponentModule {

    private static final int MAX_SAMPLES = 120;

    public final SeparatorSetting behaviour = this.register(new SeparatorSetting("График"));
    public final BooleanSetting showWidget = this.register(new BooleanSetting("Виджет в HUD", "Показывать график FPS на экране.", true));
    public final SliderSetting window = this.register(new SliderSetting("Окно, сек", "Сколько секунд показывать на графике.").range(30, 120).increment(10).setValue(60.0f));
    public final BooleanSetting showLow = this.register(new BooleanSetting("Показывать 1% low", "Средний FPS в худших кадрах — главный показатель статтеров.", true));
    public final BooleanSetting chatNotice = this.register(new BooleanSetting("Предупреждать о просадках", "Писать в чат, если FPS надолго падает ниже половины обычного.", false));
    public final ButtonSetting reset = this.register(new ButtonSetting("Сбросить статистику", "Начать собирать заново.").label("Сбросить").onClick(this::reset));

    private final float[] samples = new float[MAX_SAMPLES];
    private int count;
    private int head;
    private int ticks;
    private long lastNotice;
    private int frequentFps = 60;

    public PerfGraphModule() {
        super("Perf Graph", "График FPS и мониторинг статтеров: средний FPS, «1% low» и картинка за последние минуты.");
    }

    /** Средний FPS за окно наблюдения. */
    public float average() {
        int total = Math.min(this.count, (int) this.window.getValue());
        if (total <= 0) {
            return 0.0f;
        }
        float sum = 0.0f;
        for (int i = 0; i < total; ++i) {
            sum += this.sample(i);
        }
        return sum / (float) total;
    }

    /** Средний FPS в худших 1 % секунд — показатель статтеров. */
    public float low() {
        int total = Math.min(this.count, (int) this.window.getValue());
        if (total <= 1) {
            return this.average();
        }
        float[] copy = new float[total];
        for (int i = 0; i < total; ++i) {
            copy[i] = this.sample(i);
        }
        java.util.Arrays.sort(copy);
        int worst = Math.max(1, total / 100);
        float sum = 0.0f;
        for (int i = 0; i < worst; ++i) {
            sum += copy[i];
        }
        return sum / (float) worst;
    }

    /** Образец графика: 0 — самый старый доступный, дальше по времени вперёд. */
    public float sample(int index) {
        int total = Math.min(this.count, MAX_SAMPLES);
        int start = (this.head - total + MAX_SAMPLES) % MAX_SAMPLES;
        return this.samples[(start + index) % MAX_SAMPLES];
    }

    /** Насколько «заполнено» окно наблюдения (для отрисовки). */
    public int sampleCount() {
        return Math.min(this.count, (int) this.window.getValue());
    }

    public void reset() {
        this.count = 0;
        this.head = 0;
        this.samples[0] = 0.0f;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre() || !this.isEnabled()) {
            return;
        }
        if (++this.ticks < 20) {
            return;
        }
        this.ticks = 0;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        float fps = client.getCurrentFps();
        this.samples[this.head] = fps;
        this.head = (this.head + 1) % MAX_SAMPLES;
        if (this.count < MAX_SAMPLES) {
            ++this.count;
        }
        if (fps > this.frequentFps) {
            this.frequentFps = (int) fps;
        }
        else {
            this.frequentFps = Math.max((int) fps, (int) ((float) this.frequentFps * 0.995f));
        }
        if (this.chatNotice.getValue() && this.frequentFps > 40 && fps < (float) this.frequentFps * 0.5f) {
            long now = System.currentTimeMillis();
            if (now - this.lastNotice > 60000L) {
                this.lastNotice = now;
                rtx.byazen.utils.chat.ChatMessage.brandmessage("Просадка FPS: " + (int) fps + " (обычно " + this.frequentFps + ") — откройте Perf Graph, чтобы посмотреть график.");
            }
        }
    }

    @Override
    protected void onDisable() {
        this.reset();
    }
}
