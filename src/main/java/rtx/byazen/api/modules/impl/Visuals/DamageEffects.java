package rtx.byazen.api.modules.impl.Visuals;

import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.render.HudRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.drags.Position;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Аккуратные пост-эффекты урона (идея №75 из IDEAS.md).
 * <p>
 * Вместо резкой красной рамки — мягкий градиент по краям экрана, который плавно проявляется при
 * уроне и при низком здоровье, плюс деликатный указатель направления, откуда прилетело.
 */
public final class DamageEffects
extends Module {

    public final SeparatorSetting main = this.register(new SeparatorSetting("Виньетка"));
    public final BooleanSetting enabledHint = this.register(new BooleanSetting("Мягкая виньетка", "Затемнять края экрана при уроне и низком здоровье.", true));
    public final SliderSetting strength = this.register(new SliderSetting("Плотность, %", "Насколько плотные края экрана. Градиент всегда мягкий, без рамок.").range(10.0f, 100.0f).increment(5.0f).setValue(55.0f).visible(this.enabledHint::getValue));
    public final SliderSetting fadeSpeed = this.register(new SliderSetting("Скорость проявления", "Как быстро эффект появляется и уходит.").range(0.5f, 6.0f).increment(0.1f).setValue(2.4f).visible(this.enabledHint::getValue));
    public final ColorSetting color = this.register(new ColorSetting("Цвет", "Цвет краёв экрана.", new Color(120, 16, 28, 255)).visible(this.enabledHint::getValue));

    public final SeparatorSetting lowSeparator = this.register(new SeparatorSetting("Низкое здоровье"));
    public final BooleanSetting lowHealth = this.register(new BooleanSetting("Пульс на сердце", "Когда здоровья мало, края дышат в ритме сердца.", true));
    public final SliderSetting threshold = this.register(new SliderSetting("Порог, %", "С какого здоровья включается пульс.").range(5.0f, 80.0f).increment(1.0f).setValue(35.0f).visible(this.lowHealth::getValue));
    public final SliderSetting pulse = this.register(new SliderSetting("Период пульса, сек", "Как часто бьётся пульс.").range(0.4f, 3.0f).increment(0.1f).setValue(1.1f).visible(this.lowHealth::getValue));

    public final SeparatorSetting directionSeparator = this.register(new SeparatorSetting("Направление урона"));
    public final BooleanSetting direction = this.register(new BooleanSetting("Показывать, откуда прилетело", "Деликатная дуга со стороны источника урона.", true));
    public final SliderSetting directionTime = this.register(new SliderSetting("Время показа, мс", "Сколько держится указатель направления.").range(200.0f, 2500.0f).increment(100.0f).setValue(1100.0f).visible(this.direction::getValue));
    public final BooleanSetting playersOnly = this.register(new BooleanSetting("Только от игроков", "Показывать направление только для урона от игроков.", false).visible(this.direction::getValue));

    private float vignette;
    private float directionAmount;
    private float directionAngle;
    private long directionUntil;
    private float lastHealth = -1.0f;
    private float pulseClock;

    public DamageEffects() {
        super("Damage Effects", "Мягкая виньетка урона, пульс при низком здоровье и указатель направления.", Category.VISUALS);
    }

    @Override
    protected void onDisable() {
        this.vignette = 0.0f;
        this.directionAmount = 0.0f;
        this.lastHealth = -1.0f;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            this.lastHealth = -1.0f;
            return;
        }
        float health = client.player.getHealth() + client.player.getAbsorptionAmount();
        if (this.lastHealth < 0.0f) {
            this.lastHealth = health;
            return;
        }
        if (health < this.lastHealth - 0.01f) {
            this.rememberDirection(client);
        }
        this.lastHealth = health;
    }

    private void rememberDirection(MinecraftClient client) {
        if (!this.direction.getValue()) {
            return;
        }
        Entity attacker = client.player.getAttacker();
        if (attacker == null) {
            return;
        }
        if (this.playersOnly.getValue() && !(attacker instanceof PlayerEntity)) {
            return;
        }
        double dx = attacker.getX() - client.player.getX();
        double dz = attacker.getZ() - client.player.getZ();
        if (dx * dx + dz * dz < 1.0E-4) {
            return;
        }
        float playerYaw = client.player.getYaw();
        double attackerYaw = Math.toDegrees(Math.atan2(-dx, dz));
        this.directionAngle = (float)(attackerYaw - (double)playerYaw);
        this.directionUntil = System.currentTimeMillis() + (long)this.directionTime.getValue();
    }

    @EventHandler
    public void onHudRender(HudRenderEvent hudRenderEvent) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }
        float delta = 0.05f;
        float width = Position.screenWidth();
        float height = Position.screenHeight();
        float maxHealth = Math.max(1.0f, client.player.getMaxHealth());
        float health = client.player.getHealth() + client.player.getAbsorptionAmount();
        float healthRatio = Math.max(0.0f, Math.min(1.0f, health / maxHealth));
        float damageBoost = 0.0f;
        float target = 0.0f;
        if (this.enabledHint.getValue()) {
            float base = this.strength.getValue() / 100.0f;
            target = base * 0.35f;
            if (this.lowHealth.getValue() && healthRatio * 100.0f <= this.threshold.getValue()) {
                this.pulseClock += delta;
                float period = Math.max(0.2f, this.pulse.getValue());
                float beat = (float)Math.abs(Math.sin(Math.PI * (double)this.pulseClock / (double)period));
                float scaled = base * (0.75f + 0.45f * beat);
                target = Math.max(target, scaled * (1.0f - healthRatio * 0.4f));
            }
            if (client.player.hurtTime > 0) {
                damageBoost = base * 0.7f * ((float)client.player.hurtTime / 10.0f);
                target = Math.max(target, base * 0.8f + damageBoost);
            }
        }
        else {
            this.pulseClock = 0.0f;
        }
        float speed = Math.max(0.2f, this.fadeSpeed.getValue());
        this.vignette += (target - this.vignette) * Math.min(1.0f, delta * 20.0f * speed);
        boolean showDirection = this.direction.getValue() && System.currentTimeMillis() < this.directionUntil;
        this.directionAmount += ((showDirection ? 1.0f : 0.0f) - this.directionAmount) * Math.min(1.0f, delta * 14.0f);
        if (this.vignette <= 0.01f && this.directionAmount <= 0.01f) {
            return;
        }
        Render2D.beginFrame(hudRenderEvent.getGraphics());
        if (this.vignette > 0.01f) {
            this.drawVignette(width, height, this.vignette);
        }
        if (this.directionAmount > 0.01f) {
            this.drawDirection(width, height, this.directionAmount);
        }
        Render2D.flush();
    }

    /** Мягкие края: четыре градиентные полосы с прозрачной серединой. */
    private void drawVignette(float width, float height, float amount) {
        int rgb = this.color.getValue() & 0xFFFFFF;
        float thickness = Math.min(width, height) * 0.34f;
        int solid = DamageEffects.alpha(amount * 235.0f);
        int clear = rgb & 0xFFFFFF;
        Render2D.rect(0.0f, 0.0f, width, thickness, 0.0f,
                DamageEffects.argb(solid, rgb), DamageEffects.argb(clear, rgb), DamageEffects.argb(solid, rgb), DamageEffects.argb(clear, rgb));
        Render2D.rect(0.0f, height - thickness, width, thickness, 0.0f,
                DamageEffects.argb(clear, rgb), DamageEffects.argb(solid, rgb), DamageEffects.argb(clear, rgb), DamageEffects.argb(solid, rgb));
        Render2D.rect(0.0f, 0.0f, thickness, height, 0.0f,
                DamageEffects.argb(solid, rgb), DamageEffects.argb(solid, rgb), DamageEffects.argb(clear, rgb), DamageEffects.argb(clear, rgb));
        Render2D.rect(width - thickness, 0.0f, thickness, height, 0.0f,
                DamageEffects.argb(clear, rgb), DamageEffects.argb(clear, rgb), DamageEffects.argb(solid, rgb), DamageEffects.argb(solid, rgb));
    }

    /** Дуга со стороны источника урона: несколько коротких штрихов по окружности. */
    private void drawDirection(float width, float height, float amount) {
        float centerX = width * 0.5f;
        float centerY = height * 0.5f;
        float radius = Math.min(width, height) * 0.32f;
        float angle = (float)Math.toRadians(this.directionAngle - 90.0f);
        int color = DamageEffects.argb(Math.round(amount * 200.0f), 0xFF5A6A);
        int strokes = 7;
        float spread = 0.55f;
        for (int i = 0; i < strokes; ++i) {
            float offset = ((float)i / (float)(strokes - 1) - 0.5f) * spread;
            float current = angle + offset;
            float inner = radius - 26.0f - Math.abs(offset) * 30.0f;
            float outer = radius + 6.0f;
            Render2D.line(centerX + (float)Math.cos(current) * inner, centerY + (float)Math.sin(current) * inner,
                    centerX + (float)Math.cos(current) * outer, centerY + (float)Math.sin(current) * outer,
                    3.0f, color);
        }
    }

    private static int alpha(float value) {
        return Math.max(0, Math.min(255, Math.round(value)));
    }

    private static int argb(int alpha, int rgb) {
        return alpha << 24 | rgb & 0xFFFFFF;
    }
}
