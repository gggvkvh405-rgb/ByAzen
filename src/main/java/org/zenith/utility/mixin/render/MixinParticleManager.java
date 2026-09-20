package org.zenith.utility.mixin.render;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.mixin.accessors.ParticleAccessor;

@Mixin(ParticleManager.class)
public class MixinParticleManager {
   @Shadow
   protected ClientWorld world;

   @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true)
   public void zenith_dropParticleWithoutManagerWorld(
      ParticleEffect var1, double var2, double var4, double var6, double var8, double var10, double var12, CallbackInfoReturnable<Particle> var14
   ) {
      if (this.world == null) {
         var14.setReturnValue(null);
      }
   }

   @Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), cancellable = true)
   public void zenith_dropWorldlessParticle(Particle var1, CallbackInfo var2) {
      if (var1 == null || ((ParticleAccessor)var1).zenith_getWorld() == null) {
         var2.cancel();
      }
   }
}
