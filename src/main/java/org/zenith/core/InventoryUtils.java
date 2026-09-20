package org.zenith.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class InventoryUtils {
   public static final int Criticals = 36;
   public static final int FakeLag = 4;
   public final List<ItemStack> OffHandManager;
   public final List<ItemStack> Reach;
   public final ItemStack RotationRecorder;

   public InventoryUtils(List<ItemStack> var1, List<ItemStack> var2, ItemStack var3) {
      this.OffHandManager = List.copyOf(on23(var1, 36));
      this.Reach = List.copyOf(on23(var2, 4));
      this.RotationRecorder = var3 == null ? ItemStack.EMPTY : var3.copy();
   }

   public List<ItemStack> EventItemRenderHook() {
      return this.OffHandManager;
   }

   public List<ItemStack> HudRenderEvent() {
      return this.Reach;
   }

   public ItemStack EventHookWorldRender() {
      return this.RotationRecorder;
   }

   public ItemStack UiAnimation(int var1) {
      return var1 >= 0 && var1 < 9 ? this.OffHandManager.get(var1) : ItemStack.EMPTY;
   }

   public ItemStack Easing(int var1) {
      int i = 9 + var1;
      return var1 >= 0 && i < 36 ? this.OffHandManager.get(i) : ItemStack.EMPTY;
   }

   public int Event18Ext3() {
      int i = 0;

      for (int j = 9; j < 36; j++) {
         if (!this.OffHandManager.get(j).isEmpty()) {
            i++;
         }
      }

      return i;
   }

   public int EventRenderScreenHook() {
      int i = 0;

      for (int j = 0; j < 9; j++) {
         if (!this.OffHandManager.get(j).isEmpty()) {
            i++;
         }
      }

      return i;
   }

   public JsonObject on23(UUID var1, long var2) {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("streamId", var1.toString());
      jsonobject.addProperty("seq", var2);
      jsonobject.add("main", on23(this.OffHandManager));
      jsonobject.add("armor", on23(this.Reach));
      if (!this.RotationRecorder.isEmpty()) {
         jsonobject.add("offhand", on23(this.RotationRecorder, null));
      }

      return jsonobject;
   }

   public static InventoryUtils UiAnimation(JsonObject var0) {
      if (var0 == null) {
         return null;
      }

      try {
         List<ItemStack> list = on23(var0.get("main"), 36);
         List<ItemStack> list1 = on23(var0.get("armor"), 4);
         ItemStack itemstack = var0.has("offhand") ? on23(var0.get("offhand")) : ItemStack.EMPTY;
         return new InventoryUtils(list, list1, itemstack);
      } catch (RuntimeException runtimeexception) {
         return null;
      }
   }

   public static InventoryUtils GameMessageEvent() {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient != null && minecraftclient.player != null) {
         PlayerInventory playerinventory = minecraftclient.player.getInventory();
         List<ItemStack> armor = new ArrayList<>(4);
         for (int slot = 36; slot < 40; slot++) {
            armor.add(playerinventory.getStack(slot));
         }
         return new InventoryUtils(
            playerinventory.getMainStacks(),
            armor,
            playerinventory.getStack(PlayerInventory.OFF_HAND_SLOT)
         );
      } else {
         return null;
      }
   }

   public int PacketEvent() {
      int i = 1;

      for (ItemStack itemstack : this.OffHandManager) {
         i = 31 * i + Easing(itemstack);
      }

      for (ItemStack itemstack1 : this.Reach) {
         i = 31 * i + Easing(itemstack1);
      }

      return 31 * i + Easing(this.RotationRecorder);
   }

   public static int Easing(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         Identifier identifier = Registries.ITEM.getId(var0.getItem());
         int i = 31 * identifier.hashCode() + var0.getCount();
         return 31 * i + Boolean.hashCode(var0.hasGlint());
      } else {
         return 0;
      }
   }

   public static JsonArray on23(List<ItemStack> var0) {
      JsonArray jsonarray = new JsonArray();

      for (int i = 0; i < var0.size(); i++) {
         ItemStack itemstack = var0.get(i);
         if (itemstack != null && !itemstack.isEmpty()) {
            jsonarray.add(on23(itemstack, Integer.valueOf(i)));
         }
      }

      return jsonarray;
   }

   public static JsonObject on23(ItemStack var0, Integer var1) {
      Identifier identifier = Registries.ITEM.getId(var0.getItem());
      JsonObject jsonobject = new JsonObject();
      if (var1 != null) {
         jsonobject.addProperty("slot", var1);
      }

      jsonobject.addProperty("itemId", identifier.toString());
      jsonobject.addProperty("count", Math.max(1, Math.min(127, var0.getCount())));
      jsonobject.addProperty("glint", var0.hasGlint());
      return jsonobject;
   }

   public static List<ItemStack> on23(JsonElement var0, int var1) {
      List<ItemStack> list = ColorAnimator(var1);
      if (var0 != null && var0.isJsonArray()) {
         for (JsonElement jsonelement : var0.getAsJsonArray()) {
            if (jsonelement.isJsonObject()) {
               JsonObject jsonobject = jsonelement.getAsJsonObject();
               int i = jsonobject.has("slot") ? jsonobject.get("slot").getAsInt() : -1;
               if (i >= 0 && i < var1) {
                  list.set(i, on23(jsonobject));
               }
            }
         }

         return list;
      } else {
         return list;
      }
   }

   public static ItemStack on23(JsonElement var0) {
      if (var0 != null && var0.isJsonObject()) {
         JsonObject jsonobject = var0.getAsJsonObject();
         Identifier identifier = AnalyticsTracker(jsonobject.has("itemId") ? jsonobject.get("itemId").getAsString() : "");
         if (identifier == null) {
            return ItemStack.EMPTY;
         }

         Item item = (Item)Registries.ITEM.get(identifier);
         if (item == Items.AIR) {
            return ItemStack.EMPTY;
         }

         ItemStack itemstack = new ItemStack(item);
         if (jsonobject.has("count")) {
            itemstack.setCount(Math.max(1, Math.min(127, jsonobject.get("count").getAsInt())));
         }

         if (jsonobject.has("glint") && jsonobject.get("glint").getAsBoolean()) {
            itemstack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
         }

         return itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public static List<ItemStack> ColorAnimator(int var0) {
      List<ItemStack> arraylist = new ArrayList<>(var0);

      while (arraylist.size() < var0) {
         arraylist.add(ItemStack.EMPTY);
      }

      return arraylist;
   }

   public static Identifier AnalyticsTracker(String var0) {
      if (var0 == null) {
         return null;
      }

      String s = var0.trim();
      if (s.isEmpty()) {
         return null;
      }

      if (!s.contains(":")) {
         s = "minecraft:" + s;
      }

      return Identifier.tryParse(s);
   }

   public static List<ItemStack> on23(List<ItemStack> var0, int var1) {
      ArrayList arraylist = new ArrayList(var1);
      if (var0 != null) {
         for (ItemStack itemstack : var0) {
            arraylist.add(itemstack == null ? ItemStack.EMPTY : itemstack.copy());
         }
      }

      while (arraylist.size() < var1) {
         arraylist.add(ItemStack.EMPTY);
      }

      return arraylist.size() > var1 ? arraylist.subList(0, var1) : arraylist;
   }
}
