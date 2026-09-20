package rtx.kimiko.api.mods.waveycapes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

public class CapeNodeCollector {
    private final List<CapeNode> capes = new ArrayList<CapeNode>();

    public void clear() {
        this.capes.clear();
    }

    public void submitCape(PlayerEntityRenderState playerEntityRenderState, MatrixStack matrixStack, int n) {
        this.capes.add(new CapeNode(playerEntityRenderState, matrixStack.peek().copy(), n));
    }

    public List<CapeNode> getCapes() {
        return this.capes;
    }
}

