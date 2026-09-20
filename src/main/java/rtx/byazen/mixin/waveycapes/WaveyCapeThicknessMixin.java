package rtx.byazen.mixin.waveycapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import rtx.byazen.api.mods.waveycapes.CustomCapeRenderer;

@Mixin(targets = {"rtx.byazen.api.mods.waveycapes.CustomCapeRenderer"}, remap = false)

public class WaveyCapeThicknessMixin {
    private static final float BYAZEN_THIN_CAPE_DEPTH = -0.015625f;

    @ModifyConstant(method={"renderSmoothCape"}, constant={@Constant(floatValue=-0.0625f)}, remap=false, require = 0)
    private float byazen_thinWaveyCapeDepth(float original) {
        return -0.015625f;
    }
}

