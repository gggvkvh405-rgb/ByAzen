package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.zenith.ZenithClient;
import org.zenith.base.figura.ducks.LivingEntityRendererAccessor;
import org.zenith.core.ClientProvider;
import org.zenith.event.VelocityChangeEvent;
import org.zenith.module.render.Chams;
import org.zenith.module.movement.Speed;
import org.zenith.render.WorldRender;
import org.zenith.rotation.RotationManager;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> implements ClientProvider {
   @ModifyExpressionValue(
      method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;clampBodyYaw(Lnet/minecraft/entity/LivingEntity;FF)F")
   )
   public float changeYaw(float var1, LivingEntity var2) {
      RotationManager i11l1llliliili11i = ZenithClient.on23().CloudRouter();
      return var2.equals(minecraftClient3.player) && !i11l1llliliili11i.Var05() && (!Speed.speed11.isEnabled() || !Speed.speed11.call016())
         ? MathHelper.lerpAngleDegrees(WorldRender.getTickDelta(), i11l1llliliili11i.ZClass018().GrimGlide(), i11l1llliliili11i.ZClass092().GrimGlide())
         : var1;
   }

   @ModifyExpressionValue(
      method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerpAngleDegrees(FFF)F")
   )
   public float changeHeadYaw(float var1, LivingEntity var2) {
      RotationManager i11l1llliliili11i = ZenithClient.on23().CloudRouter();
      return var2.equals(minecraftClient3.player) && !i11l1llliliili11i.Var05() && (!Speed.speed11.isEnabled() || !Speed.speed11.call016())
         ? MathHelper.lerpAngleDegrees(WorldRender.getTickDelta(), i11l1llliliili11i.ZClass018().GrimGlide(), i11l1llliliili11i.ZClass092().GrimGlide())
         : var1;
   }

   @ModifyExpressionValue(
      method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getLerpedPitch(F)F")
   )
   public float changePitch(float var1, LivingEntity var2) {
      RotationManager i11l1llliliili11i = ZenithClient.on23().CloudRouter();
      return var2.equals(minecraftClient3.player) && !i11l1llliliili11i.Var05() && (!Speed.speed11.isEnabled() || !Speed.speed11.call016())
         ? MathHelper.lerpAngleDegrees(WorldRender.getTickDelta(), i11l1llliliili11i.ZClass018().GuiWalk(), i11l1llliliili11i.ZClass092().GuiWalk())
         : var1;
   }

   @Shadow
   @Nullable
   protected abstract RenderLayer getRenderLayer(LivingEntityRenderState var1, boolean var2, boolean var3, boolean var4);

   @Redirect(
      method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;ZZZ)Lnet/minecraft/client/render/RenderLayer;"
      )
   )
   public RenderLayer renderHook(LivingEntityRenderer var1, LivingEntityRenderState var2, boolean var3, boolean var4, boolean var5) {
      Chams l1ill111l1ll1illlil11 = Chams.chams;
      if (l1ill111l1ll1illlil11.on23(var2)) {
         l1ill111l1ll1illlil11.int399();
         return l1ill111l1ll1illlil11.int401() ? l1ill111l1ll1illlil11.int400() : this.getRenderLayer(var2, var3, true, var5);
      }

      if (!var4 && var2.width == 0.6F) {
         VelocityChangeEvent li1i11ill1ll1iiiil1li1iil111l1 = new VelocityChangeEvent(-1);
         EventManager.call(li1i11ill1ll1iiiil1li1iil111l1);
         if (li1i11ill1ll1iiiil1li1iil111l1.isCancelled()) {
            var4 = true;
         }
      }

      return this.getRenderLayer(var2, var3, var4, var5);
   }

   @ModifyExpressionValue(
      method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getOverlay(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)I")
   )
   private int zenith_overrideOverlay(int overlay) {
      return LivingEntityRendererAccessor.overrideOverlay.orElse(overlay);
   }

   @ModifyExpressionValue(
      method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ColorHelper;mix(II)I")
   )
   private int zenith_modifyModelColor(int color, @Local(argsOnly = true) LivingEntityRenderState state) {
      Chams l1ill111l1ll1illlil11 = Chams.chams;
      if (l1ill111l1ll1illlil11.on23(state)) {
         return l1ill111l1ll1illlil11.int401()
            ? l1ill111l1ll1illlil11.PreventActionEvent(color)
            : l1ill111l1ll1illlil11.ModuleToggleEvent(color);
      }

      VelocityChangeEvent li1i11ill1ll1iiiil1li1iil111l1 = new VelocityChangeEvent(color);
      if (state.invisibleToPlayer) {
         EventManager.call(li1i11ill1ll1iiiil1li1iil111l1);
      }
      return li1i11ill1ll1iiiil1li1iil111l1.ItemUseController();
   }
}
