package org.zenith.core;

import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.module.render.Interface;
import org.zenith.module.Module;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

class HudStatusRenderer extends HudStatusState {
   final Module val319;
   final boolean val199;

   HudStatusRenderer(Module var1, boolean var2, long var3) {
      super(var3);
      this.val319 = var1;
      this.val199 = var2;
   }

   @Override
   void on23(CustomDrawContext var1, float var2, float var3, Font var4, ZenithStyle var5, float var6, HudStatusPanel var7) {
      float f = Interface.float212();
      float f1 = 16.0F;
      ArgbColor i11ii1llliilllii1i1 = var5.getHeaderHudBackground().getColor();
      ArgbColor i11ii1llliilllii1i11 = var5.getHudBackground().getColor();
      ArgbColor i11ii1llliilllii1i12 = this.val199 ? var5.getPrimaryColor().getColor() : var5.getTextSecondary().getColor();
      ArgbColor i11ii1llliilllii1i13 = this.val199 ? var5.getTextEnable().getColor() : var5.getTextTertiary().getColor();
      String s = this.val319.getName();
      String s1 = "  "
         + ZenithClient.on23()
            .Easing()
            .translate(this.val199 ? "module.interface.notifications.state.enabled" : "module.interface.notifications.state.disabled");
      float f2 = var4.width(s);
      float f3 = var4.width(s1);
      float f4 = f1 + 4.0F + f2 + f3 + 8.0F;
      var7.height = var6;
      var7.width = Math.max(var7.width, f4);
      var2 += (100.0F - f4) / 2.0F;
      float f5 = this.val146.CancellableEvent();
      Font font = Fonts.NEW_ICONS.getFont(6.0F);
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(var2 + f4 / 2.0F, var3 + var6 / 2.0F);
      var1.getMatrices().scale(f5, f5);
      var1.getMatrices().translate(-(var2 + f4 / 2.0F), -(var3 + var6 / 2.0F));
      var1.drawBlurHud(var2, var3, f4, var6, 21.0F, CornerRadius.MovementInputEvent(f), ArgbColor.var11934);
      var1.drawRoundedRect(var2, var3, f4, var6, CornerRadius.MovementInputEvent(f), i11ii1llliilllii1i1);
      var1.drawRoundedRect(var2, var3, f1, var6, CornerRadius.MovementInputEvent(f), i11ii1llliilllii1i11);
      String s2 = this.val319.getCategory().getIcon();
      float f6 = var2 + (f1 - font.width(s2)) / 2.0F;
      float f7 = var3 + (var6 - font.height()) / 2.0F;
      var1.drawText(font, s2, f6, f7, i11ii1llliilllii1i12);
      float f8 = var2 + f1 + 4.0F;
      float f9 = var3 + (var6 - var4.height()) / 2.0F;
      var1.drawText(var4, s, f8, f9, i11ii1llliilllii1i12);
      var1.drawText(var4, s1, f8 + f2, f9, i11ii1llliilllii1i13);
      var1.getMatrices().popMatrix();
   }
}
