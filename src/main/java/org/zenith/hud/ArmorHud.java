package org.zenith.hud;


import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.core.ClientProvider;
import java.util.ArrayList;
import java.util.List;
import org.zenith.ZenithClient;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.module.render.Interface;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class ArmorHud extends HudElement {
   public List<ArmorHud.TextValue> list84 = new ArrayList<>();

   public ArmorHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      float f = 22.0F;
      this.width = f * 4.0F;
      this.height = f;

      for (int i = 0; i < 4; i++) {
         this.list84.add(new ArmorHud.TextValue(this, i));
      }
   }

   @Override
   public void on23(CustomDrawContext var1) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      float f = 22.0F;
      this.width = f * 4.0F;
      this.height = f;
      float f1 = Interface.float212();
      var1.pushMatrix();
      var1.drawBlurHud(this.x, this.y, this.width, this.height, 21.0F, CornerRadius.MovementInputEvent(f1), ArgbColor.var11934);
      var1.drawRoundedRect(this.x, this.y, this.width, this.height, CornerRadius.MovementInputEvent(f1), zenithstyle.getHudBackground().getColor());
      float f2 = this.x;
      float f3 = this.y;

      for (ArmorHud.TextValue iiiililli1111i1lil_ii1il11l111ii11iil : this.list84) {
         iiiililli1111i1lil_ii1il11l111ii11iil.on23(var1, f2, f3, zenithstyle);
         f2 += f;
      }

      var1.popMatrix();
   }


   public static class TextValue {
      public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
      public final int int125;

      public TextValue(ArmorHud var1, int var2) {
         this.int125 = var2;
      }

      public void on23(CustomDrawContext var1, float var2, float var3, ZenithStyle var4) {
         float f = Interface.float212();
         CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(f);
         Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
         ArgbColor i11ii1llliilllii1i1 = var4.getTextEnable().getColor();
         ItemStack itemstack = ClientProvider.minecraftClient3.player.getInventory().getStack(39 - this.int125);
         if (!itemstack.isEmpty()) {
            var1.pushMatrix();
            var1.getMatrices().translate(var2 + 4.6F, var3 + 4.6F);
            var1.getMatrices().scale(0.8F, 0.8F);
            var1.drawItem(itemstack, 0, 0);
            var1.drawItemBar(itemstack, 0, 0);
            var1.drawCooldownProgress(itemstack, 0, 0);
            var1.popMatrix();
            if (itemstack.getCount() > 1) {
               String s = "x" + itemstack.getCount();
               float f1 = font.width(s);
               float f2 = var2 + 22.0F - f1 - 3.0F;
               float f3 = var3 + 22.0F - font.height() - 3.0F;
               var1.drawText(font, s, f2, f3, i11ii1llliilllii1i1);
            }
         } else {
            Font font1 = Fonts.ICONS.getFont(4.5F);
            var1.drawText(
               font1,
               "M",
               var2 + (22.0F - font1.width("M")) / 2.0F,
               var3 + (22.0F - font1.height()) / 2.0F,
               ZenithClient.on23().TextScanner().getCurrentStyle().getTextTertiary().getColor()
            );
         }
      }
   }
}
