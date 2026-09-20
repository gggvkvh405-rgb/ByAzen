package org.zenith.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;

public class EventMouseScrollHook extends EventCancellable {
   public final double x;
   public final double y;

   public EventMouseScrollHook(double var1, double var3) {
      this.x = var1;
      this.y = var3;
   }

   public double TapeMouse() {
      return this.x;
   }
}
