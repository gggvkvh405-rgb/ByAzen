package rtx.byazen.mixin.waveycapes;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.mods.waveycapes.renderlayers.CustomCapeRenderLayer;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {
    @Unique
    private boolean injectedCape = false;

    public PlayerRendererMixin(EntityRendererFactory.Context context, PlayerEntityModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method="<init>", at={@At(value="RETURN")}, require = 0)
    public void onCreate(EntityRendererFactory.Context context, boolean slim, CallbackInfo info) {
        this.injectedCape = true;
        this.addFeature(new CustomCapeRenderLayer((FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel>)(Object)this));
    }
}
