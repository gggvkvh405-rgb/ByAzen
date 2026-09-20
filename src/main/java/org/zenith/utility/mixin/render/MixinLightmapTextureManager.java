package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.module.render.WorldTweaks;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {
   @ModifyExpressionValue(method = "update(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"))
   public Object injectXRayFullBright(Object var1) {
      WorldTweaks il11llii1l1 = WorldTweaks.worldTweaks;
      return il11llii1l1.isEnabled() && il11llii1l1.modeSetting19.ConfigJsonUtil(0)
         ? Math.max((Double)var1, il11llii1l1.brightSetting.getCurrent() * 10.0F)
         : var1;
   }
}
