package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import java.util.Random;
import rtx.kimiko.api.events.impl.render.HudRenderEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.SliderSetting;
import rtx.kimiko.mixin.accessor.MinecraftAccessor;

public final class TapeMouse
extends Module {
    private static final String LEFT = "\u041b\u041a\u041c";
    private static final String RIGHT = "\u041f\u041a\u041c";
    private final ModeSetting button = this.register(new ModeSetting("\u041a\u043d\u043e\u043f\u043a\u0430", "\u041a\u0430\u043a\u0443\u044e \u043a\u043d\u043e\u043f\u043a\u0443 \u043c\u044b\u0448\u0438 \u0430\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u043a\u043b\u0438\u043a\u0430\u0442\u044c.", "\u041b\u041a\u041c", "\u041b\u041a\u041c", "\u041f\u041a\u041c"));
    private final SliderSetting cps = this.register(new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", "\u041a\u043b\u0438\u043a\u043e\u0432 \u0432 \u0441\u0435\u043a\u0443\u043d\u0434\u0443 (CPS).").setValue(10.0f).range(1, 20).increment(1));
    private final Random random = new Random();
    private long lastClick;
    private long nextDelayMs;

    public TapeMouse() {
        super("Tape Mouse", "\u0410\u0432\u0442\u043e\u043a\u043b\u0438\u043a\u0435\u0440 \u0432\u044b\u0431\u0440\u0430\u043d\u043d\u043e\u0439 \u043a\u043d\u043e\u043f\u043a\u043e\u0439 \u043c\u044b\u0448\u0438 \u0441 \u043d\u0430\u0441\u0442\u0440\u0430\u0438\u0432\u0430\u0435\u043c\u043e\u0439 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c\u044e.", Category.UTILS);
    }

    @EventHandler
    public void onHud(HudRenderEvent hudRenderEvent) {
        if (this.mc.player == null || this.mc.world == null || this.mc.currentScreen != null || this.mc.getWindow() == null) {
            return;
        }
        long l = System.currentTimeMillis();
        if (l - this.lastClick < this.nextDelayMs) {
            return;
        }
        this.lastClick = l;
        this.nextDelayMs = this.computeDelay();
        if (this.button.is(RIGHT)) {
            ((MinecraftAccessor)(Object)this.mc).kimiko_startUseItem();
        } else {
            ((MinecraftAccessor)(Object)this.mc).kimiko_startAttack();
        }
    }

    @Override
    protected void onEnable() {
        this.lastClick = 0L;
        this.nextDelayMs = 0L;
    }

    private long computeDelay() {
        double d = 1000.0 / (double)Math.max(1, this.cps.getInt());
        double d2 = 0.8 + this.random.nextDouble() * 0.4;
        return Math.max(1L, Math.round(d * d2));
    }
}

