package org.zenith.utility.mixin.world;

import net.minecraft.client.world.ClientWorld.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.module.render.WorldTweaks;

@Mixin(Properties.class)
public abstract class MixinClientWorldProperties {
   @Shadow
   public long timeOfDay;

   @Shadow
   public abstract boolean isRaining();

   @Inject(method = "setTimeOfDay", at = @At("HEAD"), cancellable = true)
   public void setTimeOfDayHook(long var1, CallbackInfo var3) {
      WorldTweaks il11llii1l1 = WorldTweaks.worldTweaks;
      if (il11llii1l1.isEnabled()) {
         this.timeOfDay = (long)(il11llii1l1.timeSetting.getCurrent() * 1000.0F);
         var3.cancel();
      }
   }
}
