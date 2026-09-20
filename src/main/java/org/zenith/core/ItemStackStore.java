package org.zenith.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;

public class ItemStackStore {
   public String string61;
   public String PacketSendEvent;
   public int count;
   public int int359;
   public long long144;
   public int int360;
   public String string91;
   public String string92;

   public ItemStackStore(String var1, String var2, int var3, int var4, long var5, int var7, String var8) {
      this(var1, var2, var3, var4, var5, var7, var8, null);
   }

   public ItemStackStore(String var1, String var2, int var3, int var4, long var5, int var7, String var8, String var9) {
      this.string61 = var1;
      this.PacketSendEvent = var2;
      this.count = var3;
      this.int359 = var4;
      this.long144 = var5;
      this.int360 = var7;
      this.string91 = var8;
      this.string92 = var9;
   }

   public ItemStackStore() {
   }

   public static ItemStackStore on23(ItemStack var0, String var1, int var2, long var3, int var5) {
      String s = Registries.ITEM.getId(var0.getItem()).toString();
      String s1 = ItemRegistry(var0);
      return new ItemStackStore(s, var1, var0.getCount(), var2, var3, var5, s1);
   }

   public static String ItemRegistry(ItemStack var0) {
      try {
         MinecraftClient minecraftclient = MinecraftClient.getInstance();
         ClientWorld clientworld = minecraftclient.world;
         if (clientworld == null) {
            return null;
         }

         RegistryOps registryops = clientworld.getRegistryManager().getOps(JsonOps.INSTANCE);
         DataResult dataresult = ItemStack.CODEC.encodeStart(registryops, var0);
         if (dataresult.result().isPresent()) {
            return ((JsonElement)dataresult.result().get()).toString();
         }
      } catch (Exception var5) {
      }

      return null;
   }

   public ItemStack BlockOverLay() {
      if (this.string91 != null && !this.string91.isEmpty()) {
         try {
            MinecraftClient minecraftclient = MinecraftClient.getInstance();
            ClientWorld clientworld = minecraftclient.world;
            if (clientworld == null) {
               throw new IllegalStateException("World is not loaded");
            }

            JsonElement jsonelement = JsonParser.parseString(this.string91);
            RegistryOps registryops = clientworld.getRegistryManager().getOps(JsonOps.INSTANCE);
            DataResult dataresult = ItemStack.CODEC.decode(registryops, jsonelement);
            if (dataresult.result().isPresent()) {
               return (ItemStack)((Pair)dataresult.result().get()).getFirst();
            }
         } catch (Exception var6) {
         }
      }

      Item item = (Item)Registries.ITEM.get(Identifier.tryParse(this.string61));
      return new ItemStack(item, this.count);
   }

   public JsonObject save() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("itemId", this.string61);
      if (this.PacketSendEvent != null) {
         jsonobject.addProperty("searchName", this.PacketSendEvent);
      }

      jsonobject.addProperty("count", this.count);
      jsonobject.addProperty("slotId", this.int359);
      jsonobject.addProperty("maxSumBuy", this.long144);
      jsonobject.addProperty("countBuy", this.int360);
      if (this.string91 != null) {
         jsonobject.addProperty("componentsJson", this.string91);
      }

      if (this.string92 != null) {
         jsonobject.addProperty("extraSettingsJson", this.string92);
      }

      return jsonobject;
   }

   public void load(JsonObject var1) {
      if (var1.has("itemId")) {
         this.string61 = var1.get("itemId").getAsString();
      }

      if (var1.has("searchName")) {
         this.PacketSendEvent = var1.get("searchName").getAsString();
      }

      if (var1.has("count")) {
         this.count = var1.get("count").getAsInt();
      }

      if (var1.has("slotId")) {
         this.int359 = var1.get("slotId").getAsInt();
      }

      if (var1.has("maxSumBuy")) {
         this.long144 = var1.get("maxSumBuy").getAsLong();
      }

      if (var1.has("countBuy")) {
         this.int360 = var1.get("countBuy").getAsInt();
      }

      if (var1.has("componentsJson")) {
         this.string91 = var1.get("componentsJson").getAsString();
      }

      if (var1.has("extraSettingsJson")) {
         this.string92 = var1.get("extraSettingsJson").getAsString();
      }
   }

   public String BoxShaderRenderer() {
      return this.string61;
   }

   public String EventMouseButton() {
      return this.PacketSendEvent;
   }

   public int getCount() {
      return this.count;
   }

   public int PricedItem() {
      return this.int359;
   }

   public long Predictions() {
      return this.long144;
   }

   public int PostProcessPass() {
      return this.int360;
   }

   public String ShaderESP() {
      return this.string91;
   }

   public String FrameGraphPass() {
      return this.string92;
   }

   public void EventMouseScrollHook(String var1) {
      this.string61 = var1;
   }

   public void EventInteractBlock(String var1) {
      this.PacketSendEvent = var1;
   }

   public void setCount(int var1) {
      this.count = var1;
   }

   public void NbtItemSpec(int var1) {
      this.int359 = var1;
   }

   public void ItemSpec(long var1) {
      this.long144 = var1;
   }

   public void NbtEditor(int var1) {
      this.int360 = var1;
   }

   public void EventTriggerKeyEvent(String var1) {
      this.string91 = var1;
   }

   public void EventInjectHandleInputEvents(String var1) {
      this.string92 = var1;
   }
}
