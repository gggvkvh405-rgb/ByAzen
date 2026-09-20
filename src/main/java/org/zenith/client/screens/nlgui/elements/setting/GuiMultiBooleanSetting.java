package org.zenith.client.screens.nlgui.elements.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiMultiBooleanSetting extends GuiSetting<MultiSelectSetting> {
   public final UiAnimation animationExpanded = new UiAnimation(200L, Easing.StopUsingItemEvent);
   public final UiAnimation textFadeAnimation = new UiAnimation(130L, 1.0F, Easing.CloseScreenEvent);
   public final List<GuiMultiBooleanSetting_RenderValue> renderValues = new ArrayList<>();
   public CornerRadiusF bounds;
   public CornerRadiusF rectBounds;
   public boolean expanded;
   public String displayedValueText = "";
   public String pendingValueText = "";
   public boolean textSwitchPending;

   public GuiMultiBooleanSetting(MultiSelectSetting var1) {
      this(var1, 166.0F);
   }

   public GuiMultiBooleanSetting(MultiSelectSetting var1, float var2) {
      super(var2, var1);

      for (MultiSelectSetting.Option i1i1lll1liii1il1llll1_ii1il11l111ii11iil : var1.int212()) {
         this.renderValues.add(new GuiMultiBooleanSetting_RenderValue(this, i1i1lll1liii1il1llll1_ii1il11l111ii11iil));
      }

      this.displayedValueText = this.collectCurrentValueText();
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
         String s = this.collectCurrentValueText();
         if (this.displayedValueText.isEmpty()) {
            this.displayedValueText = s;
            this.textFadeAnimation.setValue(1.0F);
         }

         if (!this.textSwitchPending && !s.equals(this.displayedValueText)) {
            this.pendingValueText = s;
            this.textSwitchPending = true;
            this.textFadeAnimation.Easing(0.0F);
         } else if (this.textSwitchPending && !s.equals(this.pendingValueText)) {
            this.pendingValueText = s;
         }

         if (this.textSwitchPending && this.textFadeAnimation.EmotePlayback() <= 0.02F) {
            this.displayedValueText = this.pendingValueText;
            this.textSwitchPending = false;
            this.textFadeAnimation.setValue(0.0F);
            this.textFadeAnimation.Easing(1.0F);
         } else if (!this.textSwitchPending) {
            this.textFadeAnimation.on23(1.0F);
         }

         Font font2 = Fonts.NEW_MEDIUM.getFont(5.3F);
         f = f1 - GuiStyle.PADDING * 2;
         float f3 = 0.4F;
         float f4 = this.textFadeAnimation.CancellableEvent();
         MsdfRenderer.renderText(
            font2.getFont(),
            this.displayedValueText,
            font2.getSize(),
            i11ii1llliilllii1i1.SprintStateEvent(f4).call001(),
            org.zenith.render.GuiMatrixAdapter.toMatrix4f(var1.getMatrices()),
            this.bounds.x() + GuiStyle.PADDING.intValue(),
            this.bounds.y() + (f2 - font2.height()) / 2.0F,
            0.0F,
            true,
            f3,
            1.0F,
            f
         );
      }
   }

   @Override
   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      var6 *= this.animationExpanded.CancellableEvent() * this.animationVisible.CancellableEvent();
      float f = this.width / 2.0F;
      float f1 = this.getHeight();
      this.bounds = new CornerRadiusF(var4 + this.width - f, var5 + (this.getHeight() - f1) / 2.0F, f, f1);
      this.animationExpanded.on23(this.expanded);
      if (this.animationExpanded.CancellableEvent() > 0.0F) {
         float f2 = this.getWidth();
         float f3 = GuiStyle.PADDING.intValue() / 2.0F + this.renderValues.size() * (10.0F + GuiStyle.PADDING.intValue() / 2.0F);
         float f4 = this.bounds.y() + f1 + GuiStyle.PADDING.intValue();
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(this.bounds.x() + f / 2.0F, f4);
         var1.getMatrices().scale(this.animationExpanded.CancellableEvent(), this.animationExpanded.CancellableEvent());
         var1.getMatrices().translate(-(this.bounds.x() + f / 2.0F), -f4);
         this.rectBounds = new CornerRadiusF(this.bounds.x(), f4, f, f3);
         CornerRadius popupRadius = CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F);
         ShapeRenderer.ItemSpec(
            var1.getMatrices(),
            this.bounds.x(),
            f4,
            f,
            f3,
            12.0F,
            popupRadius,
            ArgbColor.var11934.SprintStateEvent(var6)
         );
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         if (zenithstyle != null) {
            var1.drawRoundedRectBatched(
               this.bounds.x(), f4, f, f3, popupRadius, zenithstyle.getLeftBackground().getColor().SprintStateEvent(var6)
            );
            var1.flushRoundedRects();
            var1.drawRoundedBorder(
               this.bounds.x(),
               f4,
               f,
               f3,
               0.1F,
               popupRadius,
               zenithstyle.getFieldBorder().getColor().SprintStateEvent(var6 * 2.0F)
            );
         }

         f4 += GuiStyle.PADDING.intValue() / 2.0F;
         Font font = Fonts.NEW_MEDIUM.getFont(5.3F);
         Font font1 = Fonts.NEW_ICONS.getFont(5.0F);

         for (GuiMultiBooleanSetting_RenderValue guimultibooleansetting_rendervalue : this.renderValues) {
            guimultibooleansetting_rendervalue.addRectToBatch(var1, var2, var3, this.bounds.x(), f4, var6);
            f4 += 10.0F + GuiStyle.PADDING.intValue() / 2.0F;
         }

         var1.flushRoundedRects();
         float f5 = this.bounds.y() + f1 + GuiStyle.PADDING.intValue() + GuiStyle.PADDING.intValue() / 2.0F;

         for (GuiMultiBooleanSetting_RenderValue guimultibooleansetting_rendervalue1 : this.renderValues) {
            guimultibooleansetting_rendervalue1.renderText(var1, this.bounds.x(), f5, var6, font, font1);
            f5 += 10.0F + GuiStyle.PADDING.intValue() / 2.0F;
         }

         var1.getMatrices().popMatrix();
      }
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      if (this.expanded && this.rectBounds != null && this.rectBounds.PotionItemBuilder(var1, var3) && var5.int203() == 0) {
         for (GuiMultiBooleanSetting_RenderValue guimultibooleansetting_rendervalue : this.renderValues) {
            if (guimultibooleansetting_rendervalue.onMouseClicked(var1, var3, var5)) {
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
   public float getHeight() {
      return 14.0F;
   }

   @Override
   public boolean onMousePriorityScroll(double var1, double var3, double var5, double var7) {
      this.expanded = false;
      return false;
   }

   public String collectCurrentValueText() {
      String s = this.setting.int212().stream().filter(MultiSelectSetting.Option::isEnabled).map(MultiSelectSetting.Option::getName).collect(Collectors.joining(",  "));
      return s.isEmpty() ? "---------" : s;
   }
}
