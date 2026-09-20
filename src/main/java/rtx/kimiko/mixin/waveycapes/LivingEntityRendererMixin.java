package rtx.kimiko.mixin.waveycapes;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rtx.kimiko.api.mods.waveycapes.support.ModSupport;
import rtx.kimiko.api.mods.waveycapes.support.SupportManager;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<S extends LivingEntity, T extends LivingEntityRenderState, M extends EntityModel<? super T>> {
    @Inject(method="addFeature", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void addLayer(FeatureRenderer<T, M> renderLayer, CallbackInfoReturnable<Boolean> info) {
        if (renderLayer instanceof CapeFeatureRenderer) {
            info.cancel();
            return;
        }
        for (ModSupport support : SupportManager.getSupportedMods()) {
            if (!support.blockFeatureRenderer(renderLayer)) continue;
            info.cancel();
            return;
        }
    }
}
