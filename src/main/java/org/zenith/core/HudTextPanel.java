package org.zenith.core;

import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElement;
import org.zenith.module.render.Interface;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudTextPanel extends HudElement {
   public HudTextPanel(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void on23(CustomDrawContext var1) {
      Font font = Fonts.NEW_ICONS.getFont(6.6F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
      float f = 7.0F;
      float f1 = 24.0F;
      float f2 = Math.max(
         font2.width(ClientSession.DISPLAY_CREDIT_FIRST_LINE),
         f + GuiStyle.PADDING.intValue() / 2.0F + font1.width(ClientSession.DISPLAY_CREDIT_SECOND_LINE)
      );
      this.width = Math.max(65.0F, f1 + GuiStyle.PADDING.intValue() * 2.0F + f2);
      this.height = f1;
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      var1.drawBlurHud(this.x, this.y, this.width, f1, 12.0F, CornerRadius.MovementInputEvent(Interface.float212()), ArgbColor.var11934);
      var1.drawRoundedRect(this.x, this.y, this.width, f1, CornerRadius.MovementInputEvent(Interface.float212()), zenithstyle.getHudBackground().getColor());
      var1.drawRoundedRect(this.x, this.y, f1, f1, CornerRadius.MovementInputEvent(Interface.float212()), zenithstyle.getHeaderHudBackground().getColor());
      var1.drawText(font, "1", this.x + (f1 - font.width("1")) / 2.0F, this.y + (f1 - font.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor());
      float f3 = this.x + f1 + GuiStyle.PADDING.intValue();
      float f4 = this.y + (f1 - (font2.height() + 1.0F + f)) / 2.0F;
      var1.drawText(font2, ClientSession.DISPLAY_CREDIT_FIRST_LINE, f3, f4, zenithstyle.getTextTertiary().getColor());
      var1.drawRoundedTexture(
         ZenithClient.on23("icons/avatar.png"), f3, f4 + font2.height() + 2.0F, f, f, CornerRadius.MovementInputEvent(2.0F), ArgbColor.var11934
      );
      var1.drawText(
         font1,
         ClientSession.DISPLAY_CREDIT_SECOND_LINE,
         f3 + f + GuiStyle.PADDING.intValue() / 2.0F,
         f4 + font2.height() + 2.0F,
         zenithstyle.getTextEnable().getColor()
      );
   }
}
