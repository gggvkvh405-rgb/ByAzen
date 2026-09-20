package org.zenith.utility.mixin.world;

import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.module.misc.ShulkerPreview;

@Mixin(ItemStack.class)
public abstract class MixinShulkerLookItemStack {
   @Inject(method = "getTooltipData()Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
   public void injectShulkerTooltipData(CallbackInfoReturnable<Optional<TooltipData>> var1) {
      Optional<TooltipData> optional = ShulkerPreview.InventoryUtils((ItemStack)(Object)this);
      if (optional.isPresent()) {
         var1.setReturnValue(optional);
      }
   }
}
