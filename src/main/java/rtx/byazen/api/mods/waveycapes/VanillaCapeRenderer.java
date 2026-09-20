package rtx.byazen.api.mods.waveycapes;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import rtx.byazen.api.mods.waveycapes.CapeRenderer;
import rtx.byazen.api.mods.waveycapes.compat.PlayerWrapper;
import rtx.byazen.utils.render.cape.CapeVertexColor;

public class VanillaCapeRenderer
implements CapeRenderer {
    @Override
    public RenderLayer getRenderType(PlayerWrapper playerWrapper) {
        Identifier identifier = playerWrapper.getCapeTexture();
        if (identifier != null) {
            return RenderLayers.entityTranslucent((Identifier)identifier);
        }
        return null;
    }

    @Override
    public boolean vanillaUvValues() {
        return true;
    }

    @Override
    public void render(PlayerWrapper playerWrapper, int n, ModelPart modelPart, MatrixStack matrixStack, VertexConsumer vertexConsumer, int n2, int n3) {
        float f = ((float)n + 0.5f) / 16.0f;
        int n4 = playerWrapper.isLocalPlayer() ? CapeVertexColor.at((float)f, (float)f, (float)1.0f) : -1;
        modelPart.render(matrixStack, vertexConsumer, n2, n3, n4);
    }
}

