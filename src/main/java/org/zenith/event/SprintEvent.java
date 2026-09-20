package org.zenith.event;

import org.zenith.rotation.Rotation;

public class SprintEvent extends CancellableEvent {
   public boolean boolean163;
   public float float21;
   public Rotation var1184;

   public boolean Strafe() {
      return this.boolean163;
   }

   public float Timer() {
      return this.float21;
   }

   public Rotation Velocity() {
      return this.var1184;
   }

   public void ProfileItemBuilder(boolean var1) {
      this.boolean163 = var1;
   }

   public void ProfileItemBuilder(float var1) {
      this.float21 = var1;
   }

   public void on23(Rotation var1) {
      this.var1184 = var1;
   }

   public SprintEvent(boolean var1, float var2, Rotation var3) {
      this.boolean163 = var1;
      this.float21 = var2;
      this.var1184 = var3;
   }
}
