package rtx.byazen.api.mods.waveycapes.compat;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

public final class VertexConsumerUtil {
    public static void addVertex(VertexConsumer vc, Matrix4f mat, float x, float y, float z, float u, float v, int overlay, int light, float nx, float ny, float nz, float alpha, boolean bl) {
        vc.vertex(mat, x, y, z).color(255, 255, 255, (int)(alpha * 255.0f)).texture(u, v).overlay(overlay).light(light).normal(nx, ny, nz);
    }
}