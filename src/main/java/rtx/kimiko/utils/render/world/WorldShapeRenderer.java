package rtx.kimiko.utils.render.world;

import java.util.List;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class WorldShapeRenderer {
    private WorldShapeRenderer() {}

    public static void boxes(VertexConsumerProvider.Immediate consumers, MatrixStack matrixStack, Vec3d cameraPos, List<Box> boxes, int fillColor, int outlineColor, float lineWidth) {
        if (boxes == null || boxes.isEmpty() || consumers == null || matrixStack == null) {
            return;
        }
        MatrixStack.Entry entry = matrixStack.peek();
        VertexConsumer fillConsumer = consumers.getBuffer(RenderLayers.lightning());

        for (Box box : boxes) {
            float x1 = (float) (box.minX - cameraPos.x);
            float y1 = (float) (box.minY - cameraPos.y);
            float z1 = (float) (box.minZ - cameraPos.z);
            float x2 = (float) (box.maxX - cameraPos.x);
            float y2 = (float) (box.maxY - cameraPos.y);
            float z2 = (float) (box.maxZ - cameraPos.z);

            if ((fillColor >>> 24) > 0) {
                // Bottom
                fillConsumer.vertex(entry, x1, y1, z1).color(fillColor);
                fillConsumer.vertex(entry, x2, y1, z1).color(fillColor);
                fillConsumer.vertex(entry, x2, y1, z2).color(fillColor);
                fillConsumer.vertex(entry, x1, y1, z2).color(fillColor);

                // Top
                fillConsumer.vertex(entry, x1, y2, z1).color(fillColor);
                fillConsumer.vertex(entry, x1, y2, z2).color(fillColor);
                fillConsumer.vertex(entry, x2, y2, z2).color(fillColor);
                fillConsumer.vertex(entry, x2, y2, z1).color(fillColor);

                // North
                fillConsumer.vertex(entry, x1, y1, z1).color(fillColor);
                fillConsumer.vertex(entry, x1, y2, z1).color(fillColor);
                fillConsumer.vertex(entry, x2, y2, z1).color(fillColor);
                fillConsumer.vertex(entry, x2, y1, z1).color(fillColor);

                // South
                fillConsumer.vertex(entry, x1, y1, z2).color(fillColor);
                fillConsumer.vertex(entry, x2, y1, z2).color(fillColor);
                fillConsumer.vertex(entry, x2, y2, z2).color(fillColor);
                fillConsumer.vertex(entry, x1, y2, z2).color(fillColor);

                // West
                fillConsumer.vertex(entry, x1, y1, z1).color(fillColor);
                fillConsumer.vertex(entry, x1, y1, z2).color(fillColor);
                fillConsumer.vertex(entry, x1, y2, z2).color(fillColor);
                fillConsumer.vertex(entry, x1, y2, z1).color(fillColor);

                // East
                fillConsumer.vertex(entry, x2, y1, z1).color(fillColor);
                fillConsumer.vertex(entry, x2, y2, z1).color(fillColor);
                fillConsumer.vertex(entry, x2, y2, z2).color(fillColor);
                fillConsumer.vertex(entry, x2, y1, z2).color(fillColor);
            }

            if ((outlineColor >>> 24) > 0) {
                drawEdge(fillConsumer, entry, x1, y1, z1, x2, y1, z1, outlineColor);
                drawEdge(fillConsumer, entry, x2, y1, z1, x2, y1, z2, outlineColor);
                drawEdge(fillConsumer, entry, x2, y1, z2, x1, y1, z2, outlineColor);
                drawEdge(fillConsumer, entry, x1, y1, z2, x1, y1, z1, outlineColor);

                drawEdge(fillConsumer, entry, x1, y2, z1, x2, y2, z1, outlineColor);
                drawEdge(fillConsumer, entry, x2, y2, z1, x2, y2, z2, outlineColor);
                drawEdge(fillConsumer, entry, x2, y2, z2, x1, y2, z2, outlineColor);
                drawEdge(fillConsumer, entry, x1, y2, z2, x1, y2, z1, outlineColor);

                drawEdge(fillConsumer, entry, x1, y1, z1, x1, y2, z1, outlineColor);
                drawEdge(fillConsumer, entry, x2, y1, z1, x2, y2, z1, outlineColor);
                drawEdge(fillConsumer, entry, x2, y1, z2, x2, y2, z2, outlineColor);
                drawEdge(fillConsumer, entry, x1, y1, z2, x1, y2, z2, outlineColor);
            }
        }
    }

    private static void drawEdge(VertexConsumer consumer, MatrixStack.Entry entry, float x1, float y1, float z1, float x2, float y2, float z2, int color) {
        consumer.vertex(entry, x1, y1, z1).color(color);
        consumer.vertex(entry, x2, y2, z2).color(color);
        consumer.vertex(entry, x2, y2, z2).color(color);
        consumer.vertex(entry, x1, y1, z1).color(color);
    }
}
