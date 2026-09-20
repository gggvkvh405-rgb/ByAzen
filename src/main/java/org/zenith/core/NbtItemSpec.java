package org.zenith.core;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.zenith.ZenithClient;

public class NbtItemSpec extends ItemSpec {
   public NbtItemSpec(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean on23(ItemStack var1) {
      NbtComponent nbtcomponent = (NbtComponent)var1.get(DataComponentTypes.CUSTOM_DATA);
      NbtCompound customData = nbtcomponent == null ? null : nbtcomponent.copyNbt();
      String enchantmentsKey = ZenithClient.on23().CloudApiClient().soundEvent7() ? "custom-enchantments" : "Enchantments";
      if (nbtcomponent != null
         && customData.getList(enchantmentsKey).isPresent()) {
         NbtList nbtlist = customData.getListOrEmpty(enchantmentsKey);

         for (int i = 0; i < nbtlist.size(); i++) {
            NbtCompound nbtcompound = nbtlist.getCompoundOrEmpty(i);
            String s = nbtcompound.getString(ZenithClient.on23().CloudApiClient().soundEvent7() ? "type" : "id").orElse("");
            int j = nbtcompound.getInt(ZenithClient.on23().CloudApiClient().soundEvent7() ? "level" : "lvl").orElse(0);
            if (s.equals(this.Event18Ext3)) {
               return j >= this.EventRenderScreenHook;
            }
         }
      }

      return false;
   }
}
