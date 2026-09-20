package org.zenith.client.screens.autosbor.panels.body;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.Vector2f;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.autosbor.AutoSborStyle;
import org.zenith.core.ItemServiceBase;
import org.zenith.core.MenuScreenId;
import org.zenith.hud.SearchBox;
import org.zenith.hud.SearchBox;
import org.zenith.util.ArgbColor;
import org.zenith.util.MathUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class SborHeader {
   public static final float panelXOffset = 140.0F;
   public static final float panelYOffset = 4.0F;
   public static final float panelWidth = 336.0F;
   public static final float panelHeight = 23.0F;
   public static final float buttonSize = 23.0F;
   public static final float priceBoxWidth = 128.0F;
   public static final float textOffsetX = 8.0F;
   public static final float itemIconSize = 8.0F;
   public static final float itemIconScale = 0.5F;
   public static final float itemNameOffsetX = 8.0F;
   public static final CornerRadius panelRadius = CornerRadius.MovementInputEvent(7.0F);
   public static final Font priceFont = Fonts.MEDIUM.getFont(6.0F);
   public static final Font itemNameFont = Fonts.MEDIUM.getFont(6.0F);
   public static final Font selectItemFont = Fonts.MEDIUM.getFont(7.0F);
   public final SearchBox priceBox = new SearchBox(new Vector2f(0.0F, 0.0F), priceFont, "Buy this item for up to", 112.0F);
   public float priceBoxX;
   public float priceBoxY;
   public ItemServiceBase selectedItem;

   public SborHeader() {
      this.priceBox.on23(SearchBox.MatchMode.val297);
      this.priceBox.EventItemRenderHook(11);
   }

   public void render(HudDrawContext var1, float var2, float var3, float var4) {
      float f = var2 + 140.0F;
      float f1 = var3 + 4.0F;
      this.priceBoxX = f + 336.0F - 128.0F;
      this.priceBoxY = f1;
      var1.drawRoundedRect(f, f1, 336.0F, 23.0F, panelRadius, AutoSborStyle.surface().SprintStateEvent(var4));
      if (this.selectedItem != null) {
         var1.drawRoundedRect(f, f1, 23.0F, 23.0F, panelRadius, AutoSborStyle.surface().SprintStateEvent(var4));
         this.renderSelectedItem(var1, f, f1, var4);
         this.renderPriceBox(var1, var4);
      } else {
         this.renderSelectItemText(var1, f, f1, var4);
      }
   }

   public void setSelectedItem(ItemServiceBase var1) {
      this.selectedItem = var1;
      if (this.selectedItem == null) {
         this.priceBox.VelocityChangeEvent(false);
      }
   }

   public long getPrice() {
      String s = this.priceBox.getText();
      if (s != null && !s.isBlank()) {
         try {
            return Long.parseLong(s);
         } catch (NumberFormatException numberformatexception) {
            return 0L;
         }
      } else {
         return 0L;
      }
   }

   public void setPrice(long var1) {
      String s = var1 <= 0L ? "" : Long.toString(var1);
      this.priceBox.HudHotbarPanel(s);
      this.priceBox.EventRender(s.length());
   }

   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (var5 != MenuScreenId.call004) {
         return false;
      } else if (this.selectedItem == null) {
         return false;
      } else if (this.isPriceBoxHovered(var1, var3)) {
         this.priceBox.VelocityChangeEvent(true);
         return true;
      } else {
         this.priceBox.VelocityChangeEvent(false);
         return false;
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.selectedItem == null) {
         return false;
      }

      if (!this.priceBox.isSelected()) {
         return false;
      }

      if (var1 != 256 && var1 != 257) {
         return this.priceBox.keyPressed(var1, var2, var3);
      }

      this.priceBox.VelocityChangeEvent(false);
      return true;
   }

   public boolean charTyped(char var1, int var2) {
      if (this.selectedItem == null) {
         return false;
      } else {
         return !this.priceBox.isSelected() ? false : this.priceBox.charTyped(var1, var2);
      }
   }

   public void renderSelectedItem(HudDrawContext var1, float var2, float var3, float var4) {
      if (this.selectedItem != null) {
         float f = var2 + 7.5F;
         float f1 = var3 + 7.5F;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(f, f1);
         var1.getMatrices().scale(0.5F, 0.5F);
         org.zenith.render.LegacyRenderBridge.enableBlend();
         org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, var4);
         var1.drawItem(this.selectedItem.EventInjectHandleInputEvents(), 0, 0);
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         var1.getMatrices().popMatrix();
         float f2 = var2 + 23.0F + 8.0F;
         float f3 = var3 + (23.0F - itemNameFont.height()) / 2.0F;
         var1.drawText(itemNameFont, this.selectedItem.getDisplayName(), f2, f3, AutoSborStyle.text().SprintStateEvent(var4));
      }
   }

   public void renderSelectItemText(HudDrawContext var1, float var2, float var3, float var4) {
      String s = "Select item";
      float f = var2 + (336.0F - selectItemFont.width(s)) / 2.0F;
      float f1 = var3 + (23.0F - selectItemFont.height()) / 2.0F;
      var1.drawText(selectItemFont, s, f, f1, AutoSborStyle.textTertiary().SprintStateEvent(var4));
   }

   public void renderPriceBox(HudDrawContext var1, float var2) {
      float f = this.priceBoxX + 8.0F;
      float f1 = this.priceBoxY + (23.0F - this.priceBox.call050().height()) / 2.0F;
      boolean flag = this.isPriceBoxHovered(var1.getMouseX(), var1.getMouseY());
      ArgbColor i11ii1llliilllii1i1 = this.priceBox.isSelected()
         ? AutoSborStyle.transparentText()
         : (flag ? AutoSborStyle.text() : AutoSborStyle.textSecondary());
      var1.drawRoundedRect(this.priceBoxX, this.priceBoxY, 128.0F, 23.0F, panelRadius, AutoSborStyle.surface().SprintStateEvent(var2));
      if (!this.priceBox.isSelected() && !this.priceBox.isEmpty()) {
         this.renderFormattedPrice(var1, f, f1, var2);
      } else {
         this.priceBox.on23(var1, f, f1, AutoSborStyle.text().SprintStateEvent(var2), i11ii1llliilllii1i1.SprintStateEvent(var2));
      }
   }

   public void renderFormattedPrice(HudDrawContext var1, float var2, float var3, float var4) {
      String s = "$";
      String s1 = this.formatPrice(this.getPrice());
      float f = priceFont.width(s);
      var1.drawText(priceFont, s, var2, var3, AutoSborStyle.primary().SprintStateEvent(var4));
      var1.drawText(priceFont, s1, var2 + f, var3, AutoSborStyle.text().SprintStateEvent(var4));
   }

   public String formatPrice(long var1) {
      String s = Long.toString(Math.max(0L, var1));
      StringBuilder stringbuilder = new StringBuilder(s.length() + s.length() / 3);

      for (int i = 0; i < s.length(); i++) {
         if (i > 0 && (s.length() - i) % 3 == 0) {
            stringbuilder.append(' ');
         }

         stringbuilder.append(s.charAt(i));
      }

      return stringbuilder.toString();
   }

   public boolean isPriceBoxHovered(double var1, double var3) {
      return MathUtils.on23(var1, var3, this.priceBoxX, this.priceBoxY, 128.0, 23.0);
   }
}
