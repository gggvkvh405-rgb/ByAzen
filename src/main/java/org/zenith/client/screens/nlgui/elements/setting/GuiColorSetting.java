package org.zenith.client.screens.nlgui.elements.setting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector2f;
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
import org.zenith.hud.SearchBox;
import org.zenith.hud.SearchBox;
import org.zenith.render.ShapeRenderer;
import org.zenith.setting.ColorSetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.ColorUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.GradientRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiColorSetting extends GuiSetting<ColorSetting> {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final float THEME_COLOR_SIZE = 6.0F;
   public static final float THEME_COLOR_GAP = 2.0F;
   public final UiAnimation animationExpanded = new UiAnimation(200L, Easing.StopUsingItemEvent);
   public final UiAnimation saturationAnimation;
   public final UiAnimation brightnessAnimation;
   public final UiAnimation hueAnimation;
   public final UiAnimation alphaAnimation;
   public final List<CornerRadiusF> themeColorBounds = new ArrayList<>();
   public final List<ArgbColor> themeColors = new ArrayList<>();
   public CornerRadiusF bounds;
   public CornerRadiusF rectBounds;
   public CornerRadiusF colorBounds;
   public CornerRadiusF hueBounds;
   public CornerRadiusF alphaBounds;
   public CornerRadiusF textBounds;
   public CornerRadiusF copyBounds;
   public CornerRadiusF pasteBounds;
   public CornerRadiusF exitBounds;
   public boolean expanded;
   public float hue;
   public float saturation;
   public float brightness;
   public int alpha;
   public boolean sbFocused;
   public boolean hFocused;
   public boolean aFocused;
   public SearchBox colorString;

   public GuiColorSetting(ColorSetting var1) {
      this(var1, 166.0F);
   }

   public GuiColorSetting(ColorSetting var1, float var2) {
      super(var2, var1);
      this.updateFromColor();
      this.saturationAnimation = this.createSliderAnimation(this.saturation);
      this.brightnessAnimation = this.createSliderAnimation(this.brightness);
      this.hueAnimation = this.createSliderAnimation(this.hue);
      this.alphaAnimation = this.createSliderAnimation(this.alpha / 255.0F);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.bounds != null && this.bounds.on23(var1, var3, 2.0F)) {
         if (this.animationExpanded.BotDisconnectEvent() == 0.0F) {
            this.expanded = true;
         } else {
            this.expanded = false;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      if (this.colorString == null) {
         this.colorString = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.5F), "Enter hex color", 0.0F);
         this.colorString.on23(SearchBox.MatchMode.val129);
         this.colorString.EventItemRenderHook(6);
      }

      if (!this.expanded) {
         this.updateFromColor();
      }

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
            "x",
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
         float f1 = 4.0F;
         float f2 = 4.0F;
         this.bounds = new CornerRadiusF(var4 + this.width - f1, var5 + (this.getHeight() - f2) / 2.0F, f1, f2);
         var1.drawRoundedRect(
            this.bounds.x(),
            this.bounds.y(),
            f1,
            f2,
            CornerRadius.MovementInputEvent(1.0F),
            zenithstyle.getFieldSurfaceBackground().getColor().SprintStateEvent(var6)
         );
         var1.drawRoundedBorder(
            this.bounds.x(),
            this.bounds.y(),
            f1,
            f2,
            -0.5F,
            CornerRadius.MovementInputEvent(1.0F),
            zenithstyle.getFieldBorder().getColor().SprintStateEvent(var6)
         );
         Font font2 = Fonts.NEW_ICONS.getFont(6.0F);
         float f3 = 1.5F;
         ArgbColor i11ii1llliilllii1i13 = this.setting.getColor(var6);
         ArgbColor i11ii1llliilllii1i14 = ArgbColor.var11942.SprintStateEvent(var6);
         var1.drawRoundedBorder(
            this.bounds.x() - f3, this.bounds.y() - f3, f1 + f3 * 2.0F, f2 + f3 * 2.0F, 0.1F, CornerRadius.MovementInputEvent(3.0F), i11ii1llliilllii1i13
         );
         var1.drawRoundedRect(
            this.bounds.x(),
            this.bounds.y(),
            f1,
            f2,
            CornerRadius.MovementInputEvent(3.0F),
            GradientRadius.on23(i11ii1llliilllii1i14, i11ii1llliilllii1i13, i11ii1llliilllii1i13, i11ii1llliilllii1i14)
         );
      }
   }

   @Override
   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.animationExpanded.on23(this.expanded);
      if (!(this.animationExpanded.CancellableEvent() <= 0.0F) && this.bounds != null) {
         float f = GuiStyle.PADDING * 2 + this.getHeight() + GuiStyle.PADDING * 2;
         float f1 = GuiStyle.PADDING * 2;
         float f2 = 126.0F;
         float f3 = 64.0F;
         float f4 = 4.0F;
         float f5 = 14.0F;
         this.updateThemeColors();
         float f6 = f2 - f1 * 2.0F;
         int i = Math.max(1, (int)((f6 + 2.0F) / 8.0F));
         int j = (this.themeColors.size() + i - 1) / i;
         float f7 = j == 0 ? 0.0F : f1 * 2.0F + j * 6.0F + (j - 1) * 2.0F;
         float f8 = f + f1 + f3 + f1 + f7 + f1 + f4 + f1 + f4 + f1 * 2.0F + f5 + f1;
         float f9 = this.bounds.x() + GuiStyle.PADDING.intValue() * 2.0F;
         float f10 = this.bounds.y() - f8 / 2.0F;
         this.rectBounds = new CornerRadiusF(f9, f10, f2, f8);
         var6 *= this.animationExpanded.CancellableEvent();
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(f9, this.bounds.y());
         var1.getMatrices().scale(this.animationExpanded.CancellableEvent(), this.animationExpanded.CancellableEvent());
         var1.getMatrices().translate(-f9, -this.bounds.y());
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         if (zenithstyle != null) {
            float popupCorner = GuiStyle.ROUND.intValue() / 2.0F;
            CornerRadius popupRadius = CornerRadius.MovementInputEvent(popupCorner);
            ShapeRenderer.ItemSpec(
               var1.getMatrices(),
               f9,
               f10,
               f2,
               f8,
               12.0F,
               popupRadius,
               ArgbColor.var11934.SprintStateEvent(var6)
            );
            var1.drawRoundedRect(
               f9,
               f10,
               f2,
               f,
               CornerRadius.BotPacketEvent(popupCorner, popupCorner),
               zenithstyle.getRightBackground().getColor().SprintStateEvent(var6)
            );
            Font font = Fonts.NEW_ICONS.getFont(4.0F);
            float f11 = f9 + f2 - font.width("2") - f1;
            float f12 = f10 + f1 + font.height();
            this.exitBounds = new CornerRadiusF(f11, f12, 5.0F, 5.0F);
            var1.drawText(font, "2", f11, f12, zenithstyle.getTextTertiary().getColor().SprintStateEvent(var6));
            float f13 = f9 + f1;
            float f14 = f10 + f + f1;
            float f15 = f2 - f1 * 2.0F;
            this.colorBounds = new CornerRadiusF(f13, f14, f15, f3);
            float f16 = this.updateSliderAnimation(this.saturationAnimation, this.saturation);
            float f17 = this.updateSliderAnimation(this.brightnessAnimation, this.brightness);
            float f18 = this.updateSliderAnimation(this.hueAnimation, this.hue);
            float f19 = this.updateSliderAnimation(this.alphaAnimation, this.alpha / 255.0F);
            float f20 = f13 + f15 * f16;
            float f21 = f14 + (f3 - f3 * f17);
            float f22 = f15 * f18;
            float f23 = f15 * f19;
            ArgbColor i11ii1llliilllii1i1 = new ArgbColor(Color.getHSBColor(this.hue, 0.0F, 1.0F)).SprintStateEvent(var6);
            ArgbColor i11ii1llliilllii1i11 = new ArgbColor(Color.getHSBColor(this.hue, 1.0F, 1.0F)).SprintStateEvent(var6);
            ArgbColor i11ii1llliilllii1i12 = new ArgbColor(new Color(0, 0, 0, 0)).SprintStateEvent(var6);
            ArgbColor i11ii1llliilllii1i13 = new ArgbColor(new Color(0, 0, 0)).SprintStateEvent(var6);
            var1.drawRoundedRect(f9, f14 - f1, f2, f3 + f1 * 2.0F, CornerRadius.var159, zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(var6));
            var1.drawRoundedRect(
               f13,
               f14,
               f15,
               f3,
               CornerRadius.MovementInputEvent(4.0F),
               GradientRadius.on23(i11ii1llliilllii1i1, i11ii1llliilllii1i1, i11ii1llliilllii1i11, i11ii1llliilllii1i11)
            );
            var1.drawRoundedRect(
               f13,
               f14,
               f15,
               f3,
               CornerRadius.MovementInputEvent(4.0F),
               GradientRadius.on23(i11ii1llliilllii1i12, i11ii1llliilllii1i13, i11ii1llliilllii1i12, i11ii1llliilllii1i13)
            );
            var1.drawRoundedRect(f20 - 2.0F, f21 - 2.0F, 6.0F, 6.0F, CornerRadius.MovementInputEvent(2.0F), ArgbColor.var11934.SprintStateEvent(var6));
            Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
            Font font2 = Fonts.NEW_REGULAR.getFont(5.4F);
            float f24 = f2 / 1.4F - GuiStyle.PADDING.intValue();
            ArgbColor i11ii1llliilllii1i14 = zenithstyle.getTextEnable().getColor().SprintStateEvent(var6);
            ArgbColor i11ii1llliilllii1i15 = zenithstyle.getTextSecondary().getColor().SprintStateEvent(var6);
            ArgbColor i11ii1llliilllii1i16 = zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6);
            this.drawDefault(
               var1,
               var2,
               var3,
               "x",
               this.setting.getName(),
               this.setting.getDescription(),
               font1,
               font2,
               f13,
               f10 + f1,
               f24,
               i11ii1llliilllii1i14,
               i11ii1llliilllii1i15,
               i11ii1llliilllii1i16
            );
            float f27 = f14 + f3 + f1 * 2.0F;
            var1.drawRoundedRect(
               f9, f27 - f1, f2, f1 * 3.0F + f4 * 2.0F, CornerRadius.var159, zenithstyle.getRightBackground().getColor().SprintStateEvent(var6)
            );
            this.hueBounds = new CornerRadiusF(f13, f27, f15, f4);
            ShapeRenderer.on23(
               var1.getMatrices(),
               ZenithClient.on23("icons/sliderhue.png"),
               f13,
               f27,
               f15,
               f4,
               CornerRadius.MovementInputEvent(1.0F),
               ArgbColor.var11934.SprintStateEvent(var6)
            );
            var1.drawRoundedRect(f13 + f22 - 2.0F, f27 - 1.0F, 6.0F, 6.0F, CornerRadius.MovementInputEvent(2.0F), ArgbColor.var11934.SprintStateEvent(var6));
            float f28 = f27 + f4 + f1;
            this.alphaBounds = new CornerRadiusF(f13, f28, f15, f4);
            ShapeRenderer.on23(
               var1.getMatrices(),
               ZenithClient.on23("icons/slidertransparent.png"),
               f13,
               f28,
               f15,
               f4,
               CornerRadius.MovementInputEvent(1.0F),
               ArgbColor.var11934.SprintStateEvent(var6)
            );
            ArgbColor i11ii1llliilllii1i18 = this.setting.getColor().EventHookWorldRender(255).SprintStateEvent(var6);
            var1.drawRoundedRect(
               f13,
               f28,
               f15,
               f4,
               CornerRadius.MovementInputEvent(1.0F),
               GradientRadius.on23(ArgbColor.var11941, ArgbColor.var11941, i11ii1llliilllii1i18, i11ii1llliilllii1i18)
            );
            var1.drawRoundedRect(f13 + f23 - 2.0F, f28 - 1.0F, 6.0F, 6.0F, CornerRadius.MovementInputEvent(2.0F), ArgbColor.var11934.SprintStateEvent(var6));
            float f29 = f28 + f1 + f4;
            var1.drawRoundedRect(f9, f29, f2, f7, CornerRadius.var159, zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(var6));
            this.themeColorBounds.clear();
            Font font3 = Fonts.NEW_ICONS.getFont(4.0F);

            for (int i1 = 0; i1 < this.themeColors.size(); i1++) {
               int k = i1 % i;
               int l = i1 / i;
               float f25 = f13 + k * 8.0F;
               float f26 = f29 + f1 + l * 8.0F;
               CornerRadiusF l11liliill1iii1 = new CornerRadiusF(f25, f26, 6.0F, 6.0F);
               ArgbColor i11ii1llliilllii1i17 = this.themeColors.get(i1);
               boolean flag = this.hasSameRgb(this.setting.getColor(), i11ii1llliilllii1i17);
               this.themeColorBounds.add(l11liliill1iii1);
               var1.drawRoundedBorder(f25, f26, 6.0F, 6.0F, 0.2F, CornerRadius.MovementInputEvent(3.0F), i11ii1llliilllii1i17.SprintStateEvent(var6 * 0.5F));
               var1.drawRoundedRect(f25 + 1.0F, f26 + 1.0F, 4.0F, 4.0F, CornerRadius.MovementInputEvent(2.0F), i11ii1llliilllii1i17.SprintStateEvent(var6));
            }

            float f30 = f29 + f7 + f1;
            var1.drawRoundedRect(
               f9,
               f30 - f1,
               f2,
               f5 + f1 * 2.0F,
               CornerRadius.RotationUpdateStartEvent(popupCorner),
               zenithstyle.getRightBackground().getColor().SprintStateEvent(var6)
            );
            float f31 = f13 + f15 - f5;
            float f32 = f31 - f1 / 2.0F - f5;
            float f33 = f32 - f1 / 2.0F - f13;
            this.textBounds = new CornerRadiusF(f13, f30, f33, f5);
            this.copyBounds = new CornerRadiusF(f32, f30, f5, f5);
            this.pasteBounds = new CornerRadiusF(f31, f30, f5, f5);
            var1.drawRoundedRect(
               this.textBounds.x(),
               this.textBounds.y(),
               this.textBounds.width(),
               this.textBounds.height(),
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
               zenithstyle.getFieldSurfaceBackground().getColor().SprintStateEvent(var6)
            );
            var1.drawRoundedBorder(
               this.textBounds.x(),
               this.textBounds.y(),
               this.textBounds.width(),
               this.textBounds.height(),
               0.1F,
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
               zenithstyle.getFieldBorder().getColor().SprintStateEvent(var6)
            );
            Font font4 = Fonts.NEW_MEDIUM.getFont(5.4F);
            var1.drawText(
               font4,
               "#",
               this.textBounds.x() + f1,
               this.textBounds.y() + (f5 - font4.height()) / 2.0F,
               zenithstyle.getTextTertiary().getColor().SprintStateEvent(var6)
            );
            this.colorString.setWidth(this.textBounds.width() - f1 * 2.0F - font4.width("#"));
            this.colorString
               .on23(
                  var1,
                  this.textBounds.x() + f1 + font4.width("#") + 1.0F,
                  this.textBounds.y() + (f5 - font4.height()) / 2.0F,
                  zenithstyle.getTextEnable().getColor().SprintStateEvent(var6),
                  zenithstyle.getTextTertiary().getColor().SprintStateEvent(var6)
               );
            var1.drawRoundedRect(
               this.copyBounds.x(),
               this.copyBounds.y(),
               this.copyBounds.width(),
               this.copyBounds.height(),
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
               zenithstyle.getDisableActiveBg().getColor().SprintStateEvent(var6)
            );
            var1.drawRoundedRect(
               this.pasteBounds.x(),
               this.pasteBounds.y(),
               this.pasteBounds.width(),
               this.pasteBounds.height(),
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
               zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6)
            );
            Font font5 = Fonts.NEW_ICONS.getFont(5.0F);
            var1.drawText(
               font5,
               "L",
               this.copyBounds.x() + (f5 - font5.width("C")) / 2.0F,
               this.copyBounds.y() + (f5 - font5.height()) / 2.0F,
               zenithstyle.getTextTertiary().getColor().SprintStateEvent(var6)
            );
            var1.drawText(
               font5,
               "M",
               this.pasteBounds.x() + (f5 - font5.width("P")) / 2.0F + 0.2F,
               this.pasteBounds.y() + (f5 - font5.height()) / 2.0F,
               zenithstyle.getTextEnable().getColor().SprintStateEvent(var6)
            );
            if (this.sbFocused) {
               this.saturation = MathHelper.clamp(var2 - f13, 0.0F, f15) / f15;
               this.brightness = (f3 - MathHelper.clamp(var3 - f14, 0.0F, f3)) / f3;
               this.saturation = MathHelper.clamp(this.saturation, 0.0F, 1.0F);
               this.brightness = MathHelper.clamp(this.brightness, 0.0F, 1.0F);
               this.setColorFromHSB();
            }

            if (this.hFocused) {
               this.hue = MathHelper.clamp(var2 - f13, 0.0F, f15) / f15;
               this.hue = MathHelper.clamp(this.hue, 0.0F, 1.0F);
               this.setColorFromHSB();
            }

            if (this.aFocused) {
               this.alpha = (int)(MathHelper.clamp((var2 - f13) / f15, 0.0F, 1.0F) * 255.0F);
               this.setColorFromHSB();
            }

            if (this.colorString.isSelected()) {
               this.setColor(ColorUtils.on23(this.colorString.getText(), this.setting.getColor()).EventHookWorldRender(this.alpha));
               this.updateFromColor();
            } else {
               this.colorString.HudHotbarPanel(ColorUtils.EmoteManager(this.setting.getColor()));
               this.colorString.EventRender(6);
            }

            var1.getMatrices().popMatrix();
         }
      }
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      if (!this.expanded || this.rectBounds == null) {
         return false;
      }

      if (!this.rectBounds.PotionItemBuilder(var1, var3)) {
         this.expanded = false;
         this.colorString.VelocityChangeEvent(false);
         this.sbFocused = false;
         this.hFocused = false;
         this.aFocused = false;
         return false;
      }

      if (this.exitBounds != null && this.exitBounds.on23(var1, var3, 2.0F)) {
         this.expanded = false;
         this.colorString.VelocityChangeEvent(false);
         this.sbFocused = false;
         this.hFocused = false;
         this.aFocused = false;
         return true;
      }

      if (this.textBounds != null && this.textBounds.PotionItemBuilder(var1, var3)) {
         this.colorString.VelocityChangeEvent(true);
      } else {
         this.colorString.VelocityChangeEvent(false);
      }

      for (int i = 0; i < this.themeColorBounds.size() && i < this.themeColors.size(); i++) {
         if (this.themeColorBounds.get(i).PotionItemBuilder(var1, var3)) {
            this.setColor(this.themeColors.get(i).EventHookWorldRender(this.alpha));
            this.updateFromColor();
            return true;
         }
      }

      if (this.copyBounds != null && this.copyBounds.PotionItemBuilder(var1, var3)) {
         minecraftClient3.keyboard.setClipboard(ColorUtils.EmoteManager(this.setting.getColor()));
         return true;
      }

      if (this.pasteBounds != null && this.pasteBounds.PotionItemBuilder(var1, var3)) {
         String s = minecraftClient3.keyboard.getClipboard();
         if (s != null) {
            s = s.replace("#", "").trim();
            this.colorString.HudHotbarPanel(s);
            this.colorString.EventRender(Math.min(s.length(), 6));
            this.setColor(ColorUtils.on23(s, this.setting.getColor()).EventHookWorldRender(this.alpha));
            this.updateFromColor();
         }

         return true;
      } else if (this.colorBounds != null && this.colorBounds.PotionItemBuilder(var1, var3)) {
         if (!this.hFocused && !this.aFocused) {
            this.sbFocused = true;
         }

         return true;
      } else if (this.hueBounds != null && this.hueBounds.PotionItemBuilder(var1, var3)) {
         if (!this.sbFocused && !this.aFocused) {
            this.hFocused = true;
         }

         return true;
      } else if (this.alphaBounds != null && this.alphaBounds.PotionItemBuilder(var1, var3)) {
         if (!this.sbFocused && !this.hFocused) {
            this.aFocused = true;
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public boolean onMousePriorityScroll(double var1, double var3, double var5, double var7) {
      if (!this.expanded) {
         return false;
      }

      this.expanded = false;
      this.colorString.VelocityChangeEvent(false);
      this.sbFocused = false;
      this.hFocused = false;
      this.aFocused = false;
      return false;
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return this.expanded && this.colorString.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return this.expanded && this.colorString.charTyped(var1, var2) ? true : super.charTyped(var1, var2);
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      this.sbFocused = false;
      this.hFocused = false;
      this.aFocused = false;
      super.onMouseReleased(var1, var3, var5);
   }

   public void updateFromColor() {
      float[] afloat = Color.RGBtoHSB(this.setting.getColor().float240(), this.setting.getColor().var14323(), this.setting.getColor().var14324(), null);
      this.hue = afloat[0];
      this.saturation = afloat[1];
      this.brightness = afloat[2];
      this.alpha = this.setting.getColor().var14325();
   }

   public void setColorFromHSB() {
      Color color = Color.getHSBColor(this.hue, this.saturation, this.brightness);
      this.setting.setColor(new ArgbColor(color.getRed(), color.getGreen(), color.getBlue(), this.alpha));
   }

   public void setColor(ArgbColor var1) {
      this.setting.setColor(var1);
   }

   public UiAnimation createSliderAnimation(float var1) {
      UiAnimation l1i1illlili = new UiAnimation(120L, var1, Easing.Event18Ext5);
      l1i1illlili.setValue(var1);
      return l1i1illlili;
   }

   public float updateSliderAnimation(UiAnimation var1, float var2) {
      float f = MathHelper.lerp(0.3F, var1.CancellableEvent(), var2);
      var1.setValue(f);
      return f;
   }

   public void updateThemeColors() {
      this.themeColors.clear();

      for (ZenithStyle zenithstyle : ZenithClient.on23().TextScanner().getStyles()) {
         this.themeColors.add(zenithstyle.getPrimaryColor().getColor());
         this.themeColors.add(zenithstyle.getSecondaryPrimaryColor().getColor());
      }
   }

   public boolean hasSameRgb(ArgbColor var1, ArgbColor var2) {
      return var1.float240() == var2.float240() && var1.var14323() == var2.var14323() && var1.var14324() == var2.var14324();
   }
}
