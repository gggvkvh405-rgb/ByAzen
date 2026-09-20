package org.zenith.core;

import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.CraftingScreenHandler;
import org.zenith.ZenithClient;
import org.zenith.module.misc.AutoCraft;

public final class CraftingExecutor {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final int int433 = 0;
   public static final int int434 = 1;
   public static final String string125 = "custom";
   public final AutoCraft autoCraft3;
   public TextLibrary var131 = TextLibrary.call208();

   public CraftingExecutor(AutoCraft var1) {
      this.autoCraft3 = var1;
   }

   public boolean isActive() {
      return !this.var131.call038();
   }

   public String call116() {
      return this.var131.call038() ? "" : this.var131.Easing(this.autoCraft3);
   }

   public void reset() {
      this.var131 = TextLibrary.call208();
   }

   public void call267() {
      String s = this.autoCraft3.call056();
      if (s == null || s.isBlank()) {
         s = "custom";
      }

      this.autoCraft3.call240();
      this.var131 = new TextLibrary(s);
      this.autoCraft3.call117();
      AutoCraft.minecraftClient3.setScreen(null);
   }

   public boolean call152() {
      if (this.var131.call038()) {
         return false;
      }

      if (AutoCraft.minecraftClient3.player != null && AutoCraft.minecraftClient3.world != null) {
         CraftingScreenHandler craftingscreenhandler = AutoCraft.minecraftClient3.player.currentScreenHandler instanceof CraftingScreenHandler craftingscreenhandler1
            ? craftingscreenhandler1
            : null;
         if (craftingscreenhandler != null && this.var131.call011() && this.on23(craftingscreenhandler)) {
            this.int394();
            return true;
         }

         if (craftingscreenhandler == null) {
            if (this.var131.call011()) {
               if (this.on23((CraftingScreenHandler)null)) {
                  this.int394();
                  return true;
               }

               this.var131.call039();
            }

            return true;
         } else {
            ItemStack itemstack = craftingscreenhandler.getSlot(0).getStack();
            if (this.var131.call011() && itemstack.isEmpty()) {
               if (this.on23(craftingscreenhandler)) {
                  this.int394();
                  return true;
               } else {
                  this.var131.call039();
                  return true;
               }
            } else {
               if (!itemstack.isEmpty() && !this.var131.call011()) {
                  this.on23(craftingscreenhandler, itemstack);
               }

               if (this.var131.call011() && this.on23(craftingscreenhandler)) {
                  this.int394();
               }

               return true;
            }
         }
      } else {
         return true;
      }
   }

   public void on23(CraftingScreenHandler var1, ItemStack var2) {
      String s = ColorAnimator(var2.getItem());
      if (!s.isBlank()) {
         String s1 = this.ProfileItemBuilder(var2);
         String[] astring = new String[9];
         String[] astring1 = new String[9];
         int i = 0;

         for (int j = 0; j < 9; j++) {
            ItemStack itemstack = var1.getSlot(1 + j).getStack();
            if (itemstack.isEmpty()) {
               astring[j] = "";
               astring1[j] = "";
            } else {
               astring[j] = ColorAnimator(itemstack.getItem());
               astring1[j] = this.ProfileItemBuilder(itemstack);
               if (!astring[j].isBlank()) {
                  i++;
               }
            }
         }

         if (i >= 2) {
            int k = this.on23(var1, s, s1);
            this.var131.on23(s, s1, astring, astring1, k);
         }
      }
   }

   public boolean on23(CraftingScreenHandler var1) {
      if (!this.var131.call038() && this.var131.call011()) {
         int i = this.on23(var1, this.var131.float275(), this.var131.boolean178());
         return i > this.var131.call408();
      } else {
         return false;
      }
   }

   public int on23(CraftingScreenHandler var1, String var2, String var3) {
      int i = 0;

      for (int j = 0; j < 36; j++) {
         ItemStack itemstack = AutoCraft.minecraftClient3.player.getInventory().getStack(j);
         if (!itemstack.isEmpty() && this.autoCraft3.on23(itemstack, var2, var3)) {
            i += itemstack.getCount();
         }
      }

      if (var1 != null) {
         ItemStack itemstack1 = var1.getCursorStack();
         if (!itemstack1.isEmpty() && this.autoCraft3.on23(itemstack1, var2, var3)) {
            i += itemstack1.getCount();
         }
      }

      return i;
   }

   public String ProfileItemBuilder(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         return var1.get(DataComponentTypes.CUSTOM_NAME) == null ? "" : var1.getName().getString();
      } else {
         return "";
      }
   }

   public void int394() {
      ItemFilterRules iiilili1lli1i11lilillliiii1iii = this.on23(this.var131);
      this.var131 = TextLibrary.call208();
      if (!this.autoCraft3.isEnabledRaw()) {
         this.autoCraft3.call155();
      }

      if (iiilili1lli1i11lilillliiii1iii != null) {
         this.autoCraft3.PacketSendEvent("Craft saved: " + iiilili1lli1i11lilillliiii1iii.getDisplayName());
      } else {
         this.autoCraft3.VisualSettingsStore("Unable to save crafted preset");
      }
   }

   public ItemFilterRules on23(TextLibrary var1) {
      if (var1 != null && !var1.call038() && var1.call011()) {
         String s = var1.string112();
         if (s == null || s.isBlank()) {
            s = "custom";
         }

         String s1 = this.autoCraft3.ProtocolMessage(var1.float275(), var1.boolean178());
         String s2 = this.TradeGuardService(s, s1);
         String s3 = this.CommandManager(s2, var1.float275());
         String s4 = s3;
         int i = 1;

         while (this.autoCraft3.CloudRouter(s, s4) != null) {
            s4 = s3 + "_" + i++;
         }

         ItemFilterRules iiilili1lli1i11lilillliiii1iii = new ItemFilterRules(s4, s, s2);
         iiilili1lli1i11lilillliiii1iii.CosmeticManager(false);
         iiilili1lli1i11lilillliiii1iii.EmotePlayback(false);
         iiilili1lli1i11lilillliiii1iii.HolyWorldClient(var1.float275());
         iiilili1lli1i11lilillliiii1iii.RotationQueue(var1.boolean178());

         for (int j = 0; j < 9; j++) {
            iiilili1lli1i11lilillliiii1iii.Easing(j, var1.EventMouseScrollHook(j));
            iiilili1lli1i11lilillliiii1iii.ColorAnimator(j, var1.EventInteractBlock(j));
         }

         this.autoCraft3.StringCodec(iiilili1lli1i11lilillliiii1iii);
         this.autoCraft3.AnalyticsTracker(s, s4);
         this.autoCraft3.call033();
         ZenithClient.on23().TradeGuardService().save();
         return iiilili1lli1i11lilillliiii1iii;
      } else {
         return null;
      }
   }

   public String TradeGuardService(String var1, String var2) {
      String s = var2 == null ? "" : var2;
      if (s.isBlank()) {
         s = "New Craft";
      }

      String s1 = s;
      int i = 2;

      while (this.BotFeaturesDto(var1, s1)) {
         s1 = s + " " + i++;
      }

      return s1;
   }

   public boolean BotFeaturesDto(String var1, String var2) {
      for (ItemFilterRules iiilili1lli1i11lilillliiii1iii : this.autoCraft3.EventGetFogColorHook(var1)) {
         if (iiilili1lli1i11lilillliiii1iii.getDisplayName().equalsIgnoreCase(var2)) {
            return true;
         }
      }

      return false;
   }

   public String CommandManager(String var1, String var2) {
      String s = var1 == null ? "" : var1.toLowerCase(Locale.ROOT).replace(" ", "_").replaceAll("[^a-z0-9_]+", "");
      if (s.isBlank()) {
         String s1 = var2 == null ? "" : var2;
         int i = s1.indexOf(58);
         if (i != -1 && i + 1 < s1.length()) {
            s1 = s1.substring(i + 1);
         }

         s = s1.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]+", "_");
      }

      if (s.isBlank()) {
         s = "item";
      }

      return "captured_" + s;
   }

   public static String ColorAnimator(Item var0) {
      return var0 != null && var0 != Items.AIR ? Registries.ITEM.getId(var0).toString() : "";
   }
}
