package rtx.kimiko.api.mods.waveycapes;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import rtx.kimiko.api.mods.waveycapes.compat.PlayerWrapper;

public interface CapeRenderer {
    public RenderLayer getRenderType(PlayerWrapper var1);

    default public boolean vanillaUvValues() {
        return true;
    }

    default public void render(PlayerWrapper playerWrapper, int n, ModelPart modelPart, MatrixStack matrixStack, VertexConsumer vertexConsumer, int n2, int n3) {
        modelPart.render(matrixStack, vertexConsumer, n2, OverlayTexture.DEFAULT_UV);
    }
}

