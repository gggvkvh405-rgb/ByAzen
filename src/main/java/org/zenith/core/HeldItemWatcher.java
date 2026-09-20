package org.zenith.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;

public class HeldItemWatcher {
   public static final int int397 = 100;
   public final List<PricedItem> list101 = new ArrayList<>();

   public void on23(ItemStack var1, String var2, int var3, long var4) {
      if (var1 != null && !var1.isEmpty() && var3 > 0) {
         ItemStack itemstack = var1.copy();
         itemstack.setCount(var3);
         this.list101.add(0, new PricedItem(itemstack, var2, var3, Math.max(0L, var4)));

         while (this.list101.size() > 100) {
            this.list101.removeLast();
         }
      }
   }

   public List<PricedItem> call005() {
      return List.copyOf(this.list101);
   }
}
