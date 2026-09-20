package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.module.Module;

public class ModuleToggleEvent implements Event {
   public final Module module;
   public final boolean boolean48;

   public ModuleToggleEvent(Module var1, boolean var2) {
      this.module = var1;
      this.boolean48 = var2;
   }

   public Module getModule() {
      return this.module;
   }

   public boolean isEnabled() {
      return this.boolean48;
   }
}
