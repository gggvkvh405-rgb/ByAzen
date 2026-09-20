package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Predicate;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.ZenithClient;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.core.ClientProvider;
import org.zenith.event.EventGetBasicProjectionMatrixHook;
import org.zenith.event.CrosshairTargetUpdateEvent;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.EventRenderScreenHook;
import org.zenith.event.FovEvent;
import org.zenith.module.misc.NoFriendDamage;
import org.zenith.module.render.ShaderHand;
import org.zenith.module.render.WorldTweaks;
import org.zenith.render.HandShaderManager;
import org.zenith.render.WorldRender;
import org.zenith.rotation.Rotation;
import org.zenith.utility.render.display.base.HudDrawContext;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
   @Unique
   private static String zenith$hudRenderErrorLogged;
   @Shadow @Final private GuiRenderer guiRenderer;
   @Shadow @Final private GuiRenderState guiState;
   @Shadow @Final private FogRenderer fogRenderer;

   @Shadow
   public abstract float getFarPlaneDistance();

   @Shadow
   protected abstract void renderHand(float tickProgress, boolean sleeping, Matrix4f positionMatrix);

   @Inject(
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/state/GuiRenderState;clear()V", shift = At.Shift.AFTER)
   )
   private void beginZenithGuiDeferral(RenderTickCounter tickCounter, boolean tick, CallbackInfo callbackInfo) {
      org.zenith.render.LegacyImmediateRenderer.beginGuiDeferral();
   }

   @Inject(
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
         shift = At.Shift.AFTER
      )
   )
   private void flushZenithGuiDeferral(RenderTickCounter tickCounter, boolean tick, CallbackInfo callbackInfo) {
      org.zenith.render.LegacyImmediateRenderer.flushGuiDeferral();

      if (org.zenith.render.LegacyImmediateRenderer.hasDeferredGuiOverlays()) {
         this.guiState.clear();
         org.zenith.render.LegacyImmediateRenderer.extractDeferredGuiOverlays();
         this.guiRenderer.render(this.fogRenderer.getFogBuffer(FogRenderer.FogType.NONE));
         this.guiState.clear();
      }
   }

   @Inject(method = "getBasicProjectionMatrix", at = @At("TAIL"), cancellable = true)
   public void getBasicProjectionMatrixHook(float var1, CallbackInfoReturnable<Matrix4f> var2) {
      EventGetBasicProjectionMatrixHook i1il11li11liill1l = new EventGetBasicProjectionMatrixHook();
      EventManager.call(i1il11li11liill1l);
      if (i1il11li11liill1l.isCancelled()) {
         Matrix4f matrix4f = new Matrix4f();
         matrix4f.perspective(var1 * (float) (Math.PI / 180.0), i1il11li11liill1l.Spider(), 0.05F, this.getFarPlaneDistance());
         var2.setReturnValue(matrix4f);
      }
   }

   @Inject(method = "updateCrosshairTarget", at = @At("RETURN"))
   public void getBasicProjectionMatrixHook(float var1, CallbackInfo var2) {
      EventManager.call(new CrosshairTargetUpdateEvent());
   }

   @ModifyExpressionValue(method = "getFov", at = @At(value = "INVOKE", target = "Ljava/lang/Integer;intValue()I", remap = false))
   public int hookGetFov(int var1) {
      FovEvent iililil11ii1i = new FovEvent();
      EventManager.call(iililil11ii1i);
      return iililil11ii1i.isCancelled() ? iililil11ii1i.AHHelper() : var1;
   }

   @Inject(method = "renderWorld", at = @At("HEAD"))
   public void beginWorldTweaksSaturation(RenderTickCounter var1, CallbackInfo var2) {
      WorldTweaks.worldTweaks.BotDisconnectEvent(true);
   }

   @Inject(method = "renderWorld", at = @At("RETURN"))
   public void endWorldTweaksSaturation(RenderTickCounter var1, CallbackInfo var2) {
      WorldTweaks.worldTweaks.BotDisconnectEvent(false);
   }

   @Redirect(
      method = "renderWorld",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderHand(FZLorg/joml/Matrix4f;)V")
   )
   public void afterRenderHand(GameRenderer var1, float var2, boolean sleeping, Matrix4f var3) {
      ShaderHand llillll1i1i11iiii1ii11il = ShaderHand.shaderHand;
      if (llillll1i1i11iiii1ii11il != null && llillll1i1i11iiii1ii11il.isEnabled()) {
         if (!HandShaderManager.isInitialized()) {
            HandShaderManager.float246();
         }
         HandShaderManager.float247();
         llillll1i1i11iiii1ii11il.on23(() -> this.renderHand(var2, sleeping, var3), var2);
      } else {
         this.renderHand(var2, sleeping, var3);
      }
   }

   @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 2))
   public void hookWorldRender(
      RenderTickCounter var1,
      CallbackInfo var2,
      @Local(ordinal = 0) Matrix4f projectionMatrix,
      @Local(ordinal = 1) Matrix4f positionMatrix
   ) {
      MatrixStack matrixstack = new MatrixStack();
      matrixstack.multiplyPositionMatrix(positionMatrix);
      WorldRender.on23(new Matrix4f(projectionMatrix));
      WorldRender.UiAnimation(new Matrix4f(positionMatrix));
      WorldRender.Easing(new Matrix4f(positionMatrix));
      EventHookWorldRender i111liliill1iii1iiii1 = new EventHookWorldRender(matrixstack, var1.getTickProgress(false));
      EventManager.call(i111liliill1iii1iiii1);
      WorldRender.ItemRegistry(i111liliill1iii1iiii1.ClanUpgrade());
   }

   @Inject(
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;", ordinal = 0)
   )
   public void renderScreenHook(
      RenderTickCounter var1, boolean var2, CallbackInfo var3, @Local(ordinal = 0) int var4, @Local(ordinal = 1) int var5, @Local DrawContext var6
   ) {
      var6.getMatrices().pushMatrix();

      try {
         EventManager.call(new EventRenderScreenHook(HudDrawContext.of(var6, var4, var5, ClientProvider.minecraftClient3.getRenderTickCounter().getTickProgress(false))));
         MsdfRenderer.flushBatch();
         org.zenith.render.LegacyRenderBridge.clear(256);
      } catch (Exception exception) {
         if (zenith$hudRenderErrorLogged == null || !zenith$hudRenderErrorLogged.equals(String.valueOf(exception))) {
            zenith$hudRenderErrorLogged = String.valueOf(exception);
            System.err.println("[Zenith] HUD render failed:");
            exception.printStackTrace();
         }
      }

      var6.getMatrices().popMatrix();
   }

}
