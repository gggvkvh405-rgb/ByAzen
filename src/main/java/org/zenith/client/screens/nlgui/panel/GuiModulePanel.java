package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.addon.internal.AddonBackedModule;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.modules.BotModuleUiAdapter;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.NLMenuScreen_ElementsType;
import org.zenith.client.screens.nlgui.elements.GuiModuleElement;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.module.Category;
import org.zenith.module.render.Menu;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiModulePanel extends ElementPanel {
   public final UiAnimation animationChangeCategory = new UiAnimation(200L, 1.0F, Easing.StopUsingItemEvent);
   public final UiAnimation animationEnable = new UiAnimation(200L, 1.0F, Easing.CloseScreenEvent);
   public final Map<Category, List<GuiModuleElement>> categories = new HashMap<>();
   public Category currentCategory = Category.COMBAT;
   public Category lastCategory;
   public boolean botOnly;
   public boolean botContext;
   public final List<GuiModuleElement> botModuleElements = new ArrayList<>();
   public CornerRadiusF scissorBounds;
   public float scroll = 0.0F;
   public float scrollTarget = 0.0F;
   public static final float SCROLL_SPEED = 22.0F;
   public static final float SCROLL_SMOOTH = 0.25F;

   public GuiModulePanel() {
      for (Category i1i1lillillll11 : Category.values()) {
         this.categories
            .put(
               i1i1lillillll11,
               ZenithClient.on23()
                  .ColorAnimator()
                  .PacketDispatcher()
                  .stream()
                  .filter(var0 -> !(var0 instanceof AddonBackedModule))
                  .filter(var1 -> var1.getCategory() == i1i1lillillll11)
                  .map(GuiModuleElement::new)
                  .toList()
            );
      }

      this.categories.forEach((var1, var2) -> {
         for (GuiModuleElement guimoduleelement : var2) {
            guimoduleelement.resetSearchVisible(var1 == this.currentCategory);
         }
      });
   }

   @Override
   public void render(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.scissorBounds = new CornerRadiusF(var5, var6, 376.0F, 295.5F - GuiStyle.PADDING * 2);
      this.animationChangeCategory.on23(Menu.menu.int470());
      List<GuiModuleElement> list = this.getSearchRenderableElements(this.currentCategory);
      float f = this.getContentHeight(list, GuiStyle.PADDING.intValue());
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
      if (this.lastCategory != null && f2 > 0.0F) {
         float f4 = var4 * f2;
         this.renderElements(this.getSearchRenderableElements(this.lastCategory), this.lastCategory, var1, var2, var3, f4, var5, var6, false);
      }

      if (f3 > 0.0F) {
         float f6 = var4 * f3;
         float f5 = 20.0F * (1.0F - f3);
         this.renderElements(list, this.currentCategory, var1, var2, var3, f6, var5, var6 + f5, false);
         var1.disableScissor();
      } else {
         var1.disableScissor();
      }

      if (this.lastCategory != null) {
         ZenithClient.on23()
            .NbtEditor()
            .renderElementSwapBlur(var1, this.scissorBounds.x(), this.scissorBounds.y(), this.scissorBounds.width(), this.scissorBounds.height(), 1.0F - f1);
      }
   }

   public void renderElements(
      List<GuiModuleElement> var1, Category var2, HudDrawContext var3, int var4, int var5, float var6, float var7, float var8, boolean var9
   ) {
      if (!var1.isEmpty()) {
         List<GuiModuleElement> list = this.getOrderedElements(var1);
         float f = var7 + GuiStyle.PADDING.intValue();
         float f1 = var8 + this.scroll;
         float f2 = 376.0F - GuiStyle.PADDING * 2;
         float f3 = GuiStyle.PADDING.intValue();
         float f4 = (f2 - f3) / 2.0F;
         byte b0 = 2;
         float[] afloat = new float[b0];
         int i = 0;
         boolean flag = false;

         for (GuiModuleElement guimoduleelement : list) {
            try {
               int j = 0;

               for (int k = 1; k < b0; k++) {
                  if (afloat[k] < afloat[j]) {
                     j = k;
                  }
               }

               float f7 = f + j * (f4 + f3);
               float f5 = f1 + afloat[j];
               float f6 = guimoduleelement.getSearchVisibleProgress();
               if (!(f6 <= 0.01F)) {
                  CornerRadiusF l11liliill1iii1 = new CornerRadiusF(f7, f5, guimoduleelement.getWidth(), guimoduleelement.getHeight() * f6);
                  if (var9) {
                     guimoduleelement.renderPriority(var3, var4, var5, f7, f5, var6 * f6);
                  } else if (this.scissorBounds.on23(l11liliill1iii1)) {
                     guimoduleelement.render(var3, var4, var5, f7, f5, var6 * f6, i + j, this.scissorBounds);
                  }

                  if (guimoduleelement.isEnable()) {
                     flag = true;
                  }

                  afloat[j] += (guimoduleelement.getHeight() + f3) * f6;
                  i++;
               }
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }

         if (var2 == this.currentCategory) {
            this.animationEnable.on23(flag);
         }
      }
   }

   public GuiModuleElement getElementAt(double var1, double var3, List<GuiModuleElement> var5) {
      if (this.scissorBounds != null && !var5.isEmpty()) {
         float f = this.scissorBounds.x() + GuiStyle.PADDING.intValue();
         float f1 = this.scissorBounds.y() + this.scroll;
         float f2 = this.scissorBounds.width() - GuiStyle.PADDING.intValue() * 2.0F;
         float f3 = GuiStyle.PADDING.intValue();
         float f4 = (f2 - f3) / 2.0F;
         byte b0 = 2;
         float[] afloat = new float[b0];

         for (GuiModuleElement guimoduleelement : this.getOrderedElements(var5)) {
            int i = 0;

            for (int j = 1; j < b0; j++) {
               if (afloat[j] < afloat[i]) {
                  i = j;
               }
            }

            float f8 = guimoduleelement.getSearchVisibleProgress();
            if (!(f8 <= 0.01F)) {
               float f5 = f + i * (f4 + f3);
               float f6 = f1 + afloat[i];
               float f7 = guimoduleelement.getHeight() * f8;
               CornerRadiusF l11liliill1iii1 = new CornerRadiusF(f5, f6, guimoduleelement.getWidth(), f7);
               if (l11liliill1iii1.PotionItemBuilder(var1, var3)) {
                  return guimoduleelement;
               }

               afloat[i] += (guimoduleelement.getHeight() + f3) * f8;
            }
         }

         return null;
      } else {
         return null;
      }
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
            this.renderHeader(this.lastCategory, font, font1, var1, var2, var3, var4, 1.0F - f1, f, var6 + f2, var7);
         }

         if (f1 > 0.0F) {
            float f3 = 8.0F * (1.0F - f1);
            this.renderHeader(this.currentCategory, font, font1, var1, var2, var3, var4, f1, f, var6 + f3, var7);
         }
      }
   }

   public void renderHeader(
      Category var1, Font var2, Font var3, HudDrawContext var4, int var5, int var6, float var7, float var8, float var9, float var10, float var11
   ) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = var8 * var7;
         String s = var1.getIcon();
         float f1 = var10 + (23.0F - var2.height()) / 2.0F;
         float f2 = var10 + (23.0F - var3.height()) / 2.0F - 0.1F;
         float f3 = var9 + var3.width(s) + GuiStyle.PADDING.intValue();
         var4.drawText(var2, var1.getName(), f3, f1, zenithstyle.getTextEnable().getColor().SprintStateEvent(f));
         var4.drawText(var3, s, var9, f2, zenithstyle.getPrimaryColor().getColor().SprintStateEvent(f));
      }
   }

   @Override
   public void renderPriority(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      float f = this.animationChangeCategory.CancellableEvent();
      float f1 = MathHelper.clamp((f - 0.3F) / 0.7F, 0.0F, 1.0F);
      if (!(f1 <= 0.0F)) {
         float f2 = var4 * f1;
         float f3 = 20.0F * (1.0F - f1);
         this.renderElements(this.getSearchRenderableElements(this.currentCategory), this.currentCategory, var1, var2, var3, f2, var5, var6 + f3, true);
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.scissorBounds == null) {
         return false;
      }

      List<GuiModuleElement> list = this.getFilteredElements(this.currentCategory);

      for (GuiModuleElement guimoduleelement : list) {
         if (guimoduleelement.mouseScrolled(var1, var3, var5, var7)) {
            return true;
         }
      }

      float f = this.scissorBounds.height();
      if (list.isEmpty()) {
         return false;
      }

      float f1 = this.getContentHeight(list, GuiStyle.PADDING.intValue()) + GuiStyle.PADDING.intValue();
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

   public float getContentHeight(List<GuiModuleElement> var1, float var2) {
      if (var1.isEmpty()) {
         return 0.0F;
      }

      List<GuiModuleElement> list = this.getOrderedElements(var1);
      byte b0 = 2;
      float[] afloat = new float[b0];

      for (GuiModuleElement guimoduleelement : list) {
         int i = 0;

         for (int j = 1; j < b0; j++) {
            if (afloat[j] < afloat[i]) {
               i = j;
            }
         }

         float f1 = guimoduleelement.getSearchVisibleProgress();
         if (!(f1 <= 0.01F)) {
            afloat[i] += (guimoduleelement.getHeight() + var2) * f1;
         }
      }

      float f = Math.max(afloat[0], afloat[1]);
      return Math.max(0.0F, f - var2);
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (!this.animationChangeCategory.isDone()) {
         return false;
      }

      boolean flag = false;
      List<GuiModuleElement> list = this.getFilteredElements(this.currentCategory);

      for (GuiModuleElement guimoduleelement : list) {
         if (guimoduleelement.onMousePriorityClicked(var1, var3, var5)) {
            flag = true;
         }
      }

      if (flag) {
         return true;
      } else if (this.scissorBounds != null && this.scissorBounds.PotionItemBuilder(var1, var3)) {
         GuiModuleElement guimoduleelement1 = this.getElementAt(var1, var3, list);
         return guimoduleelement1 != null && guimoduleelement1.onMouseClicked(var1, var3, var5);
      } else {
         return false;
      }
   }

   public List<GuiModuleElement> getOrderedElements(List<GuiModuleElement> var1) {
      List<GuiModuleElement> arraylist = new ArrayList<>(var1.size());

      for (GuiModuleElement guimoduleelement : var1) {
         if (guimoduleelement.isPriority()) {
            arraylist.add(guimoduleelement);
         }
      }

      for (GuiModuleElement guimoduleelement1 : var1) {
         if (!guimoduleelement1.isPriority()) {
            arraylist.add(guimoduleelement1);
         }
      }

      return arraylist;
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, MenuScreenId var5) {
      for (GuiModuleElement guimoduleelement : this.getFilteredElements(this.currentCategory)) {
         guimoduleelement.onMouseReleased(var1, var3, var5);
      }

      return super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      for (GuiModuleElement guimoduleelement : this.getFilteredElements(this.currentCategory)) {
         if (guimoduleelement.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      for (GuiModuleElement guimoduleelement : this.getFilteredElements(this.currentCategory)) {
         if (guimoduleelement.charTyped(var1, var2)) {
            return true;
         }
      }

      return super.charTyped(var1, var2);
   }

   public void setCategory(Category var1) {
      if (this.animationChangeCategory.isDone() && this.currentCategory != var1) {
         if (ZenithClient.on23().NbtEditor().getType() == NLMenuScreen_ElementsType.CATEGORY) {
            this.lastCategory = this.currentCategory;
            this.scrollTarget = 0.0F;
            this.animationChangeCategory.UiAnimation(0.0F);
            this.animationChangeCategory.Easing(1.0F);
         }

         this.currentCategory = var1;

         for (GuiModuleElement guimoduleelement : this.categories.get(var1)) {
            guimoduleelement.setPositionInitialized(false);
         }
      }
   }

   public boolean isCategorySwitching() {
      return this.lastCategory != null && !this.animationChangeCategory.isDone();
   }

   public void setBotOnly(boolean var1) {
      if (this.botOnly != var1) {
         this.botOnly = var1;
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
         List<GuiModuleElement> list = this.categories.get(this.currentCategory);
         if (list != null) {
            for (GuiModuleElement guimoduleelement : list) {
               guimoduleelement.setPositionInitialized(false);
            }
         }
      }
   }

   public void forceCategory(Category var1) {
      this.currentCategory = var1;
      this.lastCategory = null;
      this.animationChangeCategory.UiAnimation(1.0F);
      this.scrollTarget = 0.0F;
      this.scroll = 0.0F;
      List<GuiModuleElement> list = this.categories.get(var1);
      if (list != null) {
         for (GuiModuleElement guimoduleelement : list) {
            guimoduleelement.setPositionInitialized(false);
         }
      }
   }

   public void setBotContext(String var1) {
      this.botModuleElements.clear();
      this.botContext = var1 != null;
      BotClient botclient = var1 != null ? HeadlessBots.get(var1) : null;
      if (botclient != null) {
         for (BotModule botmodule : botclient.getModules().getModules()) {
            GuiModuleElement guimoduleelement = new GuiModuleElement(new BotModuleUiAdapter(var1, botmodule));
            guimoduleelement.resetSearchVisible(true);
            this.botModuleElements.add(guimoduleelement);
         }
      }
   }

   public List<GuiModuleElement> applyBotFilter(List<GuiModuleElement> var1) {
      return this.botOnly && !var1.isEmpty() ? var1.stream().filter(var0 -> var0.getModule().isBotModule()).toList() : var1;
   }

   public List<GuiModuleElement> withBotElements(List<GuiModuleElement> var1, boolean var2) {
      if (this.botContext && var2 && !this.botModuleElements.isEmpty()) {
         List<GuiModuleElement> arraylist = new ArrayList<>(var1);
         arraylist.addAll(this.botModuleElements);
         return arraylist;
      } else {
         return var1;
      }
   }

   @Override
   public List<GuiModuleElement> getElements() {
      return this.getFilteredElements(this.currentCategory);
   }

   public List<GuiModuleElement> getFilteredElements(Category var1) {
      List<GuiModuleElement> list = this.categories.get(var1);
      if (list == null) {
         return Collections.emptyList();
      }

      list = this.withBotElements(this.applyBotFilter(list), var1 == Category.PLAYER);
      String s = ZenithClient.on23().NbtEditor().getSearchValue();
      if (s == null) {
         return list;
      }

      String s1 = s.trim().toLowerCase();
      return s1.isEmpty()
         ? list
         : this.withBotElements(this.applyBotFilter(this.categories.values().stream().flatMap(Collection::stream).toList()), true)
            .stream()
            .filter(var1x -> var1x.getName().toLowerCase().contains(s1))
            .toList();
   }

   public List<GuiModuleElement> getSearchRenderableElements(Category var1) {
      List<GuiModuleElement> list = this.getSearchSourceElements(var1);
      String s = this.getSearchQuery();
      boolean flag = !s.isEmpty();
      List<GuiModuleElement> arraylist = new ArrayList<>(list.size());

      for (GuiModuleElement guimoduleelement : list) {
         boolean flag1 = !flag || guimoduleelement.getName().toLowerCase().contains(s);
         guimoduleelement.setSearchVisible(flag1);
         guimoduleelement.updateSearchVisible();
         if (guimoduleelement.shouldRenderInSearch()) {
            arraylist.add(guimoduleelement);
         }
      }

      return arraylist;
   }

   public List<GuiModuleElement> getSearchSourceElements(Category var1) {
      if (this.getSearchQuery().isEmpty()) {
         List<GuiModuleElement> list = this.categories.get(var1);
         return list == null ? Collections.emptyList() : this.withBotElements(this.applyBotFilter(list), var1 == Category.PLAYER);
      } else {
         return this.withBotElements(this.applyBotFilter(this.categories.values().stream().flatMap(Collection::stream).toList()), true);
      }
   }

   public String getSearchQuery() {
      String s = ZenithClient.on23().NbtEditor().getSearchValue();
      return s == null ? "" : s.trim().toLowerCase();
   }

   @Override
   public void close() {
      this.animationChangeCategory.UiAnimation(1.0F);
      this.lastCategory = null;
   }

   public Category getCurrentCategory() {
      return this.currentCategory;
   }

   public boolean isBotOnly() {
      return this.botOnly;
   }
}
