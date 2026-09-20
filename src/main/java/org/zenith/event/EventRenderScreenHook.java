package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.utility.render.display.base.HudDrawContext;

public class EventRenderScreenHook implements Event {
   public final HudDrawContext val377;

   public HudDrawContext WarpFarm() {
      return this.val377;
   }

   public EventRenderScreenHook(HudDrawContext var1) {
      this.val377 = var1;
   }
}
