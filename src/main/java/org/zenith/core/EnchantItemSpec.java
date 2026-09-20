package org.zenith.core;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

public class EnchantItemSpec extends ItemSpec {
   public EnchantItemSpec(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean on23(ItemStack var1) {
      if (this.EventRenderScreenHook <= 0) {
         return true;
      }

      ItemEnchantmentsComponent itemenchantmentscomponent = var1.getEnchantments();

      for (Entry<RegistryEntry<Enchantment>> entry : itemenchantmentscomponent.getEnchantmentEntries()) {
         String s = ((RegistryEntry)entry.getKey()).getKey().toString();
         if (s.contains(this.Event18Ext3)) {
            return entry.getIntValue() >= this.EventRenderScreenHook;
         }
      }

      return false;
   }
}
