package rtx.kimiko.utils.render.world;

import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class BlockOverlayRenderer {
    private BlockOverlayRenderer() {}

    public static void render(VertexConsumerProvider.Immediate consumers, MatrixStack matrixStack, Vec3d cameraPos, Box box, int[] colors1, int[] colors2, int[] colors3) {
        if (box == null || consumers == null || matrixStack == null) {
            return;
        }
        MatrixStack.Entry entry = matrixStack.peek();
        double minX = box.minX - cameraPos.x;
        double minY = box.minY - cameraPos.y;
        double minZ = box.minZ - cameraPos.z;
        double maxX = box.maxX - cameraPos.x;
        double maxY = box.maxY - cameraPos.y;
        double maxZ = box.maxZ - cameraPos.z;

        float x1 = (float) minX;
        float y1 = (float) minY;
        float z1 = (float) minZ;
        float x2 = (float) maxX;
        float y2 = (float) maxY;
        float z2 = (float) maxZ;

        int fill = (colors2 != null && colors2.length > 0) ? colors2[0] : 0x33FFFFFF;
        int outline = (colors1 != null && colors1.length > 0) ? colors1[0] : 0xFFFFFFFF;

        VertexConsumer consumer = consumers.getBuffer(RenderLayers.lightning());

        if ((fill >>> 24) > 0) {
            // Bottom (y1)
            consumer.vertex(entry, x1, y1, z1).color(fill);
            consumer.vertex(entry, x2, y1, z1).color(fill);
            consumer.vertex(entry, x2, y1, z2).color(fill);
            consumer.vertex(entry, x1, y1, z2).color(fill);
            // Top (y2)
            consumer.vertex(entry, x1, y2, z1).color(fill);
            consumer.vertex(entry, x1, y2, z2).color(fill);
            consumer.vertex(entry, x2, y2, z2).color(fill);
            consumer.vertex(entry, x2, y2, z1).color(fill);
            // North (z1)
            consumer.vertex(entry, x1, y1, z1).color(fill);
            consumer.vertex(entry, x1, y2, z1).color(fill);
            consumer.vertex(entry, x2, y2, z1).color(fill);
            consumer.vertex(entry, x2, y1, z1).color(fill);
            // South (z2)
            consumer.vertex(entry, x1, y1, z2).color(fill);
            consumer.vertex(entry, x2, y1, z2).color(fill);
            consumer.vertex(entry, x2, y2, z2).color(fill);
            consumer.vertex(entry, x1, y2, z2).color(fill);
            // West (x1)
            consumer.vertex(entry, x1, y1, z1).color(fill);
            consumer.vertex(entry, x1, y1, z2).color(fill);
            consumer.vertex(entry, x1, y2, z2).color(fill);
            consumer.vertex(entry, x1, y2, z1).color(fill);
            // East (x2)
            consumer.vertex(entry, x2, y1, z1).color(fill);
            consumer.vertex(entry, x2, y2, z1).color(fill);
            consumer.vertex(entry, x2, y2, z2).color(fill);
            consumer.vertex(entry, x2, y1, z2).color(fill);
        }

        if ((outline >>> 24) > 0) {
            drawEdge(consumer, entry, x1, y1, z1, x2, y1, z1, outline);
            drawEdge(consumer, entry, x2, y1, z1, x2, y1, z2, outline);
            drawEdge(consumer, entry, x2, y1, z2, x1, y1, z2, outline);
            drawEdge(consumer, entry, x1, y1, z2, x1, y1, z1, outline);

            drawEdge(consumer, entry, x1, y2, z1, x2, y2, z1, outline);
            drawEdge(consumer, entry, x2, y2, z1, x2, y2, z2, outline);
            drawEdge(consumer, entry, x2, y2, z2, x1, y2, z2, outline);
            drawEdge(consumer, entry, x1, y2, z2, x1, y2, z1, outline);

            drawEdge(consumer, entry, x1, y1, z1, x1, y2, z1, outline);
            drawEdge(consumer, entry, x2, y1, z1, x2, y2, z1, outline);
            drawEdge(consumer, entry, x2, y1, z2, x2, y2, z2, outline);
            drawEdge(consumer, entry, x1, y1, z2, x1, y2, z2, outline);
        }
    }

    private static void drawEdge(VertexConsumer consumer, MatrixStack.Entry entry, float x1, float y1, float z1, float x2, float y2, float z2, int color) {
        consumer.vertex(entry, x1, y1, z1).color(color);
        consumer.vertex(entry, x2, y2, z2).color(color);
        consumer.vertex(entry, x2, y2, z2).color(color);
        consumer.vertex(entry, x1, y1, z1).color(color);
    }
}
