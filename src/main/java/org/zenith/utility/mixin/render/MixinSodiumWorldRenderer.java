package org.zenith.utility.mixin.render;

import java.lang.reflect.Field;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer", remap = false)
public class MixinSodiumWorldRenderer {
   @Unique
   private static Field zenith_renderSectionManagerField;

   @Inject(method = "scheduleRebuildForChunk", at = @At("HEAD"), cancellable = true, remap = false)
   public void zenith_skipUninitializedRebuild(int var1, int var2, int var3, boolean var4, CallbackInfo var5) {
      if (this.zenith_isRenderSectionManagerMissing()) {
         var5.cancel();
      }
   }

   @Unique
   public boolean zenith_isRenderSectionManagerMissing() {
      try {
         Field field = zenith_renderSectionManagerField;
         if (field == null) {
            field = this.getClass().getDeclaredField("renderSectionManager");
            field.setAccessible(true);
            zenith_renderSectionManagerField = field;
         }

         return field.get(this) == null;
      } catch (Exception exception) {
         return true;
      }
   }
}
