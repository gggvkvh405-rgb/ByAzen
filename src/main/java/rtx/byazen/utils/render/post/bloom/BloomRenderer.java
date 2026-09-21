package rtx.byazen.utils.render.post.bloom;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleConstants;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleRenderer;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleTexture;

/**
 * Мягкое свечение вокруг эмиссивных объектов (идея №53 из IDEAS.md).
 * <p>
 * Свечение рисуется сглаженным радиальным ореолом в мировых координатах: вокруг блока лежит
 * плотное ядро и широкий полупрозрачный гало, яркость которого медленно «дышит». Никаких
 * пиксельных текстур и резких прямоугольников — только плавные градиенты.
 */
public final class BloomRenderer {

    private static final String GLOW_MODE = "Свечение";
    private static final String SPARK_MODE = "Искра";
    private static final ParticleRenderer RENDERER = new ParticleRenderer();

    private static Identifier glowTexture;
    private static Identifier sparkTexture;

    private BloomRenderer() {
    }

    public static void clear() {
        RENDERER.clear();
    }

    private static Identifier lookup(String mode, Identifier identifier) {
        if (identifier != null) {
            return identifier;
        }
        for (ParticleTexture texture : ParticleConstants.TEXTURES) {
            if (texture.mode().equals(mode)) {
                return texture.id();
            }
        }
        return ParticleConstants.TEXTURES[0].id();
    }

    public static Identifier glowTexture() {
        glowTexture = BloomRenderer.lookup(GLOW_MODE, glowTexture);
        return glowTexture;
    }

    public static Identifier sparkTexture() {
        sparkTexture = BloomRenderer.lookup(SPARK_MODE, sparkTexture);
        return sparkTexture;
    }

    /**
     * Рисует свечение для набора точек.
     *
     * @param baseSize размер ядра свечения в блоках
     * @param strength яркость свечения (0…255)
     * @param pulse    скорость «дыхания», 0 — статично
     */
    public static void render(List<Point> points, MatrixStack matrixStack, Vec3d cameraPos, Quaternionf cameraRotation,
                              float baseSize, float strength, float pulse, boolean sparks) {
        if (points == null || points.isEmpty() || matrixStack == null || cameraPos == null || cameraRotation == null) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        long now = System.currentTimeMillis();
        float core = Math.max(0.02f, baseSize);
        RENDERER.clear();
        int index = 0;
        for (Point point : points) {
            if (point == null) {
                continue;
            }
            float wave = pulse <= 0.01f
                    ? 1.0f
                    : (float)(0.84 + 0.16 * Math.sin((double)now * 0.0021 * (double)pulse + (double)point.phase));
            int halo = BloomRenderer.withAlpha(point.color, Math.round(strength * 0.30f * wave));
            RENDERER.drawTexture(matrixStack, immediate, BloomRenderer.glowTexture(), point.center, cameraPos,
                    cameraRotation, core * point.size * 2.35f, 0.0f, halo, true);
            int bright = BloomRenderer.withAlpha(point.color, Math.round(strength * 0.82f * wave));
            RENDERER.drawTexture(matrixStack, immediate, BloomRenderer.glowTexture(), point.center, cameraPos,
                    cameraRotation, core * point.size, 0.0f, bright, true);
            if (sparks && index % 2 == 0) {
                float flicker = (float)((now / 16L + (long)index * 41L) % 360L);
                RENDERER.drawTexture(matrixStack, immediate, BloomRenderer.sparkTexture(),
                        point.center.add(0.0, (double)(point.size * 0.45f), 0.0), cameraPos, cameraRotation,
                        core * point.size * 0.5f, flicker,
                        BloomRenderer.withAlpha(point.color, Math.round(strength * 0.55f * wave)), true);
            }
            ++index;
        }
        RENDERER.flush(immediate);
    }

    public static int pack(int red, int green, int blue) {
        return (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);
    }

    public static int mix(int from, int to, float factor) {
        float f = Math.max(0.0f, Math.min(1.0f, factor));
        int r = Math.round((float)(from >> 16 & 0xFF) + (float)((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * f);
        int g = Math.round((float)(from >> 8 & 0xFF) + (float)((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * f);
        int b = Math.round((float)(from & 0xFF) + (float)((to & 0xFF) - (from & 0xFF)) * f);
        return r << 16 | g << 8 | b;
    }

    public static int withAlpha(int rgb, int alpha) {
        int a = Math.max(0, Math.min(255, alpha));
        return a << 24 | rgb & 0xFFFFFF;
    }

    /** Точка свечения: центр блока, относительный размер, цвет и фаза «дыхания». */
    public static final class Point {

        public final Vec3d center;
        public final float size;
        public final int color;
        public final float phase;

        public Point(Vec3d vec3d, float size, int color, float phase) {
            this.center = vec3d;
            this.size = size;
            this.color = color;
            this.phase = phase;
        }
    }
}
