package org.zenith.client.screens.autosbor.panels;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.autosbor.AutoSborStyle;
import org.zenith.core.Easing;
import org.zenith.core.ItemServiceBase;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.hud.ScrollHandler;
import org.zenith.hud.SearchBox;
import org.zenith.util.ArgbColor;
import org.zenith.util.MathUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class ItemListPanel {
   public static final float panelWidth = 128.0F;
   public static final float panelHeight = 23.0F;
   public static final float panelOffset = 4.0F;
   public static final float textOffsetX = 8.0F;
   public static final float searchIconRightOffset = 8.0F;
   public static final float searchIconYOffset = -0.5F;
   public static final float listHeight = 258.0F;
   public static final float itemPanelSize = 23.0F;
   public static final float itemIconSize = 8.0F;
   public static final float itemIconScale = 0.5F;
   public static final float itemOpacity = 0.48F;
   public static final float itemHoverOpacity = 1.0F;
   public static final float titleIconGap = 4.0F;
   public static final float itemStep = 25.0F;
   public static final int columns = 5;
   public static final float listWidth = 123.0F;
   public static final float scrollWidth = 1.0F;
   public static final float scrollGap = 4.0F;
   public static final float scrollHitPadding = 3.0F;
   public static final float minScrollHeight = 20.0F;
   public static final float minScrollStep = 20.0F;
   public static final float maxScrollStep = 60.0F;
   public static final float scrollStepScale = 10.0F;
   public static final float scrollStepDivider = 8.0F;
   public static final float scrollSnapEpsilon = 0.05F;
   public static final long itemMoveDuration = 180L;
   public static final CornerRadius panelRadius = CornerRadius.MovementInputEvent(7.0F);
   public static final Font panelTitleFont = Fonts.MEDIUM.getFont(6.0F);
   public static final Font panelTitleIconFont = Fonts.ICONS.getFont(7.0F);
   public static final Font searchIconFont = Fonts.ICONS.getFont(6.0F);
   public static final Font searchFont = Fonts.MEDIUM.getFont(6.0F);
   public static final String panelTitleText = "Auto Inventory";
   public static final String panelTitleIcon = "7";
   public static final String searchIcon = "S";
   public final SearchBox searchBox = new SearchBox(new Vector2f(0.0F, 0.0F), searchFont, "Search for item...", 112.0F - searchIconFont.width("S") - 8.0F);
   public final ScrollHandler scrollHandler = new ScrollHandler();
   public final UiAnimation animationScrollHeight = new UiAnimation(150L, 1.0F, Easing.StopUsingItemEvent);
   public final List<ItemServiceBase> filteredItems = new ArrayList<>();
   public final Map<ItemServiceBase, UiAnimation> itemXAnimations = new IdentityHashMap<>();
   public final Map<ItemServiceBase, UiAnimation> itemYAnimations = new IdentityHashMap<>();
   public final Supplier<String> serverSupplier;
   public String lastSearchText = "";
   public String lastServer = "";
   public int lastSourceSize = -1;
   public float panelX;
   public float panelY;
   public float searchPanelX;
   public float searchPanelY;
   public float gridX;
   public float gridY;
   public float scrollbarX;
   public float scrollbarY;
   public float scrollbarThumbY;
   public float scrollbarHeight;
   public float scrollClickOffset;
   public float lastScrollOffset;
   public boolean draggingScrollbar;
   public ItemServiceBase draggedItem;

   public ItemListPanel(Supplier<String> var1) {
      this.serverSupplier = var1;
   }

   public void render(HudDrawContext var1, float var2, float var3, float var4) {
      this.updateLayout(var2, var3);
      this.renderPanels(var1, var4);
      List<ItemServiceBase> list = this.updateFilteredItems();
      this.updateScroll(list.size());
      float f = (float)this.scrollHandler.float260();
      boolean flag = Math.abs(f - this.lastScrollOffset) > 0.05F;
      this.lastScrollOffset = f;
      var1.enableScissor((int)this.gridX, (int)this.gridY, (int)(this.gridX + 123.0F), (int)(this.gridY + 258.0F));
      this.renderVisibleItems(var1, list, f, flag, var4);
      var1.disableScissor();
      this.renderScrollBar(var1, var4);
   }

   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (var5 != MenuScreenId.call004) {
         return false;
      }

      if (this.isSearchHovered(var1, var3)) {
         this.searchBox.VelocityChangeEvent(true);
         return true;
      }

      this.searchBox.VelocityChangeEvent(false);
      if (!this.isScrollbarHovered(var1, var3)) {
         return false;
      }

      this.draggingScrollbar = true;
      this.scrollClickOffset = MathUtils.on23(var1, var3, this.scrollbarX - 3.0F, this.scrollbarThumbY, 7.0, this.scrollbarHeight)
         ? (float)var3 - this.scrollbarThumbY
         : this.scrollbarHeight / 2.0F;
      this.updateDraggedScrollbar(var3);
      return true;
   }

   public void onMouseReleased(MenuScreenId var1) {
      if (var1 == MenuScreenId.call004) {
         this.draggingScrollbar = false;
      }
   }

   public void onMouseDragged(double var1, MenuScreenId var3) {
      if (var3 == MenuScreenId.call004 && this.draggingScrollbar) {
         this.updateDraggedScrollbar(var1);
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.searchBox.isSelected()) {
         return false;
      }

      if (var1 != 256 && var1 != 257) {
         return this.searchBox.keyPressed(var1, var2, var3);
      }

      this.searchBox.VelocityChangeEvent(false);
      return true;
   }

   public boolean charTyped(char var1, int var2) {
      return !this.searchBox.isSelected() ? false : this.searchBox.charTyped(var1, var2);
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (this.isNotListHovered(var1, var3)) {
         return false;
      }

      if (this.scrollHandler.float261() <= 0.0) {
         return true;
      }

      float f = MathHelper.clamp((float)(this.scrollHandler.float261() / 258.0 * 10.0), 20.0F, 60.0F);
      this.scrollHandler.CloudRouter(var5 * f / 8.0);
      return true;
   }

   public boolean isListHovered(double var1, double var3) {
      return !this.isNotListHovered(var1, var3);
   }

   public ItemServiceBase getItemAt(double var1, double var3) {
      if (this.isNotListHovered(var1, var3)) {
         return null;
      }

      float f = (float)this.scrollHandler.float260();
      int i = (int)((var1 - this.gridX) / 25.0);
      int j = (int)((var3 - this.gridY + f) / 25.0);
      if (i >= 0 && i < 5 && j >= 0) {
         float f1 = this.gridX + i * 25.0F;
         float f2 = this.gridY + j * 25.0F - f;
         if (!MathUtils.on23(var1, var3, f1, f2, 23.0, 23.0)) {
            return null;
         }

         List<ItemServiceBase> list = this.updateFilteredItems();
         int k = j * 5 + i;
         return k >= 0 && k < list.size() ? list.get(k) : null;
      } else {
         return null;
      }
   }

   public float getItemPanelSize() {
      return 23.0F;
   }

   public void renderDraggedItem(HudDrawContext var1, ItemServiceBase var2, float var3, float var4, float var5) {
      this.renderItem(var1, var2, var3, var4, var5);
   }

   public void setDraggedItem(ItemServiceBase var1) {
      if (this.draggedItem != var1) {
         this.draggedItem = var1;
         this.lastSourceSize = -1;
      }
   }

   public void updateLayout(float var1, float var2) {
      this.panelX = var1 + 4.0F;
      this.panelY = var2 + 4.0F;
      this.searchPanelX = this.panelX;
      this.searchPanelY = this.panelY + 23.0F + 4.0F;
      this.gridX = this.panelX;
      this.gridY = this.searchPanelY + 23.0F + 4.0F;
      this.scrollbarX = this.gridX + 123.0F + 4.0F;
      this.scrollbarY = this.gridY;
   }

   public void renderPanels(HudDrawContext var1, float var2) {
      var1.drawRoundedRect(this.panelX, this.panelY, 128.0F, 23.0F, panelRadius, AutoSborStyle.panelBackground().SprintStateEvent(var2));
      var1.drawRoundedRect(this.searchPanelX, this.searchPanelY, 128.0F, 23.0F, panelRadius, AutoSborStyle.panelBackground().SprintStateEvent(var2));
      this.renderTitle(var1, var2);
      this.renderSearchBox(var1, var2);
   }

   public void renderTitle(HudDrawContext var1, float var2) {
      float f = panelTitleIconFont.width("7");
      float f1 = panelTitleFont.width("Auto Inventory");
      float f2 = this.panelX + (128.0F - f - 4.0F - f1) / 2.0F;
      float f3 = this.panelY + (23.0F - panelTitleIconFont.height()) / 2.0F - 0.5F;
      float f4 = f2 + f + 4.0F;
      float f5 = this.panelY + (23.0F - panelTitleFont.height()) / 2.0F;
      var1.drawText(panelTitleIconFont, "7", f2, f3, AutoSborStyle.primary().SprintStateEvent(var2));
      var1.drawText(panelTitleFont, "Auto Inventory", f4, f5, AutoSborStyle.text().SprintStateEvent(var2));
   }

   public void renderSearchBox(HudDrawContext var1, float var2) {
      float f = this.searchPanelX + 8.0F;
      float f1 = this.searchPanelX + 128.0F - searchIconFont.width("S") - 8.0F;
      float f2 = this.searchPanelY + (23.0F - this.searchBox.call050().height()) / 2.0F;
      boolean flag = this.isSearchHovered(var1.getMouseX(), var1.getMouseY());
      ArgbColor i11ii1llliilllii1i1 = this.searchBox.isSelected()
         ? AutoSborStyle.transparentText()
         : (flag ? AutoSborStyle.text() : AutoSborStyle.textSecondary());
      var1.enableScissor((int)f, (int)this.searchPanelY, (int)(f + this.searchBox.getWidth()), (int)(this.searchPanelY + 23.0F));
      this.searchBox.on23(var1, f, f2, AutoSborStyle.text().SprintStateEvent(var2), i11ii1llliilllii1i1.SprintStateEvent(var2));
      var1.disableScissor();
      this.renderSearchIcon(var1, f1, var2);
   }

   public void renderSearchIcon(HudDrawContext var1, float var2, float var3) {
      float f = this.searchPanelY + (23.0F - searchIconFont.height()) / 2.0F + -0.5F;
      var1.drawText(searchIconFont, "S", var2, f, AutoSborStyle.textTertiary().SprintStateEvent(var3));
   }

   public List<ItemServiceBase> updateFilteredItems() {
      List<ItemServiceBase> list = this.getSourceItems();
      String s = this.searchBox.getText().trim().toLowerCase(Locale.ROOT);
      String s1 = this.getServer();
      if (s.equals(this.lastSearchText) && s1.equals(this.lastServer) && list.size() == this.lastSourceSize) {
         return this.filteredItems;
      }

      boolean flag = !s.equals(this.lastSearchText) || !s1.equals(this.lastServer);
      this.filteredItems.clear();
      if (s.isEmpty()) {
         for (ItemServiceBase i1l11iiliiill1l1li1ii : list) {
            if (i1l11iiliiill1l1li1ii != this.draggedItem) {
               this.filteredItems.add(i1l11iiliiill1l1li1ii);
            }
         }
      } else {
         for (ItemServiceBase i1l11iiliiill1l1li1ii1 : list) {
            if (i1l11iiliiill1l1li1ii1 != this.draggedItem && i1l11iiliiill1l1li1ii1.EventMouseButton().toLowerCase(Locale.ROOT).contains(s)) {
               this.filteredItems.add(i1l11iiliiill1l1li1ii1);
            }
         }
      }

      this.lastSearchText = s;
      this.lastServer = s1;
      this.lastSourceSize = list.size();
      if (flag) {
         this.scrollHandler.AnalyticsTracker(0.0);
      }

      return this.filteredItems;
   }

   public List<ItemServiceBase> getSourceItems() {
      List<ItemServiceBase> arraylist = new ArrayList<>();
      if ("Funtime 1.21".equals(this.getServer())) {
         arraylist.addAll(ZenithClient.on23().AnalyticsTracker().DataChangedEvent());
      } else {
         arraylist.addAll(ZenithClient.on23().AnalyticsTracker().EventInjectPlaced());
      }

      arraylist.addAll(ZenithClient.on23().AnalyticsTracker().CrosshairTargetUpdateEvent());
      return arraylist;
   }

   public String getServer() {
      return this.serverSupplier == null ? "HolyWorld" : this.serverSupplier.get();
   }

   public void updateScroll(int var1) {
      this.scrollHandler.ProtocolMessage(Math.max(0.0F, this.getContentHeight(var1) - 258.0F));
      this.scrollHandler.update();
   }

   public void renderVisibleItems(HudDrawContext var1, List<ItemServiceBase> var2, float var3, boolean var4, float var5) {
      int i = this.getRows(var2.size());
      int j = Math.max(0, (int)Math.floor((var3 - 23.0F) / 25.0F));
      int k = Math.min(i - 1, (int)Math.ceil((var3 + 258.0F) / 25.0F));
      int l = j * 5;
      int i1 = Math.min(var2.size(), (k + 1) * 5);

      for (int j1 = l; j1 < i1; j1++) {
         int k1 = j1 % 5;
         int l1 = j1 / 5;
         float f = this.gridX + k1 * 25.0F;
         float f1 = this.gridY + l1 * 25.0F;
         ItemServiceBase i1l11iiliiill1l1li1ii = var2.get(j1);
         float f2 = this.updateItemAnimation(this.itemXAnimations, i1l11iiliiill1l1li1ii, f, var4);
         float f3 = this.updateItemAnimation(this.itemYAnimations, i1l11iiliiill1l1li1ii, f1, var4);
         this.renderItem(var1, i1l11iiliiill1l1li1ii, f2, f3 - var3, var5);
      }
   }

   public float updateItemAnimation(Map<ItemServiceBase, UiAnimation> var1, ItemServiceBase var2, float var3, boolean var4) {
      UiAnimation l1i1illlili = var1.computeIfAbsent(var2, var1x -> new UiAnimation(180L, var3, Easing.PreventActionEvent));
      if (var4) {
         l1i1illlili.setValue(var3);
         return var3;
      } else {
         return l1i1illlili.on23(var3);
      }
   }

   public void renderItem(HudDrawContext var1, ItemServiceBase var2, float var3, float var4, float var5) {
      float f = var3 + 7.5F;
      float f1 = var4 + 7.5F;
      boolean flag = MathUtils.on23(var1.getMouseX(), var1.getMouseY(), var3, var4, 23.0, 23.0);
      float f2 = flag ? 1.0F : 0.48F;
      var1.drawRoundedRect(var3, var4, 23.0F, 23.0F, panelRadius, AutoSborStyle.surface().SprintStateEvent(var5));
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(f, f1);
      var1.getMatrices().scale(0.5F, 0.5F);
      org.zenith.render.LegacyRenderBridge.enableBlend();
      org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
      org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, f2 * var5);
      var1.drawItem(var2.EventInjectHandleInputEvents(), 0, 0);
      org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      var1.getMatrices().popMatrix();
   }

   public void renderScrollBar(HudDrawContext var1, float var2) {
      float f = this.scrollHandler.float261() == 0.0 ? 0.0F : (float)(this.scrollHandler.float260() / this.scrollHandler.float261());
      this.scrollbarHeight = Math.max(258.0F * (258.0F / (float)(258.0 + this.scrollHandler.float261())), 20.0F);
      this.scrollbarHeight = Math.min(258.0F, this.animationScrollHeight.on23(this.scrollbarHeight));
      float f1 = Math.max(1.0F, 258.0F - this.scrollbarHeight);
      this.scrollbarThumbY = this.scrollbarY + f1 * f;
      this.scrollbarThumbY = MathHelper.clamp(this.scrollbarThumbY, this.scrollbarY, this.scrollbarY + 258.0F - this.scrollbarHeight);
      var1.drawRoundedRect(
         this.scrollbarX, this.scrollbarY, 1.0F, 258.0F, CornerRadius.MovementInputEvent(0.5F), AutoSborStyle.textAlpha(10).SprintStateEvent(var2)
      );
      var1.drawRoundedRect(
         this.scrollbarX,
         this.scrollbarThumbY,
         1.0F,
         this.scrollbarHeight,
         CornerRadius.MovementInputEvent(1.0F),
         AutoSborStyle.textAlpha(24).SprintStateEvent(var2)
      );
   }

   public float getContentHeight(int var1) {
      int i = this.getRows(var1);
      return i <= 0 ? 0.0F : (i - 1) * 25.0F + 23.0F;
   }

   public int getRows(int var1) {
      return (int)Math.ceil(var1 / 5.0F);
   }

   public boolean isSearchHovered(double var1, double var3) {
      return MathUtils.on23(var1, var3, this.searchPanelX, this.searchPanelY, 128.0, 23.0);
   }

   public boolean isNotListHovered(double var1, double var3) {
      return !MathUtils.on23(var1, var3, this.gridX, this.gridY, 123.0, 258.0);
   }

   public boolean isScrollbarHovered(double var1, double var3) {
      return this.scrollHandler.float261() > 0.0 && MathUtils.on23(var1, var3, this.scrollbarX - 3.0F, this.scrollbarY, 7.0, 258.0);
   }

   public void updateDraggedScrollbar(double var1) {
      if (this.draggingScrollbar && !(this.scrollHandler.float261() <= 0.0)) {
         float f = Math.max(1.0F, 258.0F - this.scrollbarHeight);
         float f1 = (float)var1 - this.scrollbarY - this.scrollClickOffset;
         float f2 = MathHelper.clamp(f1 / f, 0.0F, 1.0F);
         this.scrollHandler.AnalyticsTracker(-(f2 * this.scrollHandler.float261()));
      }
   }
}
