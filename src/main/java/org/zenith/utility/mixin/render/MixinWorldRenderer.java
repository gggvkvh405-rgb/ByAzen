package org.zenith.utility.mixin.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.PostEffectProcessor.FramebufferSet;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.core.FrameGraphPass;
import org.zenith.core.PostProcessPass;
import org.zenith.render.ShaderPostProcess;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
   @Shadow
   @Final
   public MinecraftClient client;
   @Shadow
   @Final
   public DefaultFramebufferSet framebufferSet;
   @Shadow
   public ClientWorld world;
   @Shadow
   public BuiltChunkStorage chunks;

   @Inject(method = "isRenderingReady(Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
   public void zenith_guardRenderingReadyWithoutChunks(BlockPos var1, CallbackInfoReturnable<Boolean> var2) {
      if (this.world == null || this.chunks == null) {
         var2.setReturnValue(false);
      }
   }

   @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
   public void zenith_skipTickWithoutWorld(CallbackInfo var1) {
      if (this.world == null) {
         var1.cancel();
      }
   }

   @Inject(method = "render", at = @At("HEAD"), cancellable = true)
   public void zenith_skipMainRenderWithoutWorld(CallbackInfo var1) {
      if ((Object)this == this.client.worldRenderer && (this.world == null || this.chunks == null)) {
         var1.cancel();
      }
   }

   @Inject(method = "scheduleBlockRerenderIfNeeded", at = @At("HEAD"), cancellable = true)
   public void zenith_skipOffscreenBlockRerender(BlockPos var1, BlockState var2, BlockState var3, CallbackInfo var4) {
      if (this.world != this.client.world) {
         var4.cancel();
      }
   }

   @Inject(method = "scheduleBlockRenders(IIIIII)V", at = @At("HEAD"), cancellable = true)
   public void zenith_skipOffscreenBlockRenders(int var1, int var2, int var3, int var4, int var5, int var6, CallbackInfo var7) {
      if (this.world != this.client.world) {
         var7.cancel();
      }
   }

   @Inject(method = "scheduleSectionRender(Lnet/minecraft/util/math/BlockPos;Z)V", at = @At("HEAD"), cancellable = true)
   public void zenith_skipOffscreenSectionRender(BlockPos var1, boolean var2, CallbackInfo var3) {
      if (this.world != this.client.world) {
         var3.cancel();
      }
   }

   @Inject(method = "scheduleChunkRenders3x3x3(III)V", at = @At("HEAD"), cancellable = true)
   public void zenith_skipOffscreenChunkRenders3x3x3(int var1, int var2, int var3, CallbackInfo var4) {
      if (this.world != this.client.world) {
         var4.cancel();
      }
   }

   @Redirect(
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(Lnet/minecraft/client/render/FrameGraphBuilder;IILnet/minecraft/client/gl/PostEffectProcessor$FramebufferSet;)V",
         ordinal = 0
      )
   )
   public void zenith_renderShaderEspOutline(PostEffectProcessor var1, FrameGraphBuilder var2, int var3, int var4, FramebufferSet var5) {
      boolean flag = FrameGraphPass.on23(var2, var3, var4, var5);
      flag |= PostProcessPass.on23(var2, var3, var4, var5);
      if (!flag) {
         var1.render(var2, var3, var4, var5);
      }
   }

   @Inject(
      method = "renderSky(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Camera;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/FramePass;setRenderer(Ljava/lang/Runnable;)V", shift = Shift.AFTER)
   )
   public void zenith_renderShaderFogOnSky(FrameGraphBuilder var1, Camera var2, GpuBufferSlice var3, CallbackInfo var4) {
      int i = this.client.getFramebuffer().textureWidth;
      int j = this.client.getFramebuffer().textureHeight;
      ShaderPostProcess.render(var1, i, j, this.framebufferSet, var2);
   }
}
