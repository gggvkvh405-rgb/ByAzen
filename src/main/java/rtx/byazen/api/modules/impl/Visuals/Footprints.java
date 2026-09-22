package rtx.byazen.api.modules.impl.Visuals;

import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.impl.Visuals.CustomPet;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.SelectSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.cosmetics.FootprintTrail;

/**
 * Следы под ногами (идея №130 из IDEAS.md).
 * <p>
 * На земле остаются мягкие светящиеся отпечатки: они чередуются левая-правая, разворачиваются по
 * ходу движения и постепенно растворяются. Есть готовые цвета под наборы ByAzen, размер, шаг и
 * время жизни — можно сделать и лёгкий след на прогулку, и яркую дорожку для красивого скриншота.
 */
public final class Footprints
extends Module {

    private static final String COLOR_CUSTOM = "Свой";
    private static final String COLOR_SNOW = "Зима";
    private static final String COLOR_NEON = "Неон";
    private static final String COLOR_SPACE = "Космос";

    private final SliderSetting size = this.register(new SliderSetting("Размер", "Размер отпечатка.").range(0.4f, 2.6f).increment(0.1f).setValue(1.1f));
    private final SliderSetting spacing = this.register(new SliderSetting("Шаг", "Через сколько блоков ставить следующий след.").range(0.4f, 2.5f).increment(0.1f).setValue(0.85f));
    private final SliderSetting lifetime = this.register(new SliderSetting("Время жизни, с", "Как долго след держится на земле.").range(1.0f, 30.0f).increment(0.5f).setValue(6.0f));
    private final SelectSetting colorMode = this.register(new SelectSetting("Цвет", "Готовый оттенок набора или свой.")
            .value(COLOR_CUSTOM, COLOR_SNOW, COLOR_NEON, COLOR_SPACE).selected(COLOR_NEON));
    private final ColorSetting color = this.register(new ColorSetting("Свой цвет", "Цвет отпечатка.").value(0x7FE3FF).visible(() -> COLOR_CUSTOM.equals(this.colorMode.getSelected())));
    private final BooleanSetting glow = this.register(new BooleanSetting("Свечение", "Добавляет мягкий блик вокруг следа.").setValue(true));
    private final BooleanSetting others = this.register(new BooleanSetting("Следы других", "Показывать следы других игроков тоже.").setValue(true));
    private final BooleanSetting pet = this.register(new BooleanSetting("След питомца", "Питомец ByAzen тоже оставляет отпечатки.").setValue(false));

    private long lastTick;

    public Footprints() {
        super("Footprints", "Светящиеся следы под ногами: размер, шаг, цвет и время жизни.", Category.VISUALS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    protected void onDisable() {
        FootprintTrail.clear();
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (!event.isPre() || this.mc.player == null || this.mc.world == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastTick < 40L) {
            return;
        }
        this.lastTick = now;
        int tint = this.tint();
        FootprintTrail.update(this.mc.player, now, this.spacing.getFloat(), tint);
        if (this.pet.getValue()) {
            CustomPet customPet = CustomPet.getInstance();
            if (customPet != null && customPet.localPet() != null) {
                FootprintTrail.update(customPet.localPet(), now, this.spacing.getFloat(), tint);
            }
        }
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        FootprintTrail.render(event, this.mc, (long)(this.lifetime.getFloat() * 1000.0f), this.size.getFloat(),
                this.glow.getValue(), this.others.getValue(), this.tint());
    }

    private int tint() {
        String selected = this.colorMode.getSelected();
        if (COLOR_SNOW.equals(selected)) {
            return 0xBFE8FF;
        }
        if (COLOR_SPACE.equals(selected)) {
            return 0xA98BFF;
        }
        if (COLOR_NEON.equals(selected)) {
            return 0x7FF3C8;
        }
        return this.color.getColor();
    }
}
