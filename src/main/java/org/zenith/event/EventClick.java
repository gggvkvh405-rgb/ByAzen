package org.zenith.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import net.minecraft.screen.slot.SlotActionType;

public class EventClick extends EventCancellable {
   public final int a;
   public final int b;
   public final int c;
   public final SlotActionType action;

   public EventClick(int var1, int var2, int var3, SlotActionType var4) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.action = var4;
   }

   public int PricedItem() {
      return this.a;
   }

   public int ContainerScanner() {
      return this.b;
   }

   public SlotActionType HeldItemWatcher() {
      return this.action;
   }
}
