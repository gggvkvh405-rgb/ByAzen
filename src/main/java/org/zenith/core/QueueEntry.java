package org.zenith.core;

class QueueEntry<E> {
   public final Class<E> class2;
   public final CapacityLimited<E> zClass100Var143Var143;
   public final int int212;
   public final boolean boolean120;
   public final int getMaxLength;

   QueueEntry(Class<E> var1, CapacityLimited<E> var2, int var3, boolean var4, int var5) {
      this.class2 = var1;
      this.zClass100Var143Var143 = var2;
      this.int212 = var3;
      this.boolean120 = var4;
      this.getMaxLength = var5;
   }

   boolean ItemRegistry(Object var1) {
      return this.zClass100Var143Var143.accept(this.class2.cast(var1));
   }

   @Override
   public String toString() {
      return "Step{index="
         + this.int212
         + ", event="
         + this.class2.getSimpleName()
         + ", persistent="
         + this.boolean120
         + ", priority="
         + this.getMaxLength
         + "}";
   }
}
