package rtx.byazen.utils.render.post.glowesp;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.utils.render.world.WorldShapeRenderer;

/**
 * Подсветка целей (идея №61 из IDEAS.md).
 * <p>
 * Рисуется честной геометрией мира: мягкий ореол вокруг модели, лёгкая заливка и аккуратный
 * контур. Никаких пиксельных текстур — только сглаженные полупрозрачные слои.
 */
public final class GlowEspRenderer {

    private GlowEspRenderer() {
    }

    public static void clear() {
    }

    /** Ореол и контур вокруг живых целей (игроки, мобы). */
    public static void render(List<? extends Entity> entities, float tickDelta, MatrixStack matrixStack, Vec3d cameraPos,
                              boolean glow, boolean outline, float glowRadius, float glowStrength, float outlineWidth,
                              boolean blending, int[] colors1, int[] colors2) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || matrixStack == null || cameraPos == null || entities == null || entities.isEmpty()) {
            return;
        }
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        float expansion = Math.max(0.01f, glowRadius) * 0.01f * Math.max(0.25f, glowStrength);
        int index = 0;
        for (Entity entity : entities) {
            if (entity == null || entity.isRemoved()) {
                continue;
            }
            Vec3d pos = entity.getLerpedPos(tickDelta);
            float half = entity.getWidth() * 0.5f + 0.06f;
            float height = entity.getHeight() + 0.06f;
            Box box = new Box(pos.x - half, pos.y, pos.z - half, pos.x + half, pos.y + height, pos.z + half);
            int body = colors1[index % colors1.length];
            int edge = colors2[index % colors2.length];
            GlowEspRenderer.draw(immediate, matrixStack, cameraPos, box, glow, outline, expansion,
                    Math.max(0.05f, outlineWidth), body, edge);
            index++;
        }
    }

    /** Ореол и контур вокруг блоков-контейнеров: те же слои, но цвет один. */
    public static void renderBoxes(List<Box> boxes, MatrixStack matrixStack, Vec3d cameraPos, boolean glow, boolean outline,
                                   float glowRadius, float glowStrength, float outlineWidth, int color) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || matrixStack == null || cameraPos == null || boxes == null || boxes.isEmpty()) {
            return;
        }
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        float expansion = Math.max(0.01f, glowRadius) * 0.01f * Math.max(0.25f, glowStrength);
        int index = 0;
        for (Box box : boxes) {
            if (box == null) {
                continue;
            }
            float shift = (float)(index % 4) * 0.02f;
            GlowEspRenderer.draw(immediate, matrixStack, cameraPos, box.expand(0.02 + (double)shift), glow, outline,
                    expansion, Math.max(0.05f, outlineWidth), color, color);
            index++;
        }
    }

    private static void draw(VertexConsumerProvider.Immediate immediate, MatrixStack matrixStack, Vec3d cameraPos, Box box,
                             boolean glow, boolean outline, float expansion, float outlineWidth, int bodyColor, int edgeColor) {
        if (glow) {
            int halo = GlowEspRenderer.withAlpha(bodyColor, GlowEspRenderer.alpha(bodyColor) * 0.22f);
            WorldShapeRenderer.boxes(immediate, matrixStack, cameraPos, List.of(box.expand((double)expansion * 2.6)), halo, 0, 1.0f);
            int inner = GlowEspRenderer.withAlpha(bodyColor, GlowEspRenderer.alpha(bodyColor) * 0.55f);
            WorldShapeRenderer.boxes(immediate, matrixStack, cameraPos, List.of(box.expand((double)expansion)), inner, 0, 1.0f);
        }
        if (outline) {
            int bright = GlowEspRenderer.withAlpha(edgeColor, Math.min(255.0f, GlowEspRenderer.alpha(edgeColor) * 1.25f));
            WorldShapeRenderer.boxes(immediate, matrixStack, cameraPos, List.of(box.expand((double)expansion + 0.015)), 0, bright,
                    Math.max(0.06f, outlineWidth));
        }
    }

    private static float alpha(int color) {
        int value = color >>> 24 & 0xFF;
        return value == 0 ? 190.0f : (float)value;
    }

    private static int withAlpha(int color, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return a << 24 | color & 0xFFFFFF;
    }
}
