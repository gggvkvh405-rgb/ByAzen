package org.zenith.client.screens.nlgui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.InterfaceElement;
import org.zenith.client.screens.nlgui.elements.setting.GuiWindowSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElementValue;
import org.zenith.render.HudPreviewRenderQueue;
import org.zenith.setting.SettingGroup;
import org.zenith.setting.Setting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;
import org.zenith.utility.render.display.base.HudQueuedContext;

public class GuiInterfaceDragElement extends InterfaceElement {
   public static final float HEADER_HEIGHT = 23.0F;
   public final HudElement draggableElement;
   public final UiAnimation animationEnable = new UiAnimation(200L, Easing.CloseScreenEvent);
   public final GuiWindowSetting guiWindowSetting;
   public final HudPreviewRenderQueue hudPreviewRenderQueue = new HudPreviewRenderQueue();
   public float animationPosX = 0.0F;
   public float animationPosY = 0.0F;
   public float lastX = 0.0F;
   public float lastY = 0.0F;
   public int lastIndex = -1;
   public boolean animated = false;
   public boolean positionInitialized = false;
   public CornerRadiusF bounds;
   public CornerRadiusF headerBounds;
   public CornerRadiusF toggleBounds;
   public float settingX;
   public float settingY;

   public GuiInterfaceDragElement(HudElement var1) {
      this.draggableElement = var1;
      if (var1.isEnabled()) {
         this.animationEnable.setValue(1.0F);
      }

      Setting[] al1illl1lllllll1l1l1l1ili11l1 = var1.getSettings().toArray(new Setting[0]);
      SettingGroup l1lili1ii11 = new SettingGroup(this.getDisplayName(), "", () -> true, al1illl1lllllll1l1l1l1ili11l1);
      this.guiWindowSetting = new GuiWindowSetting(l1lili1ii11, this.getWidth() - GuiStyle.PADDING * 4);
   }

   @Override
   public String getName() {
      return this.draggableElement.getName();
   }

   public HudElement getDraggableElement() {
      return this.draggableElement;
   }

   public void setSettingsExpanded(boolean var1) {
      this.guiWindowSetting.setExpanded(var1);
   }

   @Override
   public float getHeight() {
      return 23.0F + GuiStyle.PADDING.intValue() * 2.0F + this.getPreviewHeight() + GuiStyle.PADDING.intValue() * 2.0F;
   }

   @Override
   public float getWidth() {
      return 182.0F;
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      if (!this.positionInitialized) {
         this.animationPosX = var4;
         this.animationPosY = var5;
         this.lastX = var4;
         this.lastY = var5;
         this.lastIndex = var7;
         this.positionInitialized = true;
      } else if ((var4 != this.lastX || var5 != this.lastY) && var7 != this.lastIndex) {
         this.animated = true;
         this.lastX = var4;
         this.lastY = var5;
      }

      if (this.animated) {
         this.animationPosX = Math.round(MathHelper.lerp(0.4F, this.animationPosX, var4));
         this.animationPosY = Math.round(MathHelper.lerp(0.4F, this.animationPosY, var5));
         if (Math.abs(this.animationPosX - var4) < 2.0F && Math.abs(this.animationPosY - var5) < 2.0F) {
            this.animated = false;
         } else {
            var4 = this.animationPosX;
            var5 = this.animationPosY;
         }
      } else {
         this.animationPosX = var4;
         this.animationPosY = var5;
      }

      this.lastIndex = var7;
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         this.animationEnable.on23(this.draggableElement.isEnabled());
         float f = this.animationEnable.CancellableEvent();
         float f1 = this.getWidth();
         float f2 = this.getHeight();
         this.bounds = new CornerRadiusF(var4, var5, f1, f2);
         this.headerBounds = new CornerRadiusF(var4, var5, f1, 23.0F);
         var1.drawRoundedRectBatched(
            var4,
            var5,
            f1,
            f2,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getSurfaceDisableBackground().getColor().SprintStateEvent(var6)
         );
         var1.drawRoundedRectBatched(
            var4,
            var5,
            f1,
            23.0F,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getHeaderDisableBackground().getColor().Easing(zenithstyle.getSurfaceEnableBackground().getColor(), f).SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
         Font font1 = Fonts.NEW_ICONS.getFont(6.0F);
         float f3 = var4 + GuiStyle.PADDING.intValue() * 2.0F + (font1.width("O") + GuiStyle.PADDING.intValue()) * f;
         float f4 = var5 + (23.0F - font.height()) / 2.0F;
         var1.drawText(
            font, this.getName(), f3, f4, zenithstyle.getTextSecondary().getColor().Easing(zenithstyle.getTextEnable().getColor(), f).SprintStateEvent(var6)
         );
         var1.drawText(
            font1,
            "O",
            var4 + GuiStyle.PADDING.intValue() * 2.0F,
            var5 + (23.0F - font1.height()) / 2.0F,
            ArgbColor.var11941.Easing(zenithstyle.getPrimaryColor().getColor(), f).SprintStateEvent(var6)
         );
         float f5 = 12.0F;
         float f6 = 7.0F;
         float f7 = var4 + f1 - GuiStyle.PADDING.intValue() * 2.0F - f5;
         float f8 = var5 + GuiStyle.PADDING.intValue() * 2.0F;
         this.toggleBounds = new CornerRadiusF(f7, f8, f5, f6);
         var1.drawRoundedRectBatched(
            f7,
            f8,
            f5,
            f6,
            CornerRadius.MovementInputEvent(2.5F),
            zenithstyle.getDisableActiveBg().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), f).SprintStateEvent(var6)
         );
         float f9 = MathHelper.lerp(f, 1.0F, f5 - 1.0F - 5.0F);
         var1.drawRoundedRectBatched(
            f7 + f9,
            f8 + 1.0F,
            5.0F,
            5.0F,
            CornerRadius.MovementInputEvent(1.5F),
            zenithstyle.getTextTertiary().getColor().Easing(zenithstyle.getTextEnable().getColor(), f).SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         this.settingX = f7 - GuiStyle.PADDING.intValue() - this.guiWindowSetting.getWidth();
         this.settingY = var5 + (23.0F - this.guiWindowSetting.getHeight()) / 2.0F;
         var1.enableScissor(this.settingX + 100.0F, this.settingY - 1.0F, this.settingX + this.getWidth(), this.settingY + this.getHeight());
         this.guiWindowSetting.render(var1, var2, var3, this.settingX, this.settingY, var6);
         var1.disableScissor();
         float f10 = MathHelper.lerp(f, 0.5F, 1.0F);
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F * f10, 1.0F * f10, 1.0F * f10, var6 * var6);
         this.renderPreview(var1, zenithstyle, var4, var5, var6);
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      this.guiWindowSetting.renderPriority(var1, var2, var3, this.settingX, this.settingY, var6, 1.0F);
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      return this.guiWindowSetting.onMousePriorityClicked(var1, var3, var5);
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.guiWindowSetting.onMouseClicked(var1, var3, var5)) {
         return true;
      } else if (var5 != MenuScreenId.call004) {
         return false;
      } else if (this.toggleBounds != null && this.toggleBounds.PotionItemBuilder(var1, var3)) {
         this.draggableElement.toggle();
         return true;
      } else if (this.headerBounds != null && this.headerBounds.PotionItemBuilder(var1, var3)) {
         this.draggableElement.toggle();
         return true;
      } else {
         return this.bounds != null && this.bounds.PotionItemBuilder(var1, var3);
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return this.guiWindowSetting.onMousePriorityScroll(var1, var3, var5, var7);
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      this.guiWindowSetting.onMouseReleased(var1, var3, var5);
      super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return this.guiWindowSetting.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return this.guiWindowSetting.charTyped(var1, var2) ? true : super.charTyped(var1, var2);
   }

   public void renderPreview(HudDrawContext var1, ZenithStyle var2, float var3, float var4, float var5) {
      float f = var3 + GuiStyle.PADDING.intValue() * 2.0F;
      float f1 = var4 + 23.0F + GuiStyle.PADDING.intValue() * 2.0F;
      float f2 = this.getWidth() - GuiStyle.PADDING.intValue() * 4.0F;
      float f3 = this.getPreviewHeight();
      if (!(f2 <= 0.0F) && !(f3 <= 0.0F)) {
         float f4 = this.sanitizeSize(this.draggableElement.zClass06744());
         float f5 = this.sanitizeSize(this.draggableElement.int437());
         if (!(f4 <= 0.0F) && !(f5 <= 0.0F)) {
            float f6 = GuiStyle.PADDING.intValue();
            float f7 = Math.max(1.0F, f2 - f6 * 2.0F);
            float f8 = Math.max(1.0F, f3 - f6 * 2.0F);
            float f9 = f + f6;
            float f10 = f1 + f6;
            float f11 = Math.min(1.0F, Math.min(f7 / f4, f8 / f5));
            if (!Float.isFinite(f11) || f11 <= 0.0F) {
               f11 = 1.0F;
            }

            f11 *= var5 * var5;
            float f12 = f4 * f11;
            float f13 = f5 * f11;
            float f14 = f9 + (f7 - f12) / 2.0F;
            float f15 = f10 + (f8 - f13) / 2.0F;
            if (this.draggableElement instanceof HudElementValue) {
               var1.enableScissor(0.0F, var4 + 23.0F + GuiStyle.PADDING.intValue(), f9 + f7 + 100.0F, f10 + f8 + GuiStyle.PADDING.intValue());
            }

            var1.pushMatrix();
            var1.getMatrices().translate(f14, f15);
            var1.getMatrices().scale(f11, f11);
            var1.getMatrices().translate(-this.draggableElement.blockPos30(), -this.draggableElement.blockPos31());
            HudQueuedContext hudqueuedcontext = HudQueuedContext.of(var1);
            HudPreviewRenderQueue.on23(this.hudPreviewRenderQueue);

            try {
               this.draggableElement.on23(hudqueuedcontext);
               this.hudPreviewRenderQueue.flush();
            } catch (Exception var27) {
            } finally {
               HudPreviewRenderQueue.UiAnimation(this.hudPreviewRenderQueue);
               var1.popMatrix();
               if (this.draggableElement instanceof HudElementValue) {
                  var1.disableScissor();
               }
            }
         }
      }
   }

   public float sanitizeSize(float var1) {
      return !Float.isFinite(var1) ? 0.0F : Math.max(0.0F, var1);
   }

   public float getPreviewHeight() {
      float f = this.sanitizeSize(this.draggableElement.int437());
      return f <= 0.0F ? -GuiStyle.PADDING * 4 : f;
   }

   public String getDisplayName() {
      String s = this.draggableElement.getName();
      String s1 = ZenithClient.on23().Easing().translate(s);
      if (s1 != null && !s1.isBlank() && !s1.equals(s)) {
         return s1;
      }

      int i = s.lastIndexOf(46);
      String s2 = i >= 0 ? s.substring(i + 1) : s;
      s2 = s2.replace('_', ' ').trim();
      return s2.isEmpty() ? "Hud element" : Character.toUpperCase(s2.charAt(0)) + s2.substring(1);
   }
}
