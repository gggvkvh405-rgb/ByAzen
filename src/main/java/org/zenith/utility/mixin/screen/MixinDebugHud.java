package org.zenith.utility.mixin.screen;

import java.util.List;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.zenith.util.TextUtils;

@Mixin(DebugHud.class)
public abstract class MixinDebugHud {
   @ModifyArg(
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/DebugHud;drawText(Lnet/minecraft/client/gui/DrawContext;Ljava/util/List;Z)V"
      ),
      index = 1
   )
   private List<String> zenith_randomizeDebugText(List<String> lines) {
      return TextUtils.isActive() ? TextUtils.PotionItemBuilder(lines) : lines;
   }
}
