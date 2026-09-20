package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.rotation.Rotation;

public record EventImpl(Rotation var1185) implements Event {
   public Rotation rotation() {
      return this.var1185;
   }
}
