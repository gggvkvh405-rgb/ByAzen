package rtx.byazen.api.modules.impl.Visuals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.MultiSelectSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.render.post.bloom.BloomRenderer;
import rtx.byazen.utils.world.MinimapColors;

/**
 * Bloom — свечение для эмиссивных объектов (идея №53 из IDEAS.md).
 * <p>
 * Лава, порталы, кристаллы, руды, факелы и огонь получают мягкое радиальное свечение с плавным
 * «дыханием» и необязательными искрами. Скан блоков идёт волной по слоям, поэтому радиус поиска
 * не роняет FPS.
 */
public final class Bloom
extends Module {

    private static final String WHO_LAVA = "Лава и магма";
    private static final String WHO_PORTALS = "Порталы";
    private static final String WHO_CRYSTALS = "Кристаллы";
    private static final String WHO_ORES = "Руды";
    private static final String WHO_LIGHTS = "Факелы и фонари";
    private static final String WHO_FIRE = "Огонь";

    public final SeparatorSetting glowSeparator = this.register(new SeparatorSetting("Свечение"));
    public final SliderSetting strength = this.register(new SliderSetting("Сила, %", "Насколько яркое свечение вокруг блока.").range(10.0f, 100.0f).increment(2.0f).setValue(42.0f));
    public final SliderSetting radius = this.register(new SliderSetting("Радиус, %", "Размер ореола вокруг блока.").range(40.0f, 300.0f).increment(5.0f).setValue(110.0f));
    public final SliderSetting pulse = this.register(new SliderSetting("Пульс", "Скорость плавного «дыхания» свечения, 0 — ровный свет.").range(0.0f, 100.0f).increment(5.0f).setValue(35.0f));
    public final BooleanSetting sparks = this.register(new BooleanSetting("Искры", "Добавлять к свечению лёгкие блики-искры.", false));

    public final SeparatorSetting scanSeparator = this.register(new SeparatorSetting("Поиск"));
    public final SliderSetting searchRadius = this.register(new SliderSetting("Радиус поиска", "В каком радиусе искать светящиеся блоки.").range(6.0f, 40.0f).increment(2.0f).setValue(18.0f));
    public final SliderSetting limit = this.register(new SliderSetting("Максимум блоков", "Сколько блоков светится одновременно.").range(8.0f, 200.0f).increment(4.0f).setValue(72.0f));
    public final SliderSetting layers = this.register(new SliderSetting("Слоёв за тик", "Сколько слоёв по высоте проверять за один тик.").range(4.0f, 24.0f).increment(1.0f).setValue(10.0f));

    public final SeparatorSetting whatSeparator = this.register(new SeparatorSetting("Что светится"));
    public final MultiSelectSetting what = this.register(new MultiSelectSetting("Категории", "Какие блоки получают свечение.").value(WHO_LAVA, WHO_PORTALS, WHO_CRYSTALS, WHO_ORES, WHO_LIGHTS, WHO_FIRE).selected(WHO_LAVA, WHO_PORTALS, WHO_CRYSTALS, WHO_ORES, WHO_LIGHTS));
    public final ColorSetting lavaColor = this.register(new ColorSetting("Цвет лавы", "Оттенок свечения лавы и магмы.", new java.awt.Color(255, 138, 60, 255)));
    public final ColorSetting portalColor = this.register(new ColorSetting("Цвет порталов", "Оттенок свечения порталов и якорей.", new java.awt.Color(178, 110, 255, 255)));
    public final ColorSetting crystalColor = this.register(new ColorSetting("Цвет кристаллов", "Оттенок свечения аметиста и кристаллов.", new java.awt.Color(168, 140, 255, 255)));
    public final ColorSetting oreColor = this.register(new ColorSetting("Цвет руд", "Оттенок свечения руд.", new java.awt.Color(255, 226, 150, 255)));
    public final BooleanSetting oreBlockColor = this.register(new BooleanSetting("Руды в цвет блока", "Красить свечение руды под её собственный цвет.", true));
    public final ColorSetting lightColor = this.register(new ColorSetting("Цвет источников света", "Оттенок свечения факелов, фонарей и фонарных блоков.", new java.awt.Color(255, 226, 176, 255)));
    public final ColorSetting fireColor = this.register(new ColorSetting("Цвет огня", "Оттенок свечения огня и грим-факелов.", new java.awt.Color(255, 170, 70, 255)));

    private final Map<Integer, List<BloomRenderer.Point>> layersCache = new HashMap<Integer, List<BloomRenderer.Point>>();
    private final List<BloomRenderer.Point> framePoints = new ArrayList<BloomRenderer.Point>();
    private int scanLayer;

    public Bloom() {
        super("Bloom", "Мягкое свечение вокруг лавы, порталов, кристаллов, руд и источников света.", Category.VISUALS);
    }

    @Override
    protected void onDisable() {
        this.layersCache.clear();
        this.framePoints.clear();
        BloomRenderer.clear();
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        if (!this.isEnabled() || this.mc.world == null || this.mc.player == null) {
            this.layersCache.clear();
            return;
        }
        int radius = (int)this.searchRadius.getValue();
        int centerX = (int)Math.floor(this.mc.player.getX());
        int centerZ = (int)Math.floor(this.mc.player.getZ());
        int bottom = (int)Math.floor(this.mc.player.getY()) - 8;
        int total = 25;
        int perTick = Math.max(1, (int)this.layers.getValue());
        for (int step = 0; step < perTick; ++step) {
            if (this.scanLayer >= total) {
                this.scanLayer = 0;
            }
            int y = bottom + this.scanLayer;
            this.layersCache.put(y, this.scanLayer(y, centerX, centerZ, radius));
            ++this.scanLayer;
        }
        int low = bottom - 8;
        int high = bottom + total + 8;
        this.layersCache.keySet().removeIf(key -> key < low || key > high);
    }

    private List<BloomRenderer.Point> scanLayer(int y, int centerX, int centerZ, int radius) {
        ArrayList<BloomRenderer.Point> found = new ArrayList<BloomRenderer.Point>();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int squared = radius * radius;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                if (dx * dx + dz * dz > squared) {
                    continue;
                }
                mutable.set(centerX + dx, y, centerZ + dz);
                BlockState state = this.mc.world.getBlockState(mutable);
                if (state.isAir()) {
                    continue;
                }
                String key = state.getBlock().getTranslationKey();
                int index = Bloom.category(key, state, this.what);
                if (index < 0) {
                    continue;
                }
                found.add(new BloomRenderer.Point(
                        new Vec3d((double)mutable.getX() + 0.5, (double)mutable.getY() + 0.5, (double)mutable.getZ() + 0.5),
                        Bloom.sizeOf(index), this.colorOf(index, state), (float)((mutable.getX() * 31 + mutable.getZ() * 17) % 360) * 0.0175f));
            }
        }
        return found;
    }

    /** Индекс категории свечения или -1, если блок не светится. */
    private static int category(String key, BlockState state, MultiSelectSetting what) {
        if (what.isSelected(WHO_LAVA) && (key.contains("lava") || key.contains("magma"))) {
            return 0;
        }
        if (what.isSelected(WHO_PORTALS) && (key.contains("portal") || key.contains("respawn_anchor") || key.contains("crying_obsidian"))) {
            return 1;
        }
        if (what.isSelected(WHO_CRYSTALS) && (key.contains("amethyst") || key.contains("crystal"))) {
            return 2;
        }
        if (what.isSelected(WHO_ORES) && (MinimapColors.isOre(state) || key.contains("_ore"))) {
            return 3;
        }
        if (what.isSelected(WHO_LIGHTS) && (key.contains("torch") || key.contains("lantern") || key.contains("campfire") || key.contains("glowstone") || key.contains("shroomlight") || key.contains("sea_lantern") || key.contains("beacon") || key.contains("end_rod") || key.contains("froglight") || key.contains("redstone_lamp") || key.contains("jack_o_lantern") || key.contains("candle"))) {
            return 4;
        }
        if (what.isSelected(WHO_FIRE) && (key.contains("fire") || key.contains("sculk") || key.contains("furnace"))) {
            return 5;
        }
        return -1;
    }

    private static float sizeOf(int index) {
        switch (index) {
            case 3: {
                return 0.72f;
            }
            case 2: {
                return 0.8f;
            }
            case 5: {
                return 0.85f;
            }
        }
        return 1.05f;
    }

    private int colorOf(int index, BlockState state) {
        switch (index) {
            case 0: {
                return this.lavaColor.getValue() & 0xFFFFFF;
            }
            case 1: {
                return this.portalColor.getValue() & 0xFFFFFF;
            }
            case 2: {
                return this.crystalColor.getValue() & 0xFFFFFF;
            }
            case 3: {
                int ore = this.oreColor.getValue() & 0xFFFFFF;
                if (this.oreBlockColor.getValue()) {
                    return BloomRenderer.mix(MinimapColors.base(state), ore, 0.45f);
                }
                return ore;
            }
            case 4: {
                return this.lightColor.getValue() & 0xFFFFFF;
            }
        }
        return this.fireColor.getValue() & 0xFFFFFF;
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (!this.isEnabled() || this.mc.world == null || this.mc.player == null || this.mc.gameRenderer == null) {
            return;
        }
        Vec3d cameraPos = worldRenderEvent.getCamera() != null
                ? worldRenderEvent.getCamera().getCameraPos()
                : this.mc.gameRenderer.getCamera().getCameraPos();
        int limit = (int)this.limit.getValue();
        this.framePoints.clear();
        for (List<BloomRenderer.Point> layer : this.layersCache.values()) {
            for (BloomRenderer.Point point : layer) {
                if (this.framePoints.size() >= limit) {
                    break;
                }
                if (point.center.squaredDistanceTo(cameraPos) > 2304.0) {
                    continue;
                }
                this.framePoints.add(point);
            }
        }
        if (this.framePoints.isEmpty()) {
            return;
        }
        float baseSize = this.radius.getValue() / 100.0f * 1.15f;
        float strength = this.strength.getValue() / 100.0f * 255.0f;
        BloomRenderer.render(this.framePoints, worldRenderEvent.getStack(), cameraPos,
                this.mc.gameRenderer.getCamera().getRotation(), baseSize, strength, this.pulse.getValue(),
                this.sparks.getValue());
    }
}
