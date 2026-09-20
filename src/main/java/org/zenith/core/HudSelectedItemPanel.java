package org.zenith.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElement;
import org.zenith.module.render.Interface;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudSelectedItemPanel extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final UiAnimation var14342 = new UiAnimation(300L, Easing.EventWindowSizeChanged);
   public final UiAnimation var14343 = new UiAnimation(150L, Easing.EventWindowSizeChanged);
   public String string102 = "";
   public float float250 = 0.0F;
   public float float251 = 0.0F;

   public HudSelectedItemPanel(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void on23(CustomDrawContext var1) {
      if (minecraftClient3.player == null) {
         this.var14342.on23(0.0F);
         if (this.var14342.CancellableEvent() > 0.01F) {
            this.UiAnimation(var1, this.var14342.CancellableEvent());
         }
      } else {
         this.var14342.on23(1.0F);
         if (!(this.var14342.CancellableEvent() <= 0.01F)) {
            String s = "";

            for (int i = 9; i < 36; i++) {
               ItemStack itemstack = minecraftClient3.player.getInventory().getStack(i);
               s = s + itemstack.getItem().toString() + itemstack.getCount();
            }

            if (!s.equals(this.string102)) {
               this.var14343.on23(0.0F);
               this.string102 = s;
            }

            this.var14343.on23(1.0F);
            this.UiAnimation(var1, this.var14342.CancellableEvent() * this.var14343.CancellableEvent());
         }
      }
   }

   public void UiAnimation(CustomDrawContext var1, float var2) {
      if (minecraftClient3.player != null) {
         Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
         Font font1 = Fonts.ICONS.getFont(4.5F);
         float f = 14.0F;
         float f1 = 1.0F;
         float f2 = 4.0F;
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         byte b0 = 9;
         byte b1 = 3;
         float f3 = b0 * f + (b0 - 1) * f1;
         float f4 = b1 * f + (b1 - 1) * 0.5F;
         this.width = f3;
         this.height = f4;
         float f5 = Interface.float212();
         this.float250 = this.width;
         this.float251 = this.height;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(this.x + this.width / 2.0F, this.y + this.height / 2.0F);
         var1.getMatrices().scale(var2, var2);
         var1.getMatrices().translate(-(this.x + this.width / 2.0F), -(this.y + this.height / 2.0F));
         var1.drawBlurHud(this.x, this.y, this.width, this.height, 21.0F, CornerRadius.MovementInputEvent(f5), ArgbColor.var11934);
         var1.drawRoundedRect(this.x, this.y, this.width, this.height, CornerRadius.MovementInputEvent(f5), zenithstyle.getHudBackground().getColor());
         float f6 = (f - font1.width("X")) / 2.0F;

         for (int i = 0; i < b1; i++) {
            for (int j = 0; j < b0; j++) {
               int k = 9 + i * 9 + j;
               ItemStack itemstack = minecraftClient3.player.getInventory().getStack(k);
               float f7 = this.x + j * (f + f1);
               float f8 = this.y + i * (f + 0.5F);
               float f9 = Math.min(f5, f / 2.0F);
               CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(f9);
               if (!itemstack.isEmpty()) {
                  float f10 = 8.0F;
                  float f11 = f10 / 16.0F;
                  var1.pushMatrix();
                  var1.getMatrices().translate(f7 + (f - f10) / 2.0F, f8 + (f - f10) / 2.0F);
                  var1.getMatrices().scale(f11, f11);
                  var1.drawItem(itemstack, 0, 0);
                  var1.drawItemBar(itemstack, 0, 0);
                  var1.drawCooldownProgress(itemstack, 0, 0);
                  var1.popMatrix();
                  if (itemstack.getCount() > 1) {
                     String s = "x" + itemstack.getCount();
                     float f12 = font.width(s);
                     float f13 = f7 + f - f12 - 1.5F;
                     float f14 = f8 + f - font.height() - 1.0F;
                     var1.drawText(font, s, f13, f14, zenithstyle.getTextEnable().getColor());
                  }
               } else {
                  var1.drawText(
                     font1, "M", f7 + f6, f8 + (f - font1.height()) / 2.0F, ZenithClient.on23().TextScanner().getCurrentStyle().getTextTertiary().getColor()
                  );
               }
            }
         }

         var1.getMatrices().popMatrix();
      }
   }
}
