package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.slot.Slot;

public class EventRender implements Event {
   public final DrawContext context;
   public final Slot slot;
   public final int x;
   public final int y;

   public EventRender(DrawContext var1, Slot var2, int var3, int var4) {
      this.context = var1;
      this.slot = var2;
      this.x = var3;
      this.y = var4;
   }

   public Slot AutoLoot() {
      return this.slot;
   }
}
