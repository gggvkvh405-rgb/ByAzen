package rtx.kimiko.mixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.kimiko.api.modules.impl.Visuals.Customization;

@Mixin(net.minecraft.client.render.entity.feature.ElytraFeatureRenderer.class)

public abstract class ElytraHideMixin {
    @Inject(method="render", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void kimiko_hideElytra(MatrixStack poseStack, OrderedRenderCommandQueue collector, int lightCoords, BipedEntityRenderState state, float yRot, float xRot, CallbackInfo ci) {
        AbstractClientPlayerEntity player;
        if (!(state instanceof PlayerEntityRenderState)) {
            return;
        }
        PlayerEntityRenderState avatarState = (PlayerEntityRenderState)state;
        Customization customization = Customization.getInstance();
        if (customization == null) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) {
            return;
        }
        Entity entity = mc.world.getEntityById(avatarState.id);
        if (entity instanceof AbstractClientPlayerEntity && customization.wingsEnabledFor(player = (AbstractClientPlayerEntity)entity)) {
            ci.cancel();
        }
    }
}

