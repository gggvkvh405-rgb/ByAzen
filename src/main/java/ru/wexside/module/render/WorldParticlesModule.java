/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_12249
 *  net.minecraft.class_243
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  net.minecraft.class_746
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.wexside.module.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_12249;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.SpriteAtlasRegion;
import ru.wexside.misc.WexsideHitParticles;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class WorldParticlesModule
extends Module
implements ConfigSerializable {
    private static final double DEFAULT_DELTA = 0.016666666666666666;
    private final BooleanSetting enabledSetting;
    private final ColorSetting color;
    private final NumberSetting spawnCount;
    private final NumberSetting maxCount;
    private final NumberSetting range;
    private final NumberSetting size;
    private final NumberSetting strength;
    private final NumberSetting duration;
    private final BooleanSetting glowing;
    private final BooleanSetting onlyMove;
    private final ModeSetting direction;
    private final ModeSetting particleType;
    private final List<Particle> particles = new ArrayList<Particle>();
    private long lastNanoTime;
    private double spawnAccumulator;

    public WorldParticlesModule(EventBus eventBus) {
        super(eventBus, "world_particles", "World Particles", "\u041e\u043a\u0440\u0443\u0436\u0430\u044e\u0449\u0438\u0435 \u0438\u0433\u0440\u043e\u043a\u0430 \u043f\u0430\u0440\u044f\u0449\u0438\u0435 \u0447\u0430\u0441\u0442\u0438\u0446\u044b", ModuleCategory.valueOf("RENDER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043e\u043a\u0440\u0443\u0436\u0430\u044e\u0449\u0438\u0435 \u0447\u0430\u0441\u0442\u0438\u0446\u044b").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Color").id("color").description("\u0426\u0432\u0435\u0442 \u0447\u0430\u0441\u0442\u0438\u0446").aliases("color", "\u0446\u0432\u0435\u0442")).build();
        colorSetting.setPrimaryColor(0, -11753627);
        colorSetting.setPrimaryColor(1, -1543135);
        colorSetting.setPrimaryColor(2, -9279489);
        colorSetting.setPrimaryColor(3, -46001);
        colorSetting.setPrimaryColor(4, -13218);
        colorSetting.setPrimaryColor(5, -10582785);
        colorSetting.setPrimaryColor(6, -2732032);
        this.color = colorSetting;
        this.registerSetting(colorSetting);
        this.spawnCount = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 20.0).defaultValue(5.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Spawn count").id("spawn_count").description("\u0421\u043a\u043e\u043b\u044c\u043a\u043e \u0447\u0430\u0441\u0442\u0438\u0446 \u0441\u043f\u0430\u0432\u043d\u0438\u0442\u044c \u0437\u0430 \u0440\u0430\u0437")).build();
        this.registerSetting(this.spawnCount);
        this.maxCount = ((NumberSettingBuilder)NumberSetting.builder().range(50.0, 300.0).defaultValue(150.0).multiplier(1.0).precision(0).animationSpeed(20.0f).snapTo(25.0).name("Max count").id("max_count").description("\u041c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u043e\u0435 \u0447\u0438\u0441\u043b\u043e \u0436\u0438\u0432\u044b\u0445 \u0447\u0430\u0441\u0442\u0438\u0446 \u043e\u0434\u043d\u043e\u0432\u0440\u0435\u043c\u0435\u043d\u043d\u043e")).build();
        this.registerSetting(this.maxCount);
        this.range = ((NumberSettingBuilder)NumberSetting.builder().range(2.0, 24.0).defaultValue(8.0).multiplier(1.0).precision(0).animationSpeed(20.0f).showMarkers().name("Distance").id("range").description("\u0420\u0430\u0434\u0438\u0443\u0441 \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u044f \u0447\u0430\u0441\u0442\u0438\u0446 \u0432\u043e\u043a\u0440\u0443\u0433 \u0438\u0433\u0440\u043e\u043a\u0430")).build();
        this.registerSetting(this.range);
        this.size = ((NumberSettingBuilder)NumberSetting.builder().range(10.0, 100.0).defaultValue(50.0).multiplier(0.005).precision(0).animationSpeed(20.0f).markers(10.0).snapTo(10.0).name("Size").id("size").description("\u0420\u0430\u0437\u043c\u0435\u0440 \u0447\u0430\u0441\u0442\u0438\u0446\u044b").aliases("size", "\u0440\u0430\u0437\u043c\u0435\u0440")).build();
        this.registerSetting(this.size);
        this.strength = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 16.0).defaultValue(4.0).multiplier(0.05).precision(0).animationSpeed(20.0f).name("Move strength").id("strength").description("\u0421\u0438\u043b\u0430 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f \u0447\u0430\u0441\u0442\u0438\u0446")).build();
        this.registerSetting(this.strength);
        this.duration = ((NumberSettingBuilder)NumberSetting.builder().range(10.0, 50.0).defaultValue(30.0).multiplier(100.0).precision(0).animationSpeed(20.0f).snapTo(10.0).name("Life time").id("duration").description("\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438 \u0447\u0430\u0441\u0442\u0438\u0446\u044b")).build();
        this.registerSetting(this.duration);
        this.glowing = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Glowing").id("glowing").description("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u0442\u0435\u043a\u0441\u0442\u0443\u0440\u044b \u0447\u0430\u0441\u0442\u0438\u0446\u044b")).build();
        this.registerSetting(this.glowing);
        this.onlyMove = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Only move").id("only_move").description("\u0421\u043f\u0430\u0432\u043d\u0438\u0442\u044c \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0442\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0438 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u0438 \u0438\u0433\u0440\u043e\u043a\u0430")).build();
        this.registerSetting(this.onlyMove);
        this.direction = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Up", "Down", "Random").defaultOption("Up").name("Direction").id("direction").description("\u041d\u0430\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f \u0447\u0430\u0441\u0442\u0438\u0446").aliases("direction", "\u043d\u0430\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435")).build();
        this.registerSetting(this.direction);
        this.particleType = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Cross", "Dollar", "Star", "Bloom", "Snowflake", "Line", "Light").defaultOption("Cross").name("Particle type").id("particle_type").description("\u0422\u0435\u043a\u0441\u0442\u0443\u0440\u0430 \u0447\u0430\u0441\u0442\u0438\u0446\u044b")).build();
        this.registerSetting(this.particleType);
    }

    @Override
    protected void initialize() {
        this.listen(WorldRenderEvent.class, this::onWorldRender);
    }

    private void onWorldRender(WorldRenderEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            this.clear();
            return;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (client.field_1687 == null || player == null) {
            this.resetClock();
            return;
        }
        double delta = this.frameDelta();
        class_243 pos = this.lerpedPos(player, event.getFloatType());
        boolean moving = player.method_18798().method_37268() > 1.0E-4;
        this.tickParticles(delta);
        this.spawnAround(pos, delta, moving);
        this.trimOverflow();
        this.renderParticles();
    }

    private void spawnAround(class_243 origin, double delta, boolean moving) {
        if (this.onlyMove.isEnabled() && !moving) {
            return;
        }
        int cap = this.maxCount.getIntValue();
        if (this.particles.size() >= cap) {
            return;
        }
        this.spawnAccumulator += delta * 20.0;
        int ticks = (int)this.spawnAccumulator;
        if (ticks <= 0) {
            return;
        }
        this.spawnAccumulator -= (double)ticks;
        int spawn = Math.min(cap - this.particles.size(), ticks * this.spawnCount.getIntValue());
        for (int i = 0; i < spawn; ++i) {
            this.particles.add(this.createParticle(origin));
        }
    }

    private Particle createParticle(class_243 origin) {
        double vz;
        double vy;
        double vx;
        double z;
        double y;
        double x;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double radius = this.range.getValue();
        double strength = this.strength.getValue();
        float size = 0.05f + this.size.getFloatValue() * 0.35f;
        double offsetX = random.nextDouble(-radius, radius);
        double offsetZ = random.nextDouble(-radius, radius);
        String dir = this.direction.getSelectedOption();
        if ("Up".equals(dir)) {
            x = origin.field_1352 + offsetX;
            y = origin.field_1351 + random.nextDouble(0.0, 0.35) + (double)size;
            z = origin.field_1350 + offsetZ;
            vx = random.nextDouble(-0.02, 0.02) * 0.01;
            vy = random.nextDouble(0.02, strength) * 0.01;
            vz = random.nextDouble(-0.02, 0.02) * 0.01;
        } else if ("Down".equals(dir)) {
            x = origin.field_1352 + offsetX;
            y = origin.field_1351 + random.nextDouble(1.0, radius) + (double)size;
            z = origin.field_1350 + offsetZ;
            vx = random.nextDouble(-0.02, 0.02) * 0.01;
            vy = -random.nextDouble(0.02, strength) * 0.01;
            vz = random.nextDouble(-0.02, 0.02) * 0.01;
        } else {
            x = origin.field_1352 + offsetX;
            y = origin.field_1351 + random.nextDouble(-radius * 0.5, radius * 0.5) + (double)size;
            z = origin.field_1350 + offsetZ;
            vx = random.nextDouble(-strength, strength) * 0.01;
            vy = random.nextDouble(-strength, strength) * 0.01;
            vz = random.nextDouble(-strength, strength) * 0.01;
        }
        float rotation = (float)((double)Math.round(random.nextDouble(0.0, 360.0) / 15.0) * 15.0);
        return new Particle(x, y, z, vx, vy, vz, this.sampleColor(random.nextFloat()), size, rotation);
    }

    private void tickParticles(double delta) {
        long lifetime = this.duration.getLongValue();
        double step = Math.max(delta * 60.0, 1.0);
        for (int i = this.particles.size() - 1; i >= 0; --i) {
            Particle particle = this.particles.get(i);
            if (particle.isDead(lifetime)) {
                this.particles.remove(i);
                continue;
            }
            particle.tick(step);
        }
    }

    private void renderParticles() {
        long lifetime = this.duration.getLongValue();
        boolean glow = this.glowing.isEnabled();
        WexsideHitParticles texture = WexsideHitParticles.process2(this.particleType.getSelectedOption());
        SpriteAtlasRegion sprite = texture.process3(glow);
        class_310 client = class_310.method_1551();
        class_4184 camera = client.field_1773.method_19418();
        class_243 cameraPos = camera.method_71156();
        Matrix4f matrix = new Matrix4f().rotation((Quaternionfc)camera.method_23767());
        class_287 consumer = class_289.method_1348().method_60827(VertexFormat.class_5596.field_27382, class_290.field_1576);
        for (Particle particle : this.particles) {
            float life = particle.life(lifetime);
            if (life <= 0.02f) continue;
            int tint = WorldParticlesModule.withAlpha(particle.color, class_3532.method_15340((int)((int)(life * 255.0f)), (int)0, (int)255));
            this.drawBillboard(consumer, matrix, cameraPos, particle.x, particle.y, particle.z, particle.size, particle.rotation + sprite.minU(), tint);
        }
        class_12249.method_76023().method_60895(consumer.method_60800());
    }

    private void drawBillboard(class_287 consumer, Matrix4f matrix, class_243 cameraPos, double x, double y, double z, float size, float rotation, int color) {
        float half = size * 0.5f;
        float radians = (float)Math.toRadians(rotation);
        float cos = class_3532.method_15362((double)radians);
        float sin = class_3532.method_15374((double)radians);
        float px = (float)(x - cameraPos.field_1352);
        float py = (float)(y - cameraPos.field_1351);
        float pz = (float)(z - cameraPos.field_1350);
        this.vertex(consumer, matrix, px + (-half * cos - -half * sin), py + (-half * sin + -half * cos), pz, color);
        this.vertex(consumer, matrix, px + (half * cos - -half * sin), py + (half * sin + -half * cos), pz, color);
        this.vertex(consumer, matrix, px + (half * cos - half * sin), py + (half * sin + half * cos), pz, color);
        this.vertex(consumer, matrix, px + (-half * cos - half * sin), py + (-half * sin + half * cos), pz, color);
    }

    private void vertex(class_287 consumer, Matrix4f matrix, float x, float y, float z, int color) {
        consumer.method_22918((Matrix4fc)matrix, x, y, z).method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24 & 0xFF);
    }

    private class_243 lerpedPos(class_746 player, float tickDelta) {
        return new class_243(class_3532.method_16436((double)tickDelta, (double)player.field_6038, (double)player.method_23317()), class_3532.method_16436((double)tickDelta, (double)player.field_5971, (double)player.method_23318()), class_3532.method_16436((double)tickDelta, (double)player.field_5989, (double)player.method_23321()));
    }

    private int sampleColor(float t) {
        if (this.color.isAstolfoMode()) {
            return this.color.getEditingColor(t);
        }
        if (this.color.isDoubleColorMode()) {
            return WorldParticlesModule.lerpColor(this.color.getPrimaryColor(), this.color.getSecondaryColor(), t);
        }
        return this.color.getPrimaryColor();
    }

    private static int lerpColor(int from, int to, float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        int a = (int)((float)(from >>> 24 & 0xFF) + (float)((to >>> 24 & 0xFF) - (from >>> 24 & 0xFF)) * t);
        int r = (int)((float)(from >> 16 & 0xFF) + (float)((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * t);
        int g = (int)((float)(from >> 8 & 0xFF) + (float)((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * t);
        int b = (int)((float)(from & 0xFF) + (float)((to & 0xFF) - (from & 0xFF)) * t);
        return a << 24 | r << 16 | g << 8 | b;
    }

    private static int withAlpha(int color, int alpha) {
        return color & 0xFFFFFF | alpha << 24;
    }

    private void trimOverflow() {
        int overflow = this.particles.size() - this.maxCount.getIntValue();
        if (overflow > 0) {
            this.particles.subList(0, overflow).clear();
        }
    }

    private void clear() {
        this.particles.clear();
        this.resetClock();
    }

    private void resetClock() {
        this.spawnAccumulator = 0.0;
        this.lastNanoTime = 0L;
    }

    private double frameDelta() {
        long now = System.nanoTime();
        long previous = this.lastNanoTime;
        this.lastNanoTime = now;
        if (previous == 0L) {
            return 0.016666666666666666;
        }
        double delta = (double)(now - previous) / 1.0E9;
        if (!Double.isFinite(delta) || delta < 0.0) {
            return 0.016666666666666666;
        }
        return Math.min(delta, 0.05);
    }

    static final class Particle {
        double x;
        double y;
        double z;
        double vx;
        double vy;
        double vz;
        final int color;
        float size;
        float rotation;
        long age;

        Particle(double x, double y, double z, double vx, double vy, double vz, int color, float size, float rotation) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            this.color = color;
            this.size = size;
            this.rotation = rotation;
        }

        boolean isDead(long lifetime) {
            return this.age >= lifetime;
        }

        float life(long lifetime) {
            if (lifetime <= 0L) {
                return 0.0f;
            }
            return class_3532.method_15363((float)(1.0f - (float)this.age / (float)lifetime), (float)0.0f, (float)1.0f);
        }

        void tick(double step) {
            this.x += this.vx * step;
            this.y += this.vy * step;
            this.z += this.vz * step;
            ++this.age;
            this.rotation += 1.5f;
        }
    }
}

