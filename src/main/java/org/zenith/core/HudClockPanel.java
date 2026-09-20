package org.zenith.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElement;
import org.zenith.module.render.Interface;
import org.zenith.module.movement.Timer;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudClockPanel extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final float float252 = 26.0F;
   public static final float float253 = 26.0F;
   public static final float float254 = 2.0F;
   public static final float float255 = 76.0F;
   public final UiAnimation var14346 = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);
   public final UiAnimation var14347 = new UiAnimation(180L, 0.0F, Easing.CloseScreenEvent);

   public HudClockPanel(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      this.width = 26.0F + GuiStyle.PADDING.intValue() * 2.0F + 76.0F;
      this.height = 26.0F;
   }

   @Override
   public void on23(CustomDrawContext var1) {
      Timer iii1ii11i111 = Timer.timer;
      boolean flag = minecraftClient3.currentScreen instanceof ChatScreen;
      boolean flag1 = flag || iii1ii11i111.isEnabled();
      float f = iii1ii11i111.isEnabled() ? iii1ii11i111.call048() : 0.72F;
      String s = "Timer";
      String s1 = Math.round(f * 100.0F) + "%";
      this.var14346.on23(flag1);
      if (this.var14346.CancellableEvent() <= 0.01F) {
         this.width = 26.0F + GuiStyle.PADDING.intValue() * 2.0F + 76.0F;
         this.height = 26.0F;
      } else {
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         Font font = Fonts.NEW_ICONS.getFont(6.2F);
         Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font2 = Fonts.NEW_SEMIBOLD.getFont(5.3F);
         float f1 = MathHelper.clamp(this.var14347.on23(f), 0.0F, 1.0F);
         float f2 = font2.width(s1);
         float f3 = Math.max(18.0F, f2 + GuiStyle.PADDING.intValue() * 1.5F);
         float f4 = Math.max(76.0F, font1.width(s) + GuiStyle.PADDING.intValue() + f3);
         this.width = 26.0F + GuiStyle.PADDING.intValue() * 2.0F + f4;
         this.height = 26.0F;
         float f5 = Interface.float212();
         float f6 = this.x + 26.0F + GuiStyle.PADDING.intValue();
         float f7 = this.x + this.width - GuiStyle.PADDING.intValue();
         float f8 = f7 - f3;
         float f9 = this.y + 8.0F;
         float f10 = 8.0F;
         float f11 = this.y + 7.5F;
         float f12 = f7 - f6;
         float f13 = this.y + this.height - 5.0F;
         var1.pushMatrix();
         var1.getMatrices().translate(this.x + this.width / 2.0F, this.y + this.height / 2.0F);
         var1.getMatrices().scale(this.var14346.CancellableEvent(), this.var14346.CancellableEvent());
         var1.getMatrices().translate(-(this.x + this.width / 2.0F), -(this.y + this.height / 2.0F));
         var1.drawBlurHud(this.x, this.y, this.width, this.height, 21.0F, CornerRadius.MovementInputEvent(f5), ArgbColor.var11934);
         var1.drawRoundedRect(this.x, this.y, this.width, this.height, CornerRadius.MovementInputEvent(f5), zenithstyle.getHudBackground().getColor());
         var1.drawRoundedRect(this.x, this.y, 26.0F, this.height, CornerRadius.BotTickEvent(f5, f5), zenithstyle.getHeaderHudBackground().getColor());
         var1.drawRoundedRect(f8, f11, f3, f10, CornerRadius.MovementInputEvent(1.4F), zenithstyle.getHeaderHudBackground().getColor());
         String s2 = iii1ii11i111.getCategory().getIcon();
         var1.drawText(
            font, s2, this.x + (26.0F - font.width(s2)) / 2.0F, this.y + (this.height - font.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor()
         );
         var1.drawText(font1, s, f6, f9, zenithstyle.getTextEnable().getColor());
         var1.drawText(font2, s1, f8 + (f3 - f2) / 2.0F, f11 + (f10 - font2.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor());
         var1.drawRoundedRect(f6, f13, f12, 2.0F, CornerRadius.MovementInputEvent(0.6F), zenithstyle.getFieldBorder().getColor());
         if (f1 > 0.0F) {
            var1.drawRoundedRect(f6, f13, f12 * f1, 2.0F, CornerRadius.MovementInputEvent(0.6F), zenithstyle.getPrimaryColor().getColor());
         }

         var1.popMatrix();
      }
   }
}
