package org.zenith.event;

public class SprintStateEvent extends CancellableEvent {
   public boolean state;

   public SprintStateEvent(boolean var1) {
      this.state = var1;
   }

   public boolean Speed() {
      return this.state;
   }

   public void PotionItemBuilder(boolean var1) {
      this.state = var1;
   }
}
