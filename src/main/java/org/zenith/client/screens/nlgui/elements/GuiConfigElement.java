package org.zenith.client.screens.nlgui.elements;

import net.minecraft.client.util.math.Vector2f;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.InterfaceElement;
import org.zenith.client.screens.nlgui.elements.setting.GuiModeSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.ModuleStateStore;
import org.zenith.core.PollMode;
import org.zenith.core.UiAnimation;
import org.zenith.hud.SearchBox;
import org.zenith.hud.SearchBox;
import org.zenith.hud.SearchBox;
import org.zenith.render.ShapeRenderer;
import org.zenith.setting.ModeSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiConfigElement extends InterfaceElement {
   public static final float HEIGHT = 34.0F;
   public static final float HEADER_HEIGHT = 23.0F;
   public static final long DOUBLE_CLICK_MS = 260L;
   public final Runnable onChanged;
   public final SearchBox nameBox;
   public ModuleStateStore config;
   public String displayName;
   public final UiAnimation animationVisible = new UiAnimation(220L, Easing.CloseScreenEvent);
   public final UiAnimation heartAnimation = new UiAnimation(300L, Easing.CloseScreenEvent);
   public final UiAnimation deleteAnimation = new UiAnimation(300L, Easing.CloseScreenEvent);
   public final UiAnimation loadAnimation = new UiAnimation(300L, Easing.CloseScreenEvent);
   public final UiAnimation loadButtonAnimation = new UiAnimation(220L, Easing.CloseScreenEvent);
   public final UiAnimation animationPosX = new UiAnimation(150L, Easing.StopUsingItemEvent);
   public final UiAnimation animationPosY = new UiAnimation(150L, Easing.StopUsingItemEvent);
   public final UiAnimation animationExpanded = new UiAnimation(200L, Easing.StopUsingItemEvent);
   public final UiAnimation focusAnimation = new UiAnimation(200L, Easing.StopUsingItemEvent);
   public final ModeSetting loadMode = new ModeSetting(
      "gui.configelement.mode", "", "gui.configelement.mode.all", "gui.configelement.mode.ignoreBinds", "gui.configelement.mode.onlyThemes"
   );
   public GuiModeSetting guiLoadMode = new GuiModeSetting(this.loadMode, this.getWidth() - GuiStyle.PADDING * 4);
   public CornerRadiusF bounds;
   public CornerRadiusF nameBounds;
   public CornerRadiusF buttonBounds;
   public CornerRadiusF deleteBounds;
   public CornerRadiusF heartBounds;
   public CornerRadiusF popupBounds;
   public CornerRadiusF popupExitBounds;
   public boolean expanded;
   public boolean editingName;
   public boolean deleting;
   public boolean deleteCommitted;
   public long lastNameClick;
   public boolean animated;
   public boolean positionInitialized;
   public float lastX;
   public float lastY;
   public int lastIndex;

   public GuiConfigElement(ModuleStateStore var1, Runnable var2) {
      this.config = var1;
      this.onChanged = var2;
      this.displayName = var1.getName();
      this.nameBox = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.5F), "config", 60.0F);
      this.nameBox.on23(SearchBox.MatchMode.val129);
      this.nameBox.on23(SearchBox.SearchScope.val298);
      this.nameBox.EventItemRenderHook(28);
      this.nameBox.HudHotbarPanel(this.displayName);
      this.nameBox.EventRender(this.displayName.length());
   }

   @Override
   public String getName() {
      return this.displayName;
   }

   @Override
   public float getHeight() {
      return 34.0F;
   }

   @Override
   public float getWidth() {
      return 120.0F;
   }

   public boolean isPriority() {
      return this.config.isPriority();
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         var6 *= this.animationVisible.on23(this.deleting ? 0.0F : 1.0F);
         if (this.deleting && !this.deleteCommitted && this.animationVisible.CancellableEvent() <= 0.02F) {
            this.deleteCommitted = true;
            if (ZenithClient.on23().TradeGuardService().EmoteMetadata(this.config.getName()) && this.onChanged != null) {
               this.onChanged.run();
            } else {
               this.deleting = false;
               this.deleteCommitted = false;
            }
         }

         if (!this.positionInitialized) {
            this.animationPosX.UiAnimation(var4);
            this.animationPosY.UiAnimation(var5);
            this.lastX = var4;
            this.lastY = var5;
            this.positionInitialized = true;
            this.lastIndex = var7;
         } else if ((var4 != this.lastX || var5 != this.lastY) && var7 != this.lastIndex) {
            this.animated = true;
            this.animationPosX.Easing(var4);
            this.animationPosY.Easing(var5);
            this.lastX = var4;
            this.lastY = var5;
         }

         if (this.animated) {
            var4 = this.animationPosX.on23(var4);
            var5 = this.animationPosY.on23(var5);
            if (this.animationPosX.isDone() && this.animationPosY.isDone()) {
               this.animated = false;
            }
         } else {
            this.animationPosX.UiAnimation(var4);
            this.animationPosY.UiAnimation(var5);
         }

         this.lastIndex = var7;
         float f = this.getWidth();
         this.bounds = new CornerRadiusF(var4, var5, f, 34.0F);
         var1.drawRoundedRectBatched(
            var4,
            var5,
            f,
            34.0F,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getSurfaceDisableBackground().getColor().SprintStateEvent(var6)
         );
         var1.drawRoundedRectBatched(
            var4,
            var5,
            f,
            34.0F,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getHeaderDisableBackground().getColor().Easing(zenithstyle.getSurfaceEnableBackground().getColor(), 0.65F).SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font1 = Fonts.NEW_MEDIUM.getFont(4.8F);
         Font font2 = Fonts.NEW_ICONS.getFont(4.8F);
         float f1 = var4 + GuiStyle.PADDING.intValue() * 2.0F;
         float f2 = var5 + (23.0F - font.height()) / 2.0F;
         float f3 = Math.max(12.0F, f - GuiStyle.PADDING.intValue() * 4.0F - 15.0F);
         this.nameBounds = new CornerRadiusF(f1 - 1.0F, var5, f3 + 2.0F, 23.0F);
         if (this.editingName) {
            this.nameBox.on23(font);
            this.nameBox.setWidth(f3);
            this.nameBox
               .on23(
                  var1, f1, f2, zenithstyle.getTextEnable().getColor().SprintStateEvent(var6), zenithstyle.getTextSecondary().getColor().SprintStateEvent(var6)
               );
         } else {
            var1.drawText(font, this.displayName, f1, f2, zenithstyle.getTextEnable().getColor().SprintStateEvent(var6));
         }

         float f4 = 7.0F;
         float f5 = var4 + f - GuiStyle.PADDING.intValue() * 2.0F - f4 - GuiStyle.PADDING.intValue() - f4;
         float f6 = var5 + GuiStyle.PADDING.intValue() * 2.0F;
         this.heartBounds = new CornerRadiusF(f5, f6, f4, f4);
         this.deleteBounds = new CornerRadiusF(f5 + f4 + GuiStyle.PADDING.intValue(), f6, f4, f4);
         this.deleteAnimation.on23(this.deleteBounds.PotionItemBuilder(var2, var3));
         this.loadAnimation.on23(this.bounds.PotionItemBuilder(var2, var3));
         this.heartAnimation.on23(this.config.isPriority() ? 1.0F : (this.heartBounds.PotionItemBuilder(var2, var3) ? 0.5F : 0.0F));
         var1.drawRoundedRectBatched(
            f5,
            f6,
            f4,
            f4,
            CornerRadius.MovementInputEvent(1.5F),
            zenithstyle.getDisableActiveBg().getColor().Easing(zenithstyle.getHeartActiveBg().getColor(), this.heartAnimation.CancellableEvent()).SprintStateEvent(var6)
         );
         var1.drawRoundedRectBatched(
            this.deleteBounds.x(),
            this.deleteBounds.y(),
            f4,
            f4,
            CornerRadius.MovementInputEvent(1.5F),
            zenithstyle.getDisableActiveBg().getColor().SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         Font font3 = Fonts.NEW_ICONS.getFont(5.0F);
         Font font4 = Fonts.NEW_ICONS.getFont(4.3F);
         var1.drawText(
            font3,
            "U",
            f5 + 1.0F,
            f6 + 1.5F,
            ArgbColor.var11941.Easing(zenithstyle.getTextTertiary().getColor(), 1.0F - this.heartAnimation.CancellableEvent()).SprintStateEvent(var6)
         );
         var1.drawText(
            font4,
            "V",
            f5 + 1.05F,
            f6 + 1.78F,
            ArgbColor.var11941.Easing(zenithstyle.getHeartIcon().getColor(), this.heartAnimation.CancellableEvent()).SprintStateEvent(var6)
         );
         var1.drawText(
            font4,
            "[",
            this.deleteBounds.x() + (this.deleteBounds.width() - font4.width("[")) / 2.0F,
            f6 + 1.5F,
            zenithstyle.getTextTertiary().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), this.deleteAnimation.CancellableEvent()).SprintStateEvent(var6)
         );
         float f7 = var5 + 34.0F - GuiStyle.PADDING.intValue() * 2.0F - font1.height();
         var1.drawText(font2, "a", var4 + GuiStyle.PADDING.intValue() * 2.0F, f7 + 0.46F, zenithstyle.getTextTertiary().getColor().SprintStateEvent(var6));
         String s = this.translate("gui.configelement.local");
         String s1 = this.translate("gui.configelemen.load");
         var1.drawText(
            font1,
            s,
            var4 + GuiStyle.PADDING.intValue() * 2.0F + font2.width("a") + GuiStyle.PADDING.intValue() / 2.0F,
            f7,
            zenithstyle.getTextSecondary().getColor().SprintStateEvent(var6)
         );
         float f8 = font1.width(s1);
         float f9 = font2.width("n");
         float f10 = f9 + GuiStyle.PADDING.intValue() / 2.0F + f8;
         float f11 = var4 + f - f10 - GuiStyle.PADDING.intValue() * 2.0F;
         var1.drawText(
            font2,
            "n",
            f11,
            f7 + 0.4F,
            zenithstyle.getTextTertiary().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), this.loadAnimation.CancellableEvent()).SprintStateEvent(var6)
         );
         var1.drawText(
            font1,
            s1,
            f11 + f9 + GuiStyle.PADDING.intValue() / 2.0F,
            f7,
            zenithstyle.getTextSecondary().getColor().Easing(zenithstyle.getTextEnable().getColor(), this.loadAnimation.CancellableEvent()).SprintStateEvent(var6)
         );
      }
   }

   @Override
   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float f = this.animationExpanded.on23(this.expanded ? 1.0F : 0.0F);
      if (!(f <= 0.001F) && this.bounds != null) {
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         if (zenithstyle != null) {
            float f1 = 120.0F;
            float f2 = 26.0F;
            float f3 = GuiStyle.PADDING * 5 + 14 + 14;
            float f4 = f2 + f3;
            float f5 = this.bounds.x();
            float f6 = this.bounds.y() + this.bounds.height() + GuiStyle.PADDING.intValue();
            this.popupBounds = new CornerRadiusF(f5, f6, f1, f4);
            var6 *= f;
            var1.getMatrices().pushMatrix();
            var1.getMatrices().translate(f5 + f1 / 2.0F, f6);
            var1.getMatrices().scale(f, f);
            var1.getMatrices().translate(-(f5 + f1 / 2.0F), -f6);
            ShapeRenderer.ItemSpec(
               var1.getMatrices(),
               f5,
               f6,
               f1,
               f4,
               12.0F,
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
               ArgbColor.var11934.SprintStateEvent(var6)
            );
            var1.drawRoundedRect(
               f5,
               f6,
               f1,
               f2,
               CornerRadius.BotPacketEvent(GuiStyle.ROUND.intValue(), GuiStyle.ROUND.intValue()),
               zenithstyle.getRightBackground().getColor().SprintStateEvent(var6)
            );
            var1.drawRoundedRect(
               f5,
               f6 + f2,
               f1,
               f3,
               CornerRadius.BotRespawnEvent(GuiStyle.ROUND.intValue(), GuiStyle.ROUND.intValue()),
               zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(var6)
            );
            Font font = Fonts.NEW_ICONS.getFont(5.0F);
            Font font1 = Fonts.NEW_MEDIUM.getFont(5.4F);
            var1.drawText(
               font,
               "n",
               f5 + GuiStyle.PADDING.intValue() * 2.0F,
               f6 + (f2 - font.height()) / 2.0F,
               zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6)
            );
            var1.drawText(
               font1,
               this.translate("gui.configelement.loadType"),
               f5 + GuiStyle.PADDING.intValue() * 2.0F + font.width("n") + GuiStyle.PADDING.intValue() / 2.0F,
               f6 + (f2 - font1.height()) / 2.0F,
               zenithstyle.getTextEnable().getColor().SprintStateEvent(var6)
            );
            Font font2 = Fonts.NEW_ICONS.getFont(4.0F);
            float f7 = f5 + f1 - font2.width("2") - GuiStyle.PADDING.intValue() * 2.0F;
            float f8 = f6 + GuiStyle.PADDING.intValue() + font2.height();
            this.popupExitBounds = new CornerRadiusF(f7, f8, 5.0F, 5.0F);
            var1.drawText(font2, "2", f7, f8, zenithstyle.getTextTertiary().getColor().SprintStateEvent(var6));
            float f9 = f5 + GuiStyle.PADDING * 2;
            float f10 = f6 + f2 + GuiStyle.PADDING.intValue();
            this.guiLoadMode.render(var1, var2, var3, f9, f10, var6);
            float f11 = f10 + this.guiLoadMode.getAnimHeight() + GuiStyle.PADDING * 2;
            this.buttonBounds = new CornerRadiusF(f9, f11, this.guiLoadMode.getWidth(), 14.0F);
            float f12 = this.loadButtonAnimation.on23(this.buttonBounds.PotionItemBuilder(var2, var3) && !this.guiLoadMode.contains(var2, var3) ? 1.0F : 0.0F);
            var1.drawRoundedRect(
               this.buttonBounds.x(),
               this.buttonBounds.y(),
               this.buttonBounds.width(),
               this.buttonBounds.height(),
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
               zenithstyle.getFieldSurfaceBackground().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), f12 * 0.15F).SprintStateEvent(var6)
            );
            var1.drawRoundedBorder(
               this.buttonBounds.x(),
               this.buttonBounds.y(),
               this.buttonBounds.width(),
               this.buttonBounds.height(),
               0.1F,
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
               zenithstyle.getFieldBorder().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), f12 * 0.35F).SprintStateEvent(var6)
            );
            Font font3 = Fonts.NEW_ICONS.getFont(5.0F);
            Font font4 = Fonts.NEW_MEDIUM.getFont(5.3F);
            String s = this.translate("gui.configelemen.load");
            float f13 = font3.width("a") + GuiStyle.PADDING.intValue() / 2.0F + font4.width(s);
            float f14 = this.buttonBounds.x() + (this.buttonBounds.width() - f13) / 2.0F;
            float f15 = this.buttonBounds.y() + (this.buttonBounds.height() - font3.height()) / 2.0F + 0.28F;
            float f16 = this.buttonBounds.y() + (this.buttonBounds.height() - font4.height()) / 2.0F;
            var1.drawText(
               font3, "a", f14, f15, zenithstyle.getTextTertiary().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), f12).SprintStateEvent(var6)
            );
            var1.drawText(
               font4,
               s,
               f14 + font3.width("a") + GuiStyle.PADDING.intValue() / 2.0F,
               f16,
               zenithstyle.getTextSecondary().getColor().Easing(zenithstyle.getTextEnable().getColor(), f12).SprintStateEvent(var6)
            );
            this.guiLoadMode.renderPriority(var1, var2, var3, f9, f10, var6, 1.0F);
            var1.getMatrices().popMatrix();
         }
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.deleting) {
         return true;
      }

      if (var5 == MenuScreenId.call004 && this.deleteBounds != null && this.deleteBounds.PotionItemBuilder(var1, var3)) {
         this.deleting = true;
         this.editingName = false;
         this.nameBox.VelocityChangeEvent(false);
         return true;
      }

      if (this.heartBounds != null && this.heartBounds.PotionItemBuilder(var1, var3)) {
         this.config.TriggerBot();
         return true;
      }

      if (this.nameBox.onMouseClicked(var1, var3, var5)) {
         if (this.nameBox.isSelected() && !this.editingName) {
            this.beginEdit();
         }

         return true;
      } else if (this.bounds != null && this.bounds.PotionItemBuilder(var1, var3)) {
         this.expanded = !this.expanded;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      if (!this.expanded || this.popupBounds == null) {
         return false;
      } else if (this.guiLoadMode.onMousePriorityClicked(var1, var3, var5)) {
         return true;
      } else if (!this.popupBounds.PotionItemBuilder(var1, var3)) {
         this.expanded = false;
         return false;
      } else if (this.popupExitBounds != null && this.popupExitBounds.on23(var1, var3, 2.0F)) {
         this.expanded = false;
         return true;
      } else if (this.guiLoadMode.onMouseClicked(var1, var3, var5)) {
         return true;
      } else if (var5 == MenuScreenId.call004 && this.buttonBounds != null && this.buttonBounds.PotionItemBuilder(var1, var3)) {
         this.loadByMode();
         return true;
      } else {
         return true;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return this.expanded && this.guiLoadMode.onMousePriorityScroll(var1, var3, var5, var7);
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      this.guiLoadMode.onMouseReleased(var1, var3, var5);
      super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.editingName) {
         if (var1 == 257 || var1 == 335) {
            this.finishEdit(true);
            return true;
         } else if (var1 == 256) {
            this.finishEdit(false);
            return true;
         } else {
            return this.nameBox.keyPressed(var1, var2, var3);
         }
      } else {
         return this.guiLoadMode.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
      }
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (this.editingName) {
         return this.nameBox.charTyped(var1, var2);
      } else {
         return this.guiLoadMode.charTyped(var1, var2) ? true : super.charTyped(var1, var2);
      }
   }

   public void beginEdit() {
      this.expanded = false;
      this.editingName = true;
      this.nameBox.HudHotbarPanel(this.displayName);
      this.nameBox.EventRender(this.displayName.length());
      this.nameBox.VelocityChangeEvent(true);
   }

   public void finishEdit(boolean var1) {
      this.editingName = false;
      this.nameBox.VelocityChangeEvent(false);
      if (!var1) {
         this.nameBox.HudHotbarPanel(this.displayName);
         this.nameBox.EventRender(this.displayName.length());
      } else {
         String s = this.nameBox.getText() == null ? "" : this.nameBox.getText().trim();
         if (!s.isEmpty() && !s.equals(this.displayName)) {
            if (ZenithClient.on23().TradeGuardService().NbtItemSpec(this.config.getName(), s)) {
               ModuleStateStore illlll11i11i1illi1l1ii1i111 = ZenithClient.on23().TradeGuardService().CloudPoller(s);
               if (illlll11i11i1illi1l1ii1i111 != null) {
                  this.config = illlll11i11i1illi1l1ii1i111;
               }

               this.displayName = s;
               if (this.onChanged != null) {
                  this.onChanged.run();
               }
            } else {
               this.nameBox.HudHotbarPanel(this.displayName);
               this.nameBox.EventRender(this.displayName.length());
            }
         } else {
            this.nameBox.HudHotbarPanel(this.displayName);
            this.nameBox.EventRender(this.displayName.length());
         }
      }
   }

   public void loadByMode() {
      String s = this.loadMode.get();

      PollMode liill1llill11i11il_ii1il11l111ii11iil = switch (s) {
         case "gui.configelement.mode.ignoreBinds" -> PollMode.call137;
         case "gui.configelement.mode.onlyThemes" -> PollMode.getThis3;
         default -> PollMode.call107;
      };
      ZenithClient.on23().TradeGuardService().on23(this.config.getName(), liill1llill11i11il_ii1il11l111ii11iil);
      this.expanded = false;
   }

   public String translate(String var1) {
      return ZenithClient.on23().Easing().translate(var1);
   }
}
