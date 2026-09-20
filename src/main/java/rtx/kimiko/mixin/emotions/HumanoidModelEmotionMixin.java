package rtx.kimiko.mixin.emotions;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.kimiko.api.modules.impl.Visuals.emotions.EmotionPlayback;
import rtx.kimiko.api.modules.impl.Visuals.emotions.EmotionStateHolder;

@Mixin(BipedEntityModel.class)
public abstract class HumanoidModelEmotionMixin {
    @Inject(method = "setAngles", at = {@At("TAIL")}, require = 0)
    private void kimiko_applyEmotion(BipedEntityRenderState state, CallbackInfo ci) {
        if (!(state instanceof PlayerEntityRenderState)) {
            return;
        }
        EmotionPlayback.applyTo((BipedEntityModel<?>)(Object)this, (EmotionStateHolder)(Object)state, state.limbSwingAmplitude);
    }
}
