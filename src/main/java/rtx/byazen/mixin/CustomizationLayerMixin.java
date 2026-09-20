package rtx.byazen.mixin;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.modules.impl.Visuals.customization.CustomizationLayer;

@Mixin(PlayerEntityRenderer.class)
public abstract class CustomizationLayerMixin {
    @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Z)V", at = @At("RETURN"))
    private void byazen_addCustomizationLayer(EntityRendererFactory.Context context, boolean slim, CallbackInfo ci) {
        PlayerEntityRenderer self = (PlayerEntityRenderer)(Object)this;
        self.features.add(new CustomizationLayer(self));
    }
}
