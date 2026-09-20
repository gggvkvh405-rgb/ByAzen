package org.zenith.core;

import java.util.function.BooleanSupplier;

public final class TimedStep {
   public int ticks;
   public TickGate var23;
   public BooleanSupplier booleanSupplier;
   public int int171;

   public TimedStep(int var1, TickGate var2, BooleanSupplier var3, int var4) {
      this.ticks = var1;
      this.var23 = var2;
      this.booleanSupplier = var3;
      this.int171 = var4;
   }

   public int UiAnimation(TimedStep var1) {
      return Integer.compare(var1.int331(), this.int331());
   }

   public void int332() {
      this.ticks--;
   }

   public int getTicks() {
      return this.ticks;
   }

   public TickGate double85() {
      return this.var23;
   }

   public BooleanSupplier double86() {
      return this.booleanSupplier;
   }

   public int int331() {
      return this.int171;
   }

   public TimedStep Event29(int var1) {
      this.ticks = var1;
      return this;
   }

   public TimedStep UiAnimation(TickGate var1) {
      this.var23 = var1;
      return this;
   }

   public TimedStep UiAnimation(BooleanSupplier var1) {
      this.booleanSupplier = var1;
      return this;
   }

   public TimedStep RotationUpdateStartEvent(int var1) {
      this.int171 = var1;
      return this;
   }
}
