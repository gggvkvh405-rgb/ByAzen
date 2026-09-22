package rtx.byazen.api.modules.impl.Visuals;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
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
import rtx.byazen.utils.render.world.WorldShapeRenderer;

/**
 * Предсказание траекторий (идея №63 из IDEAS.md).
 * <p>
 * Модуль показывает то, что случится с блоком дальше: куда растечётся вода и лава, что толкнёт
 * поршень, где пойдёт сигнал редстоуна и куда упадёт песок. Предсказания рисуются сглаженными
 * полупрозрачными боксами и мягкими линиями, яркость подсказывает силу или направление.
 */
public final class Trajectories
extends Module {

    private static final String WHO_FLUIDS = "Вода и лава";
    private static final String WHO_PISTONS = "Поршни";
    private static final String WHO_REDSTONE = "Редстоун";
    private static final String WHO_FALLING = "Падающий песок";

    private static final int MAX_FLOOD = 900;

    private final SeparatorSetting mainSeparator = this.register(new SeparatorSetting("Предсказания"));
    public final MultiSelectSetting what = this.register(new MultiSelectSetting("Что показывать", "Какие предсказания рисовать.")
            .value(WHO_FLUIDS, WHO_PISTONS, WHO_REDSTONE, WHO_FALLING)
            .selected(WHO_FLUIDS, WHO_PISTONS, WHO_FALLING));

    private final SeparatorSetting scanSeparator = this.register(new SeparatorSetting("Поиск"));
    public final SliderSetting radius = this.register(new SliderSetting("Радиус поиска", "В каком радиусе искать блоки для предсказания.", 20.0f, 6.0f, 48.0f, 2.0f));
    public final SliderSetting limit = this.register(new SliderSetting("Максимум блоков", "Сколько блоков показывать одновременно.", 48.0f, 8.0f, 200.0f, 4.0f));
    public final SliderSetting layers = this.register(new SliderSetting("Слоёв за тик", "Сколько слоёв по высоте проверять за один тик.", 6.0f, 1.0f, 16.0f, 1.0f));

    private final SeparatorSetting lookSeparator = this.register(new SeparatorSetting("Вид"));
    public final SliderSetting opacity = this.register(new SliderSetting("Прозрачность, %", "Насколько плотные подсказки.", 42.0f, 10.0f, 85.0f, 1.0f));
    public final BooleanSetting pulse = this.register(new BooleanSetting("Пульс", "Подсказки плавно дышат, чтобы не сливаться с миром.", true));
    public final ColorSetting fluidColor = this.register(new ColorSetting("Цвет воды", "Оттенок предсказания для воды.", new java.awt.Color(90, 170, 255, 255)));
    public final ColorSetting lavaColor = this.register(new ColorSetting("Цвет лавы", "Оттенок предсказания для лавы.", new java.awt.Color(255, 138, 60, 255)));
    public final ColorSetting pistonColor = this.register(new ColorSetting("Цвет поршней", "Оттенок предсказания для поршней.", new java.awt.Color(210, 190, 255, 255)));
    public final ColorSetting redstoneColor = this.register(new ColorSetting("Цвет редстоуна", "Оттенок цепочек редстоуна: чем сильнее сигнал, тем ярче.", new java.awt.Color(255, 70, 70, 255)));
    public final ColorSetting fallingColor = this.register(new ColorSetting("Цвет песка", "Оттенок предсказания падающих блоков.", new java.awt.Color(226, 205, 150, 255)));

    private final Map<Integer, List<HintBox>> layerCache = new HashMap<Integer, List<HintBox>>();
    private final List<HintBox> frameHints = new ArrayList<HintBox>();
    private final Map<BlockState, Direction> facingCache = new IdentityHashMap<BlockState, Direction>();
    private int scanLayer;

    public Trajectories() {
        super("Trajectories", "Куда растечётся вода, что толкнёт поршень, куда пойдёт редстоун и как упадёт песок.", Category.VISUALS);
    }

    @Override
    protected void onDisable() {
        this.layerCache.clear();
        this.frameHints.clear();
        this.facingCache.clear();
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        if (!this.isEnabled() || this.mc.world == null || this.mc.player == null) {
            this.layerCache.clear();
            return;
        }
        int range = (int)this.radius.getValue();
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
            this.layerCache.put(y, this.scanLayer(y, centerX, centerZ, range));
            ++this.scanLayer;
        }
        int low = bottom - 8;
        int high = bottom + total + 8;
        this.layerCache.keySet().removeIf(key -> key < low || key > high);
    }

    private List<HintBox> scanLayer(int y, int centerX, int centerZ, int range) {
        ArrayList<HintBox> hints = new ArrayList<HintBox>();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int dx = -range; dx <= range; ++dx) {
            for (int dz = -range; dz <= range; ++dz) {
                if (dx * dx + dz * dz > range * range) {
                    continue;
                }
                mutable.set(centerX + dx, y, centerZ + dz);
                BlockState state = this.mc.world.getBlockState(mutable);
                if (state.isAir()) {
                    continue;
                }
                BlockPos pos = mutable.toImmutable();
                this.inspect(hints, pos, state);
            }
        }
        return hints;
    }

    private void inspect(List<HintBox> hints, BlockPos pos, BlockState state) {
        String key = state.getBlock().getTranslationKey();
        if (this.what.isSelected(WHO_FALLING) && Trajectories.isFallingBlock(key)) {
            this.predictFall(hints, pos);
            return;
        }
        if (this.what.isSelected(WHO_PISTONS) && Trajectories.isPiston(key)) {
            this.predictPiston(hints, pos, state);
            return;
        }
        if (this.what.isSelected(WHO_REDSTONE) && key.contains("redstone_wire")) {
            this.predictRedstoneNetwork(hints, pos, state);
            return;
        }
        if (this.what.isSelected(WHO_FLUIDS) && Trajectories.isFluid(key, state)) {
            this.predictFlood(hints, pos, key.contains("lava"));
        }
    }

    private static boolean isFallingBlock(String key) {
        return key.contains("sand") && !key.contains("sandstone") || key.contains("gravel") || key.contains("concrete_powder");
    }

    private static boolean isPiston(String key) {
        return key.contains("piston") && !key.contains("piston_head") && !key.contains("sticky_piston_head");
    }

    private static boolean isFluid(String key, BlockState state) {
        return state.isOf(Blocks.WATER) || state.isOf(Blocks.LAVA) || key.endsWith(".water") || key.endsWith(".lava");
    }

    /** Куда упадёт песок или гравий: путь вниз до первой твёрдой опоры. */
    private void predictFall(List<HintBox> hints, BlockPos pos) {
        BlockPos.Mutable probe = new BlockPos.Mutable(pos.getX(), pos.getY() - 1, pos.getZ());
        if (!this.mc.world.getBlockState(probe).isAir()) {
            return;
        }
        int drop = 0;
        while (drop < 24 && probe.getY() > this.mc.world.getBottomY()) {
            if (!this.mc.world.getBlockState(probe).isAir()) {
                break;
            }
            probe.setY(probe.getY() - 1);
            ++drop;
        }
        if (drop <= 0) {
            return;
        }
        int color = this.fallingColor.getValue() & 0xFFFFFF;
        hints.add(new HintBox(new Box(pos.getX() + 0.06, probe.getY() + 1, pos.getZ() + 0.06,
                pos.getX() + 0.94, probe.getY() + 1 + drop, pos.getZ() + 0.94), color, 0.55f));
        hints.add(new HintBox(new Box(pos.getX(), probe.getY() + 1, pos.getZ(),
                pos.getX() + 1.0, probe.getY() + 2.0, pos.getZ() + 1.0), color, 1.0f));
    }

    /** Что толкнёт поршень: блок перед ним и место, куда он уедет. */
    private void predictPiston(List<HintBox> hints, BlockPos pos, BlockState state) {
        Direction facing = this.facingOf(state);
        if (facing == null) {
            return;
        }
        BlockPos target = pos.offset(facing);
        BlockPos landing = target.offset(facing);
        BlockState pushed = this.mc.world.getBlockState(target);
        if (pushed.isAir()) {
            return;
        }
        BlockState destination = this.mc.world.getBlockState(landing);
        int color = this.pistonColor.getValue() & 0xFFFFFF;
        hints.add(new HintBox(new Box(target.getX() + 0.02, target.getY() + 0.02, target.getZ() + 0.02,
                target.getX() + 0.98, target.getY() + 0.98, target.getZ() + 0.98), color, 1.0f));
        if (destination.isAir()) {
            hints.add(new HintBox(new Box(landing.getX() + 0.08, landing.getY() + 0.08, landing.getZ() + 0.08,
                    landing.getX() + 0.92, landing.getY() + 0.92, landing.getZ() + 0.92), color, 0.4f));
            int steps = 6;
            for (int i = 1; i < steps; ++i) {
                double t = (double)i / (double)steps;
                double x = (double)target.getX() + 0.5 + (double)facing.getOffsetX() * t;
                double z = (double)target.getZ() + 0.5 + (double)facing.getOffsetZ() * t;
                double y = (double)target.getY() + 0.5;
                hints.add(new HintBox(new Box(x - 0.06, y - 0.06, z - 0.06, x + 0.06, y + 0.06, z + 0.06), color, 0.7f));
            }
        }
    }

    /** Цепочка редстоуна: соединения между проводами, яркость — сила сигнала. */
    private void predictRedstoneNetwork(List<HintBox> hints, BlockPos pos, BlockState state) {
        int power = this.powerOf(state);
        float strength = 0.25f + (float)power / 15.0f * 0.75f;
        int color = this.redstoneColor.getValue() & 0xFFFFFF;
        for (Direction direction : Direction.values()) {
            if (direction.getOffsetY() != 0) {
                continue;
            }
            BlockPos neighbor = pos.offset(direction);
            BlockState other = this.mc.world.getBlockState(neighbor);
            if (!other.getBlock().getTranslationKey().contains("redstone_wire")) {
                continue;
            }
            double x = (double)pos.getX() + 0.5 + (double)direction.getOffsetX() * 0.5;
            double z = (double)pos.getZ() + 0.5 + (double)direction.getOffsetZ() * 0.5;
            double y = (double)pos.getY() + 0.06;
            hints.add(new HintBox(new Box(x - 0.09 * (double)direction.getOffsetZ() - 0.06, y, z - 0.09 * (double)direction.getOffsetX() - 0.06,
                    x + 0.09 * (double)direction.getOffsetZ() + 0.06, y + 0.1, z + 0.09 * (double)direction.getOffsetX() + 0.06),
                    color, strength));
        }
    }

    /** Разлив воды или лавы: волна по воздуху от источника. */
    private void predictFlood(List<HintBox> hints, BlockPos source, boolean lava) {
        int range = (int)this.radius.getValue();
        ArrayDeque<BlockPos> queue = new ArrayDeque<BlockPos>();
        HashMap<Long, Integer> distance = new HashMap<Long, Integer>();
        queue.add(source);
        distance.put(Trajectories.key(source), 0);
        int visited = 0;
        int maxDistance = lava ? 3 : 6;
        while (!queue.isEmpty() && visited < MAX_FLOOD) {
            BlockPos current = queue.poll();
            Integer step = distance.get(Trajectories.key(current));
            if (step == null) {
                continue;
            }
            ++visited;
            if (step >= maxDistance) {
                continue;
            }
            for (Direction direction : Direction.values()) {
                BlockPos next = current.offset(direction);
                if (Math.abs(next.getX() - source.getX()) > range || Math.abs(next.getZ() - source.getZ()) > range) {
                    continue;
                }
                long nextKey = Trajectories.key(next);
                if (distance.containsKey(nextKey)) {
                    continue;
                }
                BlockState nextState = this.mc.world.getBlockState(next);
                if (!nextState.isAir()) {
                    continue;
                }
                distance.put(nextKey, step + 1);
                if (next.getY() <= source.getY()) {
                    queue.add(next);
                }
            }
        }
        int color = (lava ? this.lavaColor.getValue() : this.fluidColor.getValue()) & 0xFFFFFF;
        distance.remove(Trajectories.key(source));
        for (Map.Entry<Long, Integer> entry : distance.entrySet()) {
            BlockPos pos = Trajectories.unkey(entry.getKey());
            float fade = 1.0f - (float)entry.getValue() / (float)(maxDistance + 1);
            hints.add(new HintBox(new Box(pos.getX() + 0.05, pos.getY() + 0.02, pos.getZ() + 0.05,
                    pos.getX() + 0.95, pos.getY() + 0.1 + (double)fade * 0.16, pos.getZ() + 0.95),
                    color, Math.max(0.18f, fade)));
        }
    }

    private static long key(BlockPos pos) {
        return (long)(pos.getX() & 0x3FFFFFF) << 38 | (long)(pos.getY() & 0xFFF) << 26 | (long)(pos.getZ() & 0x3FFFFFF);
    }

    private static BlockPos unkey(long value) {
        int x = (int)(value >> 38 & 0x3FFFFFFL);
        int y = (int)(value >> 26 & 0xFFFL);
        int z = (int)(value & 0x3FFFFFFL);
        return new BlockPos(x >= 0x2000000 ? x - 0x4000000 : x, y, z >= 0x2000000 ? z - 0x4000000 : z);
    }

    /** Направление поршня читается из свойств состояния — без привязки к конкретному классу блока. */
    private Direction facingOf(BlockState state) {
        Direction cached = this.facingCache.get(state);
        if (cached != null) {
            return cached == Direction.UP ? null : cached;
        }
        Direction found = null;
        try {
            Object entries = state.getClass().getMethod("getEntries").invoke(state);
            if (entries instanceof Map) {
                for (Map.Entry<?, ?> entry : ((Map<?, ?>)entries).entrySet()) {
                    Object property = entry.getKey();
                    String name = String.valueOf(property.getClass().getMethod("getName").invoke(property));
                    if (!"facing".equals(name)) {
                        continue;
                    }
                    Object value = entry.getValue();
                    if (value instanceof Direction) {
                        found = (Direction)value;
                    }
                }
            }
        }
        catch (Throwable throwable) {
            found = null;
        }
        if (this.facingCache.size() > 512) {
            this.facingCache.clear();
        }
        this.facingCache.put(state, found == null ? Direction.UP : found);
        return found;
    }

    /** Сила сигнала редстоуна из свойств состояния, 0 — если прочитать не удалось. */
    private int powerOf(BlockState state) {
        try {
            Object entries = state.getClass().getMethod("getEntries").invoke(state);
            if (entries instanceof Map) {
                for (Map.Entry<?, ?> entry : ((Map<?, ?>)entries).entrySet()) {
                    Object property = entry.getKey();
                    String name = String.valueOf(property.getClass().getMethod("getName").invoke(property));
                    if (!"power".equals(name)) {
                        continue;
                    }
                    Object value = entry.getValue();
                    if (value instanceof Integer) {
                        return (Integer)value;
                    }
                }
            }
        }
        catch (Throwable throwable) {
            return 0;
        }
        return 0;
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (!this.isEnabled() || this.mc.player == null || this.mc.gameRenderer == null) {
            return;
        }
        Vec3d camera = worldRenderEvent.getCamera() != null
                ? worldRenderEvent.getCamera().getCameraPos()
                : this.mc.gameRenderer.getCamera().getCameraPos();
        int limit = (int)this.limit.getValue();
        this.frameHints.clear();
        for (List<HintBox> layer : this.layerCache.values()) {
            for (HintBox hint : layer) {
                if (this.frameHints.size() >= limit) {
                    break;
                }
                if (hint.box.getCenter().squaredDistanceTo(camera) > 4096.0) {
                    continue;
                }
                this.frameHints.add(hint);
            }
        }
        if (this.frameHints.isEmpty()) {
            return;
        }
        VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
        float base = this.opacity.getValue() / 100.0f;
        float wave = this.pulse.getValue()
                ? 0.82f + 0.18f * (float)Math.sin((double)System.currentTimeMillis() * 0.0028)
                : 1.0f;
        for (HintBox hint : this.frameHints) {
            int alpha = Math.round(Math.max(20.0f, Math.min(255.0f, 255.0f * base * hint.strength * wave)));
            int fill = BloomRenderer.withAlpha(hint.color, alpha);
            int outline = BloomRenderer.withAlpha(hint.color, Math.min(255, alpha + 60));
            WorldShapeRenderer.boxes(immediate, worldRenderEvent.getStack(), camera, List.of(hint.box), fill, outline, 1.6f);
        }
    }

    /** Один предсказанный блок: бокс, цвет и относительная яркость. */
    private static final class HintBox {

        private final Box box;
        private final int color;
        private final float strength;

        private HintBox(Box box, int color, float strength) {
            this.box = box;
            this.color = color;
            this.strength = Math.max(0.1f, Math.min(1.0f, strength));
        }
    }
}
