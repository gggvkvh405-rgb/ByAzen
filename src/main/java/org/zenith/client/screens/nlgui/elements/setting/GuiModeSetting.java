package org.zenith.client.screens.nlgui.elements.setting;

import java.util.ArrayList;
import java.util.List;
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
import org.zenith.render.ShapeRenderer;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiModeSetting extends GuiSetting<ModeSetting> {
   public final UiAnimation animationExpanded = new UiAnimation(200L, Easing.StopUsingItemEvent);
   public final UiAnimation textFadeAnimation = new UiAnimation(130L, 1.0F, Easing.CloseScreenEvent);
   public final List<GuiModeSetting_RenderValue> renderValues = new ArrayList<>();
   public CornerRadiusF bounds;
   public CornerRadiusF rectBounds;
   public boolean expanded;
   public String displayedValueText = "";
   public String pendingValueText = "";
   public boolean textSwitchPending;
   public boolean textAppearing;

   public GuiModeSetting(ModeSetting var1) {
      this(var1, 166.0F);
   }

   public GuiModeSetting(ModeSetting var1, float var2) {
      super(var2, var1);

      for (ModeSetting.Option ill11ii1ilil1liili1iliil_ii1il11l111ii11iil : var1.getValues()) {
         this.renderValues.add(new GuiModeSetting_RenderValue(this, ill11ii1ilil1liili1iliil_ii1il11l111ii11iil));
      }

      this.displayedValueText = var1.getValue().getName();
   }

   public static float access_000(GuiModeSetting this0) {
      return this0.width;
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.bounds != null && this.bounds.PotionItemBuilder(var1, var3)) {
         this.expanded = !this.expanded;
         return true;
      }

      if (this.expanded && this.rectBounds != null && this.rectBounds.PotionItemBuilder(var1, var3)) {
         return true;
      }

      this.expanded = false;
      return false;
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         this.animationVisible.on23(this.setting.isVisible());
         var6 *= this.animationVisible.CancellableEvent();
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
            "v",
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
         float f1 = this.width / 2.0F;
         float f2 = this.getHeight();
         this.bounds = new CornerRadiusF(var4 + this.width - f1, var5 + (this.getHeight() - f2) / 2.0F, f1, f2);
         var1.drawRoundedRectBatched(
            this.bounds.x(),
            this.bounds.y(),
            f1,
            f2,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
            zenithstyle.getFieldSurfaceBackground().getColor().SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         var1.drawRoundedBorder(
            this.bounds.x(),
            this.bounds.y(),
            f1,
            f2,
            0.1F,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
            zenithstyle.getFieldBorder().getColor().SprintStateEvent(var6)
         );
         String s = this.setting.getValue().getName();
         if (this.displayedValueText.isEmpty()) {
            this.displayedValueText = s;
            this.textFadeAnimation.setValue(1.0F);
         }

         if (!this.textSwitchPending && !s.equals(this.displayedValueText)) {
            this.pendingValueText = s;
            this.textSwitchPending = true;
            this.textAppearing = false;
            this.textFadeAnimation.Easing(0.0F);
         } else if (this.textSwitchPending && !s.equals(this.pendingValueText)) {
            this.pendingValueText = s;
         }

         float f3 = this.textFadeAnimation.EmotePlayback();
         if (this.textSwitchPending && !this.textAppearing && f3 <= 0.02F) {
            this.displayedValueText = this.pendingValueText;
            this.textAppearing = true;
            this.textFadeAnimation.setValue(0.0F);
            this.textFadeAnimation.Easing(1.0F);
            f3 = 0.0F;
         } else if (this.textSwitchPending && this.textAppearing && f3 >= 0.98F) {
            this.textSwitchPending = false;
            this.textAppearing = false;
            this.textFadeAnimation.setValue(1.0F);
            f3 = 1.0F;
         } else if (!this.textSwitchPending) {
            f3 = this.textFadeAnimation.on23(1.0F);
         }

         this.textFadeAnimation.on23(Easing.StopUsingItemEvent);
         this.textFadeAnimation.on23(200L);
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.3F);
         float f4 = this.bounds.x() + GuiStyle.PADDING.intValue();
         float f5 = this.bounds.y() + (f2 - font2.height()) / 2.0F;
         float f6 = f1 - GuiStyle.PADDING.intValue() * 2.0F;
         float f7;
         float f8;
         if (this.textSwitchPending && !this.textAppearing) {
            f7 = f3;
            f8 = 1.0F;
         } else if (this.textSwitchPending) {
            f7 = 0.0F;
            f8 = Math.max(0.02F, f3);
         } else {
            f7 = 1.0F;
            f8 = 1.0F;
         }

         MsdfRenderer.renderText(
            font2.getFont(),
            this.displayedValueText,
            font2.getSize(),
            zenithstyle.getTextEnable().getColor().SprintStateEvent(var6).call001(),
            org.zenith.render.GuiMatrixAdapter.toMatrix4f(var1.getMatrices()),
            f4,
            f5,
            0.0F,
            true,
            f7,
            f8,
            f6
         );
      }
   }

   @Override
   public float getHeight() {
      return 14.0F;
   }

   @Override
   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      var6 *= this.animationExpanded.CancellableEvent() * this.animationVisible.CancellableEvent();
      float f = this.width / 2.0F;
      float f1 = this.getHeight();
      this.bounds = new CornerRadiusF(var4 + this.width - f, var5 + (this.getHeight() - f1) / 2.0F, f, f1);
      this.animationExpanded.on23(this.expanded);
      if (this.animationExpanded.CancellableEvent() > 0.0F) {
         float f2 = GuiStyle.PADDING.intValue() / 2.0F + this.renderValues.size() * (10.0F + GuiStyle.PADDING.intValue() / 2.0F);
         float f3 = this.bounds.y() + f1 + GuiStyle.PADDING.intValue();
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(this.bounds.x() + f / 2.0F, f3);
         var1.getMatrices().scale(this.animationExpanded.CancellableEvent(), this.animationExpanded.CancellableEvent());
         var1.getMatrices().translate(-(this.bounds.x() + f / 2.0F), -f3);
         this.rectBounds = new CornerRadiusF(this.bounds.x(), f3, f, f2);
         CornerRadius popupRadius = CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F);
         ShapeRenderer.ItemSpec(
            var1.getMatrices(),
            this.bounds.x(),
            f3,
            f,
            f2,
            12.0F,
            popupRadius,
            ArgbColor.var11934.SprintStateEvent(var6)
         );
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         if (zenithstyle != null) {
            var1.drawRoundedRectBatched(
               this.bounds.x(), f3, f, f2, popupRadius, zenithstyle.getLeftBackground().getColor().SprintStateEvent(var6)
            );
            var1.flushRoundedRects();
            var1.drawRoundedBorder(
               this.bounds.x(),
               f3,
               f,
               f2,
               0.1F,
               popupRadius,
               zenithstyle.getFieldBorder().getColor().SprintStateEvent(var6 * 2.0F)
            );
         }

         f3 += GuiStyle.PADDING.intValue() / 2.0F;
         Font font = Fonts.NEW_MEDIUM.getFont(5.3F);
         Font font1 = Fonts.NEW_ICONS.getFont(5.0F);

         for (GuiModeSetting_RenderValue guimodesetting_rendervalue : this.renderValues) {
            guimodesetting_rendervalue.addRectToBatch(var1, var2, var3, this.bounds.x(), f3, var6);
            f3 += 10.0F + GuiStyle.PADDING.intValue() / 2.0F;
         }

         var1.flushRoundedRects();
         float f4 = this.bounds.y() + f1 + GuiStyle.PADDING.intValue() + GuiStyle.PADDING.intValue() / 2.0F;

         for (GuiModeSetting_RenderValue guimodesetting_rendervalue1 : this.renderValues) {
            guimodesetting_rendervalue1.renderText(var1, this.bounds.x(), f4, var6, font, font1);
            f4 += 10.0F + GuiStyle.PADDING.intValue() / 2.0F;
         }

         var1.getMatrices().popMatrix();
      }
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      if (this.expanded && this.rectBounds != null && this.rectBounds.PotionItemBuilder(var1, var3) && var5.int203() == 0) {
         for (GuiModeSetting_RenderValue guimodesetting_rendervalue : this.renderValues) {
            if (guimodesetting_rendervalue.onMouseClicked(var1, var3, var5)) {
               return true;
            }
         }

         return true;
      } else if ((this.bounds == null || !this.bounds.PotionItemBuilder(var1, var3)) && this.expanded) {
         this.expanded = false;
         return false;
      } else {
         return super.onMousePriorityClicked(var1, var3, var5);
      }
   }

   @Override
   public boolean onMousePriorityScroll(double var1, double var3, double var5, double var7) {
      this.expanded = false;
      return false;
   }

   public boolean contains(float var1, float var2) {
      return this.expanded && this.rectBounds != null && this.rectBounds.PotionItemBuilder(var1, var2);
   }
}
