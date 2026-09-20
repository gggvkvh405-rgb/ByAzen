package org.zenith.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

class HudEffectIconRenderer {
   public final HudEffectIcons val047;
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final UiAnimation var1437;
   public final UiAnimation var1438;
   public final boolean boolean80;
   public StatusEffectInstance statusEffectInstance;
   public int int127;

   HudEffectIconRenderer(HudEffectIcons var1, StatusEffectInstance var2, boolean var3) {
      this.val047 = var1;
      this.var1437 = new UiAnimation(150L, 0.01F, Easing.StopUsingItemEvent);
      this.var1438 = new UiAnimation(200L, 1.0F, Easing.CloseScreenEvent);
      this.statusEffectInstance = var2;
      this.boolean80 = var3;
      this.int127 = Math.max(1, var2.getDuration());
   }

   float float205() {
      Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
      Font font1 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
      String s = this.val047.UiAnimation(this.statusEffectInstance);
      String s1 = this.val047.ProtocolMessage(this.statusEffectInstance.getDuration());
      float f = 100.0F;
      float f1 = font.width(s);
      float f2 = font1.width(s1);
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

   boolean int438() {
      return this.boolean80;
   }

   void on23(CustomDrawContext var1, float var2, float var3, float var4) {
      String s = this.val047.Easing(this.statusEffectInstance);
      this.statusEffectInstance = ClientProvider.minecraftClient3
         .player
         .getActiveStatusEffects()
         .values()
         .stream()
         .filter(var2x -> this.val047.Easing(var2x).equals(s))
         .findAny()
         .orElse(this.statusEffectInstance);
      this.var1437.on23(this.val047.set16.contains(s) ? 1.0F : 0.0F);
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
      Font font1 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
      String s1 = this.val047.UiAnimation(this.statusEffectInstance);
      String s2 = this.val047.ProtocolMessage(this.statusEffectInstance.getDuration());
      float f = font1.width(s2);
      float f1 = this.getHeight();
      float f2 = Math.max(this.getHeight(), GuiStyle.PADDING.intValue() + GuiStyle.PADDING.intValue() / 2.0F + f + f1);
      int i = Math.max(0, this.statusEffectInstance.getDuration());
      if (this.val047.set16.contains(s)) {
         this.int127 = Math.max(this.int127, Math.max(1, i));
      }

      float f3 = this.int127 <= 0 ? 0.0F : MathHelper.clamp((float)i / this.int127, 0.0F, 1.0F);
      float f4 = MathHelper.clamp(this.var1438.on23(f3), 0.0F, 1.0F);
      var1.pushMatrix();
      var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + this.getHeight() / 2.0F);
      float f5 = this.var1437.CancellableEvent();
      var1.getMatrices().scale(f5, f5);
      var1.getMatrices().translate(-(var2 + var4 / 2.0F), -(var3 + this.getHeight() / 2.0F));
      float f6 = 6.0F;
      float f7 = var2 + 8.0F;
      float f8 = var3 + (this.getHeight() - f6) / 2.0F + 1.0F;
      var1.pushMatrix();
      var1.getMatrices().translate(f7, f8);
      var1.drawGuiTextureOverlay(
         RenderPipelines.GUI_TEXTURED, net.minecraft.client.gui.hud.InGameHud.getEffectTexture(this.statusEffectInstance.getEffectType()), 0, 0, (int)f6, (int)f6
      );
      var1.popMatrix();
      float f9 = var2 + 8.0F + f6 + GuiStyle.PADDING.intValue();
      var1.drawText(font, s1, f9, var3 + (this.getHeight() - font.height()) / 2.0F, zenithstyle.getTextEnable().getColor());
      float f10 = var2 + var4 - f2 - GuiStyle.PADDING * 2;
      float f11 = f10 + f2 - GuiStyle.PADDING.intValue() / 2.0F - f1;
      float f12 = var3 + (this.getHeight() - f1) / 2.0F;
      var1.drawRoundedRect(f10, var3, f2, this.getHeight(), CornerRadius.MovementInputEvent(1.0F), zenithstyle.getHeaderHudBackground().getColor());
      float f13 = f10 + GuiStyle.PADDING.intValue();
      float f14 = f11 - GuiStyle.PADDING.intValue();
      float f15 = Math.max(0.0F, f14 - f13);
      var1.drawText(font1, s2, f13 + (f15 - f) / 2.0F, var3 + (this.getHeight() - font1.height()) / 2.0F, zenithstyle.getTextEnable().getColor());
      var1.drawArcBorder(f11, f12, f1, f1, 1.0F, 360.0F, 0.5F, zenithstyle.getFieldBorder().getColor());
      var1.drawArcBorder(f11, f12, f1, f1, 1.0F, 360.0F * f4, 0.5F, zenithstyle.getPrimaryColor().getColor());
      var1.popMatrix();
   }

   boolean float206() {
      return this.var1437.CancellableEvent() == 0.0F;
   }
}
