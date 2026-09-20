package org.zenith.client.screens.autosbor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.autosbor.panels.ItemListPanel;
import org.zenith.client.screens.autosbor.panels.body.SborHeader;
import org.zenith.client.screens.autosbor.panels.body.main.SborInventory;
import org.zenith.client.screens.autosbor.panels.body.main.SborKits;
import org.zenith.client.screens.autosbor.panels.body.main.SborPurchaseHistoryPanel;
import org.zenith.core.Easing;
import org.zenith.core.ItemServiceBase;
import org.zenith.core.ItemStackStore;
import org.zenith.core.MenuScreenId;
import org.zenith.core.ServerConfigStore;
import org.zenith.core.StyledTextBuilder;
import org.zenith.core.TextAccent;
import org.zenith.core.UiAnimation;
import org.zenith.utility.game.other.render.CustomScreen;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class AutoSborScreen extends CustomScreen {
   public static final float leftPanelWidth = 136.0F;
   public static final float rightPanelWidth = 344.0F;
   public static final float panelWidth = 480.0F;
   public static final float panelHeight = 320.0F;
   public static final CornerRadius panelRadius = CornerRadius.MovementInputEvent(10.0F);
   public static final CornerRadius leftPanelRadius = CornerRadius.BotTickEvent(10.0F, 10.0F);
   public static final CornerRadius rightPanelRadius = CornerRadius.VelocityChangeEvent(10.0F, 10.0F);
   public static final long screenAnimationDuration = 180L;
   public static final float screenAnimationEpsilon = 0.001F;
   public static final int inventorySlotCount = 36;
   public static final float defaultMinDurability = 0.8F;
   public static final Font tooltipFont = Fonts.MEDIUM.getFont(6.0F);
   public static final CornerRadius tooltipRadius = CornerRadius.MovementInputEvent(6.0F);
   public static final float tooltipPaddingX = 6.0F;
   public static final float tooltipPaddingY = 4.0F;
   public static final float tooltipCursorOffset = 10.0F;
   public static final float tooltipScreenMargin = 2.0F;
   public final ItemListPanel itemListPanel;
   public final SborHeader sborHeader = new SborHeader();
   public final SborKits sborKits;
   public final SborPurchaseHistoryPanel sborPurchaseHistoryPanel = new SborPurchaseHistoryPanel();
   public final SborInventory sborInventory;
   public final UiAnimation screenAnimation = new UiAnimation(180L, 0.0F, Easing.PreventActionEvent);
   public final long[] slotPrices;
   public final int[] slotCounts;
   public final float[] slotMinDurabilities;
   public ItemServiceBase draggedItem;
   public int draggedSlotIndex = -1;
   public int selectedPriceSlotIndex = -1;
   public float dragOffsetX;
   public float dragOffsetY;
   public long draggedItemPrice = 0L;
   public int draggedItemCount = 1;
   public float draggedItemMinDurability = 0.8F;
   public final Supplier<String> serverSupplier;
   public boolean closing = false;
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public AutoSborScreen(ItemServiceBase[] var1, int[] var2, long[] var3, float[] var4, Supplier<String> var5) {
      this.slotCounts = var2;
      this.slotPrices = var3;
      this.slotMinDurabilities = var4;
      this.serverSupplier = var5;
      this.itemListPanel = new ItemListPanel(var5);
      this.sborKits = new SborKits(var5);
      this.sborInventory = new SborInventory(var1);
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3) {
      float f = (minecraftClient3.getWindow().getScaledWidth() - 480.0F) / 2.0F;
      float f1 = (minecraftClient3.getWindow().getScaledHeight() - 320.0F) / 2.0F;
      float f2 = this.screenAnimation.on23(this.closing ? 0.0F : 1.0F);
      if (this.closing && f2 <= 0.001F) {
         minecraftClient3.setScreen(null);
      } else {
         float f3 = f + 240.0F;
         float f4 = f1 + 160.0F;
         float f5 = 0.94F + 0.06F * f2;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(f3, f4);
         var1.getMatrices().scale(f5, f5);
         var1.getMatrices().translate(-f3, -f4);
         org.zenith.render.LegacyRenderBridge.enableBlend();
         org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
         boolean flag = this.isItemSettingsVisible();
         this.renderPanels(var1, f, f1, f2);
         this.itemListPanel.render(var1, f, f1, f2);
         this.sborHeader.render(var1, f, f1, f2);
         this.sborInventory.render(var1, f, f1, var2, var3, this.slotCounts, this.selectedPriceSlotIndex, this.isDurabilitySettingsVisible(), f2);
         this.sborPurchaseHistoryPanel.render(var1, f, f1, flag, f2);
         this.sborKits.render(var1, f, f1, flag, f2);
         this.renderDraggedItem(var1, var2, var3, f2);
         this.renderHoverTooltip(var1, var2, var3, f2);
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         var1.getMatrices().popMatrix();
      }
   }

   public void renderHoverTooltip(HudDrawContext var1, float var2, float var3, float var4) {
      if (!this.closing && this.draggedItem == null) {
         String s = this.getHoveredItemName(var2, var3);
         if (s != null && !s.isBlank()) {
            float f = tooltipFont.width(s) + 12.0F;
            float f1 = tooltipFont.height() + 8.0F;
            float f2 = var2 + 10.0F;
            float f3 = var3 - f1 - 5.0F;
            float f4 = minecraftClient3.getWindow().getScaledWidth();
            if (f2 + f > f4 - 2.0F) {
               f2 = f4 - 2.0F - f;
            }

            if (f3 < 2.0F) {
               f3 = var3 + 10.0F;
            }

            AutoSborStyle.drawBlur(var1, f2, f3, f, f1, tooltipRadius, var4);
            var1.drawRoundedRect(f2, f3, f, f1, tooltipRadius, AutoSborStyle.panelBackground().SprintStateEvent(var4));
            var1.drawRoundedBorder(f2, f3, f, f1, -0.1F, tooltipRadius, AutoSborStyle.fieldBorder().SprintStateEvent(var4));
            var1.drawText(tooltipFont, s, f2 + 6.0F, f3 + 4.0F, AutoSborStyle.text().SprintStateEvent(var4));
         }
      }
   }

   public String getHoveredItemName(float var1, float var2) {
      ItemServiceBase i1l11iiliiill1l1li1ii = this.itemListPanel.getItemAt(var1, var2);
      if (i1l11iiliiill1l1li1ii != null) {
         return i1l11iiliiill1l1li1ii.getDisplayName();
      }

      int i = this.sborInventory.getSlotIndex(var1, var2);
      if (i < 0) {
         return null;
      }

      ItemServiceBase i1l11iiliiill1l1li1ii1 = this.sborInventory.getItem(i);
      if (i1l11iiliiill1l1li1ii1 == null) {
         return null;
      }

      String s = i1l11iiliiill1l1li1ii1.getDisplayName();
      if (s != null && !s.isBlank()) {
         if (this.isStackable(i1l11iiliiill1l1li1ii1)) {
            int j = this.getSlotCount(i, i1l11iiliiill1l1li1ii1);
            if (j > 1) {
               return s + " x" + j;
            }
         }

         return s;
      } else {
         return null;
      }
   }

   public void renderPanels(HudDrawContext var1, float var2, float var3, float var4) {
      AutoSborStyle.drawBlur(var1, var2, var3, 480.0F, 320.0F, panelRadius, var4);
      var1.drawRoundedRect(var2, var3, 136.0F, 320.0F, leftPanelRadius, AutoSborStyle.leftBackground().SprintStateEvent(var4));
      var1.drawRoundedRect(var2 + 136.0F, var3, 344.0F, 320.0F, rightPanelRadius, AutoSborStyle.rightBackground().SprintStateEvent(var4));
      var1.drawRoundedRect(var2, var3, 480.0F, 320.0F, panelRadius, AutoSborStyle.headerSurface().EventHookWorldRender(20).SprintStateEvent(var4));
   }

   @Override
   public void onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (!this.closing && this.screenAnimation.isDone()) {
         if (var5 == MenuScreenId.call004) {
            ServerConfigStore l1ili1lllx = this.sborKits.getDeleteKitAt(var1, var3);
            if (l1ili1lllx != null) {
               this.deleteKit(l1ili1lllx);
               return;
            }

            l1ili1lllx = this.sborKits.getKitAt(var1, var3);
            if (l1ili1lllx != null) {
               this.loadKit(l1ili1lllx);
               return;
            }

            ItemServiceBase i1l11iiliiill1l1li1ii = this.itemListPanel.getItemAt(var1, var3);
            if (i1l11iiliiill1l1li1ii != null) {
               this.startDragging(i1l11iiliiill1l1li1ii, -1, true);
               return;
            }

            int i = this.sborInventory.getSlotIndex(var1, var3);
            if (i >= 0) {
               i1l11iiliiill1l1li1ii = this.sborInventory.getItem(i);
               if (i1l11iiliiill1l1li1ii != null) {
                  this.syncCurrentItemSettings();
                  this.sborInventory.clearSlot(i);
                  this.startDragging(i1l11iiliiill1l1li1ii, i, false);
                  return;
               }

               this.clearSelectedItem();
               return;
            }

            if (this.sborInventory.isCreateHovered(var1, var3)) {
               this.saveKit();
               return;
            }
         }

         if (var5 == MenuScreenId.call111) {
            int j = this.sborInventory.getSlotIndex(var1, var3);
            if (j >= 0) {
               ItemServiceBase i1l11iiliiill1l1li1ii1 = this.sborInventory.getItem(j);
               if (i1l11iiliiill1l1li1ii1 != null) {
                  this.syncCurrentItemSettings();
                  this.selectItem(j, i1l11iiliiill1l1li1ii1);
               } else {
                  this.clearSelectedItem();
               }

               return;
            }
         }

         if (!this.sborInventory.onMouseClicked(var1, var3, var5)
            && !this.itemListPanel.onMouseClicked(var1, var3, var5)
            && !this.sborHeader.onMouseClicked(var1, var3, var5)) {
            super.onMouseClicked(var1, var3, var5);
         }
      }
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      if (!this.closing) {
         this.itemListPanel.onMouseReleased(var5);
         this.sborInventory.onMouseReleased(var1, var3, var5);
         if (var5 == MenuScreenId.call004 && this.draggedItem != null) {
            int i = this.sborInventory.getSlotIndex(var1, var3);
            if (i >= 0) {
               this.sborInventory.setItem(i, this.draggedItem);
               this.slotPrices[i] = this.draggedItemPrice;
               this.slotCounts[i] = this.draggedItemCount;
               this.slotMinDurabilities[i] = this.isDurabilityItem(this.draggedItem) ? this.draggedItemMinDurability : 0.0F;
               this.selectItem(i, this.draggedItem);
               this.stopDragging();
            } else {
               if (this.draggedSlotIndex >= 0 && !this.itemListPanel.isListHovered(var1, var3)) {
                  this.sborInventory.setItem(this.draggedSlotIndex, this.draggedItem);
                  this.slotPrices[this.draggedSlotIndex] = this.draggedItemPrice;
                  this.slotCounts[this.draggedSlotIndex] = this.draggedItemCount;
                  this.slotMinDurabilities[this.draggedSlotIndex] = this.isDurabilityItem(this.draggedItem) ? this.draggedItemMinDurability : 0.0F;
                  this.selectItem(this.draggedSlotIndex, this.draggedItem);
               }

               this.stopDragging();
            }
         } else {
            super.onMouseReleased(var1, var3, var5);
         }
      }
   }

   @Override
   public void onMouseDragged(double var1, double var3, MenuScreenId var5, double var6, double var8) {
      if (!this.closing) {
         this.itemListPanel.onMouseDragged(var3, var5);
         super.onMouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.closing || !this.screenAnimation.isDone()) {
         return false;
      } else if (this.sborInventory.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (this.itemListPanel.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (this.sborHeader.keyPressed(keyCode, scanCode, modifiers)) {
         this.syncCurrentItemSettings();
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public boolean charTyped(char chr, int modifiers) {
      if (this.closing || !this.screenAnimation.isDone()) {
         return false;
      } else if (this.sborInventory.charTyped(chr, modifiers)) {
         return true;
      } else if (this.itemListPanel.charTyped(chr, modifiers)) {
         return true;
      } else if (this.sborHeader.charTyped(chr, modifiers)) {
         this.syncCurrentItemSettings();
         return true;
      } else {
         return super.charTyped(chr, modifiers);
      }
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   public boolean shouldPause() {
      return false;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.closing || !this.screenAnimation.isDone()) {
         return false;
      } else if (this.sborPurchaseHistoryPanel.mouseScrolled(mouseX, mouseY, verticalAmount)) {
         return true;
      } else if (this.sborKits.mouseScrolled(mouseX, mouseY, verticalAmount)) {
         return true;
      } else {
         return this.itemListPanel.mouseScrolled(mouseX, mouseY, verticalAmount) ? true : super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      }
   }

   public void close() {
      if (!this.closing) {
         this.syncCurrentItemSettings();
         this.closing = true;
         this.stopDragging();
      }
   }

   public void renderDraggedItem(HudDrawContext var1, float var2, float var3, float var4) {
      if (this.draggedItem != null) {
         this.itemListPanel.renderDraggedItem(var1, this.draggedItem, var2 - this.dragOffsetX, var3 - this.dragOffsetY, var4);
      }
   }

   public void startDragging(ItemServiceBase var1, int var2, boolean var3) {
      this.syncCurrentItemSettings();
      this.draggedItem = var1;
      this.draggedSlotIndex = var2;
      this.draggedItemPrice = var2 >= 0 ? this.slotPrices[var2] : 0L;
      this.draggedItemCount = var2 >= 0 ? this.getSlotCount(var2, var1) : this.getDefaultCount(var1);
      this.draggedItemMinDurability = var2 >= 0 ? this.getSlotMinDurability(var2) : 0.8F;
      if (var2 >= 0) {
         this.slotPrices[var2] = 0L;
         this.slotCounts[var2] = 0;
         this.slotMinDurabilities[var2] = 0.0F;
      }

      this.selectedPriceSlotIndex = -1;
      this.hideSelectedItem();
      if (var3) {
         this.itemListPanel.setDraggedItem(var1);
      }

      this.dragOffsetX = this.itemListPanel.getItemPanelSize() / 2.0F;
      this.dragOffsetY = this.itemListPanel.getItemPanelSize() / 2.0F;
   }

   public void stopDragging() {
      this.draggedItem = null;
      this.draggedSlotIndex = -1;
      this.itemListPanel.setDraggedItem(null);
   }

   public void syncCurrentItemSettings() {
      long i = this.sborHeader.getPrice();
      int j = this.sborInventory.getCount();
      if (this.draggedItem != null) {
         this.draggedItemPrice = i;
         this.draggedItemCount = this.isStackable(this.draggedItem) ? j : this.getDefaultCount(this.draggedItem);
         this.draggedItemMinDurability = this.isDurabilityItem(this.draggedItem) ? this.sborInventory.getMinDurability() : 0.8F;
      } else if (this.selectedPriceSlotIndex >= 0 && this.selectedPriceSlotIndex < this.slotPrices.length) {
         ItemServiceBase i1l11iiliiill1l1li1ii = this.sborInventory.getItem(this.selectedPriceSlotIndex);
         if (i1l11iiliiill1l1li1ii == null) {
            return;
         }

         this.slotPrices[this.selectedPriceSlotIndex] = i;
         this.slotCounts[this.selectedPriceSlotIndex] = this.isStackable(i1l11iiliiill1l1li1ii) ? j : 0;
         this.slotMinDurabilities[this.selectedPriceSlotIndex] = this.isDurabilityItem(i1l11iiliiill1l1li1ii) ? this.sborInventory.getMinDurability() : 0.0F;
      }
   }

   public void clearSelectedItem() {
      this.syncCurrentItemSettings();
      this.selectedPriceSlotIndex = -1;
      this.hideSelectedItem();
   }

   public void selectItem(int var1, ItemServiceBase var2) {
      this.selectedPriceSlotIndex = var1;
      this.sborHeader.setSelectedItem(var2);
      this.sborHeader.setPrice(this.slotPrices[var1]);
      this.sborInventory.setCountMax(this.getCountMax(var2));
      this.sborInventory.setCount(this.getSlotCount(var1, var2));
      this.sborInventory.setCountVisible(this.isStackable(var2));
      this.sborInventory.setMinDurability(this.getSlotMinDurability(var1));
      this.sborInventory.setDurabilityVisible(this.isDurabilityItem(var2));
   }

   public void hideSelectedItem() {
      this.sborHeader.setSelectedItem(null);
      this.sborHeader.setPrice(0L);
      this.sborInventory.setCountMax(64);
      this.sborInventory.setCountVisible(false);
      this.sborInventory.setMinDurability(0.8F);
      this.sborInventory.setDurabilityVisible(false);
   }

   public void saveKit() {
      this.syncCurrentItemSettings();
      String s = this.sborInventory.getInventoryName();
      if (s.isBlank()) {
         StyledTextBuilder.on23(TextAccent.call013, "Введите имя кита");
      } else {
         String s1 = this.getServer();
         if (ZenithClient.on23().ItemSpec().SimpleItemBuilder(s, s1) != null) {
            StyledTextBuilder.on23(TextAccent.call013, "Кит с именем '" + s + "' уже существует");
         } else {
            ArrayList arraylist = new ArrayList();

            for (int i = 0; i < 36; i++) {
               ItemServiceBase i1l11iiliiill1l1li1ii = this.sborInventory.getItem(i);
               if (i1l11iiliiill1l1li1ii != null && !i1l11iiliiill1l1li1ii.EventInjectHandleInputEvents().isEmpty()) {
                  ItemStackStore ll1l11l11l1lli1 = ItemStackStore.on23(
                     i1l11iiliiill1l1li1ii.EventInjectHandleInputEvents(),
                     i1l11iiliiill1l1li1ii.EventMouseButton(),
                     i,
                     this.slotPrices[i],
                     this.getSlotCount(i, i1l11iiliiill1l1li1ii)
                  );
                  if (this.isDurabilityItem(i1l11iiliiill1l1li1ii)) {
                     ll1l11l11l1lli1.EventInjectHandleInputEvents(this.createDurabilitySettings(this.getSlotMinDurability(i)));
                  }

                  arraylist.add(ll1l11l11l1lli1);
               }
            }

            if (arraylist.isEmpty()) {
               StyledTextBuilder.on23(TextAccent.call013, "Кит пуст");
            } else {
               ServerConfigStore l1ili1lll = ZenithClient.on23().ItemSpec().on23(s, arraylist, s1);
               if (l1ili1lll != null) {
                  StyledTextBuilder.on23(TextAccent.call002, "Кит '" + s + "' сохранен");
               } else {
                  StyledTextBuilder.on23(TextAccent.call013, "Не удалось сохранить кит '" + s + "'");
               }
            }
         }
      }
   }

   public void deleteKit(ServerConfigStore var1) {
      if (var1 != null) {
         String s = var1.getName();
         if (ZenithClient.on23().ItemSpec().UiAnimation(var1)) {
            this.sborKits.removePreview(var1);
            StyledTextBuilder.on23(TextAccent.call002, "Кит '" + s + "' удален");
         } else {
            StyledTextBuilder.on23(TextAccent.call013, "Не удалось удалить кит '" + s + "'");
         }
      }
   }

   public void loadKit(ServerConfigStore var1) {
      this.stopDragging();
      this.selectedPriceSlotIndex = -1;
      this.hideSelectedItem();
      Arrays.fill(this.slotPrices, 0L);
      Arrays.fill(this.slotCounts, 0);
      Arrays.fill(this.slotMinDurabilities, 0.0F);

      for (int i = 0; i < 36; i++) {
         this.sborInventory.clearSlot(i);
      }

      for (ItemStackStore ll1l11l11l1lli1 : var1.WorldTweaks()) {
         int j = ll1l11l11l1lli1.PricedItem();
         if (j >= 0 && j < 36) {
            ItemServiceBase i1l11iiliiill1l1li1ii = this.findItemBuy(ll1l11l11l1lli1);
            if (i1l11iiliiill1l1li1ii != null && !i1l11iiliiill1l1li1ii.EventInjectHandleInputEvents().isEmpty()) {
               this.sborInventory.setItem(j, i1l11iiliiill1l1li1ii);
               this.slotPrices[j] = ll1l11l11l1lli1.Predictions();
               this.slotCounts[j] = ll1l11l11l1lli1.PostProcessPass();
               this.slotMinDurabilities[j] = this.isDurabilityItem(i1l11iiliiill1l1li1ii) ? this.readMinDurability(ll1l11l11l1lli1) : 0.0F;
            }
         }
      }
   }

   public ItemServiceBase findItemBuy(ItemStackStore var1) {
      ArrayList<ItemServiceBase> arraylist = new ArrayList<>();
      if ("Funtime 1.21".equals(this.getServer())) {
         arraylist.addAll(ZenithClient.on23().AnalyticsTracker().DataChangedEvent());
      } else {
         arraylist.addAll(ZenithClient.on23().AnalyticsTracker().EventInjectPlaced());
      }

      arraylist.addAll(ZenithClient.on23().AnalyticsTracker().CrosshairTargetUpdateEvent());

      for (ItemServiceBase i1l11iiliiill1l1li1ii : arraylist) {
         if (i1l11iiliiill1l1li1ii.EventMouseButton().equals(var1.EventMouseButton())) {
            if (var1.BoxShaderRenderer() == null || var1.BoxShaderRenderer().isBlank()) {
               return i1l11iiliiill1l1li1ii;
            }

            if (Registries.ITEM
               .getId(i1l11iiliiill1l1li1ii.EventInjectHandleInputEvents().getItem())
               .toString()
               .equals(var1.BoxShaderRenderer())) {
               return i1l11iiliiill1l1li1ii;
            }
         }
      }

      return null;
   }

   public String getServer() {
      return this.serverSupplier == null ? "HolyWorld" : this.serverSupplier.get();
   }

   public String createDurabilitySettings(float var1) {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("maxDamage", var1);
      return jsonobject.toString();
   }

   public float readMinDurability(ItemStackStore var1) {
      if (var1.FrameGraphPass() != null && !var1.FrameGraphPass().isBlank()) {
         try {
            JsonObject jsonobject = JsonParser.parseString(var1.FrameGraphPass()).getAsJsonObject();
            if (jsonobject.has("maxDamage")) {
               return jsonobject.get("maxDamage").getAsFloat();
            }
         } catch (Exception var3) {
         }

         return 0.8F;
      } else {
         return 0.8F;
      }
   }

   public int getSlotCount(int var1, ItemServiceBase var2) {
      if (!this.isStackable(var2)) {
         return this.getDefaultCount(var2);
      }

      int i = var1 >= 0 && var1 < this.slotCounts.length ? this.slotCounts[var1] : 0;
      return i > 0 ? i : this.getDefaultCount(var2);
   }

   public int getDefaultCount(ItemServiceBase var1) {
      return var1 != null && !var1.EventInjectHandleInputEvents().isEmpty() ? Math.max(1, var1.EventInjectHandleInputEvents().getCount()) : 1;
   }

   public int getCountMax(ItemServiceBase var1) {
      if (var1 != null && !var1.EventInjectHandleInputEvents().isEmpty()) {
         return this.isPotion(var1.EventInjectHandleInputEvents().getItem())
            ? 64
            : Math.min(64, Math.max(1, var1.EventInjectHandleInputEvents().getMaxCount()));
      } else {
         return 64;
      }
   }

   public float getSlotMinDurability(int var1) {
      if (var1 >= 0 && var1 < this.slotMinDurabilities.length) {
         float f = this.slotMinDurabilities[var1];
         return f > 0.0F ? f : 0.8F;
      } else {
         return 0.8F;
      }
   }

   public boolean isStackable(ItemServiceBase var1) {
      return var1 != null && !var1.EventInjectHandleInputEvents().isEmpty()
         ? var1.EventInjectHandleInputEvents().getMaxCount() > 1 || this.isPotion(var1.EventInjectHandleInputEvents().getItem())
         : false;
   }

   public boolean isDurabilityItem(ItemServiceBase var1) {
      if (var1 != null && !var1.EventInjectHandleInputEvents().isEmpty()) {
         return "Шлем Солнца".equals(var1.EventMouseButton()) ? false : var1.EventInjectHandleInputEvents().getMaxDamage() > 0;
      } else {
         return false;
      }
   }

   public boolean isPotion(Item var1) {
      return var1 == Items.POTION || var1 == Items.SPLASH_POTION || var1 == Items.LINGERING_POTION;
   }

   public boolean isCountSettingsVisible() {
      return this.selectedPriceSlotIndex >= 0 && this.isStackable(this.sborInventory.getItem(this.selectedPriceSlotIndex));
   }

   public boolean isDurabilitySettingsVisible() {
      return this.selectedPriceSlotIndex >= 0 && this.isDurabilityItem(this.sborInventory.getItem(this.selectedPriceSlotIndex));
   }

   public boolean isItemSettingsVisible() {
      return this.isCountSettingsVisible() || this.isDurabilitySettingsVisible();
   }
}
