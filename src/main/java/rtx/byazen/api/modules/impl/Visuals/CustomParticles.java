package rtx.byazen.api.modules.impl.Visuals;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleConstants;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleTexture;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.MultiSelectSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.render.world.SimpleParticles;
import rtx.byazen.utils.world.MinimapColors;

/**
 * Свои частицы: текстуры и физика (идея №51 из IDEAS.md).
 * <p>
 * Мир наполняется мелкой жизнью: в снежных биомах кружатся снежинки, в лесу летят листья, у лавы
 * поднимаются искры, на песке висит пыль, а над водой всплывают пузыри. Стиль подбирается по
 * блокам вокруг, физика у каждого своя: снег падает, искры взлетают, пыль висит в воздухе.
 */
public final class CustomParticles
extends Module {

    private static final String STYLE_SNOW = "Снежинки";
    private static final String STYLE_LEAVES = "Листья";
    private static final String STYLE_SPARKS = "Искры";
    private static final String STYLE_DUST = "Пыль";
    private static final String STYLE_BUBBLES = "Пузыри";

    private static final String TEXTURE_SNOW = "Снежинка";
    private static final String TEXTURE_LEAF = "Ромб";
    private static final String TEXTURE_SPARK = "Искра";
    private static final String TEXTURE_DUST = "Точка";
    private static final String TEXTURE_BUBBLE = "Свечение";

    private final SeparatorSetting styleSeparator = this.register(new SeparatorSetting("Стили"));
    public final BooleanSetting auto = this.register(new BooleanSetting("Авто по окружению", "Стиль частиц подбирается по блокам вокруг: снег, листва, вода, песок, огонь.", true));
    public final MultiSelectSetting styles = this.register(new MultiSelectSetting("Стили", "Какие стили частиц разрешены, если авто-режим выключен.")
            .value(STYLE_SNOW, STYLE_LEAVES, STYLE_SPARKS, STYLE_DUST, STYLE_BUBBLES)
            .selected(STYLE_SNOW, STYLE_LEAVES, STYLE_DUST, STYLE_BUBBLES)).visible(() -> !this.auto.getValue());

    private final SeparatorSetting spawnSeparator = this.register(new SeparatorSetting("Появление"));
    public final SliderSetting frequency = this.register(new SliderSetting("Частота, в секунду", "Сколько частиц появляется каждую секунду.").range(2.0f, 80.0f).increment(2.0f).setValue(18.0f));
    public final SliderSetting radius = this.register(new SliderSetting("Радиус", "На каком расстоянии от вас появляются частицы.").range(4.0f, 32.0f).increment(1.0f).setValue(14.0f));
    public final SliderSetting height = this.register(new SliderSetting("Высота над землёй", "До какой высоты частицы поднимаются над поверхностью.").range(1.0f, 16.0f).increment(1.0f).setValue(6.0f));
    public final SliderSetting size = this.register(new SliderSetting("Размер, %", "Размер частиц.").range(40.0f, 200.0f).increment(5.0f).setValue(90.0f));
    public final SliderSetting lifetime = this.register(new SliderSetting("Время жизни, мс", "Как долго живёт частица.").range(400.0f, 4000.0f).increment(50.0f).setValue(2200.0f));

    private final SeparatorSetting colorSeparator = this.register(new SeparatorSetting("Цвета"));
    public final ColorSetting snowColor = this.register(new ColorSetting("Цвет снега", "Оттенок снежинок.", new java.awt.Color(230, 242, 255, 200)));
    public final ColorSetting leafColor = this.register(new ColorSetting("Цвет листвы", "Оттенок летящих листьев.", new java.awt.Color(140, 190, 110, 190)));
    public final ColorSetting sparkColor = this.register(new ColorSetting("Цвет искр", "Оттенок искр у огня и лавы.", new java.awt.Color(255, 190, 110, 210)));
    public final ColorSetting dustColor = this.register(new ColorSetting("Цвет пыли", "Оттенок пыли на песке и камне.", new java.awt.Color(205, 190, 160, 150)));
    public final ColorSetting bubbleColor = this.register(new ColorSetting("Цвет пузырей", "Оттенок пузырей над водой.", new java.awt.Color(170, 225, 255, 170)));
    public final BooleanSetting dustBlockColor = this.register(new BooleanSetting("Пыль в цвет блока", "Красить пыль под цвет блока, на котором она висит.", true));

    private final SimpleParticles particles = new SimpleParticles();
    private final Random random = new Random();
    private double accumulator;
    private String autoStyle = STYLE_DUST;
    private int autoStyleTicks;

    public CustomParticles() {
        super("Custom Particles", "Свои частицы мира: снежинки, листья, искры, пыль и пузыри со своей физикой.", Category.VISUALS);
    }

    @Override
    protected void onDisable() {
        this.particles.clear();
        this.accumulator = 0.0;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        this.particles.step();
        if (!this.isEnabled() || this.mc.world == null || this.mc.player == null) {
            return;
        }
        if (this.particles.size() > 900) {
            return;
        }
        if (this.auto.getValue() && this.autoStyleTicks-- <= 0) {
            this.autoStyle = this.detectStyle();
            this.autoStyleTicks = 20;
        }
        String style = this.currentStyle();
        this.accumulator += (double)this.frequency.getValue() / 20.0;
        int count = (int)Math.floor(this.accumulator);
        this.accumulator -= (double)count;
        if (count > 12) {
            count = 12;
        }
        for (int i = 0; i < count; ++i) {
            this.spawnOne(style);
        }
    }

    private String currentStyle() {
        if (this.auto.getValue()) {
            return this.autoStyle;
        }
        ArrayList<String> allowed = new ArrayList<String>();
        if (this.styles.isSelected(STYLE_SNOW)) {
            allowed.add(STYLE_SNOW);
        }
        if (this.styles.isSelected(STYLE_LEAVES)) {
            allowed.add(STYLE_LEAVES);
        }
        if (this.styles.isSelected(STYLE_SPARKS)) {
            allowed.add(STYLE_SPARKS);
        }
        if (this.styles.isSelected(STYLE_DUST)) {
            allowed.add(STYLE_DUST);
        }
        if (this.styles.isSelected(STYLE_BUBBLES)) {
            allowed.add(STYLE_BUBBLES);
        }
        if (allowed.isEmpty()) {
            return STYLE_DUST;
        }
        return allowed.get(this.random.nextInt(allowed.size()));
    }

    /** Смотрит на блоки вокруг и решает, какие частицы уместны в этой местности. */
    private String detectStyle() {
        int snow = 0;
        int leaves = 0;
        int sparks = 0;
        int dust = 0;
        int water = 0;
        double px = this.mc.player.getX();
        double pz = this.mc.player.getZ();
        int radius = Math.max(3, (int)this.radius.getValue());
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i < 12; ++i) {
            int x = (int)Math.floor(px + (this.random.nextDouble() * 2.0 - 1.0) * (double)radius);
            int z = (int)Math.floor(pz + (this.random.nextDouble() * 2.0 - 1.0) * (double)radius);
            int top = this.mc.world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
            mutable.set(x, top, z);
            BlockState ground = this.mc.world.getBlockState(mutable);
            String key = ground.getBlock().getTranslationKey();
            if (ground.isOf(Blocks.WATER) || this.mc.world.getFluidState(mutable).isIn(FluidTags.WATER)) {
                ++water;
                continue;
            }
            if (key.contains("snow") || key.contains("ice")) {
                ++snow;
                continue;
            }
            if (ground.isIn(BlockTags.LEAVES) || key.contains("leaves")) {
                ++leaves;
                continue;
            }
            if (key.contains("lava") || key.contains("magma") || key.contains("fire") || key.contains("netherrack") || key.contains("basalt")) {
                ++sparks;
                continue;
            }
            if (key.contains("sand") || key.contains("gravel") || key.contains("stone") || key.contains("dirt") || key.contains("tuff") || key.contains("clay") || key.contains("mud")) {
                ++dust;
                continue;
            }
            ++leaves;
        }
        String best = STYLE_LEAVES;
        int score = -1;
        if (water > score) {
            score = water;
            best = STYLE_BUBBLES;
        }
        if (snow > score) {
            score = snow;
            best = STYLE_SNOW;
        }
        if (leaves > score) {
            score = leaves;
            best = STYLE_LEAVES;
        }
        if (sparks > score) {
            score = sparks;
            best = STYLE_SPARKS;
        }
        if (dust > score) {
            best = STYLE_DUST;
        }
        return best;
    }

    private void spawnOne(String style) {
        double angle = this.random.nextDouble() * Math.PI * 2.0;
        double distance = Math.sqrt(this.random.nextDouble()) * (double)this.radius.getValue();
        int x = (int)Math.floor(this.mc.player.getX() + Math.cos(angle) * distance);
        int z = (int)Math.floor(this.mc.player.getZ() + Math.sin(angle) * distance);
        int top = this.mc.world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (Math.abs((double)top - this.mc.player.getY()) > Math.max(5.0, (double)this.height.getValue() + 2.0)) {
            return;
        }
        BlockPos groundPos = new BlockPos(x, top - 1, z);
        BlockState ground = this.mc.world.getBlockState(groundPos);
        int groundRgb = MinimapColors.base(ground);
        double jitterX = this.random.nextDouble() - 0.5;
        double jitterZ = this.random.nextDouble() - 0.5;
        float life = this.lifetime.getValue() * (0.7f + this.random.nextFloat() * 0.55f);
        float rotation = this.random.nextFloat() * 360.0f;
        if (style.equals(STYLE_SNOW)) {
            Vec3d pos = new Vec3d((double)x + 0.5 + jitterX, (double)top + 0.8 + this.random.nextDouble() * (double)Math.max(1.0f, this.height.getValue() * 0.6f), (double)z + 0.5 + jitterZ);
            Vec3d motion = new Vec3d(jitterX * 0.012, -0.012 - this.random.nextDouble() * 0.01, jitterZ * 0.012);
            this.particles.spawn(pos, motion, life * 1.3f, rotation, this.texture(TEXTURE_SNOW), this.snowColor.getValue(), 0.006f, 0.03f);
            return;
        }
        if (style.equals(STYLE_LEAVES)) {
            Vec3d pos = new Vec3d((double)x + 0.5 + jitterX * 1.6, (double)top + 0.4 + this.random.nextDouble() * 2.6, (double)z + 0.5 + jitterZ * 1.6);
            Vec3d motion = new Vec3d(jitterX * 0.03, -0.006, jitterZ * 0.03);
            this.particles.spawn(pos, motion, life * 1.6f, rotation, this.texture(TEXTURE_LEAF), this.leafColor.getValue(), 0.0025f, 0.2f);
            return;
        }
        if (style.equals(STYLE_SPARKS)) {
            Vec3d pos = new Vec3d((double)x + 0.5 + jitterX * 0.4, (double)top + 0.35, (double)z + 0.5 + jitterZ * 0.4);
            Vec3d motion = new Vec3d(jitterX * 0.014, 0.018 + this.random.nextDouble() * 0.03, jitterZ * 0.014);
            this.particles.spawn(pos, motion, life * 0.7f, rotation, this.texture(TEXTURE_SPARK), this.sparkColor.getValue(), -0.004f, 0.04f);
            return;
        }
        if (style.equals(STYLE_BUBBLES)) {
            Vec3d pos = new Vec3d((double)x + 0.5 + jitterX * 0.6, (double)top + 0.6 + this.random.nextDouble() * 1.4, (double)z + 0.5 + jitterZ * 0.6);
            Vec3d motion = new Vec3d(jitterX * 0.004, 0.008 + this.random.nextDouble() * 0.008, jitterZ * 0.004);
            this.particles.spawn(pos, motion, life * 0.8f, rotation, this.texture(TEXTURE_BUBBLE), this.bubbleColor.getValue(), -0.005f, 0.02f);
            return;
        }
        Vec3d pos = new Vec3d((double)x + 0.5 + jitterX * 1.4, (double)top + 0.2 + this.random.nextDouble() * 1.2, (double)z + 0.5 + jitterZ * 1.4);
        Vec3d motion = new Vec3d(jitterX * 0.02, 0.001, jitterZ * 0.02);
        int color = this.dustBlockColor.getValue()
                ? this.mix(groundRgb, this.dustColor.getValue(), 0.55f) & 0xFFFFFF | this.dustColor.getValue() & 0xFF000000
                : this.dustColor.getValue();
        this.particles.spawn(pos, motion, life * 1.9f, rotation, this.texture(TEXTURE_DUST), color, 0.0008f, 0.05f);
    }

    private int mix(int from, int to, float factor) {
        float f = Math.max(0.0f, Math.min(1.0f, factor));
        int red = Math.round((float)(from >> 16 & 0xFF) + (float)((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * f);
        int green = Math.round((float)(from >> 8 & 0xFF) + (float)((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * f);
        int blue = Math.round((float)(from & 0xFF) + (float)((to & 0xFF) - (from & 0xFF)) * f);
        return red << 16 | green << 8 | blue;
    }

    private Identifier texture(String mode) {
        for (ParticleTexture particleTexture : ParticleConstants.TEXTURES) {
            if (particleTexture.mode().equals(mode)) {
                return particleTexture.id();
            }
        }
        return ParticleConstants.TEXTURES[0].id();
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
                this.size.getValue() * 0.012f, true);
    }
}
