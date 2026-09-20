package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.GuiInterfaceDragElement;
import org.zenith.client.screens.nlgui.elements.InterfaceSettingsElement;
import org.zenith.client.screens.nlgui.elements.InterfaceStyleContainerElement;
import org.zenith.client.screens.nlgui.elements.api.InterfaceElement;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.hud.HudElement;
import org.zenith.module.render.Interface;
import org.zenith.module.render.Menu;
import org.zenith.render.ShapeRenderer;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class InterfacePanel extends ElementPanel {
   public final UiAnimation animationChangeCategory = new UiAnimation(200L, 1.0F, Easing.StopUsingItemEvent);
   public CornerRadiusF themeTabBounds;
   public CornerRadiusF hudTabBounds;
   public final Map<InterfacePanel_InterfaceCategory, List<InterfaceElement>> categories = new HashMap<>();
   public InterfacePanel_InterfaceCategory currentCategory = InterfacePanel_InterfaceCategory.THEME;
   public InterfacePanel_InterfaceCategory lastCategory;
   public CornerRadiusF scissorBounds;
   public float scroll = 0.0F;
   public float scrollTarget = 0.0F;
   public static final float SCROLL_SPEED = 22.0F;
   public static final float SCROLL_SMOOTH = 0.25F;

   @Override
   public void render(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.scissorBounds = new CornerRadiusF(var5, var6, 376.0F, 295.5F - GuiStyle.PADDING * 2);
      this.animationChangeCategory.on23(Menu.menu.int470());
      List<InterfaceElement> list = this.getFilteredElements(this.currentCategory);
      float f = this.getContentHeight(list, this.currentCategory);
      this.clampScroll(f + GuiStyle.PADDING.intValue(), this.scissorBounds.height());
      this.scroll = this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
      float f1 = this.animationChangeCategory.on23(1.0F);
      if (this.animationChangeCategory.isDone()) {
         this.lastCategory = null;
      }

      float f2 = (0.5F - f1) / 0.5F;
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      float f3 = (f1 - 0.3F) / 0.7F;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      var1.enableScissor(var5, var6, var5 + this.scissorBounds.width(), var6 + this.scissorBounds.height());
      ShapeRenderer.boolean184 = false;
      if (this.lastCategory != null && f2 > 0.0F) {
         float f4 = var4 * f2;
         this.renderElements(this.getFilteredElements(this.lastCategory), this.lastCategory, var1, var2, var3, f4, var5, var6, false);
      }

      if (f3 > 0.0F) {
         float f6 = var4 * f3;
         float f5 = 20.0F * (1.0F - f3);
         this.renderElements(list, this.currentCategory, var1, var2, var3, f6, var5, var6 + f5, false);
         var1.disableScissor();
      } else {
         var1.disableScissor();
      }

      ShapeRenderer.boolean184 = true;
   }

   public void renderElements(
      List<InterfaceElement> var1,
      InterfacePanel_InterfaceCategory var2,
      HudDrawContext var3,
      int var4,
      int var5,
      float var6,
      float var7,
      float var8,
      boolean var9
   ) {
      if (!var1.isEmpty()) {
         float f = var7 + GuiStyle.PADDING.intValue();
         float f1 = var8 + this.scroll;
         float f2 = GuiStyle.PADDING.intValue();
         int i = var2 == InterfacePanel_InterfaceCategory.HUD ? 2 : 1;
         if (i <= 1) {
            int l = 0;

            for (InterfaceElement interfaceelement1 : var1) {
               try {
                  CornerRadiusF l11liliill1iii1 = new CornerRadiusF(f, f1, interfaceelement1.getWidth(), interfaceelement1.getHeight());
                  if (var9) {
                     interfaceelement1.renderPriority(var3, var4, var5, f, f1, var6);
                  } else if (this.scissorBounds == null || this.scissorBounds.on23(l11liliill1iii1)) {
                     interfaceelement1.render(var3, var4, var5, f, f1, var6, l);
                  }

                  f1 += interfaceelement1.getHeight() + f2;
                  l++;
               } catch (Exception exception) {
                  exception.printStackTrace();
               }
            }
         } else {
            float f3 = 376.0F - GuiStyle.PADDING.intValue() * 2.0F;
            float f4 = (f3 - f2) / 2.0F;
            float[] afloat = new float[i];
            int j = 0;

            for (InterfaceElement interfaceelement : var1) {
               try {
                  int k = afloat[0] <= afloat[1] ? 0 : 1;
                  float f5 = f + k * (f4 + f2);
                  float f6 = f1 + afloat[k];
                  CornerRadiusF l11liliill1iii1x = new CornerRadiusF(f5, f6, interfaceelement.getWidth(), interfaceelement.getHeight());
                  if (var9) {
                     interfaceelement.renderPriority(var3, var4, var5, f5, f6, var6);
                  } else if (this.scissorBounds == null || this.scissorBounds.on23(l11liliill1iii1x)) {
                     interfaceelement.render(var3, var4, var5, f5, f6, var6, j + k);
                  }

                  afloat[k] += interfaceelement.getHeight() + f2;
                  j++;
               } catch (Exception exception1) {
                  exception1.printStackTrace();
               }
            }
         }
      }
   }

   public InterfacePanel() {
      this.categories.put(InterfacePanel_InterfaceCategory.THEME, List.of(new InterfaceSettingsElement(), new InterfaceStyleContainerElement()));
      ArrayList arraylist = new ArrayList();

      for (HudElement i1i1l111li : Interface.interfaceField.float211()) {
         arraylist.add(new GuiInterfaceDragElement(i1i1l111li));
      }

      this.categories.put(InterfacePanel_InterfaceCategory.HUD, arraylist);
   }

   @Override
   public void renderHeader(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = var5 + GuiStyle.PADDING.intValue() + GuiStyle.PADDING * 2;
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font1 = Fonts.NEW_ICONS.getFont(6.0F);
         float f1 = Math.min(1.0F, this.animationChangeCategory.on23(1.0F));
         if (this.lastCategory != null) {
            float f2 = -8.0F * f1;
            this.renderHeader(this.lastCategory, font, font1, var1, var4, 1.0F - f1, f, var6 + f2);
         }

         if (f1 > 0.0F) {
            float f3 = 8.0F * (1.0F - f1);
            this.renderHeader(this.currentCategory, font, font1, var1, var4, f1, f, var6 + f3);
         }

         this.renderHeaderTabs(var1, var4, var5, var6, var7);
      }
   }

   public void renderHeader(InterfacePanel_InterfaceCategory var1, Font var2, Font var3, HudDrawContext var4, float var5, float var6, float var7, float var8) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = var6 * var5;
         String s = ":";
         float f1 = var8 + (23.0F - var2.height()) / 2.0F;
         float f2 = var8 + (23.0F - var3.height()) / 2.0F - 0.1F;
         float f3 = var7 + var3.width(s) + GuiStyle.PADDING.intValue();
         var4.drawText(var2, "Interface", f3, f1, zenithstyle.getTextEnable().getColor().SprintStateEvent(f));
         var4.drawText(var3, s, var7, f2, zenithstyle.getPrimaryColor().getColor().SprintStateEvent(f));
      }
   }

   public void renderHeaderTabs(HudDrawContext var1, float var2, float var3, float var4, float var5) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
         String s = "Theme";
         String s1 = "HUD";
         float f = GuiStyle.PADDING.intValue() * 2.0F;
         float f1 = GuiStyle.PADDING.intValue();
         float f2 = font.width(s1);
         float f3 = font.width(s);
         float f4 = var3 + var5 - f - f2;
         float f5 = f4 - f1;
         float f6 = f5 - f1 - f3;
         float f7 = var4 + (23.0F - font.height()) / 2.0F;
         this.themeTabBounds = new CornerRadiusF(f6 - 2.0F, var4 + 1.0F, f3 + 4.0F, 21.0F);
         this.hudTabBounds = new CornerRadiusF(f4 - 2.0F, var4 + 1.0F, f2 + 4.0F, 21.0F);
         var1.drawText(
            font,
            s,
            f6,
            f7,
            (this.currentCategory == InterfacePanel_InterfaceCategory.THEME
                  ? zenithstyle.getTextEnable().getColor()
                  : zenithstyle.getTextSecondary().getColor())
               .SprintStateEvent(var2)
         );
         var1.drawRect(f5, f7, 0.5F, 6.0F, zenithstyle.getDisableActiveBg().getColor().SprintStateEvent(var2));
         var1.drawText(
            font,
            s1,
            f4,
            f7,
            (this.currentCategory == InterfacePanel_InterfaceCategory.HUD ? zenithstyle.getTextEnable().getColor() : zenithstyle.getTextSecondary().getColor())
               .SprintStateEvent(var2)
         );
      }
   }

   @Override
   public void renderPriority(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      float f = this.animationChangeCategory.CancellableEvent();
      float f1 = Math.max(0.0F, Math.min(1.0F, (f - 0.3F) / 0.7F));
      if (!(f1 <= 0.0F)) {
         float f2 = var4 * f1;
         float f3 = 20.0F * (1.0F - f1);
         this.renderElements(this.getFilteredElements(this.currentCategory), this.currentCategory, var1, var2, var3, f2, var5, var6 + f3, true);
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.scissorBounds == null) {
         return false;
      }

      List<InterfaceElement> list = this.getFilteredElements(this.currentCategory);

      for (InterfaceElement interfaceelement : list) {
         if (interfaceelement.mouseScrolled(var1, var3, var5, var7)) {
            return true;
         }
      }

      float f = this.scissorBounds.height();
      if (list.isEmpty()) {
         return false;
      }

      float f1 = this.getContentHeight(list, this.currentCategory) + GuiStyle.PADDING.intValue();
      if (f1 <= f) {
         return false;
      }

      this.scrollTarget = (float)(this.scrollTarget + var7 * 22.0);
      this.clampScroll(f1, f);
      return true;
   }

   public void clampScroll(float var1, float var2) {
      if (var1 <= var2) {
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
      } else {
         float f = var2 - var1;
         if (this.scrollTarget < f) {
            this.scrollTarget = f;
         }

         if (this.scrollTarget > 0.0F) {
            this.scrollTarget = 0.0F;
         }

         if (this.scroll < f) {
            this.scroll = f;
         }

         if (this.scroll > 0.0F) {
            this.scroll = 0.0F;
         }
      }
   }

   public float getContentHeight(List<InterfaceElement> var1, InterfacePanel_InterfaceCategory var2) {
      if (var1.isEmpty()) {
         return 0.0F;
      }

      if (var2 == InterfacePanel_InterfaceCategory.HUD) {
         float f1 = GuiStyle.PADDING.intValue();
         float[] afloat = new float[]{0.0F, 0.0F};

         for (InterfaceElement interfaceelement1 : var1) {
            int i = afloat[0] <= afloat[1] ? 0 : 1;
            afloat[i] += interfaceelement1.getHeight() + f1;
         }

         float f2 = Math.max(afloat[0], afloat[1]);
         return Math.max(0.0F, f2 - f1);
      } else {
         float f = 0.0F;

         for (InterfaceElement interfaceelement : var1) {
            f += interfaceelement.getHeight() + GuiStyle.PADDING.intValue();
         }

         return Math.max(0.0F, f - GuiStyle.PADDING.intValue());
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      try {
         if (var5 == MenuScreenId.call004) {
            if (this.themeTabBounds != null && this.themeTabBounds.PotionItemBuilder(var1, var3)) {
               this.setCategory(InterfacePanel_InterfaceCategory.THEME);
               return true;
            }

            if (this.hudTabBounds != null && this.hudTabBounds.PotionItemBuilder(var1, var3)) {
               this.setCategory(InterfacePanel_InterfaceCategory.HUD);
               return true;
            }
         }

         if (!this.animationChangeCategory.isDone()) {
            return false;
         }

         boolean flag = false;
         List<InterfaceElement> list = this.getFilteredElements(this.currentCategory);

         for (InterfaceElement interfaceelement : list) {
            if (interfaceelement.onMousePriorityClicked(var1, var3, var5)) {
               flag = true;
            }
         }

         if (flag) {
            return true;
         }

         if (this.scissorBounds == null || !this.scissorBounds.PotionItemBuilder(var1, var3)) {
            return false;
         }

         InterfaceElement interfaceelement1 = this.getElementAt(var1, var3, list, this.currentCategory);
         if (interfaceelement1 != null && interfaceelement1.onMouseClicked(var1, var3, var5)) {
            return true;
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      return false;
   }

   public InterfaceElement getElementAt(double var1, double var3, List<InterfaceElement> var5, InterfacePanel_InterfaceCategory var6) {
      if (this.scissorBounds != null && !var5.isEmpty()) {
         float f = this.scissorBounds.x() + GuiStyle.PADDING.intValue();
         float f1 = this.scissorBounds.y() + this.scroll;
         float f2 = GuiStyle.PADDING.intValue();
         int i = var6 == InterfacePanel_InterfaceCategory.HUD ? 2 : 1;
         if (i <= 1) {
            for (InterfaceElement interfaceelement1 : var5) {
               CornerRadiusF l11liliill1iii1 = new CornerRadiusF(f, f1, interfaceelement1.getWidth(), interfaceelement1.getHeight());
               if (l11liliill1iii1.PotionItemBuilder(var1, var3)) {
                  return interfaceelement1;
               }

               f1 += interfaceelement1.getHeight() + f2;
            }

            return null;
         } else {
            float f3 = this.scissorBounds.width() - GuiStyle.PADDING.intValue() * 2.0F;
            float f4 = (f3 - f2) / 2.0F;
            float[] afloat = new float[i];

            for (InterfaceElement interfaceelement : var5) {
               int j = afloat[0] <= afloat[1] ? 0 : 1;
               float f5 = f + j * (f4 + f2);
               float f6 = f1 + afloat[j];
               CornerRadiusF l11liliill1iii1x = new CornerRadiusF(f5, f6, interfaceelement.getWidth(), interfaceelement.getHeight());
               if (l11liliill1iii1x.PotionItemBuilder(var1, var3)) {
                  return interfaceelement;
               }

               afloat[j] += interfaceelement.getHeight() + f2;
            }

            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, MenuScreenId var5) {
      for (InterfaceElement interfaceelement : this.getFilteredElements(this.currentCategory)) {
         interfaceelement.onMouseReleased(var1, var3, var5);
      }

      return super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      for (InterfaceElement interfaceelement : this.getFilteredElements(this.currentCategory)) {
         if (interfaceelement.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      for (InterfaceElement interfaceelement : this.getFilteredElements(this.currentCategory)) {
         if (interfaceelement.charTyped(var1, var2)) {
            return true;
         }
      }

      return super.charTyped(var1, var2);
   }

   public void setCategory(InterfacePanel_InterfaceCategory var1) {
      this.setCategory(var1, false);
   }

   public void setCategory(InterfacePanel_InterfaceCategory var1, boolean var2) {
      if ((var2 || this.animationChangeCategory.isDone()) && this.currentCategory != var1) {
         this.lastCategory = this.currentCategory;
         this.currentCategory = var1;
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
         this.animationChangeCategory.UiAnimation(0.0F);
         this.animationChangeCategory.Easing(1.0F);
      }
   }

   public void openHudElementSettings(HudElement var1) {
      if (var1 != null) {
         this.setCategory(InterfacePanel_InterfaceCategory.HUD, true);
         List<InterfaceElement> list = this.categories.get(InterfacePanel_InterfaceCategory.HUD);
         if (list != null && !list.isEmpty()) {
            GuiInterfaceDragElement guiinterfacedragelement = null;

            for (InterfaceElement interfaceelement : list) {
               if (interfaceelement instanceof GuiInterfaceDragElement guiinterfacedragelement1) {
                  boolean flag = guiinterfacedragelement1.getDraggableElement().getName().equals(var1.getName());
                  guiinterfacedragelement1.setSettingsExpanded(flag);
                  if (flag) {
                     guiinterfacedragelement = guiinterfacedragelement1;
                     break;
                  }
               }
            }

            if (guiinterfacedragelement != null) {
               float[] afloat = this.getHudElementMetrics(list, guiinterfacedragelement);
               if (afloat != null) {
                  float f = this.scissorBounds != null ? this.scissorBounds.height() : 295.5F - GuiStyle.PADDING.intValue() * 2.0F;
                  float f1 = (afloat[0] + afloat[1]) / 2.0F;
                  this.scrollTarget = f / 2.0F - f1;
                  this.scroll = this.scrollTarget;
                  this.clampScroll(afloat[2] + GuiStyle.PADDING.intValue(), f);
               }
            }
         }
      }
   }

   public float[] getHudElementMetrics(List<InterfaceElement> var1, GuiInterfaceDragElement var2) {
      float f = GuiStyle.PADDING.intValue();
      float[] afloat = new float[]{0.0F, 0.0F};
      float f1 = Float.NaN;
      float f2 = Float.NaN;

      for (InterfaceElement interfaceelement : var1) {
         int i = afloat[0] <= afloat[1] ? 0 : 1;
         float f3 = afloat[i];
         if (interfaceelement == var2) {
            f1 = f3;
            f2 = f3 + interfaceelement.getHeight();
         }

         afloat[i] += interfaceelement.getHeight() + f;
      }

      if (Float.isFinite(f1) && Float.isFinite(f2)) {
         float f4 = Math.max(afloat[0], afloat[1]);
         if (f4 > 0.0F) {
            f4 -= f;
         }

         f4 = Math.max(0.0F, f4);
         return new float[]{f1, f2, f4};
      } else {
         return null;
      }
   }

   @Override
   public List<InterfaceElement> getElements() {
      return this.getFilteredElements(this.currentCategory);
   }

   public List<InterfaceElement> getFilteredElements(InterfacePanel_InterfaceCategory var1) {
      List<InterfaceElement> list = this.categories.get(var1);
      if (list != null && !list.isEmpty()) {
         String s = ZenithClient.on23().NbtEditor().getSearchValue();
         if (s != null && !s.isBlank()) {
            String s1 = s.trim().toLowerCase();
            return list.stream().filter(var2x -> {
               String s2 = var2x.getName();
               return s2 != null && !s2.isBlank() ? s2.toLowerCase().contains(s1) : var1 == InterfacePanel_InterfaceCategory.THEME;
            }).toList();
         } else {
            return list;
         }
      } else {
         return List.of();
      }
   }

   @Override
   public void close() {
      this.animationChangeCategory.UiAnimation(1.0F);
      this.lastCategory = null;
      this.themeTabBounds = null;
      this.hudTabBounds = null;
   }

   public InterfacePanel_InterfaceCategory getCurrentCategory() {
      return this.currentCategory;
   }
}
