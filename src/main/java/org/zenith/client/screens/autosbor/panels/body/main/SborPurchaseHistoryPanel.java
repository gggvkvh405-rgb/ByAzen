package org.zenith.client.screens.autosbor.panels.body.main;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.autosbor.AutoSborStyle;
import org.zenith.core.PricedItem;
import org.zenith.hud.ScrollHandler;
import org.zenith.module.misc.AutoInventory;
import org.zenith.util.MathUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class SborPurchaseHistoryPanel {
   public static final float gridXOffset = 140.0F;
   public static final float countPanelYOffset = 31.0F;
   public static final float countPanelHeight = 30.0F;
   public static final float blockGap = 4.0F;
   public static final float slotSize = 23.0F;
   public static final float slotGap = 2.0F;
   public static final int gridRows = 4;
   public static final float screenHeight = 320.0F;
   public static final float bottomPanelHeight = 21.0F;
   public static final float bottomPanelGap = 2.0F;
   public static final float historyTopGap = 4.0F;
   public static final float bottomPadding = 4.0F;
   public static final float rowWidth = 218.0F;
   public static final float rowHeight = 23.0F;
   public static final float rowGap = 2.0F;
   public static final float pricePanelWidth = 64.0F;
   public static final float scrollOffsetX = 4.0F;
   public static final float scrollWidth = 1.0F;
   public static final float scrollHeightGap = 4.0F;
   public static final float minScrollThumbHeight = 20.0F;
   public static final float iconAreaSize = 19.0F;
   public static final float iconRenderSize = 10.0F;
   public static final float iconOffsetX = 2.0F;
   public static final float textOffsetX = 29.0F;
   public static final float innerGap = 6.0F;
   public static final CornerRadius rowRadius = CornerRadius.MovementInputEvent(6.0F);
   public static final CornerRadius scrollRadius = CornerRadius.MovementInputEvent(0.5F);
   public static final Font nameFont = Fonts.MEDIUM.getFont(5.5F);
   public static final Font amountFont = Fonts.MEDIUM.getFont(5.0F);
   public static final Font priceFont = Fonts.MEDIUM.getFont(5.5F);
   public final ScrollHandler scrollHandler = new ScrollHandler();
   public float listX;
   public float listY;
   public float currentListHeight;
   public float currentScrollHeight;

   public void render(HudDrawContext var1, float var2, float var3, boolean var4, float var5) {
      List<PricedItem> list = AutoInventory.autoInventory.double131().call005();
      float f = var4 ? 1.0F : 0.0F;
      this.listX = var2 + 140.0F;
      this.listY = var3 + this.getListYOffset(f);
      this.currentListHeight = this.getListHeight(f);
      this.currentScrollHeight = this.currentListHeight - 4.0F;
      this.updateScroll(list.size());
      float f1 = (float)this.scrollHandler.float260();
      var1.enableScissor((int)this.listX, (int)this.listY, (int)(this.listX + 218.0F), (int)(this.listY + this.currentListHeight));

      for (int i = 0; i < list.size(); i++) {
         float f2 = this.listY + i * 25.0F - f1;
         if (!(f2 + 23.0F <= this.listY) && !(f2 >= this.listY + this.currentListHeight)) {
            this.renderHistoryRow(var1, list.get(i), this.listX, f2, var5);
         }
      }

      var1.disableScissor();
      this.renderScrollBar(var1, list.size(), var5);
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (!this.isHovered(var1, var3)) {
         return false;
      }

      if (this.scrollHandler.float261() <= 0.0) {
         return false;
      }

      this.scrollHandler.CloudRouter(var5 * 3.0);
      return true;
   }

   public void renderHistoryRow(HudDrawContext var1, PricedItem var2, float var3, float var4, float var5) {
      var1.drawRoundedRect(var3, var4, 218.0F, 23.0F, rowRadius, AutoSborStyle.headerSurface().SprintStateEvent(var5));
      float f = var3 + 218.0F - 64.0F;
      var1.drawRoundedRect(f, var4, 64.0F, 23.0F, rowRadius, AutoSborStyle.surface().SprintStateEvent(var5));
      this.renderItemIcon(var1, var2.EventInjectHandleInputEvents(), var3, var4, var5);
      this.renderNameAndAmount(var1, var2, var3, var4, f, var5);
      this.renderPrice(var1, var2.getPrice(), f, var4, var5);
   }

   public void renderItemIcon(HudDrawContext var1, ItemStack var2, float var3, float var4, float var5) {
      if (var2 != null && !var2.isEmpty()) {
         float f = 0.625F;
         float f1 = var3 + 2.0F + 4.5F;
         float f2 = var4 + 6.5F;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(f1, f2);
         var1.getMatrices().scale(f, f);
         org.zenith.render.LegacyRenderBridge.enableBlend();
         org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, var5);
         var1.drawItem(var2, 0, 0);
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         var1.getMatrices().popMatrix();
      }
   }

   public void renderNameAndAmount(HudDrawContext var1, PricedItem var2, float var3, float var4, float var5, float var6) {
      String s = "x" + var2.call129();
      float f = amountFont.width(s);
      float f1 = var3 + 29.0F;
      float f2 = 4.0F;
      float f3 = Math.max(10.0F, var5 - 6.0F - f1 - f2 - f);
      String s1 = this.trimToWidth(var2.call128(), f3);
      float f4 = var4 + (23.0F - nameFont.height()) / 2.0F;
      float f5 = var4 + (23.0F - amountFont.height()) / 2.0F;
      float f6 = f1 + nameFont.width(s1) + f2;
      var1.drawText(nameFont, s1, f1, f4, AutoSborStyle.text().SprintStateEvent(var6));
      var1.drawText(amountFont, s, f6, f5, AutoSborStyle.textSecondary().SprintStateEvent(var6));
   }

   public void renderPrice(HudDrawContext var1, long var2, float var4, float var5, float var6) {
      String s = "$";
      String s1 = this.formatPrice(var2);
      float f = priceFont.width(s);
      float f1 = priceFont.width(s1);
      float f2 = var4 + (64.0F - f - f1) / 2.0F;
      float f3 = var5 + (23.0F - priceFont.height()) / 2.0F;
      var1.drawText(priceFont, s, f2, f3, AutoSborStyle.primary().SprintStateEvent(var6));
      var1.drawText(priceFont, s1, f2 + f, f3, AutoSborStyle.text().SprintStateEvent(var6));
   }

   public void updateScroll(int var1) {
      this.scrollHandler.ProtocolMessage(Math.max(0.0F, this.getContentHeight(var1) - this.currentListHeight));
      this.scrollHandler.update();
   }

   public float getContentHeight(int var1) {
      return var1 <= 0 ? 0.0F : var1 * 23.0F + (var1 - 1) * 2.0F;
   }

   public float getListYOffset(float var1) {
      float f = 31.0F + 34.0F * var1;
      return f + this.getGridHeight() + 4.0F + 21.0F + 2.0F + 21.0F + 4.0F;
   }

   public float getListHeight(float var1) {
      return Math.max(0.0F, 320.0F - this.getListYOffset(var1) - 4.0F);
   }

   public float getGridHeight() {
      return 98.0F;
   }

   public void renderScrollBar(HudDrawContext var1, int var2, float var3) {
      float f = this.getContentHeight(var2);
      if (!(this.scrollHandler.float261() <= 0.0) && !(f <= this.currentListHeight)) {
         float f1 = this.listX + 218.0F + 4.0F;
         var1.drawRoundedRect(f1, this.listY, 1.0F, this.currentScrollHeight, scrollRadius, AutoSborStyle.textAlpha(10).SprintStateEvent(var3));
         float f2 = Math.max(20.0F, this.currentScrollHeight * (this.currentListHeight / f));
         f2 = Math.min(this.currentScrollHeight, f2);
         float f3 = (float)this.scrollHandler.float261();
         float f4 = Math.max(0.0F, Math.min(1.0F, (float)this.scrollHandler.float260() / f3));
         float f5 = this.listY + (this.currentScrollHeight - f2) * f4;
         var1.drawRoundedRect(f1, f5, 1.0F, f2, scrollRadius, AutoSborStyle.textAlpha(24).SprintStateEvent(var3));
      }
   }

   public String trimToWidth(String var1, float var2) {
      String s = var1 == null ? "" : var1;
      if (nameFont.width(s) <= var2) {
         return s;
      }

      while (!s.isEmpty() && nameFont.width(s + "...") > var2) {
         s = s.substring(0, s.length() - 1);
      }

      return s.isEmpty() ? "" : s + "...";
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

   public boolean isHovered(double var1, double var3) {
      float f = this.scrollHandler.float261() <= 0.0 ? 218.0F : 223.0F;
      return MathUtils.on23(var1, var3, this.listX, this.listY, f, this.currentListHeight);
   }
}
