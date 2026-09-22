package rtx.byazen.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import rtx.byazen.api.modules.impl.Visuals.PetStyle;
import rtx.byazen.api.modules.impl.Visuals.custompet.render.CustomPetRenderer;

/**
 * Размер питомца из оформления (идея №127 из IDEAS.md).
 * <p>
 * Масштаб модели, который уже посчитал рендерер (в том числе с поправкой на сову), умножается на
 * выбранный игроком размер. Ничего больше не меняется, поэтому анимации и посадка на лист лилии
 * остаются штатными.
 */
@Environment(EnvType.CLIENT)
@Mixin(CustomPetRenderer.class)
public abstract class CustomPetScaleMixin {

    @ModifyVariable(method = "scaleModelForRender", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private float byazen_petScaleX(float value) {
        return value * PetStyle.petScale();
    }

    @ModifyVariable(method = "scaleModelForRender", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private float byazen_petScaleY(float value) {
        return value * PetStyle.petScale();
    }
}
