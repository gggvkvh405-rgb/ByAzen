package org.zenith.core;

import com.darkmagician6.eventapi.EventTarget;
import org.zenith.event.EventTick;

class BaritoneGuard {
   @EventTarget
   public void onUpdate(EventTick var1) {
      BaritoneBridge.float138();
   }
}
