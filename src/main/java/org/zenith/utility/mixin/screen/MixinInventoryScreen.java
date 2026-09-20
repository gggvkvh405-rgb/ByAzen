package org.zenith.utility.mixin.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.core.DrawContextSink;

@Mixin(InventoryScreen.class)
public abstract class MixinInventoryScreen extends RecipeBookScreen<PlayerScreenHandler> {
   public MixinInventoryScreen(PlayerScreenHandler var1, RecipeBookWidget<?> var2, PlayerInventory var3, Text var4) {
      super(var1, var2, var3, var4);
   }

   @Inject(method = "render", at = @At("RETURN"))
   public void zenith_popInventoryScaleAnimation(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      DrawContextSink lll111ll1i1l11l1 = (DrawContextSink)this;
      lll111ll1i1l11l1.zenith_betterMinecraft_popScaleIfNeeded(var1);
      lll111ll1i1l11l1.zenith_betterMinecraft_finishClosingAnimation();
   }
}
