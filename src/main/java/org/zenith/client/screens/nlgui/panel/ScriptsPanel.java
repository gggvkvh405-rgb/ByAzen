package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.addon.internal.AddonBackedModule;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.GuiModuleElement;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.module.render.Menu;
import org.zenith.module.Module;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public final class ScriptsPanel extends ElementPanel {
   public static final float SCROLL_SPEED = 22.0F;
   public static final float SCROLL_SMOOTH = 0.25F;
   public static final float TAB_SCROLL_SPEED = 24.0F;
   public static final float TAB_GAP = 4.0F;
   public static final float TAB_SEPARATOR_WIDTH = 0.5F;
   public final Map<String, List<GuiModuleElement>> addons = new LinkedHashMap<>();
   public final List<ScriptsPanel_AddonTab> tabs = new ArrayList<>();
   public final UiAnimation animationChangeAddon = new UiAnimation(200L, 1.0F, Easing.StopUsingItemEvent);
   public String currentAddonId;
   public String lastAddonId;
   public CornerRadiusF scissorBounds;
   public CornerRadiusF tabViewportBounds;
   public float scroll;
   public float scrollTarget;
   public float tabScroll;
   public float tabScrollTarget;
   public float tabContentWidth;

   public void renderAddonTabs(HudDrawContext var1, int var2, int var3, float var4, ZenithStyle var5) {
      this.tabs.clear();
      if (this.tabViewportBounds != null && !(this.tabViewportBounds.width() <= 0.0F)) {
         Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
         ArrayList arraylist = new ArrayList<>(this.addons.keySet());
         if (arraylist.isEmpty()) {
            String s1 = "No add-ons";
            var1.drawText(
               font,
               s1,
               this.tabViewportBounds.x(),
               this.tabViewportBounds.y() + (this.tabViewportBounds.height() - font.height()) / 2.0F,
               var5.getTextSecondary().getColor().SprintStateEvent(var4)
            );
            this.tabContentWidth = font.width(s1);
         } else {
            this.tabContentWidth = this.calculateTabContentWidth(font, arraylist);
            this.clampTabScroll();
            this.tabScroll = this.tabScroll + (this.tabScrollTarget - this.tabScroll) * 0.25F;
            float f = this.tabViewportBounds.x() - this.tabScroll;
            float f1 = this.tabViewportBounds.y() + (this.tabViewportBounds.height() - font.height()) / 2.0F;
            float f2 = this.tabViewportBounds.y() + (this.tabViewportBounds.height() - 6.0F) / 2.0F;
            ArgbColor i11ii1llliilllii1i1 = var5.getDisableActiveBg().getColor().SprintStateEvent(var4);
            var1.enableScissor(
               this.tabViewportBounds.x(),
               this.tabViewportBounds.y(),
               this.tabViewportBounds.x() + this.tabViewportBounds.width(),
               this.tabViewportBounds.y() + this.tabViewportBounds.height()
            );

            for (int i = 0; i < arraylist.size(); i++) {
               if (i > 0) {
                  var1.drawRect(f, f2, 0.5F, 6.0F, i11ii1llliilllii1i1);
                  f += 4.5F;
               }

               String s = (String)arraylist.get(i);
               float f3 = font.width(s);
               CornerRadiusF l11liliill1iii1 = new CornerRadiusF(f - 2.0F, this.tabViewportBounds.y() + 1.0F, f3 + 4.0F, 21.0F);
               this.tabs.add(new ScriptsPanel_AddonTab(s, l11liliill1iii1));
               boolean flag = this.tabViewportBounds.PotionItemBuilder(var2, var3) && l11liliill1iii1.PotionItemBuilder(var2, var3);
               boolean flag1 = s.equals(this.currentAddonId);
               ArgbColor i11ii1llliilllii1i11 = flag1
                  ? var5.getTextEnable().getColor()
                  : var5.getTextSecondary().getColor().Easing(var5.getTextEnable().getColor(), flag ? 0.65F : 0.0F);
               var1.drawText(font, s, f, f1, i11ii1llliilllii1i11.SprintStateEvent(var4));
               f += f3 + 4.0F;
            }

            var1.disableScissor();
         }
      }
   }

   @Override
   public void render(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.scissorBounds = new CornerRadiusF(var5, var6, 376.0F, 295.5F - GuiStyle.PADDING.intValue() * 2.0F);
      this.animationChangeAddon.on23(Menu.menu.int470());
      List<GuiModuleElement> list = this.getSearchRenderableElements(this.currentAddonId);
      float f = this.getContentHeight(list) + GuiStyle.PADDING.intValue();
      this.clampScroll(f, this.scissorBounds.height());
      this.scroll = this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
      float f1 = this.animationChangeAddon.on23(1.0F);
      if (this.animationChangeAddon.isDone()) {
         this.lastAddonId = null;
      }

      float f2 = MathHelper.clamp((0.5F - f1) / 0.5F, 0.0F, 1.0F);
      float f3 = MathHelper.clamp((f1 - 0.3F) / 0.7F, 0.0F, 1.0F);
      var1.enableScissor(
         this.scissorBounds.x(),
         this.scissorBounds.y(),
         this.scissorBounds.x() + this.scissorBounds.width(),
         this.scissorBounds.y() + this.scissorBounds.height()
      );
      if (this.lastAddonId != null && f2 > 0.0F) {
         this.renderElements(this.getSearchRenderableElements(this.lastAddonId), var1, var2, var3, var4 * f2, var5, var6, false);
      }

      if (f3 > 0.0F) {
         this.renderElements(list, var1, var2, var3, var4 * f3, var5, var6 + 20.0F * (1.0F - f3), false);
      }

      var1.disableScissor();
      if (this.lastAddonId != null) {
         ZenithClient.on23()
            .NbtEditor()
            .renderElementSwapBlur(var1, this.scissorBounds.x(), this.scissorBounds.y(), this.scissorBounds.width(), this.scissorBounds.height(), 1.0F - f1);
      }
   }

   public ScriptsPanel() {
      Map<String, List<GuiModuleElement>> linkedhashmap = new LinkedHashMap<>();

      for (Module lii1lll1l1li1ii1iiillii : ZenithClient.on23().ColorAnimator().PacketDispatcher()) {
         if (lii1lll1l1li1ii1iiillii instanceof AddonBackedModule addonbackedmodule) {
            linkedhashmap.computeIfAbsent(addonbackedmodule.getAddonId(), var0 -> new ArrayList<>()).add(new GuiModuleElement(lii1lll1l1li1ii1iiillii));
         }
      }

      linkedhashmap.entrySet()
         .stream()
         .sorted(Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
         .forEach(var1x -> this.addons.put(var1x.getKey(), var1x.getValue()));
      this.currentAddonId = this.addons.keySet().stream().findFirst().orElse(null);
      this.addons.forEach((var1x, var2) -> var2.forEach(var2x -> var2x.resetSearchVisible(var1x.equals(this.currentAddonId))));
   }

   @Override
   public void renderHeader(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font1 = Fonts.NEW_ICONS.getFont(6.0F);
         float f = var5 + GuiStyle.PADDING.intValue() + GuiStyle.PADDING.intValue() * 2.0F;
         float f1 = var6 + (23.0F - font.height()) / 2.0F;
         String s = "<";
         var1.drawText(font1, s, f, var6 + (23.0F - font1.height()) / 2.0F - 0.1F, zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var4));
         float f2 = f + font1.width(s) + GuiStyle.PADDING.intValue();
         var1.drawText(font, "Scripts", f2, f1, zenithstyle.getTextEnable().getColor().SprintStateEvent(var4));
         float f3 = f2 + font.width("Scripts") + GuiStyle.PADDING.intValue() * 4.0F;
         float f4 = var5 + var7 - GuiStyle.PADDING.intValue() * 2.0F;
         this.tabViewportBounds = new CornerRadiusF(f3, var6, Math.max(0.0F, f4 - f3), 23.0F);
         this.renderAddonTabs(var1, var2, var3, var4, zenithstyle);
      }
   }

   public float calculateTabContentWidth(Font var1, List<String> var2) {
      float f = 0.0F;

      for (int i = 0; i < var2.size(); i++) {
         if (i > 0) {
            f += 4.5F;
         }

         f += var1.width(var2.get(i)) + 4.0F;
      }

      return Math.max(0.0F, f - 4.0F);
   }

   @Override
   public void renderPriority(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      float f = MathHelper.clamp((this.animationChangeAddon.CancellableEvent() - 0.3F) / 0.7F, 0.0F, 1.0F);
      if (!(f <= 0.0F)) {
         this.renderElements(this.getSearchRenderableElements(this.currentAddonId), var1, var2, var3, var4 * f, var5, var6 + 20.0F * (1.0F - f), true);
      }
   }

   public void renderElements(List<GuiModuleElement> var1, HudDrawContext var2, int var3, int var4, float var5, float var6, float var7, boolean var8) {
      if (!var1.isEmpty()) {
         List<GuiModuleElement> list = this.getOrderedElements(var1);
         float f = var6 + GuiStyle.PADDING.intValue();
         float f1 = var7 + this.scroll;
         float f2 = 376.0F - GuiStyle.PADDING.intValue() * 2.0F;
         float f3 = GuiStyle.PADDING.intValue();
         float f4 = (f2 - f3) / 2.0F;
         float[] afloat = new float[2];
         int i = 0;

         for (GuiModuleElement guimoduleelement : list) {
            int j = afloat[1] < afloat[0] ? 1 : 0;
            float f5 = guimoduleelement.getSearchVisibleProgress();
            if (!(f5 <= 0.01F)) {
               float f6 = f + j * (f4 + f3);
               float f7 = f1 + afloat[j];
               CornerRadiusF l11liliill1iii1 = new CornerRadiusF(f6, f7, guimoduleelement.getWidth(), guimoduleelement.getHeight() * f5);
               if (var8) {
                  guimoduleelement.renderPriority(var2, var3, var4, f6, f7, var5 * f5);
               } else if (this.scissorBounds != null && this.scissorBounds.on23(l11liliill1iii1)) {
                  guimoduleelement.render(var2, var3, var4, f6, f7, var5 * f5, i + j, this.scissorBounds);
               }

               afloat[j] += (guimoduleelement.getHeight() + f3) * f5;
               i++;
            }
         }
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (!this.animationChangeAddon.isDone()) {
         return false;
      }

      for (GuiModuleElement guimoduleelement : this.getFilteredElements(this.currentAddonId)) {
         if (guimoduleelement.onMousePriorityClicked(var1, var3, var5)) {
            return true;
         }
      }

      if (this.tabViewportBounds != null && this.tabViewportBounds.PotionItemBuilder(var1, var3)) {
         for (ScriptsPanel_AddonTab scriptspanel_addontab : this.tabs) {
            if (scriptspanel_addontab.bounds().PotionItemBuilder(var1, var3)) {
               this.setAddon(scriptspanel_addontab.addonId());
               return true;
            }
         }
      }

      if (this.scissorBounds != null && this.scissorBounds.PotionItemBuilder(var1, var3)) {
         GuiModuleElement guimoduleelement1 = this.getElementAt(var1, var3, this.getFilteredElements(this.currentAddonId));
         return guimoduleelement1 != null && guimoduleelement1.onMouseClicked(var1, var3, var5);
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.tabViewportBounds != null && this.tabViewportBounds.PotionItemBuilder(var1, var3) && this.tabContentWidth > this.tabViewportBounds.width()) {
         double d0 = var5 != 0.0 ? var5 : -var7;
         this.tabScrollTarget += (float)d0 * 24.0F;
         this.clampTabScroll();
         return true;
      }

      List<GuiModuleElement> list = this.getFilteredElements(this.currentAddonId);

      for (GuiModuleElement guimoduleelement : list) {
         if (guimoduleelement.mouseScrolled(var1, var3, var5, var7)) {
            return true;
         }
      }

      if (this.scissorBounds != null && this.scissorBounds.PotionItemBuilder(var1, var3) && !list.isEmpty()) {
         float f = this.getContentHeight(list) + GuiStyle.PADDING.intValue();
         if (f <= this.scissorBounds.height()) {
            return false;
         }

         this.scrollTarget += (float)var7 * 22.0F;
         this.clampScroll(f, this.scissorBounds.height());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, MenuScreenId var5) {
      for (GuiModuleElement guimoduleelement : this.getFilteredElements(this.currentAddonId)) {
         guimoduleelement.onMouseReleased(var1, var3, var5);
      }

      return false;
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      for (GuiModuleElement guimoduleelement : this.getFilteredElements(this.currentAddonId)) {
         if (guimoduleelement.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      for (GuiModuleElement guimoduleelement : this.getFilteredElements(this.currentAddonId)) {
         if (guimoduleelement.charTyped(var1, var2)) {
            return true;
         }
      }

      return false;
   }

   public void setAddon(String var1) {
      if (var1 != null && !var1.equals(this.currentAddonId) && this.addons.containsKey(var1)) {
         this.lastAddonId = this.currentAddonId;
         this.currentAddonId = var1;
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
         this.animationChangeAddon.UiAnimation(0.0F);
         this.animationChangeAddon.Easing(1.0F);

         for (GuiModuleElement guimoduleelement : this.addons.get(var1)) {
            guimoduleelement.setPositionInitialized(false);
         }
      }
   }

   public boolean isAddonSwitching() {
      return this.lastAddonId != null && !this.animationChangeAddon.isDone();
   }

   public List<GuiModuleElement> getFilteredElements(String var1) {
      List<GuiModuleElement> list = var1 == null ? null : this.addons.get(var1);
      if (list == null) {
         return Collections.emptyList();
      }

      String s = this.searchQuery();
      return s.isEmpty() ? list : list.stream().filter(var1x -> var1x.getName().toLowerCase(Locale.ROOT).contains(s)).toList();
   }

   public List<GuiModuleElement> getSearchRenderableElements(String var1) {
      List<GuiModuleElement> list = var1 == null ? Collections.emptyList() : this.addons.getOrDefault(var1, Collections.emptyList());
      String s = this.searchQuery();
      List<GuiModuleElement> arraylist = new ArrayList<>(list.size());

      for (GuiModuleElement guimoduleelement : list) {
         guimoduleelement.setSearchVisible(s.isEmpty() || guimoduleelement.getName().toLowerCase(Locale.ROOT).contains(s));
         guimoduleelement.updateSearchVisible();
         if (guimoduleelement.shouldRenderInSearch()) {
            arraylist.add(guimoduleelement);
         }
      }

      return arraylist;
   }

   public String searchQuery() {
      String s = ZenithClient.on23().NbtEditor().getSearchValue();
      return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
   }

   public List<GuiModuleElement> getOrderedElements(List<GuiModuleElement> var1) {
      List<GuiModuleElement> arraylist = new ArrayList<>(var1);
      arraylist.sort(Comparator.comparing(GuiModuleElement::isPriority).reversed());
      return arraylist;
   }

   public GuiModuleElement getElementAt(double var1, double var3, List<GuiModuleElement> var5) {
      if (this.scissorBounds != null && !var5.isEmpty()) {
         float f = this.scissorBounds.x() + GuiStyle.PADDING.intValue();
         float f1 = this.scissorBounds.y() + this.scroll;
         float f2 = GuiStyle.PADDING.intValue();
         float f3 = (this.scissorBounds.width() - GuiStyle.PADDING.intValue() * 2.0F - f2) / 2.0F;
         float[] afloat = new float[2];

         for (GuiModuleElement guimoduleelement : this.getOrderedElements(var5)) {
            int i = afloat[1] < afloat[0] ? 1 : 0;
            float f4 = guimoduleelement.getSearchVisibleProgress();
            if (!(f4 <= 0.01F)) {
               float f5 = f + i * (f3 + f2);
               float f6 = f1 + afloat[i];
               float f7 = guimoduleelement.getHeight() * f4;
               if (new CornerRadiusF(f5, f6, guimoduleelement.getWidth(), f7).PotionItemBuilder(var1, var3)) {
                  return guimoduleelement;
               }

               afloat[i] += (guimoduleelement.getHeight() + f2) * f4;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public float getContentHeight(List<GuiModuleElement> var1) {
      if (var1.isEmpty()) {
         return 0.0F;
      }

      float f = GuiStyle.PADDING.intValue();
      float[] afloat = new float[2];

      for (GuiModuleElement guimoduleelement : this.getOrderedElements(var1)) {
         int i = afloat[1] < afloat[0] ? 1 : 0;
         float f1 = guimoduleelement.getSearchVisibleProgress();
         if (f1 > 0.01F) {
            afloat[i] += (guimoduleelement.getHeight() + f) * f1;
         }
      }

      return Math.max(0.0F, Math.max(afloat[0], afloat[1]) - f);
   }

   public void clampScroll(float var1, float var2) {
      if (var1 <= var2) {
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
      } else {
         float f = var2 - var1;
         this.scrollTarget = MathHelper.clamp(this.scrollTarget, f, 0.0F);
         this.scroll = MathHelper.clamp(this.scroll, f, 0.0F);
      }
   }

   public void clampTabScroll() {
      float f = this.tabViewportBounds == null ? 0.0F : this.tabViewportBounds.width();
      float f1 = Math.max(0.0F, this.tabContentWidth - f);
      this.tabScrollTarget = MathHelper.clamp(this.tabScrollTarget, 0.0F, f1);
      this.tabScroll = MathHelper.clamp(this.tabScroll, 0.0F, f1);
   }

   @Override
   public List<GuiModuleElement> getElements() {
      return this.getFilteredElements(this.currentAddonId);
   }

   @Override
   public void close() {
      this.animationChangeAddon.UiAnimation(1.0F);
      this.lastAddonId = null;
   }
}
