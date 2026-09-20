package org.zenith.client.screens.nlgui.elements.setting;

import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.setting.ModeSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

class GuiModeSetting_RenderValue {
   public final GuiModeSetting this_0;
   public final ModeSetting.Option value;
   public final UiAnimation animationEnable;
   public CornerRadiusF bounds;

   public GuiModeSetting_RenderValue(GuiModeSetting var1, ModeSetting.Option var2) {
      this.this_0 = var1;
      this.value = var2;
      this.animationEnable = new UiAnimation(200L, var2.isSelected() ? 1.0F : 0.0F, Easing.CloseScreenEvent);
   }

   public void addRectToBatch(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = GuiModeSetting.access_000(this.this_0) / 2.0F - GuiStyle.PADDING.intValue();
         this.bounds = new CornerRadiusF(var4 + GuiStyle.PADDING.intValue() / 2.0F, var5, f, this.getHeight());
         this.animationEnable.on23(this.value.isSelected() ? 1.0F : (this.bounds.PotionItemBuilder(var2, var3) ? 0.5F : 0.0F));
         var1.drawRoundedRectBatched(
            this.bounds.x(),
            this.bounds.y(),
            this.bounds.width(),
            this.bounds.height(),
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 4.0F),
            ArgbColor.var11941.Easing(zenithstyle.getFieldSurfaceBackground().getColor(), this.animationEnable.CancellableEvent()).SprintStateEvent(var6)
         );
      }
   }

   public void renderText(HudDrawContext var1, float var2, float var3, float var4, Font var5, Font var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = this.this_0.getWidth() / 2.0F - GuiStyle.PADDING.intValue();
         var1.drawText(
            var5,
            this.value.getName(),
            var2 + GuiStyle.PADDING.intValue() / 2.0F + GuiStyle.PADDING.intValue() / 2.0F,
            var3 + (this.getHeight() - var5.height()) / 2.0F,
            zenithstyle.getTextSecondary().getColor().Easing(zenithstyle.getTextEnable().getColor(), this.animationEnable.CancellableEvent()).SprintStateEvent(var4)
         );
         var1.drawText(
            var6,
            "<",
            var2 + GuiStyle.PADDING.intValue() / 2.0F + f - var6.width("<") - GuiStyle.PADDING.intValue() / 2.0F,
            var3 + (this.getHeight() - var6.height()) / 2.0F,
            ArgbColor.var11941.Easing(zenithstyle.getPrimaryColor().getColor(), this.animationEnable.CancellableEvent()).SprintStateEvent(var4)
         );
      }
   }

   public float getHeight() {
      return 10.0F;
   }

   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.bounds != null && this.bounds.on23(var1, var3, GuiStyle.PADDING.intValue() / 2.0F)) {
         if (this.value.isSelected()) {
            this.this_0.expanded = false;
         }

         this.value.int210();
         return true;
      } else {
         return false;
      }
   }
}
