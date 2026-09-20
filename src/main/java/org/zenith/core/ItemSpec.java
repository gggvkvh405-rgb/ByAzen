package org.zenith.core;

import net.minecraft.item.ItemStack;

public abstract class ItemSpec {
   protected final String EventHookWorldRender;
   protected final String Event18Ext3;
   protected int EventRenderScreenHook;

   public abstract boolean on23(ItemStack var1);

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + " [checked=" + this.Event18Ext3 + ", level=" + this.EventRenderScreenHook + "]";
   }

   public ItemSpec(String var1, String var2, int var3) {
      this.EventHookWorldRender = var1;
      this.Event18Ext3 = var2;
      this.EventRenderScreenHook = var3;
   }

   public String getName() {
      return this.EventHookWorldRender;
   }

   public String EventMouseScrollHook() {
      return this.Event18Ext3;
   }

   public int EventInteractBlock() {
      return this.EventRenderScreenHook;
   }

   public void on23(int var1) {
      this.EventRenderScreenHook = var1;
   }
}
