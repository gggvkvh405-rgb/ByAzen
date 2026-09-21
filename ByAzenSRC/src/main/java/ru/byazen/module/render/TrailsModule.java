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
 *  org.lwjgl.opengl.GL46
 */
package ru.byazen.module.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.lwjgl.opengl.GL46;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.event.WorldSessionEvent;
import ru.byazen.misc.SpriteAtlasRegion;
import ru.byazen.misc.ByAzenHitParticles;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ColorSetting;
import ru.byazen.setting.ColorSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;
import ru.byazen.util.ColorUtils;

public final class TrailsModule
extends Module
implements ConfigSerializable {
    private static final int MAX_LINEAR_POINTS = 400;
    private static final double MIN_SEGMENT = 4.0E-4;
    private static final double MAX_SEGMENT = 1024.0;
    private static final double PARTICLE_STEP = 0.01;
    private final BooleanSetting enabledSetting;
    private final ColorSetting color;
    private final BooleanSetting hideFirstPerson;
    private final ModeSetting renderType;
    private final ModeSetting particleType;
    private final BooleanSetting glow;
    private final NumberSetting lifeTime;
    private final NumberSetting count;
    private final NumberSetting size;
    private final NumberSetting spawnScale;
    private final List<LinearTrailPoint> linearTrail = new ArrayList<LinearTrailPoint>();
    private final List<TrailParticle> particles = new ArrayList<TrailParticle>();
    private class_243 lastLinearPos;
    private double particleX;
    private double particleY;
    private double particleZ;
    private boolean particleStarted;
    private long lastNanoTime;

    public TrailsModule(EventBus eventBus) {
        super(eventBus, "trails", "Trails", "\u0421\u043b\u0435\u0434 \u0437\u0430 \u0438\u0433\u0440\u043e\u043a\u043e\u043c", ModuleCategory.valueOf("RENDER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0441\u043b\u0435\u0434 \u0437\u0430 \u0438\u0433\u0440\u043e\u043a\u043e\u043c").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Color").id("color").description("\u0426\u0432\u0435\u0442 \u0441\u043b\u0435\u0434\u0430").aliases("color", "\u0446\u0432\u0435\u0442")).build();
        colorSetting.setPrimaryColor(0, -11753627);
        colorSetting.setPrimaryColor(1, -1543135);
        colorSetting.setPrimaryColor(2, -9279489);
        colorSetting.setPrimaryColor(3, -46001);
        colorSetting.setPrimaryColor(4, -13218);
        colorSetting.setPrimaryColor(5, -10582785);
        colorSetting.setPrimaryColor(6, -2732032);
        this.color = colorSetting;
        this.registerSetting(colorSetting);
        this.hideFirstPerson = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Hide in first person").id("hide_first_person").description("\u041d\u0435 \u0440\u0438\u0441\u043e\u0432\u0430\u0442\u044c \u0441\u043b\u0435\u0434 \u043e\u0442 \u043f\u0435\u0440\u0432\u043e\u0433\u043e \u043b\u0438\u0446\u0430")).build();
        this.registerSetting(this.hideFirstPerson);
        this.renderType = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Linear", "Particles").defaultOption("Particles").name("Render type").id("render_type").description("\u0422\u0438\u043f \u043e\u0442\u0440\u0438\u0441\u043e\u0432\u043a\u0438 \u0441\u043b\u0435\u0434\u0430")).build();
        this.registerSetting(this.renderType);
        this.particleType = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Cross", "Dollar", "Star", "Bloom", "Snowflake", "Line", "Light").defaultOption("Cross").name("Particle type").id("particle_type").description("\u0422\u0435\u043a\u0441\u0442\u0443\u0440\u0430 \u0447\u0430\u0441\u0442\u0438\u0446\u044b").visibleWhen(this::particleMode)).build();
        this.registerSetting(this.particleType);
        this.glow = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Glowing").id("glow").description("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u0442\u0435\u043a\u0441\u0442\u0443\u0440\u044b \u0447\u0430\u0441\u0442\u0438\u0446\u044b").visibleWhen(this::particleMode)).build();
        this.registerSetting(this.glow);
        this.lifeTime = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 5.0).defaultValue(1.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Life time").id("life_time").description("\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438 \u0447\u0430\u0441\u0442\u0438\u0446\u044b").visibleWhen(this::particleMode)).build();
        this.registerSetting(this.lifeTime);
        this.count = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 5.0).defaultValue(5.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Count").id("count").description("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0447\u0430\u0441\u0442\u0438\u0446 \u0437\u0430 \u0448\u0430\u0433").aliases("count", "\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e").visibleWhen(this::particleMode)).build();
        this.registerSetting(this.count);
        this.size = ((NumberSettingBuilder)NumberSetting.builder().range(10.0, 100.0).defaultValue(50.0).multiplier(0.005).precision(0).animationSpeed(20.0f).markers(10.0).snapTo(10.0).name("Size").id("size").description("\u0420\u0430\u0437\u043c\u0435\u0440 \u0447\u0430\u0441\u0442\u0438\u0446\u044b").aliases("size", "\u0440\u0430\u0437\u043c\u0435\u0440").visibleWhen(this::particleMode)).build();
        this.registerSetting(this.size);
        this.spawnScale = ((NumberSettingBuilder)NumberSetting.builder().range(10.0, 100.0).defaultValue(50.0).multiplier(0.005).precision(0).animationSpeed(20.0f).markers(10.0).snapTo(10.0).name("Spawn scale").id("spawn_scale").description("\u041d\u0430\u0447\u0430\u043b\u044c\u043d\u044b\u0439 \u043c\u0430\u0441\u0448\u0442\u0430\u0431 \u0447\u0430\u0441\u0442\u0438\u0446\u044b").visibleWhen(this::particleMode)).build();
        this.registerSetting(this.spawnScale);
    }

    @Override
    protected void initialize() {
        this.listen(WorldRenderEvent.class, this::onWorldRender);
        this.listen(WorldSessionEvent.class, event -> this.clear());
    }

    private void onWorldRender(WorldRenderEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            this.clear();
            return;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (client.field_1687 == null || player == null) {
            this.clear();
            return;
        }
        class_243 pos = this.lerpedPos(player, event.getFloatType());
        if (this.particleMode()) {
            this.updateParticles(pos, player.method_17682() * 0.45f);
        } else {
            this.updateLinearTrail(pos);
        }
    }

    private void updateLinearTrail(class_243 pos) {
        if (this.lastLinearPos == null) {
            this.lastLinearPos = pos;
        }
        this.ageLinear(this.frameDelta());
        double distance = pos.method_1025(this.lastLinearPos);
        if (distance >= 1024.0) {
            this.lastLinearPos = pos;
        } else if (distance > 4.0E-4) {
            this.linearTrail.add(new LinearTrailPoint(this.lastLinearPos, pos));
            this.trimLinear();
            this.lastLinearPos = pos;
        }
        this.renderLinear();
    }

    private void updateParticles(class_243 pos, float height) {
        this.lastLinearPos = null;
        if (!this.particleStarted) {
            this.particleX = pos.field_1352;
            this.particleY = pos.field_1351;
            this.particleZ = pos.field_1350;
            this.particleStarted = true;
        }
        this.expireParticles();
        this.walkParticles(pos, height);
        this.renderParticles();
    }

    private void walkParticles(class_243 target, float height) {
        double dx = target.field_1352 - this.particleX;
        double dy = target.field_1351 - this.particleY;
        double dz = target.field_1350 - this.particleZ;
        double distanceSq = dx * dx + dy * dy + dz * dz;
        if (distanceSq >= 1024.0) {
            this.particleX = target.field_1352;
            this.particleY = target.field_1351;
            this.particleZ = target.field_1350;
            return;
        }
        while (distanceSq >= 1.0E-4) {
            double distance = Math.sqrt(distanceSq);
            double step = 0.1 / distance;
            this.particleX += dx * step;
            this.particleY += dy * step;
            this.particleZ += dz * step;
            this.spawnParticles(this.particleX, this.particleY, this.particleZ, height);
            dx = target.field_1352 - this.particleX;
            dy = target.field_1351 - this.particleY;
            dz = target.field_1350 - this.particleZ;
            distanceSq = dx * dx + dy * dy + dz * dz;
        }
    }

    private void spawnParticles(double x, double y, double z, float height) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        float size = height * (float)this.size.getValue();
        float scale = (float)this.spawnScale.getValue();
        for (int i = 0; i < this.count.getIntValue(); ++i) {
            this.particles.add(new TrailParticle(x, y + 0.4, z, size, scale, 0.1, random));
        }
    }

    private void expireParticles() {
        long now = System.currentTimeMillis();
        long lifetime = (long)(this.lifeTime.getValue() * 1000.0);
        boolean paused = class_310.method_1551().method_1493();
        this.particles.removeIf(particle -> {
            if (particle.isExpired(now, lifetime)) {
                return true;
            }
            particle.tick(paused);
            return false;
        });
    }

    private void renderParticles() {
        if (this.particles.isEmpty() || this.hideInFirstPerson()) {
            return;
        }
        long now = System.currentTimeMillis();
        long lifetime = (long)(this.lifeTime.getValue() * 1000.0);
        boolean glow = this.glow.isEnabled();
        ByAzenHitParticles texture = ByAzenHitParticles.process2(this.particleType.getSelectedOption());
        SpriteAtlasRegion sprite = texture.process3(glow);
        class_310 client = class_310.method_1551();
        class_4184 camera = client.field_1773.method_19418();
        class_243 cameraPos = camera.method_71156();
        Matrix4f matrix = new Matrix4f().rotation((Quaternionfc)camera.method_23767());
        class_287 consumer = class_289.method_1348().method_60827(VertexFormat.class_5596.field_27382, class_290.field_1576);
        float invLifetime = 1.0f / (float)lifetime;
        float count = Math.max(1.0f, (float)this.particles.size() - 1.0f);
        for (int i = 0; i < this.particles.size(); ++i) {
            TrailParticle particle = this.particles.get(i);
            long age = now - particle.spawnTime;
            if (age > lifetime) continue;
            float progress = (float)age * invLifetime;
            float fadeIn = TrailsModule.clamp01(progress / 0.18f);
            float scale = this.overshoot(fadeIn, 1.28f);
            float rise = this.easeOutCubic(progress);
            float tail = progress <= 0.56f ? 1.0f : 1.0f - this.cubic(TrailsModule.clamp01((progress - 0.56f) / 0.44f));
            float alpha = this.amount11(0.8f, 1.0f, scale * 0.22f) * tail;
            float pulse = 0.9f + 0.1f * class_3532.method_15374((double)(particle.phase + (float)age * 0.008333334f * 1.6f));
            float size = particle.baseSize * this.amount11(particle.spawnScale, 1.0f, scale) * (1.0f - progress * 0.12f) * pulse * particle.speed;
            double x = particle.x + particle.driftX * (double)rise * 0.09 + particle.liftX * (double)this.easeOutCubic(TrailsModule.clamp01((progress - 0.68f) / 0.32f)) * 0.18;
            double y = particle.y + 0.82 + particle.liftY * (double)rise + particle.driftY * (double)rise * 0.09 * 0.45;
            double z = particle.z + particle.driftZ * (double)rise * 0.09 + particle.liftZ * (double)this.easeOutCubic(TrailsModule.clamp01((progress - 0.68f) / 0.32f)) * 0.05;
            float rotation = particle.rotation * (1.0f - progress * 0.78f) + class_3532.method_15374((double)(particle.phase + progress * 6.0f)) * 5.5f;
            int tint = ColorUtils.multiplyAlpha(this.sampleColor((float)i / count), alpha);
            this.drawBillboard(consumer, matrix, cameraPos, x, y, z, size * 0.6666667f, rotation + sprite.minU(), tint);
        }
        class_12249.method_76023().method_60895(consumer.method_60800());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderLinear() {
        if (this.linearTrail.isEmpty() || this.hideInFirstPerson()) {
            return;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (player == null) {
            return;
        }
        float height = player.method_17682() - 0.05f - (player.method_6115() ? 0.1f : 0.0f);
        float step = 1.0f / Math.max((float)this.linearTrail.size() * 2.0f, 1.0f);
        boolean cull = GL46.glIsEnabled((int)2884);
        GL46.glDisable((int)2884);
        try {
            class_4184 camera = client.field_1773.method_19418();
            class_243 cameraPos = camera.method_71156();
            Matrix4f matrix = new Matrix4f().rotation((Quaternionfc)camera.method_23767());
            class_287 fill = class_289.method_1348().method_60827(VertexFormat.class_5596.field_27382, class_290.field_1576);
            class_287 lines = class_289.method_1348().method_60827(VertexFormat.class_5596.field_29344, class_290.field_1576);
            for (int i = 0; i < this.linearTrail.size(); ++i) {
                this.drawLinearSegment(this.linearTrail.get(i), (float)i * step, height, cameraPos, matrix, fill, lines);
            }
            class_12249.method_76023().method_60895(fill.method_60800());
            class_12249.method_76015().method_60895(lines.method_60800());
        }
        finally {
            if (cull) {
                GL46.glEnable((int)2884);
            }
        }
    }

    private void drawLinearSegment(LinearTrailPoint point, float gradient, float height, class_243 cameraPos, Matrix4f matrix, class_287 fill, class_287 lines) {
        float width = TrailsModule.clamp01((float)(point.age * 0.02));
        float strength = TrailsModule.clamp01((1.0f - width) * 1.8f);
        if (strength <= 0.01f) {
            return;
        }
        int color = ColorUtils.multiplyAlpha(this.sampleColor(gradient), strength);
        int nextColor = ColorUtils.multiplyAlpha(this.sampleColor(Math.min(1.0f, gradient + 0.08f)), strength);
        class_243 from = point.from.method_1031(0.0, 0.15, 0.0);
        class_243 to = point.to.method_1031(0.0, 0.15, 0.0);
        class_243 topTo = point.to.method_1031(0.0, (double)height, 0.0);
        class_243 topFrom = point.from.method_1031(0.0, (double)height, 0.0);
        this.quad(fill, matrix, cameraPos, from, to, topTo, topFrom, color, nextColor);
        this.quad(fill, matrix, cameraPos, topFrom, topTo, to, from, color, nextColor);
        this.line(lines, matrix, cameraPos, from, to, color, nextColor);
    }

    private void ageLinear(double delta) {
        Iterator<LinearTrailPoint> iterator = this.linearTrail.iterator();
        while (iterator.hasNext()) {
            LinearTrailPoint point = iterator.next();
            point.age += delta * 20.0;
            if (!(point.age >= 50.0)) continue;
            iterator.remove();
        }
    }

    private void trimLinear() {
        int overflow = this.linearTrail.size() - 400;
        if (overflow > 0) {
            this.linearTrail.subList(0, overflow).clear();
        }
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

    private void quad(class_287 consumer, Matrix4f matrix, class_243 cameraPos, class_243 a, class_243 b, class_243 c, class_243 d, int colorA, int colorB) {
        this.vertex(consumer, matrix, cameraPos, a, colorA);
        this.vertex(consumer, matrix, cameraPos, b, colorB);
        this.vertex(consumer, matrix, cameraPos, c, colorB);
        this.vertex(consumer, matrix, cameraPos, d, colorA);
    }

    private void line(class_287 consumer, Matrix4f matrix, class_243 cameraPos, class_243 a, class_243 b, int colorA, int colorB) {
        this.vertex(consumer, matrix, cameraPos, a, colorA);
        this.vertex(consumer, matrix, cameraPos, b, colorB);
    }

    private void vertex(class_287 consumer, Matrix4f matrix, class_243 cameraPos, class_243 pos, int color) {
        consumer.method_22918((Matrix4fc)matrix, (float)(pos.field_1352 - cameraPos.field_1352), (float)(pos.field_1351 - cameraPos.field_1351), (float)(pos.field_1350 - cameraPos.field_1350)).method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24 & 0xFF);
    }

    private void vertex(class_287 consumer, Matrix4f matrix, float x, float y, float z, int color) {
        consumer.method_22918((Matrix4fc)matrix, x, y, z).method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24 & 0xFF);
    }

    private int sampleColor(float t) {
        t = TrailsModule.clamp01(t);
        if (this.color.isAstolfoMode()) {
            return this.color.getEditingColor(t);
        }
        int from = this.color.getPrimaryColor();
        int to = this.color.isDoubleColorMode() ? this.color.getSecondaryColor() : from;
        return ColorUtils.lerp(from, to, t);
    }

    private class_243 lerpedPos(class_746 player, float tickDelta) {
        return new class_243(class_3532.method_16436((double)tickDelta, (double)player.field_6038, (double)player.method_23317()), class_3532.method_16436((double)tickDelta, (double)player.field_5971, (double)player.method_23318()), class_3532.method_16436((double)tickDelta, (double)player.field_5989, (double)player.method_23321()));
    }

    private boolean hideInFirstPerson() {
        class_310 client = class_310.method_1551();
        return this.hideFirstPerson.isEnabled() && client.field_1690 != null && client.field_1690.method_31044().method_31034();
    }

    private boolean particleMode() {
        return "Particles".equals(this.renderType.getSelectedOption());
    }

    private void clear() {
        this.linearTrail.clear();
        this.particles.clear();
        this.lastLinearPos = null;
        this.particleStarted = false;
        this.lastNanoTime = 0L;
    }

    private double frameDelta() {
        long now = System.nanoTime();
        long previous = this.lastNanoTime;
        this.lastNanoTime = now;
        if (previous == 0L) {
            return 0.016666666666666666;
        }
        double delta = (double)(now - previous) * 1.0E-9;
        return !Double.isFinite(delta) || delta < 0.0 ? 0.016666666666666666 : Math.min(delta, 0.05);
    }

    private static float clamp01(float value) {
        return class_3532.method_15363((float)value, (float)0.0f, (float)1.0f);
    }

    private float easeOutCubic(float value) {
        float inverse = 1.0f - TrailsModule.clamp01(value);
        return 1.0f - inverse * inverse * inverse;
    }

    private float overshoot(float value, float amount) {
        float t = TrailsModule.clamp01(value) - 1.0f;
        float s = amount + 0.8f;
        return 1.0f + t * t * ((s + 1.0f) * t + s);
    }

    private float cubic(float value) {
        float t = TrailsModule.clamp01(value);
        return t * t * t;
    }

    private float amount11(float from, float to, float t) {
        return from + (to - from) * t;
    }

    static final class LinearTrailPoint {
        final class_243 from;
        final class_243 to;
        double age;

        LinearTrailPoint(class_243 from, class_243 to) {
            this.from = from;
            this.to = to;
        }
    }

    static final class TrailParticle {
        final double x;
        final double y;
        final double z;
        final float baseSize;
        final float spawnScale;
        final float speed;
        final float phase;
        float rotation;
        final double driftX;
        final double driftY;
        final double driftZ;
        final double liftX;
        final double liftY;
        final double liftZ;
        final long spawnTime;

        TrailParticle(double x, double y, double z, float baseSize, float spawnScale, double speed, ThreadLocalRandom random) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.baseSize = baseSize;
            this.spawnScale = spawnScale;
            this.speed = (float)speed;
            this.phase = random.nextFloat() * ((float)Math.PI * 2);
            this.rotation = random.nextFloat() * 360.0f;
            this.driftX = random.nextDouble(-0.02, 0.02);
            this.driftY = random.nextDouble(0.0, 0.02);
            this.driftZ = random.nextDouble(-0.02, 0.02);
            this.liftX = random.nextDouble(-0.01, 0.01);
            this.liftY = random.nextDouble(0.0, 0.04);
            this.liftZ = random.nextDouble(-0.01, 0.01);
            this.spawnTime = System.currentTimeMillis();
        }

        boolean isExpired(long now, long lifetime) {
            return now - this.spawnTime > lifetime;
        }

        void tick(boolean paused) {
            if (!paused) {
                this.rotation += 0.5f;
            }
        }
    }
}

