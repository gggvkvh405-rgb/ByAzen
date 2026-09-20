package org.zenith.core;

import net.minecraft.item.ItemStack;

public class PricedItem {
   public final ItemStack itemStack12;
   public final String string105;
   public final int int379;
   public final long long148;
   public final long long149;

   public PricedItem(ItemStack var1, String var2, int var3, long var4) {
      this.itemStack12 = var1 == null ? ItemStack.EMPTY : var1.copy();
      this.string105 = var2 == null ? "" : var2;
      this.int379 = var3;
      this.long148 = var4;
      this.long149 = System.currentTimeMillis();
   }

   public ItemStack EventInjectHandleInputEvents() {
      return this.itemStack12;
   }

   public String call128() {
      return this.string105;
   }

   public int call129() {
      return this.int379;
   }

   public long getPrice() {
      return this.long148;
   }

   public long call452() {
      return this.long149;
   }
}
