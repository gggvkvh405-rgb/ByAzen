package org.zenith.core;

import net.minecraft.item.ItemStack;

class HudInvEntry {
   final String call017;
   final String call001;
   final ItemStack call210;
   final HudInvSortMode call024;
   final long call064;
   final long call032;

   public HudInvEntry(String var1, String var2, ItemStack var3, HudInvSortMode var4, long var5, long var7) {
      this.call017 = var1;
      this.call001 = var2;
      this.call210 = var3;
      this.call024 = var4;
      this.call064 = var5;
      this.call032 = var7;
   }

   static HudInvEntry on23(String var0, String var1, ItemStack var2, long var3, long var5) {
      return new HudInvEntry(var0, var1, var2, HudInvSortMode.call010, var3, var5);
   }

   static HudInvEntry UiAnimation(String var0, String var1, ItemStack var2, long var3, long var5) {
      return new HudInvEntry(var0, var1, var2, HudInvSortMode.call014, var3, var5);
   }

   float NbtItemSpec(long var1) {
      long i = switch (this.call024) {
         case call010 -> Math.max(0L, this.call064 + this.call032 - var1);
         case call014 -> Math.max(0L, this.call064 + this.call032 - System.nanoTime());
      };
      return this.call032 <= 0L ? 0.0F : (float)i / (float)this.call032;
   }

   boolean EnchantItemSpec(long var1) {
      return switch (this.call024) {
         case call010 -> var1 >= this.call064 + this.call032;
         case call014 -> System.nanoTime() >= this.call064 + this.call032;
      };
   }

   int SimpleItemBuilder(long var1) {
      long i = switch (this.call024) {
         case call010 -> Math.max(0L, this.call064 + this.call032 - var1);
         case call014 -> Math.max(0L, this.call064 + this.call032 - System.nanoTime());
      };
      return this.call024 == HudInvSortMode.call010 ? (int)(i / 20L) : (int)(i / 1000000000L);
   }
}
