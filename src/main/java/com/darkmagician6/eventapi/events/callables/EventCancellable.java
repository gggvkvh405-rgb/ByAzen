package com.darkmagician6.eventapi.events.callables;

import com.darkmagician6.eventapi.events.Cancellable;
import com.darkmagician6.eventapi.events.Event;

public abstract class EventCancellable implements Cancellable, Event {
   private boolean cancelled;

   protected EventCancellable() {
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }
}
