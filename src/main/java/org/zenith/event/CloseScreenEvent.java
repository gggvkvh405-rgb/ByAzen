package org.zenith.event;

import net.minecraft.client.gui.screen.Screen;

public class CloseScreenEvent extends CancellableEvent {
   public final Screen screen;

   public CloseScreenEvent(Screen var1) {
      this.screen = var1;
   }

   public Screen screen() {
      return this.screen;
   }
}
