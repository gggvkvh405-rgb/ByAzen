package org.zenith.core;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

class HudInvRowRenderer {
   public final UiAnimation var14311 = new UiAnimation(150L, 0.01F, Easing.StopUsingItemEvent);
   public final UiAnimation var14312;
   public final ItemStack itemStack9;
   public final String string63;
   public final Supplier<Float> supplier3;
   public final BooleanSupplier booleanSupplier2;
   public final Supplier<Integer> getThis4;
   public final HudInventoryPanel this_0;

   HudInvRowRenderer(HudInventoryPanel var1, ItemStack var2, String var3, float var4, Supplier<Float> var5, BooleanSupplier var6, Supplier<Integer> var7) {
      this.this_0 = var1;
      this.itemStack9 = var2;
      this.string63 = var3;
      this.supplier3 = var5;
      this.booleanSupplier2 = var6;
      this.getThis4 = var7;
      this.var14312 = new UiAnimation(200L, var4, Easing.CloseScreenEvent);
   }

   float float205() {
      Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
      Font font1 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
      String s = this.BotFeatureRegistry(this.getThis4.get());
      float f = 100.0F;
      float f1 = font.width(this.string63);
      float f2 = font1.width(s);
      float f3 = this.getHeight();
      float f4 = Math.max(this.getHeight(), GuiStyle.PADDING.intValue() + GuiStyle.PADDING.intValue() / 2.0F + f2 + f3);
      float f5 = 8 + GuiStyle.PADDING * 2 + f4;
      float f6 = f - (f5 + 8.0F);
      if (f6 < 8.0F + f1 + 8.0F) {
         f += f1 + 8.0F + 8.0F - f6;
      }

      return f;
   }

   float getHeight() {
      return 7.0F;
   }

   void on23(CustomDrawContext var1, float var2, float var3, float var4) {
      float f = this.float207();
      this.var14311.on23(f > 0.0F ? 1.0F : 0.0F);
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
      Font font1 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
      String s = this.BotFeatureRegistry(this.getThis4.get());
      float f1 = font1.width(s);
      float f2 = this.getHeight();
      float f3 = Math.max(this.getHeight(), GuiStyle.PADDING.intValue() + GuiStyle.PADDING.intValue() / 2.0F + f1 + f2);
      var1.pushMatrix();
      var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + this.getHeight() / 2.0F);
      var1.getMatrices().scale(this.var14311.CancellableEvent(), this.var14311.CancellableEvent());
      var1.getMatrices().translate(-(var2 + var4 / 2.0F), -(var3 + this.getHeight() / 2.0F));
      float f4 = 0.35F;
      float f5 = 16.0F * f4;
      float f6 = var2 + 8.0F;
      float f7 = var3 + (this.getHeight() - f5) / 2.0F - 0.1F;
      var1.pushMatrix();
      var1.getMatrices().translate(f6, f7);
      var1.getMatrices().scale(f4, f4);
      var1.drawItem(this.itemStack9, 0, 0);
      var1.popMatrix();
      float f8 = var2 + 8.0F + f5 + GuiStyle.PADDING.intValue();
      var1.drawText(font, this.string63, f8, var3 + (this.getHeight() - font.height()) / 2.0F, zenithstyle.getTextEnable().getColor());
      float f9 = var2 + var4 - f3 - GuiStyle.PADDING * 2;
      float f10 = f9 + f3 - GuiStyle.PADDING.intValue() / 2.0F - f2;
      float f11 = var3 + (this.getHeight() - f2) / 2.0F;
      var1.drawRoundedRect(f9, var3, f3, this.getHeight(), CornerRadius.MovementInputEvent(1.0F), zenithstyle.getHeaderHudBackground().getColor());
      float f12 = f9 + GuiStyle.PADDING.intValue();
      float f13 = f10 - GuiStyle.PADDING.intValue();
      float f14 = Math.max(0.0F, f13 - f12);
      var1.drawText(font1, s, f12 + (f14 - f1) / 2.0F, var3 + (this.getHeight() - font1.height()) / 2.0F, zenithstyle.getTextEnable().getColor());
      var1.drawArcBorder(f10, f11, f2, f2, 1.0F, 360.0F, 0.5F, zenithstyle.getFieldBorder().getColor());
      var1.drawArcBorder(f10, f11, f2, f2, 1.0F, 360.0F * f, 0.5F, zenithstyle.getPrimaryColor().getColor());
      var1.popMatrix();
   }

   boolean float206() {
      return this.var14311.CancellableEvent() == 0.0F && this.booleanSupplier2.getAsBoolean();
   }

   public float float207() {
      float f = MathHelper.clamp(this.supplier3.get(), 0.0F, 1.0F);
      this.var14312.on23(f);
      return MathHelper.clamp(this.var14312.CancellableEvent(), 0.0F, 1.0F);
   }

   public String BotFeatureRegistry(int var1) {
      if (var1 < 0) {
         var1 = 0;
      }

      int i = var1 / 60;
      int j = var1 % 60;
      return String.format("%d:%02d", i, j);
   }
}
