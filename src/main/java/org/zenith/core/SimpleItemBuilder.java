package org.zenith.core;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;

public class SimpleItemBuilder extends ItemServiceBase {
   protected final ArrayList<ItemSpec> PacketEvent = new ArrayList<>();

   public SimpleItemBuilder(ItemStack var1, String var2, String var3, SoundCueKind var4) {
      super(var1, var2, var3, var4);
   }

   public SimpleItemBuilder(ItemStack var1, String var2, SoundCueKind var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean UiAnimation(ItemStack var1) {
      if (!super.UiAnimation(var1)) {
         return false;
      }

      for (ItemSpec l1iil11li : this.PacketEvent) {
         if (!l1iil11li.on23(var1)) {
            return false;
         }
      }

      return true;
   }

   public void on23(ItemSpec var1) {
      this.PacketEvent.add(var1);
   }

   public ArrayList<ItemSpec> EventTriggerKeyEvent() {
      return this.PacketEvent;
   }
}
