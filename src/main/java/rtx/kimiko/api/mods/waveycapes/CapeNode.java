package rtx.kimiko.api.mods.waveycapes;

import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

public record CapeNode(PlayerEntityRenderState state, MatrixStack.Entry pose, int light) {
}
