package rtx.byazen.api.modules.impl.Visuals;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.render.HudRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BindSetting;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Кинематографический режим (идея №60 из IDEAS.md): мягкая виньетка, полосы кино,
 * деликатное зерно плёнки и плавное включение клавишей. Без резких рамок и без пиксельных эффектов.
 */
public final class Cinematic
extends Module {

    public final SeparatorSetting main = this.register(new SeparatorSetting("Кадр"));
    public final BooleanSetting vignette = this.register(new BooleanSetting("Виньетка", "Аккуратно затемняет края кадра, как объектив камеры.", true));
    public final SliderSetting vignettePower = this.register(new SliderSetting("Сила виньетки, %", "Насколько заметны края кадра.").range(5.0f, 80.0f).increment(5.0f).setValue(28.0f).visible(this.vignette::getValue));
    public final BooleanSetting bars = this.register(new BooleanSetting("Полосы кино", "Чёрные полосы сверху и снизу: широкий кадр, как в кино.", false));
    public final SliderSetting barsHeight = this.register(new SliderSetting("Высота полос, px", "Толщина полос сверху и снизу.").range(12.0f, 140.0f).increment(2.0f).setValue(56.0f).visible(this.bars::getValue));

    public final SeparatorSetting grainSeparator = this.register(new SeparatorSetting("Плёнка"));
    public final BooleanSetting grain = this.register(new BooleanSetting("Зерно плёнки", "Очень лёгкая шероховатость кадра. Деликатно, по умолчанию выключено.", false));
    public final SliderSetting grainPower = this.register(new SliderSetting("Плотность зерна, %", "Насколько заметно зерно.").range(3.0f, 30.0f).increment(1.0f).setValue(10.0f).visible(this.grain::getValue));

    public final SeparatorSetting controlSeparator = this.register(new SeparatorSetting("Управление"));
    public final BindSetting toggleKey = this.register(new BindSetting("Кино-режим", "Клавиша мгновенно включает и выключает полосы кино."));
    public final SliderSetting fadeSpeed = this.register(new SliderSetting("Плавность", "Как мягко появляется и исчезает эффект.").range(0.5f, 5.0f).increment(0.1f).setValue(2.0f));

    private float amount;
    private boolean keyHeld;
    private boolean quickBars;
    private float grainClock;

    public Cinematic() {
        super("Cinematic", "Кино-режим: виньетка, полосы кадра, лёгкое зерно и плавные переходы.", Category.VISUALS);
    }

    public boolean barsActive() {
        return this.quickBars || this.bars.getValue();
    }

    @Override
    protected void onDisable() {
        this.amount = 0.0f;
        this.quickBars = false;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre() || !this.isEnabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.currentScreen != null) {
            this.keyHeld = false;
            return;
        }
        boolean down = this.toggleKey.isBound() && this.toggleKey.getValue().isDown(client.getWindow().getHandle());
        if (down && !this.keyHeld) {
            this.keyHeld = true;
            this.quickBars = !this.quickBars;
            ChatMessage.brandmessage(this.quickBars ? "Кино-режим включён" : "Кино-режим выключен");
            return;
        }
        if (!down) {
            this.keyHeld = false;
        }
    }

    @EventHandler
    public void onHudRender(HudRenderEvent hudRenderEvent) {
        boolean active = this.isEnabled() && (this.vignette.getValue() || this.barsActive() || this.grain.getValue());
        float target = active ? 1.0f : 0.0f;
        float speed = Math.max(0.2f, this.fadeSpeed.getValue());
        float step = 0.05f * speed;
        if (this.amount < target) {
            this.amount = Math.min(target, this.amount + step);
        }
        else if (this.amount > target) {
            this.amount = Math.max(target, this.amount - step);
        }
        if (this.amount <= 0.01f) {
            return;
        }
        float width = Position.screenWidth();
        float height = Position.screenHeight();
        this.grainClock += 1.0f;
        Render2D.beginFrame(hudRenderEvent.getGraphics());
        if (this.vignette.getValue()) {
            float power = this.vignettePower.getValue() / 100.0f * this.amount;
            float thickness = Math.min(width, height) * 0.42f;
            int solid = Cinematic.alpha(power * 255.0f);
            Render2D.rect(0.0f, 0.0f, width, thickness, 0.0f, Cinematic.argb(solid, 0), 0, Cinematic.argb(solid, 0), 0);
            Render2D.rect(0.0f, height - thickness, width, thickness, 0.0f, 0, Cinematic.argb(solid, 0), 0, Cinematic.argb(solid, 0));
            Render2D.rect(0.0f, 0.0f, thickness, height, 0.0f, Cinematic.argb(solid, 0), Cinematic.argb(solid, 0), 0, 0);
            Render2D.rect(width - thickness, 0.0f, thickness, height, 0.0f, 0, 0, Cinematic.argb(solid, 0), Cinematic.argb(solid, 0));
        }
        if (this.grain.getValue()) {
            this.drawGrain(width, height);
        }
        if (this.barsActive()) {
            float barHeight = this.barsHeight.getValue() * this.amount;
            int black = Cinematic.alpha(240.0f * this.amount) << 24;
            Render2D.rect(0.0f, 0.0f, width, barHeight, 0.0f, black);
            Render2D.rect(0.0f, height - barHeight, width, barHeight, 0.0f, black);
        }
        Render2D.flush();
    }

    /** Зерно: множество очень прозрачных точек, распределённых по кадру. */
    private void drawGrain(float width, float height) {
        float power = this.grainPower.getValue() / 100.0f * this.amount;
        if (power <= 0.001f) {
            return;
        }
        int count = 260;
        long seed = (long)(this.grainClock * 0.5f) * 7919L;
        int alpha = Cinematic.alpha(power * 60.0f);
        for (int i = 0; i < count; ++i) {
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            float x = (float)((seed >>> 12 & 0xFFFF) / 65535.0) * width;
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            float y = (float)((seed >>> 12 & 0xFFFF) / 65535.0) * height;
            int light = (i & 1) == 0 ? 0xFFFFFF : 0x000000;
            Render2D.rect(x, y, 1.3f, 1.3f, 0.6f, Cinematic.argb(alpha, light));
        }
    }

    private static int alpha(float value) {
        return Math.max(0, Math.min(255, Math.round(value)));
    }

    private static int argb(int alpha, int rgb) {
        return alpha << 24 | rgb & 0xFFFFFF;
    }
}
