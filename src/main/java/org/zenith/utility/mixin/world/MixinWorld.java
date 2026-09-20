package org.zenith.utility.mixin.world;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.module.render.WorldTweaks;

@Mixin(World.class)
public class MixinWorld {
   @Inject(method = "getRainGradient", at = @At("HEAD"), cancellable = true)
   public void getRainGradient(float var1, CallbackInfoReturnable<Float> var2) {
      if (WorldTweaks.worldTweaks.isEnabled() && WorldTweaks.worldTweaks.noneRaning.isEnabled()) {
         var2.setReturnValue(0.0F);
         var2.cancel();
      }
   }

   @Inject(method = "getThunderGradient", at = @At("HEAD"), cancellable = true)
   public void getThunderGradient(float var1, CallbackInfoReturnable<Float> var2) {
      if (WorldTweaks.worldTweaks.isEnabled() && WorldTweaks.worldTweaks.noneRaning.isEnabled()) {
         var2.setReturnValue(0.0F);
         var2.cancel();
      }
   }

   @Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
   public void isRaining(CallbackInfoReturnable<Boolean> var1) {
      if (WorldTweaks.worldTweaks.isEnabled() && WorldTweaks.worldTweaks.noneRaning.isEnabled()) {
         var1.setReturnValue(false);
         var1.cancel();
      }
   }

   @Inject(method = "isThundering", at = @At("HEAD"), cancellable = true)
   public void isThundering(CallbackInfoReturnable<Boolean> var1) {
      if (WorldTweaks.worldTweaks.isEnabled() && WorldTweaks.worldTweaks.noneRaning.isEnabled()) {
         var1.setReturnValue(false);
         var1.cancel();
      }
   }
}
