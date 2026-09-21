/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_2374
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 */
package ru.byazen.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2374;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import ru.byazen.event.TotemPopEvent;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.misc.SpriteAtlasRegion;
import ru.byazen.misc.TotemEffectRenderer;
import ru.byazen.misc.ByAzenHitParticles;
import ru.byazen.render.ParticleBillboardRenderer;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.TotemEffectSettings;

public final class Snowflake
implements TotemEffectRenderer {
    private static final double FIXED_STEP = 0.016666666666666666;
    private final List<TotemParticle> particles = new ArrayList<TotemParticle>();
    private final TotemEffectSettings settings;
    private long previousFrameNanos;
    private double pendingTicks;

    public Snowflake(TotemEffectSettings settings) {
        this.settings = settings;
    }

    private void renderParticles(float tickDelta) {
        for (TotemParticle particle : this.particles) {
            if (particle.alpha <= 0.02f) continue;
            double x = class_3532.method_16436((double)tickDelta, (double)particle.previousPosition.field_1352, (double)particle.position.field_1352);
            double y = class_3532.method_16436((double)tickDelta, (double)particle.previousPosition.field_1351, (double)particle.position.field_1351);
            double z = class_3532.method_16436((double)tickDelta, (double)particle.previousPosition.field_1350, (double)particle.position.field_1350);
            SpriteAtlasRegion sprite = particle.style.texture().getSpriteAtlasRegion();
            int color = ColorUtils.withAlpha(particle.color, (float)class_3532.method_15340((int)((int)((float)(particle.color >>> 24 & 0xFF) * particle.alpha)), (int)0, (int)255));
            ParticleBillboardRenderer.draw(x, y, z, particle.size, particle.size, color, ByAzenHitParticles.getParticleTexture(), false, particle.initialRotation + particle.rotationSpeed * (float)particle.age, sprite.minU(), sprite.minV(), sprite.maxU(), sprite.maxV());
        }
    }

    private ParticleStyle selectedStyle() {
        return switch (this.settings.getString()) {
            case "Mini-star" -> new ParticleStyle(ByAzenHitParticles.STAR, 0.7f);
            case "Snowflake" -> new ParticleStyle(ByAzenHitParticles.SNOWFLAKE, 1.0f);
            case "Dollar" -> new ParticleStyle(ByAzenHitParticles.DOLLAR, 1.0f);
            case "Cross" -> new ParticleStyle(ByAzenHitParticles.CROSS, 1.0f);
            default -> new ParticleStyle(ByAzenHitParticles.STAR, 1.0f);
        };
    }

    private class_243 randomVelocity(ThreadLocalRandom random) {
        double speed = this.settings.getDoubleType();
        if (this.settings.isActive()) {
            return new class_243(random.nextDouble(-speed, speed), random.nextDouble(-speed, speed + 0.2), random.nextDouble(-speed, speed));
        }
        double azimuth = random.nextDouble() * Math.PI * 2.0;
        double polar = random.nextDouble() * Math.PI;
        return new class_243(speed * Math.cos(azimuth) * Math.sin(polar), speed * Math.cos(polar) + 0.05, speed * Math.sin(azimuth) * Math.sin(polar));
    }

    private double frameSeconds() {
        long now = System.nanoTime();
        if (this.previousFrameNanos == 0L) {
            this.previousFrameNanos = now;
            return 0.016666666666666666;
        }
        double seconds = (double)(now - this.previousFrameNanos) / 1.0E9;
        this.previousFrameNanos = now;
        return Math.clamp(seconds, 0.0, 0.05);
    }

    private void simulate(class_310 client) {
        Iterator<TotemParticle> iterator = this.particles.iterator();
        while (iterator.hasNext()) {
            class_3965 hit;
            TotemParticle particle = iterator.next();
            class_2338 blockPos = class_2338.method_49638((class_2374)particle.position);
            if (!client.field_1687.method_8477(blockPos) || particle.position.field_1351 < 0.0 || particle.alpha <= 0.0f) {
                iterator.remove();
                continue;
            }
            particle.previousPosition = particle.position;
            ++particle.age;
            class_243 velocity = this.settings.isActive() ? particle.velocity.method_1021(0.99).method_1023(0.0, 0.03, 0.0) : particle.velocity.method_1021(0.96).method_1031(0.0, 0.003, 0.0);
            class_243 end = particle.position.method_1019(velocity);
            class_3965 collision = null;
            if (this.settings.isActive() && (hit = client.field_1687.method_17742(new class_3959(particle.position, end, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)client.field_1724))).method_17783() == class_239.class_240.field_1332) {
                class_2350.class_2351 axis = hit.method_17780().method_10166();
                velocity = new class_243(axis == class_2350.class_2351.field_11048 ? -velocity.field_1352 : velocity.field_1352, axis == class_2350.class_2351.field_11052 ? -velocity.field_1351 : velocity.field_1351, axis == class_2350.class_2351.field_11051 ? -velocity.field_1350 : velocity.field_1350);
                collision = hit;
            }
            particle.position = collision != null ? collision.method_17784() : particle.position.method_1019(velocity);
            particle.velocity = velocity;
            particle.size *= 0.97f;
            if (particle.age < this.settings.getIntType2()) continue;
            particle.alpha = Math.max(0.0f, particle.alpha - 0.03f);
        }
    }

    @Override
    public void renderWorld(WorldRenderEvent event) {
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null || client.field_1724 == null) {
            this.update2();
            return;
        }
        if (this.particles.isEmpty()) {
            this.previousFrameNanos = System.nanoTime();
            return;
        }
        this.pendingTicks += this.frameSeconds() * 20.0;
        int updates = Math.min(10, (int)this.pendingTicks);
        if (updates > 0) {
            this.pendingTicks -= (double)updates;
            for (int i = 0; i < updates; ++i) {
                this.simulate(client);
            }
        }
        this.renderParticles(event.getFloatType());
    }

    @Override
    public void setTotemPopEvent(TotemPopEvent event) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        ParticleStyle style = this.selectedStyle();
        int count = this.settings.getIntType() + random.nextInt(4);
        int color = this.settings.getIntType3();
        for (int i = 0; i < count; ++i) {
            this.particles.add(new TotemParticle(event.toVec3d(), this.randomVelocity(random), color, (0.2f + random.nextFloat() * 0.15f) * style.scale(), random.nextFloat() * 360.0f, random.nextFloat() * 5.0f + 2.5f, style));
        }
    }

    @Override
    public void update2() {
        this.particles.clear();
        this.previousFrameNanos = 0L;
        this.pendingTicks = 0.0;
    }

    private static final class TotemParticle {
        private final ParticleStyle style;
        private final int color;
        private class_243 position;
        private class_243 previousPosition;
        private class_243 velocity;
        private float size;
        private float alpha = 1.0f;
        private final float initialRotation;
        private final float rotationSpeed;
        private int age;

        private TotemParticle(class_243 position, class_243 velocity, int color, float size, float initialRotation, float rotationSpeed, ParticleStyle style) {
            this.position = position;
            this.previousPosition = position;
            this.velocity = velocity;
            this.color = color;
            this.size = size;
            this.initialRotation = initialRotation;
            this.rotationSpeed = rotationSpeed;
            this.style = style;
        }
    }

    private record ParticleStyle(ByAzenHitParticles texture, float scale) {
    }
}

