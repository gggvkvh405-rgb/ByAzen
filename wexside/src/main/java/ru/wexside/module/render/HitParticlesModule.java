/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_12249
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_243
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  net.minecraft.class_9801
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
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_9801;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EntityAttackEvent;
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

public final class HitParticlesModule
extends Module
implements ConfigSerializable {
    private static final double GRAVITY = 0.015;
    private static final double DRAG = 0.96;
    private static volatile HitParticlesModule instance;
    private final BooleanSetting enabledSetting;
    private final BooleanSetting removeDefault;
    private final ColorSetting color;
    private final NumberSetting count;
    private final NumberSetting maxCount;
    private final NumberSetting strength;
    private final NumberSetting size;
    private final NumberSetting duration;
    private final BooleanSetting glowing;
    private final ModeSetting particleTexture;
    private final ModeSetting animationType;
    private final List<Particle> particles = new ArrayList<Particle>();
    private long lastNanoTime;
    private double tickAccumulator;

    public HitParticlesModule(EventBus eventBus) {
        super(eventBus, "hit_particles", "Hit Particles", "\u042d\u0444\u0444\u0435\u043a\u0442 \u0447\u0430\u0441\u0442\u0438\u0446 \u043f\u0440\u0438 \u0443\u0434\u0430\u0440\u0435 \u043f\u043e \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438", ModuleCategory.valueOf("RENDER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u044d\u0444\u0444\u0435\u043a\u0442 \u0447\u0430\u0441\u0442\u0438\u0446 \u043f\u0440\u0438 \u0443\u0434\u0430\u0440\u0435 \u043f\u043e \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.removeDefault = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Remove Default").id("remove_default").description("\u0423\u0431\u0438\u0440\u0430\u0435\u0442 \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u044b\u0435 \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0443\u0434\u0430\u0440\u0430")).build();
        this.registerSetting(this.removeDefault);
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
        this.count = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 10.0).defaultValue(5.0).multiplier(2.5).precision(0).animationSpeed(20.0f).name("Spawn count").id("count").description("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0447\u0430\u0441\u0442\u0438\u0446, \u043f\u043e\u044f\u0432\u043b\u044f\u044e\u0449\u0438\u0445\u0441\u044f \u043f\u0440\u0438 \u0443\u0434\u0430\u0440\u0435")).build();
        this.registerSetting(this.count);
        this.maxCount = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 16.0).defaultValue(4.0).multiplier(32.0).precision(0).animationSpeed(20.0f).name("Max count").id("max_count").description("\u041c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u043e\u0435 \u0447\u0438\u0441\u043b\u043e \u0436\u0438\u0432\u044b\u0445 \u0447\u0430\u0441\u0442\u0438\u0446 \u043e\u0434\u043d\u043e\u0432\u0440\u0435\u043c\u0435\u043d\u043d\u043e")).build();
        this.registerSetting(this.maxCount);
        this.strength = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 16.0).defaultValue(4.0).multiplier(0.05).precision(0).animationSpeed(20.0f).name("Move strength").id("strength").description("\u0421\u0438\u043b\u0430 \u043d\u0430\u0447\u0430\u043b\u044c\u043d\u043e\u0433\u043e \u0440\u0430\u0437\u043b\u0451\u0442\u0430 \u0447\u0430\u0441\u0442\u0438\u0446")).build();
        this.registerSetting(this.strength);
        this.size = ((NumberSettingBuilder)NumberSetting.builder().range(10.0, 100.0).defaultValue(50.0).multiplier(0.005).precision(0).animationSpeed(20.0f).markers(10.0).snapTo(10.0).name("Size").id("size").description("\u0420\u0430\u0437\u043c\u0435\u0440 \u0447\u0430\u0441\u0442\u0438\u0446\u044b").aliases("size", "\u0440\u0430\u0437\u043c\u0435\u0440")).build();
        this.registerSetting(this.size);
        this.duration = ((NumberSettingBuilder)NumberSetting.builder().range(10.0, 50.0).defaultValue(30.0).multiplier(1.0).precision(0).animationSpeed(20.0f).snapTo(10.0).name("Life time").id("duration").description("\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438 \u0447\u0430\u0441\u0442\u0438\u0446\u044b")).build();
        this.registerSetting(this.duration);
        this.glowing = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Glowing").id("glowing").description("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u0442\u0435\u043a\u0441\u0442\u0443\u0440\u044b \u0447\u0430\u0441\u0442\u0438\u0446\u044b")).build();
        this.registerSetting(this.glowing);
        this.particleTexture = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Cross", "Dollar", "Star", "Bloom", "Snowflake", "Line", "Light").defaultOption("Cross").name("Texture").id("particle_texture").description("\u0422\u0435\u043a\u0441\u0442\u0443\u0440\u0430 \u0447\u0430\u0441\u0442\u0438\u0446\u044b")).build();
        this.registerSetting(this.particleTexture);
        this.animationType = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Default", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9").defaultOption("Default").name("Type animation").id("animation_type").description("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f")).build();
        this.registerSetting(this.animationType);
    }

    @Override
    protected void initialize() {
        this.listen(EntityAttackEvent.class, this::onEntityAttack);
        this.listen(WorldRenderEvent.class, this::onWorldRender);
    }

    public static boolean isEnabled() {
        HitParticlesModule module = instance;
        return module != null && module.enabledSetting.isEnabled() && module.removeDefault.isEnabled();
    }

    private void onEntityAttack(EntityAttackEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null || client.field_1724 == null) {
            return;
        }
        class_1297 target = event.getEntity();
        if (!(target instanceof class_1309)) {
            return;
        }
        class_1309 living = (class_1309)target;
        this.spawnParticles(new class_243(living.method_23317(), living.method_23318() + (double)living.method_17682() * 0.5, living.method_23321()));
    }

    private void onWorldRender(WorldRenderEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            this.clearParticles();
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null || client.field_1724 == null) {
            this.resetClock();
            return;
        }
        this.stepPhysics();
        this.trimOverflow();
        this.renderParticles((float)this.tickAccumulator);
    }

    private void spawnParticles(class_243 origin) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int room = Math.max(0, this.maxCount.getIntValue() - this.particles.size());
        int spawn = Math.min(room, this.count.getIntValue() + random.nextInt(4));
        for (int i = 0; i < spawn; ++i) {
            float particleSize = random.nextFloat() * 0.15f + this.size.getFloatValue() * 0.7f;
            double spread = this.strength.getValue();
            class_243 velocity = new class_243(random.nextDouble(-spread, spread), random.nextDouble(-spread, spread + 0.1), random.nextDouble(-spread, spread));
            float rotation = (float)((double)Math.round(random.nextDouble(0.0, 360.0) / 15.0) * 15.0);
            this.particles.add(new Particle(origin, velocity, this.sampleColor(random.nextFloat()), particleSize, rotation, this.animationIndex()));
        }
    }

    private void stepPhysics() {
        double delta = this.frameDelta();
        this.tickAccumulator += delta * 20.0;
        int steps = (int)this.tickAccumulator;
        if (steps <= 0) {
            return;
        }
        this.tickAccumulator -= (double)steps;
        if (steps > 10) {
            steps = 10;
            this.tickAccumulator = 0.0;
        }
        int lifetime = this.duration.getIntValue();
        for (int i = 0; i < steps; ++i) {
            for (int p = this.particles.size() - 1; p >= 0; --p) {
                Particle particle = this.particles.get(p);
                if (particle.isDead(lifetime)) {
                    this.particles.remove(p);
                    continue;
                }
                particle.tick(1.0);
            }
        }
    }

    private void renderParticles(float tickDelta) {
        boolean glow = this.glowing.isEnabled();
        WexsideHitParticles texture = WexsideHitParticles.process2(this.particleTexture.getSelectedOption());
        SpriteAtlasRegion sprite = texture.process3(glow);
        int lifetime = this.duration.getIntValue();
        class_310 client = class_310.method_1551();
        class_4184 camera = client.field_1773.method_19418();
        class_243 cameraPos = camera.method_71156();
        Matrix4f matrix = new Matrix4f().rotation((Quaternionfc)camera.method_23767());
        class_287 consumer = class_289.method_1348().method_60827(VertexFormat.class_5596.field_27382, class_290.field_1576);
        for (Particle particle : this.particles) {
            float life = particle.life(lifetime);
            if (life <= 0.02f) continue;
            class_243 previous = particle.previous;
            class_243 current = particle.current;
            double x = class_3532.method_16436((double)tickDelta, (double)previous.field_1352, (double)current.field_1352);
            double y = class_3532.method_16436((double)tickDelta, (double)previous.field_1351, (double)current.field_1351);
            double z = class_3532.method_16436((double)tickDelta, (double)previous.field_1350, (double)current.field_1350);
            int tint = HitParticlesModule.withAlpha(particle.color, class_3532.method_15340((int)((int)(life * 255.0f)), (int)0, (int)255));
            this.drawBillboard(consumer, matrix, cameraPos, x, y, z, particle.size, particle.rotation + sprite.minU(), tint);
        }
        class_9801 buffer = consumer.method_60794();
        if (buffer != null) {
            class_12249.method_76023().method_60895(buffer);
        }
    }

    private void drawBillboard(class_287 consumer, Matrix4f matrix, class_243 cameraPos, double x, double y, double z, float size, float rotation, int color) {
        float half = size * 0.5f;
        float radians = (float)Math.toRadians(rotation);
        float cos = class_3532.method_15362((double)radians);
        float sin = class_3532.method_15374((double)radians);
        float x1 = -half * cos - -half * sin;
        float y1 = -half * sin + -half * cos;
        float x2 = half * cos - -half * sin;
        float y2 = half * sin + -half * cos;
        float x3 = half * cos - half * sin;
        float y3 = half * sin + half * cos;
        float x4 = -half * cos - half * sin;
        float y4 = -half * sin + half * cos;
        float px = (float)(x - cameraPos.field_1352);
        float py = (float)(y - cameraPos.field_1351);
        float pz = (float)(z - cameraPos.field_1350);
        this.vertex(consumer, matrix, px + x1, py + y1, pz, color);
        this.vertex(consumer, matrix, px + x2, py + y2, pz, color);
        this.vertex(consumer, matrix, px + x3, py + y3, pz, color);
        this.vertex(consumer, matrix, px + x4, py + y4, pz, color);
    }

    private void vertex(class_287 consumer, Matrix4f matrix, float x, float y, float z, int color) {
        consumer.method_22918((Matrix4fc)matrix, x, y, z).method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24 & 0xFF);
    }

    private void trimOverflow() {
        int overflow = this.particles.size() - this.maxCount.getIntValue();
        if (overflow > 0) {
            this.particles.subList(0, overflow).clear();
        }
    }

    private void clearParticles() {
        this.particles.clear();
        this.resetClock();
    }

    private void resetClock() {
        this.tickAccumulator = 0.0;
        this.lastNanoTime = 0L;
    }

    private double frameDelta() {
        long now = System.nanoTime();
        long previous = this.lastNanoTime;
        this.lastNanoTime = now;
        if (previous == 0L) {
            return 0.05;
        }
        double delta = (double)(now - previous) / 1.0E9;
        if (!Double.isFinite(delta) || delta < 0.0) {
            return 0.05;
        }
        return Math.min(delta, 0.25);
    }

    private int animationIndex() {
        String value = this.animationType.getSelectedOption();
        if (value == null) {
            return 0;
        }
        return switch (value) {
            case "0" -> 1;
            case "1" -> 2;
            case "2" -> 3;
            case "3" -> 4;
            case "4" -> 5;
            case "5" -> 6;
            case "6" -> 7;
            case "7" -> 8;
            case "8" -> 9;
            case "9" -> 10;
            default -> 0;
        };
    }

    private int sampleColor(float t) {
        float clamped = class_3532.method_15363((float)t, (float)0.0f, (float)1.0f);
        if (this.color.isAstolfoMode()) {
            return this.color.getEditingColor(clamped);
        }
        int from = this.color.getPrimaryColor();
        int to = this.color.isDoubleColorMode() ? this.color.getSecondaryColor() : from;
        return HitParticlesModule.lerpColor(from, to, clamped);
    }

    private static int lerpColor(int from, int to, float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        int a1 = from >>> 24 & 0xFF;
        int r1 = from >> 16 & 0xFF;
        int g1 = from >> 8 & 0xFF;
        int b1 = from & 0xFF;
        int a2 = to >>> 24 & 0xFF;
        int r2 = to >> 16 & 0xFF;
        int g2 = to >> 8 & 0xFF;
        int b2 = to & 0xFF;
        int a = (int)((float)a1 + (float)(a2 - a1) * t);
        int r = (int)((float)r1 + (float)(r2 - r1) * t);
        int g = (int)((float)g1 + (float)(g2 - g1) * t);
        int b = (int)((float)b1 + (float)(b2 - b1) * t);
        return a << 24 | r << 16 | g << 8 | b;
    }

    private static int withAlpha(int color, int alpha) {
        return color & 0xFFFFFF | alpha << 24;
    }

    static final class Particle {
        class_243 previous;
        class_243 current;
        class_243 velocity;
        final int color;
        float size;
        float rotation;
        final int animation;
        int age;

        Particle(class_243 origin, class_243 velocity, int color, float size, float rotation, int animation) {
            this.previous = origin;
            this.current = origin;
            this.velocity = velocity;
            this.color = color;
            this.size = size;
            this.rotation = rotation;
            this.animation = animation;
            this.age = 0;
        }

        boolean isDead(int lifetime) {
            return this.age >= lifetime;
        }

        float life(int lifetime) {
            if (lifetime <= 0) {
                return 0.0f;
            }
            return class_3532.method_15363((float)(1.0f - (float)this.age / (float)lifetime), (float)0.0f, (float)1.0f);
        }

        void tick(double delta) {
            this.previous = this.current;
            this.current = this.current.method_1019(this.velocity.method_1021(delta));
            this.velocity = this.velocity.method_1021(0.96).method_1031(0.0, -0.015 * delta, 0.0);
            ++this.age;
            if (this.animation > 0) {
                this.rotation += (float)this.animation * 4.5f;
                this.size *= 0.985f;
            }
        }
    }
}

