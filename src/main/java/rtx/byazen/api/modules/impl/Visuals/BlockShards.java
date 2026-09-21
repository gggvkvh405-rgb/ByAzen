package rtx.byazen.api.modules.impl.Visuals;

import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.player.BlockBreakEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleConstants;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleTexturePicker;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.render.world.MinimapColors;
import rtx.byazen.utils.render.world.SimpleParticles;

/**
 * Осколки вместо стандартной текстуры при разрушении блока (идея №64 из IDEAS.md).
 * <p>
 * Блок разлетается на осколки своего же цвета: трава — зелёными, песок — тёплыми, руды — с
 * блеском акцента. Осколки мягкие, со прозрачностью и вращением, как в аккуратных шейдерах.
 */
public final class BlockShards
extends Module {

    public final SeparatorSetting main = this.register(new SeparatorSetting("Осколки"));
    public final SliderSetting count = this.register(new SliderSetting("Количество", "Сколько осколков вылетает из блока.").range(3.0f, 40.0f).increment(1.0f).setValue(14.0f));
    public final SliderSetting size = this.register(new SliderSetting("Размер, %", "Размер осколков.").range(40.0f, 200.0f).increment(5.0f).setValue(95.0f));
    public final SliderSetting lifetime = this.register(new SliderSetting("Время жизни, мс", "Как долго живут осколки.").range(200.0f, 1600.0f).increment(50.0f).setValue(650.0f));
    public final SliderSetting power = this.register(new SliderSetting("Разлёт, %", "Сила разлёта осколков.").range(20.0f, 200.0f).increment(5.0f).setValue(80.0f));
    public final SliderSetting gravity = this.register(new SliderSetting("Гравитация", "Насколько осколки падают вниз.").range(0.0f, 100.0f).increment(5.0f).setValue(55.0f));

    public final SeparatorSetting lookSeparator = this.register(new SeparatorSetting("Вид"));
    public final ModeSetting texture = this.register(new ModeSetting("Частица", "Текстура осколков.", "Ромб", ParticleTexturePicker.modeOptions()));
    public final BooleanSetting blockColor = this.register(new BooleanSetting("Цвет блока", "Красить осколки под материал блока.", true));
    public final ColorSetting color = this.register(new ColorSetting("Свой цвет", "Цвет осколков, если цвет блока выключен.", new java.awt.Color(180, 200, 255, 235)).visible(() -> !this.blockColor.getValue()));
    public final BooleanSetting dust = this.register(new BooleanSetting("Пыль", "Добавлять мягкое облако пыли на месте блока.", true));
    public final BooleanSetting oresGlow = this.register(new BooleanSetting("Блеск руды", "Руды дают осколки с оттенком акцента клиента.", true));

    private final SimpleParticles particles = new SimpleParticles();
    private final Random random = new Random();

    public BlockShards() {
        super("Block Shards", "Осколки своего цвета при разрушении блока: мягко, без пиксельных текстур.", Category.VISUALS);
    }

    @Override
    protected void onDisable() {
        this.particles.clear();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        if (!this.isEnabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null || blockBreakEvent.getState() == null) {
            return;
        }
        int tint = this.blockColor.getValue()
                ? MinimapColors.base(blockBreakEvent.getState())
                : this.color.getValue() & 0xFFFFFF;
        if (this.blockColor.getValue() && this.oresGlow.getValue()
                && blockBreakEvent.getState().getBlock().getTranslationKey().contains("_ore")) {
            tint = MinimapColors.mix(tint, rtx.byazen.api.ui.theme.ClientAccent.accentOpaque(), 0.45f);
        }
        Vec3d center = new Vec3d(blockBreakEvent.getPos().getX() + 0.5, blockBreakEvent.getPos().getY() + 0.5,
                blockBreakEvent.getPos().getZ() + 0.5);
        float spread = this.power.getValue() / 100.0f;
        float life = this.lifetime.getValue();
        int amount = (int)this.count.getValue();
        for (int i = 0; i < amount; ++i) {
            Vec3d motion = new Vec3d(
                    (this.random.nextDouble() - 0.5) * 0.18 * (double)spread,
                    this.random.nextDouble() * 0.14 * (double)spread + 0.03,
                    (this.random.nextDouble() - 0.5) * 0.18 * (double)spread);
            this.particles.spawn(center, motion, life * (0.7f + this.random.nextFloat() * 0.6f),
                    this.random.nextFloat() * 360.0f, BlockShards.pick(this.texture.getValue()),
                    BlockShards.argb(230, tint), this.gravity.getValue() / 100.0f * 0.02f, 0.015f);
        }
        if (this.dust.getValue()) {
            for (int i = 0; i < 6; ++i) {
                Vec3d motion = new Vec3d(
                        (this.random.nextDouble() - 0.5) * 0.05,
                        0.01 + this.random.nextDouble() * 0.02,
                        (this.random.nextDouble() - 0.5) * 0.05);
                this.particles.spawn(center, motion, 420.0f + this.random.nextFloat() * 260.0f,
                        this.random.nextFloat() * 360.0f, ParticleConstants.TEXTURES[0].id(),
                        BlockShards.argb(90, MinimapColors.mix(tint, 0xFFFFFF, 0.35f)), 0.0f, 0.05f);
            }
        }
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        this.particles.step();
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (!this.isEnabled() || this.particles.size() == 0) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.gameRenderer == null || client.gameRenderer.getCamera() == null) {
            return;
        }
        this.particles.render(worldRenderEvent.getStack(), client.gameRenderer.getCamera().getCameraPos(),
                client.gameRenderer.getCamera().getRotation(), (float)worldRenderEvent.getPartialTicks(),
                this.size.getValue() * 0.010f, true);
    }

    private static net.minecraft.util.Identifier pick(String mode) {
        for (rtx.byazen.api.modules.impl.Visuals.particles.ParticleTexture texture : ParticleConstants.TEXTURES) {
            if (texture.mode().equals(mode)) {
                return texture.id();
            }
        }
        return ParticleConstants.TEXTURES[0].id();
    }

    private static int argb(int alpha, int rgb) {
        int a = Math.max(0, Math.min(255, alpha));
        return a << 24 | rgb & 0xFFFFFF;
    }
}
