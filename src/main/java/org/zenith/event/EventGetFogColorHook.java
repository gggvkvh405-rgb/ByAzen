package org.zenith.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;

public class EventGetFogColorHook extends EventCancellable {
   public float float21;
   public int int92;

   public float Timer() {
      return this.float21;
   }

   public int ItemUseController() {
      return this.int92;
   }

   public void ProfileItemBuilder(float var1) {
      this.float21 = var1;
   }

   public void setColor(int var1) {
      this.int92 = var1;
   }
}
