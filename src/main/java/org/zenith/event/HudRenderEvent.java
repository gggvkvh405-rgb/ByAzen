package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudRenderEvent implements Event {
   public final CustomDrawContext customDrawContext;
   public final float float22;

   public CustomDrawContext Bot() {
      return this.customDrawContext;
   }

   public float getTickDelta() {
      return this.float22;
   }

   public HudRenderEvent(CustomDrawContext var1, float var2) {
      this.customDrawContext = var1;
      this.float22 = var2;
   }
}
