package rtx.byazen.api.modules.impl.Visuals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.MultiSelectSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.modules.settings.impl.TextSetting;
import rtx.byazen.utils.world.MinimapColors;
import rtx.byazen.utils.render.world.WorldShapeRenderer;

/**
 * Подсветка нужных блоков (идея №62 из IDEAS.md).
 * <p>
 * Сундуки, руды, рабочие станции, порталы и свои блоки получают аккуратный градиентный контур с
 * мягким переливом: цвет плавно идёт от первого оттенка ко второму, поэтому подсветка выглядит
 * живой и не спорит с ванильным выделением блока.
 */
public final class BlockHighlight
extends Module {

    private static final String WHO_ORES = "Руды";
    private static final String WHO_CONTAINERS = "Контейнеры";
    private static final String WHO_STATIONS = "Рабочие станции";
    private static final String WHO_PORTALS = "Порталы";
    private static final String WHO_CUSTOM = "Свои блоки";

    private static final String MODE_GRADIENT = "Перелив";
    private static final String MODE_PULSE = "Пульс";
    private static final String MODE_STATIC = "Статично";

    private static final String[] STATION_TOKENS = new String[]{
            "crafting_table", "furnace", "smoker", "anvil", "grindstone", "loom", "stonecutter",
            "smithing_table", "cartography_table", "fletching_table", "enchanting_table",
            "brewing_stand", "cauldron", "composter", "lectern"};

    public final SeparatorSetting whatSeparator = this.register(new SeparatorSetting("Что подсвечивать"));
    public final MultiSelectSetting what = this.register(new MultiSelectSetting("Категории", "Какие блоки подсвечивать.")
            .value(WHO_ORES, WHO_CONTAINERS, WHO_STATIONS, WHO_PORTALS, WHO_CUSTOM)
            .selected(WHO_ORES, WHO_CONTAINERS, WHO_PORTALS));
    public final TextSetting customBlocks = this.register(new TextSetting("Свои блоки", "Список блоков через запятую: beacon, diamond_block, ancient_debris.")
            .setPlaceholder("beacon, ancient_debris").visible(() -> this.what.isSelected(WHO_CUSTOM)));

    public final SeparatorSetting scanSeparator = this.register(new SeparatorSetting("Поиск"));
    public final SliderSetting searchRadius = this.register(new SliderSetting("Радиус поиска", "На каком расстоянии искать блоки.").range(8.0f, 48.0f).increment(2.0f).setValue(24.0f));
    public final SliderSetting limit = this.register(new SliderSetting("Максимум блоков", "Сколько блоков подсвечивать одновременно.").range(4.0f, 96.0f).increment(2.0f).setValue(32.0f));
    public final SliderSetting layers = this.register(new SliderSetting("Слоёв за тик", "Сколько слоёв по высоте проверять за один тик.").range(4.0f, 16.0f).increment(1.0f).setValue(6.0f));

    public final SeparatorSetting lookSeparator = this.register(new SeparatorSetting("Контур"));
    public final ColorSetting firstColor = this.register(new ColorSetting("Цвет 1", "Начало градиента контура.", new java.awt.Color(90, 190, 255, 255)));
    public final ColorSetting secondColor = this.register(new ColorSetting("Цвет 2", "Конец градиента контура.", new java.awt.Color(255, 130, 225, 255)));
    public final ModeSetting animation = this.register(new ModeSetting("Анимация", "Как ведёт себя контур.", MODE_GRADIENT, MODE_GRADIENT, MODE_PULSE, MODE_STATIC));
    public final SliderSetting speed = this.register(new SliderSetting("Скорость", "Скорость перелива или пульса.").range(0.0f, 100.0f).increment(5.0f).setValue(45.0f)).visible(() -> !this.animation.is(MODE_STATIC));
    public final SliderSetting width = this.register(new SliderSetting("Толщина", "Толщина контура.").range(1.0f, 6.0f).increment(0.5f).setValue(2.0f));
    public final BooleanSetting fill = this.register(new BooleanSetting("Заливка", "Подкрашивать блок изнутри полупрозрачным цветом.", true));
    public final SliderSetting fillAlpha = this.register(new SliderSetting("Прозрачность заливки, %", "Насколько плотная заливка блока.").range(0.0f, 60.0f).increment(2.0f).setValue(16.0f)).visible(this.fill::getValue);

    private final Map<Integer, List<BlockHighlight.Entry>> layersCache = new HashMap<Integer, List<BlockHighlight.Entry>>();
    private final List<BlockHighlight.Entry> frameEntries = new ArrayList<BlockHighlight.Entry>();
    private int scanLayer;

    public BlockHighlight() {
        super("Block Highlight", "Градиентный контур вокруг руд, контейнеров, станций, порталов и своих блоков.", Category.VISUALS);
    }

    @Override
    protected void onDisable() {
        this.layersCache.clear();
        this.frameEntries.clear();
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
        int bottom = (int)Math.floor(this.mc.player.getY()) - 12;
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

    private List<BlockHighlight.Entry> scanLayer(int y, int centerX, int centerZ, int radius) {
        ArrayList<BlockHighlight.Entry> found = new ArrayList<BlockHighlight.Entry>();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int squared = radius * radius;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                if (dx * dx + dz * dz > squared) {
                    continue;
                }
                mutable.set(centerX + dx, y, centerZ + dz);
                BlockState state = this.mc.world.getBlockState(mutable);
                if (state.isAir() || !this.matches(state)) {
                    continue;
                }
                BlockPos pos = mutable.toImmutable();
                found.add(new BlockHighlight.Entry(
                        new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5),
                        new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0)));
            }
        }
        return found;
    }

    private boolean matches(BlockState state) {
        String key = state.getBlock().getTranslationKey();
        if (this.what.isSelected(WHO_ORES) && (MinimapColors.isOre(state) || key.contains("_ore"))) {
            return true;
        }
        if (this.what.isSelected(WHO_CONTAINERS)
                && (state.isOf(Blocks.CHEST) || state.isOf(Blocks.TRAPPED_CHEST) || state.isOf(Blocks.BARREL)
                || state.isOf(Blocks.ENDER_CHEST) || key.contains("shulker_box"))) {
            return true;
        }
        if (this.what.isSelected(WHO_STATIONS)) {
            for (String token : STATION_TOKENS) {
                if (key.contains(token)) {
                    return true;
                }
            }
        }
        if (this.what.isSelected(WHO_PORTALS) && (key.contains("portal") || key.contains("end_gateway") || key.contains("respawn_anchor"))) {
            return true;
        }
        if (this.what.isSelected(WHO_CUSTOM)) {
            String custom = this.customBlocks.getValue();
            if (custom != null && !custom.isEmpty()) {
                for (String token : custom.split(",")) {
                    String clean = token.trim().toLowerCase();
                    if (clean.isEmpty()) {
                        continue;
                    }
                    if (clean.startsWith("minecraft:")) {
                        clean = clean.substring("minecraft:".length());
                    }
                    if (key.endsWith("." + clean) || key.contains(clean)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (!this.isEnabled() || this.mc.player == null || this.mc.gameRenderer == null) {
            return;
        }
        Vec3d cameraPos = worldRenderEvent.getCamera() != null
                ? worldRenderEvent.getCamera().getCameraPos()
                : this.mc.gameRenderer.getCamera().getCameraPos();
        int limit = (int)this.limit.getValue();
        this.frameEntries.clear();
        for (List<BlockHighlight.Entry> layer : this.layersCache.values()) {
            for (BlockHighlight.Entry entry : layer) {
                if (this.frameEntries.size() >= limit) {
                    break;
                }
                if (entry.center.squaredDistanceTo(cameraPos) > 4096.0) {
                    continue;
                }
                this.frameEntries.add(entry);
            }
        }
        if (this.frameEntries.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        float speed = this.speed.getValue() / 100.0f;
        int first = this.firstColor.getValue() & 0xFFFFFF;
        int second = this.secondColor.getValue() & 0xFFFFFF;
        float outlineWidth = this.width.getValue();
        int fillAlpha = this.fill.getValue() ? Math.round(this.fillAlpha.getValue() / 100.0f * 255.0f) : 0;
        int index = 0;
        for (BlockHighlight.Entry entry : this.frameEntries) {
            float t;
            if (this.animation.is(MODE_STATIC)) {
                t = 0.35f;
            }
            else if (this.animation.is(MODE_PULSE)) {
                t = 0.5f + 0.5f * (float)Math.sin((double)now * 0.0016 * (double)speed);
            }
            else {
                t = 0.5f + 0.5f * (float)Math.sin((double)now * 0.0016 * (double)speed + (double)index * 0.55);
            }
            int rgb = BlockHighlight.mix(first, second, t);
            int outline = 0xFF000000 | rgb;
            int inside = fillAlpha <= 0 ? 0 : fillAlpha << 24 | rgb;
            WorldShapeRenderer.boxes(this.mc.getBufferBuilders().getEntityVertexConsumers(), worldRenderEvent.getStack(),
                    cameraPos, List.of(entry.box), inside, outline, Math.max(0.5f, outlineWidth));
            ++index;
        }
    }

    private static int mix(int from, int to, float factor) {
        float f = Math.max(0.0f, Math.min(1.0f, factor));
        int red = Math.round((float)(from >> 16 & 0xFF) + (float)((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * f);
        int green = Math.round((float)(from >> 8 & 0xFF) + (float)((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * f);
        int blue = Math.round((float)(from & 0xFF) + (float)((to & 0xFF) - (from & 0xFF)) * f);
        return red << 16 | green << 8 | blue;
    }

    /** Найденный блок: центр и его бокс для контура. */
    private static final class Entry {

        private final Vec3d center;
        private final Box box;

        private Entry(Vec3d vec3d, Box box) {
            this.center = vec3d;
            this.box = box;
        }
    }
}
