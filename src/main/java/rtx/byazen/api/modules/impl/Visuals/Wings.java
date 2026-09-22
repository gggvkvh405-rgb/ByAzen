package rtx.byazen.api.modules.impl.Visuals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.render.pipeline.ClientPipelines;
import rtx.byazen.utils.render.post.bloom.BloomRenderer;
import rtx.byazen.utils.render.world.SimpleParticles;
import rtx.byazen.utils.storage.friend.FriendUtils;

/**
 * Косметические крылья с физикой ткани (идея №66 из IDEAS.md).
 * <p>
 * Крыло — настоящая ткань: сетка точек, связанная пружинами и посчитанная методом Верле. Поэтому
 * крылья отстают при разгоне, колышутся от ветра, расправляются в падении и складываются на земле.
 * Рисуются плавными полупрозрачными полотнами с градиентом к кончикам перьев — никаких пиксельных
 * текстур, только мягкие слои.
 */
public final class Wings
extends Module {

    private static final String MODE_ANGEL = "Ангел";
    private static final String MODE_DRAGON = "Дракон";
    private static final String MODE_MOTH = "Мотылёк";

    private static final int COLS = 7;
    private static final int ROWS = 5;
    private static final int SIDES = 2;

    private final SeparatorSetting lookSeparator = this.register(new SeparatorSetting("Форма"));
    public final ModeSetting shape = this.register(new ModeSetting("Форма", "Какие крылья рисовать.", MODE_ANGEL, MODE_ANGEL, MODE_DRAGON, MODE_MOTH));
    public final SliderSetting size = this.register(new SliderSetting("Размер, %", "Насколько крупные крылья.").range(50.0f, 180.0f).increment(5.0f).setValue(100.0f));
    public final SliderSetting span = this.register(new SliderSetting("Размах, %", "Насколько широко раскрыты крылья.").range(50.0f, 170.0f).increment(5.0f).setValue(100.0f));
    public final SliderSetting droop = this.register(new SliderSetting("Провис, %", "Насколько сильно ткань провисает под своим весом.").range(0.0f, 120.0f).increment(5.0f).setValue(45.0f));
    public final SliderSetting wind = this.register(new SliderSetting("Ветер", "Как сильно ветер колышет ткань.").range(0.0f, 150.0f).increment(5.0f).setValue(55.0f));

    private final SeparatorSetting motionSeparator = this.register(new SeparatorSetting("Движение"));
    public final SliderSetting flapSpeed = this.register(new SliderSetting("Взмах", "Скорость взмахов при полёте и в прыжке.").range(0.0f, 160.0f).increment(5.0f).setValue(60.0f));
    public final BooleanSetting glideSpread = this.register(new BooleanSetting("Раскрывать в падении", "Раскрывать крылья шире, когда вы падаете или летите на элитрах.", true));
    public final BooleanSetting landing = this.register(new BooleanSetting("Складывать на земле", "Плавно складывать крылья, когда вы стоите спокойно.", true));

    private final SeparatorSetting colorSeparator = this.register(new SeparatorSetting("Цвета"));
    public final ColorSetting baseColor = this.register(new ColorSetting("Основной цвет", "Цвет у основания крыла.", new java.awt.Color(238, 244, 255, 220)));
    public final ColorSetting tipColor = this.register(new ColorSetting("Цвет кончиков", "Цвет на кончиках перьев.", new java.awt.Color(150, 195, 255, 120)));
    public final BooleanSetting accentGlow = this.register(new BooleanSetting("Свечение акцента", "Подмешивать акцент клиента в кончики крыльев.", true));
    public final BooleanSetting sparkles = this.register(new BooleanSetting("Искры", "Мягкие искры, слетающие с кончиков при быстром полёте.", false));

    private final SeparatorSetting whoSeparator = this.register(new SeparatorSetting("Кому"));
    public final BooleanSetting self = this.register(new BooleanSetting("Себе", "Показывать крылья на себе (видно в F5).", true));
    public final BooleanSetting others = this.register(new BooleanSetting("Другим игрокам", "Показывать крылья на других игроках.", false));
    public final BooleanSetting onlyFriends = this.register(new BooleanSetting("Только друзьям", "Показывать крылья только на друзьях из списка.", false)).visible(this.others::getValue);

    private final Map<Integer, WingState> states = new HashMap<Integer, WingState>();
    private final SimpleParticles particles = new SimpleParticles();
    private final Random random = new Random();

    public Wings() {
        super("Wings", "Косметические крылья с физикой ткани: колышутся от ветра и раскрываются в полёте.", Category.VISUALS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    protected void onDisable() {
        this.states.clear();
        this.particles.clear();
    }

    private boolean accepts(Entity entity) {
        if (!(entity instanceof LivingEntity) || entity.isRemoved()) {
            return false;
        }
        if (entity == this.mc.player) {
            return this.self.getValue();
        }
        if (!(entity instanceof AbstractClientPlayerEntity)) {
            return false;
        }
        if (!this.others.getValue()) {
            return false;
        }
        if (this.onlyFriends.getValue() && !FriendUtils.isFriend(entity.getName().getString())) {
            return false;
        }
        return entity.squaredDistanceTo(this.mc.player) < 4096.0;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        this.particles.step();
        if (!this.isEnabled() || this.mc.world == null || this.mc.player == null) {
            this.states.clear();
            return;
        }
        WingSettings settings = this.settings();
        ArrayList<Integer> alive = new ArrayList<Integer>();
        for (Entity entity : this.mc.world.getEntities()) {
            if (!this.accepts(entity)) {
                continue;
            }
            LivingEntity living = (LivingEntity)entity;
            alive.add(living.getId());
            WingState state = this.states.get(living.getId());
            if (state == null) {
                state = new WingState();
                this.states.put(living.getId(), state);
            }
            state.simulate(living, settings);
        }
        if (this.states.size() > 64) {
            this.states.keySet().removeIf(id -> !alive.contains(id));
        }
    }

    private WingSettings settings() {
        return new WingSettings(
                this.shape.getValue(),
                this.size.getValue() / 100.0f,
                this.span.getValue() / 100.0f,
                this.droop.getValue() / 100.0f,
                this.wind.getValue() / 100.0f,
                this.flapSpeed.getValue() / 100.0f,
                this.glideSpread.getValue(),
                this.landing.getValue(),
                this.baseColor.getValue() & 0xFFFFFF,
                this.tipColor.getValue() & 0xFFFFFF,
                this.accentGlow.getValue(),
                ClientAccent.accentOpaque() & 0xFFFFFF);
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (!this.isEnabled() || this.mc.world == null || this.mc.gameRenderer == null || this.states.isEmpty()) {
            return;
        }
        Vec3d camera = worldRenderEvent.getCamera() != null
                ? worldRenderEvent.getCamera().getCameraPos()
                : this.mc.gameRenderer.getCamera().getCameraPos();
        VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer consumer = immediate.getBuffer(ClientPipelines.WORLD_PARTICLES_COLOR);
        MatrixStack.Entry entry = worldRenderEvent.getStack().peek();
        float partial = (float)worldRenderEvent.getPartialTicks();
        WingSettings settings = this.settings();
        for (Entity entity : this.mc.world.getEntities()) {
            if (!this.accepts(entity)) {
                continue;
            }
            WingState state = this.states.get(entity.getId());
            if (state == null || !state.ready) {
                continue;
            }
            if (entity.squaredDistanceTo(camera.x, camera.y, camera.z) > 4096.0) {
                continue;
            }
            this.drawWings(consumer, entry, camera, entity, state, settings, partial);
            if (this.sparkles.getValue()) {
                this.emitSparkles(entity, state, settings);
            }
        }
        if (this.particles.size() > 0) {
            this.particles.render(worldRenderEvent.getStack(), camera, this.mc.gameRenderer.getCamera().getRotation(),
                    partial, 0.16f, true);
        }
    }

    private void drawWings(VertexConsumer consumer, MatrixStack.Entry entry, Vec3d camera, Entity entity,
                           WingState state, WingSettings settings, float partial) {
        for (int side = 0; side < SIDES; ++side) {
            Vec3d[][] grid = state.grid(side, partial);
            for (int row = 0; row < ROWS - 1; ++row) {
                for (int column = 0; column < COLS - 1; ++column) {
                    Vec3d a = grid[row][column];
                    Vec3d b = grid[row][column + 1];
                    Vec3d c = grid[row + 1][column + 1];
                    Vec3d d = grid[row + 1][column];
                    if (a == null || b == null || c == null || d == null) {
                        continue;
                    }
                    float u0 = (float)column / (float)(COLS - 1);
                    float u1 = (float)(column + 1) / (float)(COLS - 1);
                    float v0 = (float)row / (float)(ROWS - 1);
                    float v1 = (float)(row + 1) / (float)(ROWS - 1);
                    this.quad(consumer, entry, camera, a, b, c, d,
                            this.petalColor(settings, u0, v0),
                            this.petalColor(settings, u1, v0),
                            this.petalColor(settings, u1, v1),
                            this.petalColor(settings, u0, v1));
                }
            }
            for (int column = 0; column < COLS - 1; ++column) {
                Vec3d a = grid[0][column];
                Vec3d b = grid[0][column + 1];
                if (a == null || b == null) {
                    continue;
                }
                Vec3d lift = new Vec3d(0.0, 0.025, 0.0);
                int edge = this.edgeColor(settings);
                this.quad(consumer, entry, camera, a.add(lift), b.add(lift), b, a, edge, edge, edge, edge);
            }
        }
    }

    private void quad(VertexConsumer consumer, MatrixStack.Entry entry, Vec3d camera,
                      Vec3d a, Vec3d b, Vec3d c, Vec3d d, int ca, int cb, int cc, int cd) {
        this.vertex(consumer, entry, camera, a, ca);
        this.vertex(consumer, entry, camera, b, cb);
        this.vertex(consumer, entry, camera, c, cc);
        this.vertex(consumer, entry, camera, d, cd);
    }

    private void vertex(VertexConsumer consumer, MatrixStack.Entry entry, Vec3d camera, Vec3d point, int color) {
        consumer.vertex(entry, (float)(point.x - camera.x), (float)(point.y - camera.y), (float)(point.z - camera.z)).color(color);
    }

    private int petalColor(WingSettings settings, float u, float v) {
        float edgeFade = 1.0f - MathHelper.clamp((u - 0.55f) / 0.45f, 0.0f, 1.0f) * 0.5f;
        float feather = 1.0f - (float)Math.sin((double)MathHelper.clamp(v, 0.0f, 1.0f) * Math.PI) * 0.18f;
        int body = BloomRenderer.mix(settings.baseColor, settings.tipColor, MathHelper.clamp(u * 0.85f + v * 0.2f, 0.0f, 1.0f));
        if (settings.accentGlow) {
            body = BloomRenderer.mix(body, settings.accent, u * u * 0.5f);
        }
        float alpha = (105.0f + 115.0f * (1.0f - v)) * edgeFade * feather;
        return BloomRenderer.withAlpha(body, Math.round(Math.max(26.0f, alpha)));
    }

    private int edgeColor(WingSettings settings) {
        int body = settings.accentGlow ? BloomRenderer.mix(settings.baseColor, settings.accent, 0.35f) : settings.baseColor;
        return BloomRenderer.withAlpha(body, 200);
    }

    private void emitSparkles(Entity entity, WingState state, WingSettings settings) {
        if (entity.getVelocity().lengthSquared() < 0.02 || this.particles.size() > 240) {
            return;
        }
        for (int side = 0; side < SIDES; ++side) {
            Vec3d[][] grid = state.grid(side, 1.0f);
            for (int row = 0; row < ROWS; row += 2) {
                Vec3d tip = grid[row][COLS - 1];
                if (tip == null || this.random.nextFloat() > 0.3f) {
                    continue;
                }
                this.particles.spawn(tip, new Vec3d(0.0, -0.005, 0.0), 420.0f + this.random.nextFloat() * 320.0f,
                        this.random.nextFloat() * 360.0f, BloomRenderer.glowTexture(),
                        BloomRenderer.withAlpha(settings.accentGlow ? settings.accent : settings.tipColor, 150), 0.001f, 0.1f);
            }
        }
    }

    private static boolean isFlying(LivingEntity entity) {
        if (entity instanceof PlayerEntity && ((PlayerEntity)entity).isGliding()) {
            return true;
        }
        return !entity.isOnGround() && entity.getVelocity().y < -0.15;
    }

    private static boolean isIdle(LivingEntity entity) {
        return entity.isOnGround() && entity.getVelocity().horizontalLengthSquared() < 0.005;
    }

    private static double anchorYaw(LivingEntity entity) {
        if (entity instanceof AbstractClientPlayerEntity) {
            return Math.toRadians((double)entity.getYaw());
        }
        return Math.toRadians((double)entity.getYaw());
    }

    /** Точка крепления крыльев — чуть позади спины. */
    private static Vec3d anchor(LivingEntity entity, WingSettings settings) {
        double yaw = Wings.anchorYaw(entity);
        double backX = -(-Math.sin(yaw)) * (0.22 + 0.06 * (double)settings.size);
        double backZ = -Math.cos(yaw) * (0.22 + 0.06 * (double)settings.size);
        return new Vec3d(entity.getX() + backX, entity.getY() + (double)entity.getHeight() * 0.72, entity.getZ() + backZ);
    }

    /** Куда тянется точка ткани: раскрытая плоскость крыла с учётом взмаха и провиса. */
    private static Vec3d targetPoint(LivingEntity entity, WingSettings settings, Vec3d anchor, int side,
                                     double u, double v, double spread, double flap) {
        double yaw = Wings.anchorYaw(entity);
        double forwardX = -Math.sin(yaw);
        double forwardZ = Math.cos(yaw);
        double sideSign = side == 0 ? -1.0 : 1.0;
        double rightX = Math.cos(yaw) * sideSign;
        double rightZ = Math.sin(yaw) * sideSign;
        double length = (1.5 + 1.1 * (double)settings.size) * spread;
        double droopY = -settings.droop * 0.4 * u * u - v * 0.14 * settings.droop;
        double lift = flap * (0.55 + u) - v * 0.32;
        double x = anchor.x + rightX * length * u * 1.35 + forwardX * (0.22 + u * 0.5);
        double y = anchor.y + lift + droopY + v * 0.2;
        double z = anchor.z + rightZ * length * u * 1.35 + forwardZ * (0.22 + u * 0.5);
        return new Vec3d(x, y, z);
    }

    /** Настройки кадра, собранные один раз на тик. */
    private static final class WingSettings {

        private final String shape;
        private final float size;
        private final float span;
        private final float droop;
        private final float wind;
        private final float flapSpeed;
        private final boolean glideSpread;
        private final boolean landing;
        private final int baseColor;
        private final int tipColor;
        private final boolean accentGlow;
        private final int accent;

        private WingSettings(String shape, float size, float span, float droop, float wind, float flapSpeed,
                             boolean glideSpread, boolean landing, int baseColor, int tipColor, boolean accentGlow, int accent) {
            this.shape = shape;
            this.size = size;
            this.span = span;
            this.droop = droop;
            this.wind = wind;
            this.flapSpeed = flapSpeed;
            this.glideSpread = glideSpread;
            this.landing = landing;
            this.baseColor = baseColor;
            this.tipColor = tipColor;
            this.accentGlow = accentGlow;
            this.accent = accent;
        }

        /** Множитель «пышности» для формы крыла. */
        private float shapeBoost() {
            if (MODE_DRAGON.equals(this.shape)) {
                return 1.18f;
            }
            if (MODE_MOTH.equals(this.shape)) {
                return 0.86f;
            }
            return 1.0f;
        }
    }

    /** Ткань крыльев: две сетки точек, связанные пружинами, и метод Верле. */
    private static final class WingState {

        private final Vec3d[][][] current = new Vec3d[SIDES][ROWS][COLS];
        private final Vec3d[][][] previous = new Vec3d[SIDES][ROWS][COLS];
        private double phase;
        private boolean ready;
        private double lastAnchorX;
        private double lastAnchorY;
        private double lastAnchorZ;
        private double anchorVelocity;

        private void simulate(LivingEntity entity, WingSettings settings) {
            Vec3d anchor = Wings.anchor(entity, settings);
            double stepX = anchor.x - this.lastAnchorX;
            double stepY = anchor.y - this.lastAnchorY;
            double stepZ = anchor.z - this.lastAnchorZ;
            double speed = Math.sqrt(stepX * stepX + stepY * stepY + stepZ * stepZ);
            this.anchorVelocity += (speed - this.anchorVelocity) * 0.35;
            if (!this.ready) {
                for (int side = 0; side < SIDES; ++side) {
                    for (int row = 0; row < ROWS; ++row) {
                        for (int column = 0; column < COLS; ++column) {
                            this.current[side][row][column] = anchor;
                            this.previous[side][row][column] = anchor;
                        }
                    }
                }
                this.ready = true;
            }
            this.lastAnchorX = anchor.x;
            this.lastAnchorY = anchor.y;
            this.lastAnchorZ = anchor.z;
            this.phase += 0.12 + (double)settings.flapSpeed * 0.16;
            double spread = (double)settings.span * (double)settings.shapeBoost()
                    * (1.0 + (settings.glideSpread && Wings.isFlying(entity) ? 0.32 : 0.0));
            if (settings.landing && Wings.isIdle(entity)) {
                spread *= 0.72;
            }
            spread = Math.max(0.25, spread);
            double flap = Math.sin(this.phase) * (0.05 + 0.11 * Math.min(1.0, this.anchorVelocity * 6.0)) * (double)settings.flapSpeed;
            for (int side = 0; side < SIDES; ++side) {
                for (int row = 0; row < ROWS; ++row) {
                    double v = (double)row / (double)(ROWS - 1);
                    for (int column = 0; column < COLS; ++column) {
                        double u = (double)column / (double)(COLS - 1);
                        Vec3d target = Wings.targetPoint(entity, settings, anchor, side, u, v, spread, flap);
                        Vec3d point = this.current[side][row][column];
                        Vec3d prev = this.previous[side][row][column];
                        if (point == null || prev == null) {
                            this.current[side][row][column] = target;
                            this.previous[side][row][column] = target;
                            continue;
                        }
                        double nx = point.x + (point.x - prev.x) * 0.86 + (target.x - point.x) * 0.24;
                        double ny = point.y + (point.y - prev.y) * 0.86 + (target.y - point.y) * 0.24
                                - (double)settings.droop * 0.012 * (1.0 - v);
                        double nz = point.z + (point.z - prev.z) * 0.86 + (target.z - point.z) * 0.24;
                        this.previous[side][row][column] = point;
                        this.current[side][row][column] = new Vec3d(nx, ny, nz);
                    }
                }
            }
            if (settings.wind > 0.01f) {
                double gust = Math.sin(this.phase * 0.7) * 0.022 * (double)settings.wind;
                double flutter = Math.cos(this.phase * 1.7) * 0.01 * (double)settings.wind;
                for (int side = 0; side < SIDES; ++side) {
                    for (int row = 0; row < ROWS; ++row) {
                        for (int column = 1; column < COLS; ++column) {
                            Vec3d point = this.current[side][row][column];
                            double weight = 1.0 - (double)row / (double)ROWS;
                            this.current[side][row][column] = point.add(0.0, gust * weight, flutter * weight * (double)side * 0.6);
                        }
                    }
                }
            }
        }

        /** Сетка одного крыла с интерполяцией между тиками — кадр без рывков. */
        private Vec3d[][] grid(int side, float partial) {
            Vec3d[][] out = new Vec3d[ROWS][COLS];
            float t = Math.max(0.0f, Math.min(1.0f, partial));
            for (int row = 0; row < ROWS; ++row) {
                for (int column = 0; column < COLS; ++column) {
                    Vec3d point = this.current[side][row][column];
                    Vec3d prev = this.previous[side][row][column];
                    if (point == null && prev == null) {
                        continue;
                    }
                    if (point == null) {
                        out[row][column] = prev;
                        continue;
                    }
                    if (prev == null) {
                        out[row][column] = point;
                        continue;
                    }
                    out[row][column] = prev.add(point.subtract(prev).multiply((double)t));
                }
            }
            return out;
        }
    }
}
