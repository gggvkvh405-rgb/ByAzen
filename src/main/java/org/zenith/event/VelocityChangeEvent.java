package org.zenith.event;

public class VelocityChangeEvent extends CancellableEvent {
   public int int92;

   public int ItemUseController() {
      return this.int92;
   }

   public void setColor(int var1) {
      this.int92 = var1;
   }

   public VelocityChangeEvent(int var1) {
      this.int92 = var1;
   }
}
