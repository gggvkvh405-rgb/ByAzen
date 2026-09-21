package rtx.byazen.utils.render.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import rtx.byazen.api.modules.impl.Visuals.particles.FadeParticle;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleRenderer;

/**
 * Простые частицы для эффектов мира (идеи №57, №64): осколки при разрушении блока и разлёт при
 * смерти моба. Каждая частица имеет свой цвет, размер и время жизни, а рисуются они сглаженными
 * спрайтами с прозрачностью — никаких пиксельных текстур.
 */
public final class SimpleParticles {

    private final List<SimpleParticles.Entry> entries = new ArrayList<SimpleParticles.Entry>();
    private final ParticleRenderer renderer = new ParticleRenderer();

    /** Добавляет частицу: позиция, скорость, время жизни, поворот, текстура и цвет. */
    public void spawn(Vec3d pos, Vec3d motion, float lifetimeMs, float rotation, Identifier texture, int color, float gravity, float drag) {
        if (pos == null || texture == null) {
            return;
        }
        FadeParticle particle = new FadeParticle(pos, motion, rotation, lifetimeMs, texture, 0.0f);
        this.entries.add(new SimpleParticles.Entry(particle, color, gravity, drag));
    }

    public int size() {
        return this.entries.size();
    }

    public void clear() {
        this.entries.clear();
    }

    /** Шаг симуляции: движение, гравитация, затухание, удаление мёртвых частиц. */
    public void step() {
        if (this.entries.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        this.entries.removeIf(entry -> entry.particle.isDead(now));
        for (SimpleParticles.Entry entry : this.entries) {
            entry.particle.beginStep();
            entry.particle.posX += entry.particle.motionX;
            entry.particle.posY += entry.particle.motionY;
            entry.particle.posZ += entry.particle.motionZ;
            entry.particle.motionY -= (double)entry.gravity;
            if (entry.drag > 0.0f) {
                entry.particle.motionX *= (double)(1.0f - Math.min(0.9f, entry.drag));
                entry.particle.motionY *= (double)(1.0f - Math.min(0.9f, entry.drag));
                entry.particle.motionZ *= (double)(1.0f - Math.min(0.9f, entry.drag));
            }
        }
    }

    /** Отрисовка всех частиц в мире: сглаженные билборды, свечение как у entity-спрайтов. */
    public void render(MatrixStack matrixStack, Vec3d cameraPos, Quaternionf cameraRotation, float partialTicks, float baseSize, boolean scaleWithAlpha) {
        if (this.entries.isEmpty() || matrixStack == null) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        long now = System.currentTimeMillis();
        this.renderer.clear();
        for (SimpleParticles.Entry entry : this.entries) {
            float alpha = entry.particle.alpha01(now);
            if (alpha <= 0.004f) {
                continue;
            }
            Vec3d pos = entry.particle.renderPos(partialTicks);
            int color = SimpleParticles.withAlpha(entry.color, SimpleParticles.alpha(entry.color) * alpha);
            float size = scaleWithAlpha ? baseSize * alpha : baseSize;
            this.renderer.drawTexture(matrixStack, immediate, entry.particle.texture, pos, cameraPos, cameraRotation, size,
                    entry.particle.rotationDeg, color, true);
        }
        this.renderer.flush(immediate);
    }

    private static float alpha(int color) {
        int value = color >>> 24 & 0xFF;
        return value == 0 ? 220.0f : (float)value;
    }

    private static int withAlpha(int color, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return a << 24 | color & 0xFFFFFF;
    }

    private static final class Entry {
        private final FadeParticle particle;
        private final int color;
        private final float gravity;
        private final float drag;

        private Entry(FadeParticle particle, int color, float gravity, float drag) {
            this.particle = particle;
            this.color = color;
            this.gravity = gravity;
            this.drag = drag;
        }
    }
}
