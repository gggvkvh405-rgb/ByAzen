package org.zenith.event;

import net.minecraft.entity.Entity;

public final class AttackEntityEvent extends CancellableEvent {
   public final Entity target;
   public final AttackEntityEvent.on23 phase;

   public AttackEntityEvent(Entity var1, AttackEntityEvent.on23 var2) {
      this.target = var1;
      this.phase = var2;
   }

   public AttackEntityEvent.on23 ElytraTarget() {
      return this.phase;
   }

   public Entity ElytraMotion() {
      return this.target;
   }

   public enum on23 {
      call185,
      call077;
   }
}
