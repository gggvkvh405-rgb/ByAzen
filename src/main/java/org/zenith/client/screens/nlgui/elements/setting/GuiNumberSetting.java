package org.zenith.client.screens.nlgui.elements.setting;

import java.util.Locale;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiNumberSetting extends GuiSetting<NumberSetting> {
   public final UiAnimation hoveredEnable = new UiAnimation(200L, Easing.CloseScreenEvent);
   public final UiAnimation selectedValueAnimation = new UiAnimation(160L, 0.0F, Easing.HotbarInputEvent);
   public final UiAnimation numberAnimation;
   public CornerRadiusF bounds;
   public CornerRadiusF selectedBounds;
   public boolean selected = false;
   public float applayValue;

   public float getApplayValue() {
      return this.selected ? this.applayValue : this.setting.getCurrent();
   }

   public GuiNumberSetting(NumberSetting var1) {
      this(var1, 166.0F);
   }

   public GuiNumberSetting(NumberSetting var1, float var2) {
      super(var2, var1);
      this.numberAnimation = new UiAnimation(200L, var1.getCurrent(), Easing.CloseScreenEvent);
      this.applayValue = var1.getCurrent();
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.selectedBounds != null && this.selectedBounds.on23(var1, var3, 2.0F)) {
         this.selected = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      this.selected = false;
      this.applayValue = this.setting.getCurrent();
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         if (!this.selected) {
            this.applayValue = this.setting.getCurrent();
         }

         this.animationVisible.on23(this.setting.isVisible());
         var6 *= this.animationVisible.CancellableEvent();
         this.hoveredEnable.on23(this.selected || this.selectedBounds != null && this.selectedBounds.PotionItemBuilder(var2, var3));
         this.selectedValueAnimation.on23(this.selected);
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font1 = Fonts.NEW_REGULAR.getFont(5.4F);
         float f = this.width / 2.0F - GuiStyle.PADDING.intValue();
         ArgbColor i11ii1llliilllii1i1 = zenithstyle.getTextEnable().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i11 = zenithstyle.getTextSecondary().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i12 = zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6);
         this.drawDefault(
            var1,
            var2,
            var3,
            "u",
            this.setting.getName(),
            this.setting.getDescription(),
            font,
            font1,
            var4,
            var5,
            f,
            i11ii1llliilllii1i1,
            i11ii1llliilllii1i11,
            i11ii1llliilllii1i12
         );
         String s = String.valueOf(this.setting.getIncrement());
         int i = 0;
         int j = s.indexOf(46);
         if (j != -1 && (s.charAt(j + 1) != '0' || s.length() > j + 2 && s.charAt(j + 2) != '0')) {
            i = s.length() - j - 1;
         }

         String s1 = String.format(Locale.US, "%." + i + "f", this.numberAnimation.CancellableEvent());
         float f1 = font.width(this.setting.getSuffix());
         float f2 = font.width(s1);
         float f3 = GuiStyle.PADDING.intValue() / 2.0F + f2 + f1 + GuiStyle.PADDING.intValue() / 2.0F + 1.0F;
         float f4 = 9.0F;
         this.bounds = new CornerRadiusF(var4 + this.width - f3, var5 + (this.getHeight() - f4) / 2.0F, f3, f4);
         var1.drawRoundedRectBatched(
            this.bounds.x(),
            this.bounds.y(),
            f3,
            f4,
            CornerRadius.MovementInputEvent(1.0F),
            zenithstyle.getFieldSurfaceBackground().getColor().SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         var1.drawRoundedBorder(
            this.bounds.x(),
            this.bounds.y(),
            f3,
            f4,
            0.1F,
            CornerRadius.MovementInputEvent(1.0F),
            zenithstyle.getFieldBorder().getColor().SprintStateEvent(var6)
         );
         var1.drawText(
            font,
            s1,
            this.bounds.x() + GuiStyle.PADDING.intValue() / 2.0F,
            this.bounds.y() + (f4 - font.height()) / 2.0F,
            zenithstyle.getTextEnable().getColor().SprintStateEvent(var6)
         );
         var1.drawText(
            font,
            this.setting.getSuffix(),
            this.bounds.x() + this.bounds.width() - GuiStyle.PADDING.intValue() / 2.0F - f1,
            this.bounds.y() + (f4 - font.height()) / 2.0F,
            zenithstyle.getTextSecondary().getColor().SprintStateEvent(var6)
         );
         float f5 = GuiStyle.PADDING.intValue() / 2.0F + font.width(s1.replaceAll(".", "0")) + f1 + GuiStyle.PADDING.intValue() / 2.0F + 1.0F;
         float f6 = f - f5 - GuiStyle.PADDING.intValue();
         float f7 = 2.0F;
         float f8 = var4 + this.width - f5 - f6 - GuiStyle.PADDING.intValue();
         float f9 = var5 + (this.getHeight() - f7) / 2.0F;
         this.selectedBounds = new CornerRadiusF(f8, f9, f6, f7);
         float f10 = this.setting.getMin();
         float f11 = this.setting.getMax();
         float f12 = MathHelper.lerp(0.3F, this.numberAnimation.CancellableEvent(), this.setting.getCurrent());
         this.numberAnimation.setValue(f12);
         float f13 = MathHelper.clamp((f12 - f10) / (f11 - f10), 0.0F, 1.0F);
         this.numberAnimation.on23(120L);
         this.numberAnimation.on23(Easing.Event18Ext5);
         float f14 = f6 * f13;
         var1.drawRoundedRectBatched(
            f8, f9, f6, f7, CornerRadius.MovementInputEvent(0.04F), zenithstyle.getFieldSurfaceBackground().getColor().SprintStateEvent(var6)
         );
         var1.drawRoundedRectBatched(
            f8, f9, f14 - 0.5F, f7, CornerRadius.BotTickEvent(0.04F, 0.04F), zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6)
         );
         var1.drawRoundedRectBatched(
            f8 + f14 - 0.5F, f9 - 0.5F, 3.0F, 3.0F, CornerRadius.MovementInputEvent(0.5F), zenithstyle.getTextEnable().getColor().SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         this.updateSlider(var2);
      }
   }

   public void updateSlider(double var1) {
      if (this.selected && this.selectedBounds != null) {
         double d0 = var1 - this.selectedBounds.x();
         double d1 = Math.max(0.0, Math.min(1.0, d0 / this.selectedBounds.width()));
         double d2 = this.setting.getMin();
         double d3 = this.setting.getMax();
         float f = this.setting.getIncrement();
         double d4 = d2 + (d3 - d2) * d1;
         d4 = (float)Math.round((d4 - d2) / f) * f + d2;
         d4 = Math.max(d2, Math.min(d3, d4));
         if (this.setting.getCurrent() != (float)d4) {
            ZenithClient.on23().NbtItemSpec().on23(ZenithClient.on23().NbtItemSpec().soundEvent7);
         }

         this.setting.setCurrent((float)d4);
      }
   }

   @Override
   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         var6 *= this.animationVisible.CancellableEvent();
         float f = this.setting.getMin();
         float f1 = this.setting.getMax();
         float f2 = this.numberAnimation.CancellableEvent();
         float f3 = MathHelper.clamp((f2 - f) / (f1 - f), 0.0F, 1.0F);
         float f4 = this.selectedValueAnimation.CancellableEvent();
         if (!(f4 <= 0.01F)) {
            Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
            String s = String.valueOf(this.setting.getIncrement());
            int i = 0;
            int j = s.indexOf(46);
            if (j != -1 && (s.charAt(j + 1) != '0' || s.length() > j + 2 && s.charAt(j + 2) != '0')) {
               i = s.length() - j - 1;
            }

            String s1 = String.format(Locale.US, "%." + i + "f", f2);
            float f5 = this.width / 2.0F - GuiStyle.PADDING.intValue();
            float f6 = font.width(this.setting.getSuffix());
            float f7 = GuiStyle.PADDING.intValue() / 2.0F + font.width(s1.replaceAll(".", "0")) + f6 + GuiStyle.PADDING.intValue() / 2.0F + 1.0F;
            float f8 = f5 - f7 - GuiStyle.PADDING.intValue();
            float f9 = 2.0F;
            float f10 = var4 + this.width - f7 - f8 - GuiStyle.PADDING.intValue();
            float f11 = var5 + (this.getHeight() - f9) / 2.0F;
            float f12 = f8 * f3;
            float f13 = font.width(s1);
            float f14 = GuiStyle.PADDING.intValue() / 2.0F + f13 + GuiStyle.PADDING.intValue() / 2.0F + 1.0F;
            float f15 = 7.0F;
            float f16 = f10 + f12 + 1.0F;
            float f17 = MathHelper.clamp(f16 - f14 / 2.0F, f10, f10 + f8 - f14);
            float f18 = f11 - f15 - 4.0F + (1.0F - f4) * 3.0F;
            float f19 = var6 * f4;
            MsdfRenderer.flushBatch();
            var1.drawRoundedRectBatched(
               f17, f18, f14, f15, CornerRadius.MovementInputEvent(1.5F), zenithstyle.getPrimaryColor().getColor().SprintStateEvent(f19)
            );
            var1.flushRoundedRects();
            float f20 = f18 + (f15 - font.height()) / 2.0F;
            var1.drawText(font, s1, f17 + GuiStyle.PADDING.intValue() / 2.0F, f20, zenithstyle.getTextEnable().getColor().SprintStateEvent(f19));
         }
      }
   }
}
