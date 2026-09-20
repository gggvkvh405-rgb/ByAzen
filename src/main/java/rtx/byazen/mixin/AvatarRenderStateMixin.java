package rtx.byazen.mixin;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import rtx.byazen.api.modules.impl.Utils.guishare.GuiHoldPoseState;

@Mixin(net.minecraft.client.render.entity.state.PlayerEntityRenderState.class)

public abstract class AvatarRenderStateMixin
implements GuiHoldPoseState {
    @Unique
    private float[] byazen_guiHoldPose;

    @Override
    public void byazen_setGuiHoldPose(float[] pose) {
        this.byazen_guiHoldPose = pose;
    }

    @Override
    public float[] byazen_getGuiHoldPose() {
        return this.byazen_guiHoldPose;
    }
}

