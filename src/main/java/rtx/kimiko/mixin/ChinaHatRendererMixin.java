package rtx.kimiko.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.kimiko.api.modules.impl.Visuals.ChinaHat;

@Mixin(LivingEntityRenderer.class)
public abstract class ChinaHatRendererMixin<S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Shadow
    protected M model;

    @Shadow
    protected abstract void setupTransforms(S state, MatrixStack matrices, float bodyYaw, float baseScale);

    @Shadow
    protected abstract void scale(S state, MatrixStack matrices);

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("TAIL"), require = 0)
    private void kimiko_captureChinaHatTransform(S state, MatrixStack poseStack, OrderedRenderCommandQueue collector, CameraRenderState camera, CallbackInfo ci) {
        if (!(state instanceof PlayerEntityRenderState avatarState)) {
            return;
        }
        ChinaHat chinaHat = ChinaHat.getInstance();
        if (chinaHat == null || !chinaHat.isEnabled()) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) {
            return;
        }
        Entity entity = mc.world.getEntityById(avatarState.id);
        if (!(entity instanceof AbstractClientPlayerEntity player)) {
            return;
        }
        if (avatarState.baby || state.invisible || !chinaHat.shouldRender(player)) {
            return;
        }
        if (!(this.model instanceof ModelWithHead headedModel)) {
            return;
        }
        MatrixStack local = new MatrixStack();
        local.scale(state.baseScale, state.baseScale, state.baseScale);
        this.setupTransforms(state, local, state.bodyYaw, state.baseScale);
        local.scale(-1.0f, -1.0f, 1.0f);
        this.scale(state, local);
        local.translate(0.0f, -1.501f, 0.0f);
        ModelPart head = headedModel.getHead();
        head.applyTransform(local);
        local.multiply((Quaternionfc)RotationAxis.NEGATIVE_Z.rotationDegrees(180.0f));
        local.multiply((Quaternionfc)RotationAxis.NEGATIVE_Y.rotationDegrees(90.0f));
        chinaHat.captureTransform(player.getId(), new Matrix4f((Matrix4fc)local.peek().getPositionMatrix()));
    }
}
