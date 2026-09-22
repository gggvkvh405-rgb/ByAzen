package rtx.byazen.api.modules.impl.Visuals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.player.AttackEntityEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.render.post.bloom.BloomRenderer;
import rtx.byazen.utils.render.world.SimpleParticles;
import rtx.byazen.utils.render.world.WorldShapeRenderer;

/**
 * Новый набор килл-эффектов (идея №56 из IDEAS.md).
 * <p>
 * Четыре собственных эффекта ByAzen, целиком построенных на сглаженной геометрии и мягких частицах:
 * «Космос» схлопывает пространство в точку и разбрасывает звёзды, «Дракон» раскрывает огненные
 * крылья, «Вода» расходится кругами по земле и брызгами, «Гроза» бьёт молнией с вспышкой. Никаких
 * пиксельных текстур — только плавные градиенты, кольца и лучи.
 */
public final class KillFX
extends Module {

    private static final String FX_SPACE = "Космос";
    private static final String FX_DRAGON = "Дракон";
    private static final String FX_WATER = "Вода";
    private static final String FX_STORM = "Гроза";
    private static final String FX_AURORA = "Аврора";

    private static final int RING_SEGMENTS = 32;

    private final SeparatorSetting mainSeparator = this.register(new SeparatorSetting("Эффект"));
    public final ModeSetting effect = this.register(new ModeSetting("Эффект", "Какой эффект играет при убийстве.", FX_SPACE, FX_SPACE, FX_DRAGON, FX_WATER, FX_STORM, FX_AURORA));
    public final SliderSetting duration = this.register(new SliderSetting("Длительность, мс", "Сколько живёт эффект.", 1500.0f, 600.0f, 3000.0f, 100.0f));
    public final SliderSetting size = this.register(new SliderSetting("Размер, %", "Общий размер эффекта.", 100.0f, 50.0f, 200.0f, 5.0f));
    public final SliderSetting density = this.register(new SliderSetting("Плотность частиц, %", "Сколько частиц рождает эффект.", 100.0f, 20.0f, 250.0f, 10.0f));

    private final SeparatorSetting lookSeparator = this.register(new SeparatorSetting("Вид"));
    public final BooleanSetting rings = this.register(new BooleanSetting("Кольца и лучи", "Рисовать геометрию эффекта: кольца, крылья, молнию, занавесы.", true));
    public final BooleanSetting flash = this.register(new BooleanSetting("Вспышка", "Короткая яркая вспышка в точке убийства.", true));
    public final BooleanSetting customColors = this.register(new BooleanSetting("Свои цвета", "Заменить палитру эффекта своими цветами.", false));
    public final ColorSetting firstColor = this.register(new ColorSetting("Цвет 1", "Основной цвет эффекта.", new java.awt.Color(150, 110, 255, 255)).visible(() -> this.customColors.getValue()));
    public final ColorSetting secondColor = this.register(new ColorSetting("Цвет 2", "Дополнительный цвет эффекта.", new java.awt.Color(255, 240, 200, 255)).visible(() -> this.customColors.getValue()));

    private final SeparatorSetting whoSeparator = this.register(new SeparatorSetting("Когда играть"));
    public final BooleanSetting ownKills = this.register(new BooleanSetting("Мои убийства", "Показывать эффект, когда убиваете вы.", true));
    public final BooleanSetting otherDeaths = this.register(new BooleanSetting("Чужие смерти", "Показывать эффект и на смерти существ рядом.", false));

    private final SimpleParticles particles = new SimpleParticles();
    private final List<KillEffectInstance> active = new ArrayList<KillEffectInstance>();
    private final Map<Integer, Vec3d> nearbyDeaths = new HashMap<Integer, Vec3d>();
    private final Random random = new Random();
    private Entity attackTarget;
    private Vec3d attackTargetPos;
    private long attackTargetTime;

    public KillFX() {
        super("Kill FX", "Свои килл-эффекты ByAzen: космос, дракон, вода, гроза и аврора — геометрия и частицы.", Category.VISUALS);
    }

    @Override
    protected void onDisable() {
        this.particles.clear();
        this.active.clear();
        this.nearbyDeaths.clear();
        this.attackTarget = null;
    }

    @EventHandler
    public void onAttack(AttackEntityEvent attackEntityEvent) {
        if (!this.isEnabled() || attackEntityEvent.isSynthetic()) {
            return;
        }
        Entity entity = attackEntityEvent.getTarget();
        if (!(entity instanceof LivingEntity) || entity == this.mc.player) {
            return;
        }
        this.attackTarget = entity;
        this.attackTargetPos = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
        this.attackTargetTime = System.currentTimeMillis();
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        this.particles.step();
        if (!this.isEnabled() || this.mc.world == null || this.mc.player == null) {
            this.attackTarget = null;
            this.nearbyDeaths.clear();
            return;
        }
        long now = System.currentTimeMillis();
        if (this.attackTarget != null) {
            if (this.attackTarget.isRemoved() || (this.attackTarget instanceof LivingEntity && !((LivingEntity)this.attackTarget).isAlive())) {
                Vec3d where = this.attackTarget.isRemoved() && this.attackTargetPos != null
                        ? this.attackTargetPos
                        : new Vec3d(this.attackTarget.getX(), this.attackTarget.getY(), this.attackTarget.getZ());
                if (this.ownKills.getValue()) {
                    this.play(where);
                }
                this.attackTarget = null;
                this.attackTargetPos = null;
            }
            else if (now - this.attackTargetTime > 8000L || this.attackTarget.squaredDistanceTo(this.mc.player) > 4096.0) {
                this.attackTargetPos = new Vec3d(this.attackTarget.getX(), this.attackTarget.getY(), this.attackTarget.getZ());
                this.attackTargetTime = now;
            }
        }
        if (this.otherDeaths.getValue()) {
            this.trackNearbyDeaths();
        }
        else if (!this.nearbyDeaths.isEmpty()) {
            this.nearbyDeaths.clear();
        }
        Iterator<KillEffectInstance> iterator = this.active.iterator();
        while (iterator.hasNext()) {
            KillEffectInstance instance = iterator.next();
            if (instance.progress(now) >= 1.0f) {
                iterator.remove();
                continue;
            }
            this.spawnParticles(instance, now);
        }
    }

    /** Отдельный эффект, живущий на месте чьей-то смерти. */
    private void trackNearbyDeaths() {
        ArrayList<Integer> alive = new ArrayList<Integer>();
        for (Entity entity : this.mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity) || entity == this.mc.player || !((LivingEntity)entity).isAlive()) {
                continue;
            }
            if (this.mc.player.squaredDistanceTo(entity) > 2304.0) {
                continue;
            }
            alive.add(entity.getId());
            this.nearbyDeaths.put(entity.getId(), new Vec3d(entity.getX(), entity.getY(), entity.getZ()));
        }
        if (this.nearbyDeaths.size() > 512) {
            this.nearbyDeaths.keySet().removeIf(id -> !alive.contains(id));
            return;
        }
        Iterator<Map.Entry<Integer, Vec3d>> iterator = this.nearbyDeaths.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Vec3d> entry = iterator.next();
            if (!alive.contains(entry.getKey())) {
                this.play(entry.getValue());
                iterator.remove();
            }
        }
    }

    private void play(Vec3d where) {
        if (where == null || this.active.size() > 6) {
            return;
        }
        long now = System.currentTimeMillis();
        this.active.add(new KillEffectInstance(where, this.effect.getValue(), now, this.duration.getValue(), this.random.nextLong()));
        this.spawnBurst(where);
    }

    private int[] palette(String mode) {
        if (this.customColors.getValue()) {
            return new int[]{this.firstColor.getValue() & 0xFFFFFF, this.secondColor.getValue() & 0xFFFFFF};
        }
        if (FX_DRAGON.equals(mode)) {
            return new int[]{0xFF9A3C, 0xFFF0B0};
        }
        if (FX_WATER.equals(mode)) {
            return new int[]{0x5AC8FF, 0xE8FBFF};
        }
        if (FX_STORM.equals(mode)) {
            return new int[]{0xBFD8FF, 0xFFFFFF};
        }
        if (FX_AURORA.equals(mode)) {
            return new int[]{0x5CFFC4, 0x7AA8FF};
        }
        return new int[]{0x9A6EFF, 0xE6D6FF};
    }

    private void spawnBurst(Vec3d where) {
        int[] colors = this.palette(this.effect.getValue());
        int count = Math.round(26.0f * this.density.getValue() / 100.0f);
        for (int i = 0; i < count; ++i) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            double radius = this.random.nextDouble() * 0.9 * (double)(this.size.getValue() / 100.0f);
            Vec3d pos = where.add(Math.cos(angle) * radius, 0.2 + this.random.nextDouble() * 1.1, Math.sin(angle) * radius);
            Vec3d motion = new Vec3d(Math.cos(angle) * 0.02, 0.012 + this.random.nextDouble() * 0.02, Math.sin(angle) * 0.02);
            int color = BloomRenderer.withAlpha(colors[i % 2], 210);
            this.particles.spawn(pos, motion, 700.0f + this.random.nextFloat() * 900.0f, this.random.nextFloat() * 360.0f,
                    i % 3 == 0 ? BloomRenderer.sparkTexture() : BloomRenderer.glowTexture(), color, 0.004f, 0.05f);
        }
        if (this.flash.getValue()) {
            for (int i = 0; i < 3; ++i) {
                this.particles.spawn(where.add(0.0, 0.7, 0.0), Vec3d.ZERO, 260.0f + (float)i * 90.0f, (float)i * 40.0f,
                        BloomRenderer.glowTexture(), BloomRenderer.withAlpha(colors[1], 165 - i * 40), 0.0f, 0.0f);
            }
        }
    }

    private void spawnParticles(KillEffectInstance instance, long now) {
        if (this.particles.size() > 1200) {
            return;
        }
        float t = instance.progress(now);
        int[] colors = this.palette(instance.mode);
        float scale = this.size.getValue() / 100.0f;
        int perTick = Math.max(1, Math.round(2.0f * this.density.getValue() / 100.0f));
        for (int i = 0; i < perTick; ++i) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            double radial = this.random.nextDouble();
            if (FX_SPACE.equals(instance.mode)) {
                double radius = (1.0 - t) * 1.6 * (double)scale + 0.1;
                Vec3d pos = instance.center.add(Math.cos(angle) * radius, 0.4 + this.random.nextDouble() * 1.2, Math.sin(angle) * radius);
                Vec3d motion = instance.center.add(0.0, 0.7, 0.0).subtract(pos).normalize().multiply(0.03 * (double)(1.0f - t) + 0.004);
                this.particles.spawn(pos, motion, 460.0f + this.random.nextFloat() * 420.0f, this.random.nextFloat() * 360.0f,
                        i % 2 == 0 ? BloomRenderer.sparkTexture() : BloomRenderer.glowTexture(),
                        BloomRenderer.withAlpha(colors[i % 2], 215), 0.0f, 0.02f);
                continue;
            }
            if (FX_DRAGON.equals(instance.mode)) {
                double side = (i % 2 == 0 ? 1.0 : -1.0) * (0.4 + radial * 1.4) * (double)scale;
                Vec3d pos = instance.center.add(Math.cos(angle) * 0.25, 0.35 + t * 1.1, side * 0.6);
                Vec3d motion = new Vec3d(side * 0.012, 0.022 + this.random.nextDouble() * 0.02, side * 0.008);
                this.particles.spawn(pos, motion, 520.0f + this.random.nextFloat() * 520.0f, this.random.nextFloat() * 360.0f,
                        BloomRenderer.glowTexture(), BloomRenderer.withAlpha(colors[i % 2], 220), -0.002f, 0.03f);
                continue;
            }
            if (FX_WATER.equals(instance.mode)) {
                double radius = (0.25 + t * 1.7) * (double)scale * radial;
                Vec3d pos = instance.center.add(Math.cos(angle) * radius, 0.08, Math.sin(angle) * radius);
                Vec3d motion = new Vec3d(Math.cos(angle) * 0.012, 0.028 + this.random.nextDouble() * 0.02, Math.sin(angle) * 0.012);
                this.particles.spawn(pos, motion, 420.0f + this.random.nextFloat() * 480.0f, this.random.nextFloat() * 360.0f,
                        BloomRenderer.glowTexture(), BloomRenderer.withAlpha(colors[i % 2], 205), 0.006f, 0.04f);
                continue;
            }
            if (FX_STORM.equals(instance.mode)) {
                double radius = (0.1 + t * 0.9) * (double)scale * radial;
                Vec3d pos = instance.center.add(Math.cos(angle) * radius, 0.1 + this.random.nextDouble() * 2.2, Math.sin(angle) * radius);
                Vec3d motion = new Vec3d(Math.cos(angle) * 0.02, 0.004, Math.sin(angle) * 0.02);
                this.particles.spawn(pos, motion, 300.0f + this.random.nextFloat() * 380.0f, this.random.nextFloat() * 360.0f,
                        i % 2 == 0 ? BloomRenderer.sparkTexture() : BloomRenderer.glowTexture(),
                        BloomRenderer.withAlpha(colors[i % 2], 230), -0.001f, 0.06f);
                continue;
            }
            double radius = (0.3 + t * 1.2) * (double)scale * radial;
            Vec3d pos = instance.center.add(Math.cos(angle) * radius, 0.3 + this.random.nextDouble() * 2.6, Math.sin(angle) * radius);
            Vec3d motion = new Vec3d(Math.cos(angle) * 0.006, 0.014 + this.random.nextDouble() * 0.01, Math.sin(angle) * 0.006);
            this.particles.spawn(pos, motion, 700.0f + this.random.nextFloat() * 700.0f, this.random.nextFloat() * 360.0f,
                    BloomRenderer.glowTexture(), BloomRenderer.withAlpha(colors[i % 2], 175), -0.001f, 0.03f);
        }
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (!this.isEnabled() || this.mc.world == null || this.mc.gameRenderer == null) {
            return;
        }
        Vec3d camera = worldRenderEvent.getCamera() != null
                ? worldRenderEvent.getCamera().getCameraPos()
                : this.mc.gameRenderer.getCamera().getCameraPos();
        if (this.rings.getValue() && !this.active.isEmpty()) {
            VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
            long now = System.currentTimeMillis();
            for (KillEffectInstance instance : this.active) {
                this.renderGeometry(immediate, worldRenderEvent, camera, instance, now);
            }
        }
        if (this.particles.size() > 0) {
            this.particles.render(worldRenderEvent.getStack(), camera, this.mc.gameRenderer.getCamera().getRotation(),
                    (float)worldRenderEvent.getPartialTicks(), 0.42f * this.size.getValue() / 100.0f, true);
        }
    }

    private void renderGeometry(VertexConsumerProvider.Immediate immediate, WorldRenderEvent event, Vec3d camera,
                                KillEffectInstance instance, long now) {
        float t = instance.progress(now);
        int[] colors = this.palette(instance.mode);
        float scale = this.size.getValue() / 100.0f;
        if (FX_SPACE.equals(instance.mode)) {
            double radius = ((double)1.0f - (double)t) * 1.9 * (double)scale + 0.12;
            float alpha = 200.0f * (1.0f - t * 0.35f);
            List<Box> boxes = KillFX.ringBoxes(instance.center, radius, 0.035 + 0.02 * (double)(1.0f - t), 0.06 + t * 0.9);
            WorldShapeRenderer.boxes(immediate, event.getStack(), camera, boxes,
                    BloomRenderer.withAlpha(colors[0], Math.round(alpha * 0.35f)), BloomRenderer.withAlpha(colors[1], Math.round(alpha)), 2.2f);
            if (t > 0.45f) {
                List<Box> out = KillFX.ringBoxes(instance.center, radius * 1.5, 0.03, 0.06 + t * 1.4);
                WorldShapeRenderer.boxes(immediate, event.getStack(), camera, out, 0,
                        BloomRenderer.withAlpha(colors[1], Math.round(120.0f * (1.0f - t))), 2.0f);
            }
            return;
        }
        if (FX_DRAGON.equals(instance.mode)) {
            ArrayList<Box> wings = new ArrayList<Box>();
            for (int side = -1; side <= 1; side += 2) {
                for (int i = 0; i < 10; ++i) {
                    float f = (float)i / 9.0f;
                    double spread = (0.5 + (double)f * 1.9) * (double)scale;
                    double rise = 0.4 + (double)f * 1.7 * (double)Math.min(1.0f, t * 1.5f);
                    double x = instance.center.x + (double)side * spread;
                    double y = instance.center.y + rise;
                    double z = instance.center.z + (double)side * spread * 0.35;
                    wings.add(new Box(x - 0.05, y, z - 0.05, x + 0.05, y + 0.16 + (double)f * 0.2, z + 0.05));
                }
            }
            float alpha = 210.0f * (1.0f - t);
            WorldShapeRenderer.boxes(immediate, event.getStack(), camera, wings, 0,
                    BloomRenderer.withAlpha(colors[0], Math.round(alpha)), 2.4f);
            return;
        }
        if (FX_WATER.equals(instance.mode)) {
            for (int wave = 0; wave < 3; ++wave) {
                double radius = (0.45 + (double)wave * 0.55 + (double)t * 1.5) * (double)scale;
                float alpha = 190.0f * (1.0f - t) * (1.0f - (float)wave / 3.4f);
                List<Box> ring = KillFX.ringBoxes(instance.center, radius, 0.03, 0.04);
                int fill = BloomRenderer.withAlpha(colors[0], Math.round(alpha * 0.25f));
                int outline = BloomRenderer.withAlpha(colors[1], Math.round(alpha));
                WorldShapeRenderer.boxes(immediate, event.getStack(), camera, ring, fill, outline, 2.0f);
            }
            return;
        }
        if (FX_STORM.equals(instance.mode)) {
            ArrayList<Box> bolt = new ArrayList<Box>();
            long seed = instance.seed;
            double x = instance.center.x;
            double z = instance.center.z;
            int steps = 12;
            for (int i = 0; i < steps; ++i) {
                double nextY = instance.center.y + (double)(i + 1) * 1.05;
                seed = seed * 6364136223846793005L + 1442695040888963407L;
                double ox = ((double)(seed >>> 40 & 0xFFL) / 255.0 - 0.5) * 0.5;
                seed = seed * 6364136223846793005L + 1442695040888963407L;
                double oz = ((double)(seed >>> 40 & 0xFFL) / 255.0 - 0.5) * 0.5;
                double nx = x + ox;
                double nz = z + oz;
                bolt.add(new Box(Math.min(x, nx) - 0.045, instance.center.y + (double)i * 1.05, Math.min(z, nz) - 0.045,
                        Math.max(x, nx) + 0.045, nextY, Math.max(z, nz) + 0.045));
                x = nx;
                z = nz;
            }
            float flicker = 0.55f + 0.45f * (float)Math.sin((double)now * 0.045);
            int outline = BloomRenderer.withAlpha(colors[1], Math.round(230.0f * (1.0f - t) * flicker));
            WorldShapeRenderer.boxes(immediate, event.getStack(), camera, bolt,
                    BloomRenderer.withAlpha(colors[0], Math.round(70.0f * (1.0f - t))), outline, 2.6f);
            return;
        }
        ArrayList<Box> curtains = new ArrayList<Box>();
        for (int band = 0; band < 3; ++band) {
            double offset = ((double)band - 1.0) * 0.9 * (double)scale;
            float alpha = 130.0f * (1.0f - t) * (band == 1 ? 1.0f : 0.6f);
            double height = 2.6 + (double)band * 0.7;
            double drift = Math.sin((double)now * 0.0012 + (double)band) * 0.35;
            curtains.add(new Box(instance.center.x + offset - 0.22 + drift, instance.center.y, instance.center.z - 0.9,
                    instance.center.x + offset + 0.22 + drift, instance.center.y + height, instance.center.z + 0.9));
        }
        int outline = BloomRenderer.withAlpha(colors[1], Math.round(170.0f * (1.0f - t)));
        WorldShapeRenderer.boxes(immediate, event.getStack(), camera, curtains,
                BloomRenderer.withAlpha(colors[0], Math.round(60.0f * (1.0f - t))), outline, 2.0f);
    }

    /** Кольцо из тонких сегментов — гладкий круг без пикселей. */
    private static List<Box> ringBoxes(Vec3d center, double radius, double thickness, double lift) {
        ArrayList<Box> boxes = new ArrayList<Box>(RING_SEGMENTS);
        double step = Math.PI * 2.0 / (double)RING_SEGMENTS;
        for (int i = 0; i < RING_SEGMENTS; ++i) {
            double angle = (double)i * step;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            boxes.add(new Box(x - thickness, center.y + lift, z - thickness, x + thickness, center.y + lift + thickness, z + thickness));
        }
        return boxes;
    }

    /** Один живущий эффект: где, какой и с какого момента. */
    private static final class KillEffectInstance {

        private final Vec3d center;
        private final String mode;
        private final long start;
        private final float durationMs;
        private final long seed;

        private KillEffectInstance(Vec3d vec3d, String string, long l, float f, long l2) {
            this.center = vec3d;
            this.mode = string;
            this.start = l;
            this.durationMs = Math.max(200.0f, f);
            this.seed = l2;
        }

        private float progress(long now) {
            return Math.max(0.0f, Math.min(1.0f, (float)(now - this.start) / this.durationMs));
        }
    }
}
