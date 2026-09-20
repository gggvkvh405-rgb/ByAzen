package org.zenith.client.screens.override.button;

import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.override.main.MainMenuBlurRenderer;
import org.zenith.core.AvatarRenderer;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.GradientRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class DefaultButton extends ButtonScreen {
   public final UiAnimation hoverAnimation = new UiAnimation(300L, Easing.CloseScreenEvent);
   public final String name;
   public final String icon;
   public final Runnable onClick;
   public final MainMenuBlurRenderer blurRenderer;

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5) {
      super.render(var1, var2, var3, var4, var5);
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      this.hoverAnimation.on23(this.bounds.PotionItemBuilder(var2, var3));
      AvatarRenderer.on23(
         org.zenith.render.GuiMatrixAdapter.toMatrix4f(var1.getMatrices()),
         var4,
         var5,
         this.getWidth(),
         this.getHeight(),
         10,
         CornerRadius.MovementInputEvent(4.0F),
         GradientRadius.CloudPoller(ArgbColor.var11941.Easing(ArgbColor.var11934.SprintStateEvent(0.2F), this.hoverAnimation.CancellableEvent()))
      );
      this.blurRenderer
         .render(
            org.zenith.render.GuiMatrixAdapter.toMatrix4f(var1.getMatrices()),
            var4,
            var5,
            this.getWidth(),
            this.getHeight(),
            CornerRadius.MovementInputEvent(4.0F),
            ArgbColor.var11934
         );
      var1.drawRoundedRect(
         var4, var5, this.getWidth(), this.getHeight(), CornerRadius.MovementInputEvent(4.0F), zenithstyle.getFieldSurfaceBackground().getColor()
      );
      var1.drawRoundedBorder(
         var4, var5, this.getWidth(), this.getHeight(), -0.1F, CornerRadius.MovementInputEvent(4.0F), zenithstyle.getFieldBorder().getColor()
      );
      Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
      Font font1 = Fonts.ICONS.getFont(6.0F);
      float f = 4.0F;
      float f1 = font1.width(this.icon);
      float f2 = font.width(this.name);
      float f3 = var4 + (this.getWidth() - f2) / 2.0F;
      float f4 = var5 + (this.getHeight() - font.height()) / 2.0F;
      float f5 = var5 + (this.getHeight() - font1.height()) / 2.0F;
      if (!this.name.equals("Exit")) {
         var1.drawText(
            font1,
            this.icon,
            var4 + 25.0F,
            f5,
            zenithstyle.getTextTertiary().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), this.hoverAnimation.CancellableEvent())
         );
      }

      var1.drawText(
         font, this.name, f3, f4, zenithstyle.getTextSecondary().getColor().Easing(zenithstyle.getTextEnable().getColor(), this.hoverAnimation.CancellableEvent())
      );
   }

   public DefaultButton(String var1, String var2, float var3, float var4, Runnable var5, MainMenuBlurRenderer var6) {
      super(var3, var4);
      this.name = var1;
      this.icon = var2;
      this.onClick = var5;
      this.blurRenderer = var6;
   }

   @Override
   public void onClick(double var1, double var3, MenuScreenId var5) {
      this.onClick.run();
   }
}
