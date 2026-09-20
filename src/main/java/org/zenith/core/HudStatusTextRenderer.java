package org.zenith.core;

import net.minecraft.text.Text;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.module.render.Interface;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

class HudStatusTextRenderer extends HudStatusState {
   final String val198;
   final Text val318;

   HudStatusTextRenderer(String var1, Text var2, long var3) {
      super(var3);
      this.val198 = var1;
      this.val318 = var2;
   }

   @Override
   void on23(CustomDrawContext var1, float var2, float var3, Font var4, ZenithStyle var5, float var6, HudStatusPanel var7) {
      float f = Interface.float212();
      float f1 = 16.0F;
      ArgbColor i11ii1llliilllii1i1 = var5.getHeaderHudBackground().getColor();
      ArgbColor i11ii1llliilllii1i11 = var5.getHudBackground().getColor();
      ArgbColor i11ii1llliilllii1i12 = var5.getPrimaryColor().getColor();
      float f2 = var4.width(this.val318);
      float f3 = f1 + 4.0F + f2 + 8.0F;
      var7.height = var6;
      var7.width = Math.max(var7.width, f3);
      var2 += (100.0F - f3) / 2.0F;
      float f4 = this.val146.CancellableEvent();
      Font font = Fonts.ICONS.getFont(this.val198.equals("Y") ? 8.0F : 6.0F);
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(var2 + f3 / 2.0F, var3 + var6 / 2.0F);
      var1.getMatrices().scale(f4, f4);
      var1.getMatrices().translate(-(var2 + f3 / 2.0F), -(var3 + var6 / 2.0F));
      var1.drawBlurHud(var2, var3, f3, var6, 21.0F, CornerRadius.MovementInputEvent(f), ArgbColor.var11934);
      var1.drawRoundedRect(var2, var3, f3, var6, CornerRadius.MovementInputEvent(f), i11ii1llliilllii1i1);
      var1.drawRoundedRect(var2, var3, f1, var6, CornerRadius.MovementInputEvent(f), i11ii1llliilllii1i11);
      float f5 = var2 + (f1 - font.width(this.val198)) / 2.0F;
      float f6 = var3 + (var6 - font.height()) / 2.0F;
      var1.drawText(font, this.val198, f5, f6, i11ii1llliilllii1i12);
      float f7 = var2 + f1 + 4.0F;
      float f8 = var3 + (var6 - var4.height()) / 2.0F;
      var1.drawText(var4, this.val318, f7, f8);
      var1.getMatrices().popMatrix();
   }
}
