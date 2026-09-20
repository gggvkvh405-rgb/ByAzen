package rtx.kimiko.mixin.waveycapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import rtx.kimiko.api.mods.waveycapes.CustomCapeRenderer;

@Mixin(targets = {"rtx.kimiko.api.mods.waveycapes.CustomCapeRenderer"}, remap = false)

public class WaveyCapeThicknessMixin {
    private static final float KIMIKO_THIN_CAPE_DEPTH = -0.015625f;

    @ModifyConstant(method={"renderSmoothCape"}, constant={@Constant(floatValue=-0.0625f)}, remap=false, require = 0)
    private float kimiko_thinWaveyCapeDepth(float original) {
        return -0.015625f;
    }
}

