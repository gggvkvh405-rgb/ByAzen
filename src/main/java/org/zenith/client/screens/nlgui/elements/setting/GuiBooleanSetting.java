package org.zenith.client.screens.nlgui.elements.setting;

import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.setting.BooleanSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiBooleanSetting extends GuiSetting<BooleanSetting> {
   public final UiAnimation animationEnable = new UiAnimation(200L, Easing.CloseScreenEvent);
   public CornerRadiusF bounds;

   public GuiBooleanSetting(BooleanSetting var1) {
      super(166.0F, var1);
   }

   public GuiBooleanSetting(BooleanSetting var1, float var2) {
      super(var2, var1);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.bounds != null && this.bounds.PotionItemBuilder(var1, var3)) {
         this.setting.toggle();
         ZenithClient.on23()
            .NbtItemSpec()
            .on23(this.setting.isEnabled() ? ZenithClient.on23().NbtItemSpec().soundEvent5 : ZenithClient.on23().NbtItemSpec().soundEvent6);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         this.animationVisible.on23(this.setting.isVisible());
         var6 *= this.animationVisible.CancellableEvent();
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font1 = Fonts.NEW_REGULAR.getFont(5.4F);
         float f = this.width / 1.4F - GuiStyle.PADDING.intValue();
         ArgbColor i11ii1llliilllii1i1 = zenithstyle.getTextEnable().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i11 = zenithstyle.getTextSecondary().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i12 = zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6);
         this.drawDefault(
            var1,
            var2,
            var3,
            "t",
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
         this.animationEnable.on23(this.setting.isEnabled());
         if (!this.isShort()) {
            float f1 = 6.0F;
            float f2 = 6.0F;
            this.bounds = new CornerRadiusF(var4 + this.width - f1, var5 + (this.getHeight() - f2) / 2.0F, f1, f2);
            var1.drawRoundedRectBatched(
               this.bounds.x(),
               this.bounds.y(),
               f1,
               f2,
               CornerRadius.MovementInputEvent(1.0F),
               zenithstyle.getFieldSurfaceBackground()
                  .getColor()
                  .SprintStateEvent(2.0F)
                  .Easing(zenithstyle.getPrimaryColor().getColor(), this.animationEnable.CancellableEvent())
                  .SprintStateEvent(var6)
            );
            var1.flushRoundedRects();
            var1.drawRoundedBorder(
               this.bounds.x(),
               this.bounds.y(),
               f1,
               f2,
               -0.5F,
               CornerRadius.MovementInputEvent(1.0F),
               zenithstyle.getFieldBorder().getColor().Easing(ArgbColor.var11941, this.animationEnable.CancellableEvent()).SprintStateEvent(var6)
            );
            Font font2 = Fonts.NEW_ICONS.getFont(4.0F);
            var1.drawText(
               font2,
               "<",
               this.bounds.x() + 1.5F - 0.8F,
               this.bounds.y() + (f2 - font2.height()) / 2.0F,
               ArgbColor.var11941.Easing(zenithstyle.getTextEnable().getColor(), this.animationEnable.CancellableEvent()).SprintStateEvent(var6)
            );
         } else {
            float f4 = 12.0F;
            float f5 = 7.0F;
            float f6 = var4 + this.width - f4;
            this.bounds = new CornerRadiusF(f6, var5 + (this.getHeight() - f5) / 2.0F, f4, f5);
            var1.drawRoundedRectBatched(
               f6,
               this.bounds.y(),
               f4,
               f5,
               CornerRadius.MovementInputEvent(2.5F),
               zenithstyle.getDisableActiveBg()
                  .getColor()
                  .Easing(zenithstyle.getPrimaryColor().getColor(), this.animationEnable.CancellableEvent())
                  .SprintStateEvent(var6)
            );
            float f3 = MathHelper.lerp(this.animationEnable.CancellableEvent(), 1.0F, f4 - 1.0F - 5.0F);
            var1.drawRoundedRectBatched(
               f6 + f3,
               this.bounds.y() + 1.0F,
               5.0F,
               5.0F,
               CornerRadius.MovementInputEvent(1.5F),
               zenithstyle.getTextTertiary().getColor().Easing(zenithstyle.getTextEnable().getColor(), this.animationEnable.CancellableEvent()).SprintStateEvent(var6)
            );
            var1.flushRoundedRects();
         }
      }
   }
}
