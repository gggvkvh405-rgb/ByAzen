package org.zenith.core;

import java.util.function.BooleanSupplier;

public final class StepConditionHost {
   public int int170;
   public TickGate var23;
   public BooleanSupplier booleanSupplier;
   public int int171;

   public StepConditionHost(int var1, TickGate var2, BooleanSupplier var3, int var4) {
      this.int170 = var1;
      this.var23 = var2;
      this.booleanSupplier = var3;
      this.int171 = var4;
   }

   public int UiAnimation(StepConditionHost var1) {
      return Integer.compare(var1.int331(), this.int331());
   }

   public int double84() {
      return this.int170;
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

   public StepConditionHost RenderTickEvent(int var1) {
      this.int170 = var1;
      return this;
   }

   public StepConditionHost on23(TickGate var1) {
      this.var23 = var1;
      return this;
   }

   public StepConditionHost on23(BooleanSupplier var1) {
      this.booleanSupplier = var1;
      return this;
   }

   public StepConditionHost Event18Ext(int var1) {
      this.int171 = var1;
      return this;
   }
}
