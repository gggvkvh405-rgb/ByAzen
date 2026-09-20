package org.zenith.client.screens.autosbor.panels.body.main;

import com.mojang.blaze3d.systems.RenderSystem;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.autosbor.AutoSborStyle;
import org.zenith.core.Easing;
import org.zenith.core.ItemServiceBase;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.hud.SearchBox;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.MathUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class SborInventory {
   public static final float panelXOffset = 140.0F;
   public static final float panelYOffset = 31.0F;
   public static final float panelWidth = 336.0F;
   public static final float panelHeight = 30.0F;
   public static final float countSettingYOffset = 8.0F;
   public static final float blockGap = 4.0F;
   public static final float slotSize = 23.0F;
   public static final float itemIconSize = 8.0F;
   public static final float itemIconScale = 0.5F;
   public static final float itemCountOffset = 6.0F;
   public static final float slotGap = 2.0F;
   public static final int gridColumns = 9;
   public static final int gridRows = 4;
   public static final float bottomPanelWidth = 223.0F;
   public static final float bottomPanelHeight = 21.0F;
   public static final float inventoryNameOffsetX = 9.0F;
   public static final float inventoryNameIconGap = 8.0F;
   public static final float inventoryNameIconYOffset = -0.5F;
   public static final float createIconGap = 2.0F;
   public static final float bottomPanelGap = 2.0F;
   public static final long layoutAnimationDuration = 180L;
   public static final float animationEpsilon = 0.001F;
   public static final CornerRadius panelRadius = CornerRadius.MovementInputEvent(7.0F);
   public static final CornerRadius bottomPanelRadius = CornerRadius.MovementInputEvent(8.0F);
   public static final Font inventoryNameFont = Fonts.MEDIUM.getFont(6.0F);
   public static final Font inventoryNameIconFont = Fonts.ICONS.getFont(7.0F);
   public static final Font createIconFont = Fonts.ICONS.getFont(6.0F);
   public static final Font itemCountFont = Fonts.MEDIUM.getFont(5.0F);
   public static final String inventoryNamePlaceholder = "Inventory name";
   public static final String inventoryNameIcon = "7";
   public static final String createIcon = "{";
   public static final int defaultCountMax = 64;
   public final ItemServiceBase[] slots;
   public final NumberSetting countSetting = new NumberSetting("Количество", 1.0F, 1.0F, 64.0F, 1.0F, "module.autoSbor.count.desc");
   public final NumberSetting minDurabilitySetting = new NumberSetting("Мин. прочность", 0.8F, 0.0F, 1.0F, 0.1F);
   public final SearchBox inventoryNameBox = new SearchBox(
      new Vector2f(0.0F, 0.0F), inventoryNameFont, "Inventory name", 205.0F - inventoryNameIconFont.width("7") - 8.0F
   );
   public final UiAnimation layoutAnimation = new UiAnimation(180L, 0.0F, Easing.PreventActionEvent);
   public CornerRadiusF sliderBounds;
   public float panelX;
   public float panelY;
   public float gridX;
   public float gridY;
   public float inventoryNamePanelX;
   public float inventoryNamePanelY;
   public float createPanelX;
   public float createPanelY;
   public int countMax = 64;
   public boolean countVisible;
   public boolean durabilityVisible;
   public boolean draggingSlider;

   public SborInventory(ItemServiceBase[] var1) {
      this.slots = var1;
   }

   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, int[] var6, int var7, boolean var8, float var9) {
      this.panelX = var2 + 140.0F;
      this.panelY = var3 + 31.0F;
      this.gridX = this.panelX;
      this.durabilityVisible = var8;
      boolean flag = this.countVisible || var8;
      float f = this.layoutAnimation.on23(flag ? 1.0F : 0.0F);
      this.gridY = var3 + this.getGridYOffset(f);
      float f1 = f * var9;
      if (f > 0.001F) {
         var1.drawRoundedRect(this.panelX, this.panelY, 336.0F, 30.0F, panelRadius, AutoSborStyle.surface().SprintStateEvent(f1));
         this.renderPanelSetting(var1, var4, var5, f1);
      }

      this.renderGrid(var1, var6, var7, var9);
      float f2 = this.gridY + this.getGridHeight() + 4.0F;
      this.renderInventoryNamePanel(var1, this.panelX, f2, var9);
      this.renderCreatePanel(var1, this.panelX, f2 + 21.0F + 2.0F, var9);
   }

   public boolean placeItem(ItemServiceBase var1, double var2, double var4) {
      int i = this.getSlotIndex(var2, var4);
      if (i < 0) {
         return false;
      }

      this.slots[i] = var1;
      return true;
   }

   public ItemServiceBase getItem(int var1) {
      return !this.isSlotIndexValid(var1) ? null : this.slots[var1];
   }

   public void setItem(int var1, ItemServiceBase var2) {
      if (this.isSlotIndexValid(var1)) {
         this.slots[var1] = var2;
      }
   }

   public void clearSlot(int var1) {
      this.setItem(var1, null);
   }

   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (var5 != MenuScreenId.call004) {
         return false;
      }

      if ((this.countVisible || this.durabilityVisible) && MathUtils.on23(var1, var3, this.panelX, this.panelY, 336.0, 30.0)) {
         this.inventoryNameBox.VelocityChangeEvent(false);
         if (this.sliderBounds != null && this.sliderBounds.PotionItemBuilder(var1, var3)) {
            this.draggingSlider = true;
            this.updateSlider(var1);
            ZenithClient.on23().NbtItemSpec().on23(ZenithClient.on23().NbtItemSpec().soundEvent7);
         }

         return true;
      } else if (this.isInventoryNameHovered(var1, var3)) {
         this.inventoryNameBox.VelocityChangeEvent(true);
         return true;
      } else {
         this.inventoryNameBox.VelocityChangeEvent(false);
         return false;
      }
   }

   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      if (var5 == MenuScreenId.call004) {
         this.draggingSlider = false;
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.inventoryNameBox.isSelected()) {
         return false;
      }

      if (var1 != 256 && var1 != 257) {
         return this.inventoryNameBox.keyPressed(var1, var2, var3);
      }

      this.inventoryNameBox.VelocityChangeEvent(false);
      return true;
   }

   public boolean charTyped(char var1, int var2) {
      return !this.inventoryNameBox.isSelected() ? false : this.inventoryNameBox.charTyped(var1, var2);
   }

   public String getInventoryName() {
      return this.inventoryNameBox.getText() == null ? "" : this.inventoryNameBox.getText().trim();
   }

   public int getCount() {
      return Math.max(1, Math.min(this.countMax, Math.round(this.countSetting.getCurrent())));
   }

   public void setCount(int var1) {
      this.countSetting.setCurrent(Math.max(this.countSetting.getMin(), Math.min(this.countMax, var1)));
   }

   public void setCountMax(int var1) {
      this.countMax = Math.max(1, Math.min(64, var1));
      this.setCount(this.getCount());
   }

   public float getMinDurability() {
      return this.minDurabilitySetting.getCurrent();
   }

   public void setMinDurability(float var1) {
      this.minDurabilitySetting.setCurrent(Math.max(this.minDurabilitySetting.getMin(), Math.min(this.minDurabilitySetting.getMax(), var1)));
   }

   public void setCountVisible(boolean var1) {
      this.countVisible = var1;
   }

   public void setDurabilityVisible(boolean var1) {
      this.durabilityVisible = var1;
   }

   public boolean isCreateHovered(double var1, double var3) {
      return MathUtils.on23(var1, var3, this.createPanelX, this.createPanelY, 223.0, 21.0);
   }

   public void renderPanelSetting(HudDrawContext var1, float var2, float var3, float var4) {
      NumberSetting lilliiill11llilll1ll1l = this.getActiveSetting();
      if (lilliiill11llilll1ll1l != null) {
         if (lilliiill11llilll1ll1l == this.countSetting) {
            this.setCount(this.getCount());
         }

         Font font = Fonts.MEDIUM.getFont(6.0F);
         Font font1 = Fonts.ICONS.getFont(6.0F);
         float f = this.panelY + 8.0F;
         float f1 = 8.0F;
         float f2 = this.panelX + f1;
         float f3 = f2 + 10.0F;
         var1.drawText(font1, "D", f2, f, AutoSborStyle.primary().SprintStateEvent(var4));
         var1.drawText(font, lilliiill11llilll1ll1l.getName(), f3, f, AutoSborStyle.text().SprintStateEvent(var4));
         String s = lilliiill11llilll1ll1l.getDescription();
         if (!s.isEmpty()) {
            var1.drawText(Fonts.MEDIUM.getFont(5.0F), s, f2, f + 9.0F, AutoSborStyle.textSecondary().SprintStateEvent(var4));
         }

         String s1 = this.formatSettingValue(lilliiill11llilll1ll1l);
         float f4 = this.panelX + 336.0F - f1 - font.width(s1);
         var1.drawText(font, s1, f4, f, AutoSborStyle.primary().SprintStateEvent(var4));
         float f5 = 120.0F;
         float f6 = this.panelX + 336.0F - f1 - 4.0F - f5;
         float f7 = f + 12.0F;
         float f8 = this.getSettingPercent(lilliiill11llilll1ll1l);
         float f9 = f5 * f8;
         var1.drawRoundedRect(f6, f7, f5, 2.0F, CornerRadius.MovementInputEvent(0.2F), AutoSborStyle.fieldSurface().SprintStateEvent(var4));
         var1.drawRoundedRect(f6, f7, Math.max(0.0F, f9 - 2.0F), 2.0F, CornerRadius.var159, AutoSborStyle.primary().SprintStateEvent(var4));
         var1.drawRoundedRect(f6 + f9, f7 - 1.0F, 4.0F, 4.0F, CornerRadius.MovementInputEvent(2.0F), AutoSborStyle.text().SprintStateEvent(var4));
         this.sliderBounds = new CornerRadiusF(f6, f7 - 2.0F, f5, 6.0F);
         this.updateSlider(var2);
      }
   }

   public NumberSetting getActiveSetting() {
      if (this.countVisible) {
         return this.countSetting;
      } else {
         return this.durabilityVisible ? this.minDurabilitySetting : null;
      }
   }

   public float getSettingMax(NumberSetting var1) {
      return var1 == this.countSetting ? this.countMax : var1.getMax();
   }

   public float getSettingPercent(NumberSetting var1) {
      float f = this.getSettingMax(var1);
      return f == var1.getMin() ? 0.0F : Math.max(0.0F, Math.min(1.0F, (var1.getCurrent() - var1.getMin()) / (f - var1.getMin())));
   }

   public void updateSlider(double var1) {
      if (this.draggingSlider && this.sliderBounds != null) {
         NumberSetting lilliiill11llilll1ll1l = this.getActiveSetting();
         if (lilliiill11llilll1ll1l != null) {
            float f = this.getSettingMax(lilliiill11llilll1ll1l);
            double d0 = var1 - this.sliderBounds.x();
            double d1 = Math.max(0.0, Math.min(1.0, d0 / this.sliderBounds.width()));
            double d2 = lilliiill11llilll1ll1l.getMin() + (f - lilliiill11llilll1ll1l.getMin()) * d1;
            float f1 = lilliiill11llilll1ll1l.getIncrement();
            d2 = (float)Math.round((d2 - lilliiill11llilll1ll1l.getMin()) / f1) * f1 + lilliiill11llilll1ll1l.getMin();
            d2 = Math.max(lilliiill11llilll1ll1l.getMin(), Math.min(f, d2));
            if (lilliiill11llilll1ll1l.getCurrent() != (float)d2) {
               ZenithClient.on23().NbtItemSpec().on23(ZenithClient.on23().NbtItemSpec().soundEvent7);
            }

            lilliiill11llilll1ll1l.setCurrent((float)d2);
            if (lilliiill11llilll1ll1l == this.countSetting) {
               this.setCount(this.getCount());
            }
         }
      }
   }

   public String formatSettingValue(NumberSetting var1) {
      String s = String.valueOf(var1.getIncrement());
      int i = 0;
      int j = s.indexOf(46);
      if (j >= 0) {
         i = s.length() - j - 1;

         while (i > 0 && s.charAt(j + i) == '0') {
            i--;
         }
      }

      DecimalFormat decimalformat = new DecimalFormat();
      decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
      decimalformat.setMinimumFractionDigits(i);
      decimalformat.setMaximumFractionDigits(i);
      decimalformat.setGroupingUsed(false);
      return decimalformat.format(var1.getCurrent());
   }

   public void renderGrid(HudDrawContext var1, int[] var2, int var3, float var4) {
      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 9; j++) {
            int k = i * 9 + j;
            float f = this.gridX + j * 25.0F;
            float f1 = this.gridY + i * 25.0F;
            var1.drawRoundedRect(f, f1, 23.0F, 23.0F, panelRadius, AutoSborStyle.surface().SprintStateEvent(var4));
            if (this.slots[k] != null) {
               this.renderSlotItem(var1, this.slots[k], f, f1, var4);
               this.renderSlotCount(var1, this.slots[k], var2, var3, k, f, f1, var4);
            }
         }
      }
   }

   public void renderSlotItem(HudDrawContext var1, ItemServiceBase var2, float var3, float var4, float var5) {
      float f = var3 + 7.5F;
      float f1 = var4 + 7.5F;
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(f, f1);
      var1.getMatrices().scale(0.5F, 0.5F);
      org.zenith.render.LegacyRenderBridge.enableBlend();
      org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
      org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, var5);
      var1.drawItem(var2.EventInjectHandleInputEvents(), 0, 0);
      org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      var1.getMatrices().popMatrix();
   }

   public void renderSlotCount(HudDrawContext var1, ItemServiceBase var2, int[] var3, int var4, int var5, float var6, float var7, float var8) {
      if (var2.EventInjectHandleInputEvents().getMaxCount() > 1 || this.isPotion(var2.EventInjectHandleInputEvents().getItem())) {
         int i = this.getSlotCount(var2, var3, var4, var5);
         if (i > 1) {
            String s = "x" + i;
            float f = var6 + 23.0F - 6.0F - itemCountFont.width(s);
            float f1 = var7 + 23.0F - 6.0F - itemCountFont.height();
            var1.drawText(itemCountFont, s, f, f1, AutoSborStyle.text().SprintStateEvent(var8));
         }
      }
   }

   public int getSlotCount(ItemServiceBase var1, int[] var2, int var3, int var4) {
      int i = var4 == var3 && this.countVisible ? this.getCount() : this.getStoredSlotCount(var2, var4);
      return i > 0 ? i : this.getDefaultCount(var1);
   }

   public int getStoredSlotCount(int[] var1, int var2) {
      return var1 != null && var2 >= 0 && var2 < var1.length ? var1[var2] : 0;
   }

   public int getDefaultCount(ItemServiceBase var1) {
      return var1 != null && !var1.EventInjectHandleInputEvents().isEmpty() ? Math.max(1, var1.EventInjectHandleInputEvents().getCount()) : 1;
   }

   public boolean isPotion(Item var1) {
      return var1 == Items.POTION || var1 == Items.SPLASH_POTION || var1 == Items.LINGERING_POTION;
   }

   public float getGridYOffset(float var1) {
      return 31.0F + 34.0F * var1;
   }

   public float getGridHeight() {
      return 98.0F;
   }

   public void renderBottomPanel(HudDrawContext var1, float var2, float var3, float var4) {
      var1.drawRoundedRect(var2, var3, 223.0F, 21.0F, bottomPanelRadius, AutoSborStyle.fieldSurface().SprintStateEvent(var4));
      var1.drawRoundedBorder(var2, var3, 223.0F, 21.0F, -0.1F, bottomPanelRadius, AutoSborStyle.fieldBorder().SprintStateEvent(var4));
   }

   public void renderInventoryNamePanel(HudDrawContext var1, float var2, float var3, float var4) {
      this.inventoryNamePanelX = var2;
      this.inventoryNamePanelY = var3;
      this.renderBottomPanel(var1, var2, var3, var4);
      float f = var2 + 9.0F;
      float f1 = f + inventoryNameIconFont.width("7") + 8.0F;
      float f2 = var3 + (21.0F - this.inventoryNameBox.call050().height()) / 2.0F;
      boolean flag = MathUtils.on23(var1.getMouseX(), var1.getMouseY(), var2, var3, 223.0, 21.0);
      ArgbColor i11ii1llliilllii1i1 = flag ? AutoSborStyle.text() : AutoSborStyle.textSecondary();
      this.renderInventoryNameIcon(var1, f, var3, var4);
      this.inventoryNameBox.on23(var1, f1, f2, AutoSborStyle.text().SprintStateEvent(var4), i11ii1llliilllii1i1.SprintStateEvent(var4));
   }

   public void renderInventoryNameIcon(HudDrawContext var1, float var2, float var3, float var4) {
      float f = var3 + (21.0F - inventoryNameIconFont.height()) / 2.0F + -0.5F;
      var1.drawText(inventoryNameIconFont, "7", var2, f, AutoSborStyle.textTertiary().SprintStateEvent(var4));
   }

   public void renderCreatePanel(HudDrawContext var1, float var2, float var3, float var4) {
      this.createPanelX = var2;
      this.createPanelY = var3;
      this.renderBottomPanel(var1, var2, var3, var4);
      String s = "Create";
      float f = inventoryNameFont.width(s);
      float f1 = createIconFont.width("{");
      float f2 = f + 2.0F + f1;
      float f3 = var2 + (223.0F - f2) / 2.0F;
      float f4 = f3 + f1 + 2.0F;
      float f5 = var3 + (21.0F - inventoryNameFont.height()) / 2.0F;
      float f6 = var3 + (21.0F - createIconFont.height()) / 2.0F;
      ArgbColor i11ii1llliilllii1i1 = MathUtils.on23(var1.getMouseX(), var1.getMouseY(), var2, var3, 223.0, 21.0)
         ? AutoSborStyle.text()
         : AutoSborStyle.textSecondary();
      var1.drawText(createIconFont, "{", f3, f6, i11ii1llliilllii1i1.SprintStateEvent(var4));
      var1.drawText(inventoryNameFont, s, f4, f5, i11ii1llliilllii1i1.SprintStateEvent(var4));
   }

   public int getSlotIndex(double var1, double var3) {
      if (!MathUtils.on23(var1, var3, this.gridX, this.gridY, 223.0, 98.0)) {
         return -1;
      } else {
         int i = (int)((var1 - this.gridX) / 25.0);
         int j = (int)((var3 - this.gridY) / 25.0);
         if (i >= 0 && i < 9 && j >= 0 && j < 4) {
            float f = this.gridX + i * 25.0F;
            float f1 = this.gridY + j * 25.0F;
            return !MathUtils.on23(var1, var3, f, f1, 23.0, 23.0) ? -1 : j * 9 + i;
         } else {
            return -1;
         }
      }
   }

   public boolean isSlotIndexValid(int var1) {
      return var1 >= 0 && var1 < this.slots.length;
   }

   public boolean isInventoryNameHovered(double var1, double var3) {
      return MathUtils.on23(var1, var3, this.inventoryNamePanelX, this.inventoryNamePanelY, 223.0, 21.0);
   }
}
