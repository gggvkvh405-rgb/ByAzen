package rtx.kimiko.mixin;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import rtx.kimiko.api.modules.impl.Utils.guishare.GuiHoldPoseState;

@Mixin(net.minecraft.client.render.entity.state.PlayerEntityRenderState.class)

public abstract class AvatarRenderStateMixin
implements GuiHoldPoseState {
    @Unique
    private float[] kimiko_guiHoldPose;

    @Override
    public void kimiko_setGuiHoldPose(float[] pose) {
        this.kimiko_guiHoldPose = pose;
    }

    @Override
    public float[] kimiko_getGuiHoldPose() {
        return this.kimiko_guiHoldPose;
    }
}

