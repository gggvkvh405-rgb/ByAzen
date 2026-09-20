package org.zenith.rotation;

import java.util.function.Supplier;

public record RotationTask(Rotation var11810, Supplier<Rotation> getX, RotationEasingBase getY) {
   public Rotation map21() {
      return this.var11810;
   }

   public Supplier<Rotation> long104() {
      return this.getX;
   }

   public RotationEasingBase long103() {
      return this.getY;
   }
}
