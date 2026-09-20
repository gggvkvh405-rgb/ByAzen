package org.zenith.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;

public class EventMouseButton extends EventCancellable {
   public final int int95;
   public final int int96;

   public int ContainerScanner() {
      return this.int95;
   }

   public int TridentAimbot() {
      return this.int96;
   }

   public EventMouseButton(int var1, int var2) {
      this.int95 = var1;
      this.int96 = var2;
   }
}
