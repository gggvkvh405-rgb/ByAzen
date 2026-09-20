package org.zenith.core;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class SlotRenderRule {
   public int slot;
   public String string61;
   public boolean boolean122;
   public Set<String> set11;

   public SlotRenderRule(int var1, String var2, boolean var3, Collection<String> var4) {
      this.slot = var1;
      this.string61 = var2;
      this.boolean122 = var3;
      this.set11 = new LinkedHashSet<>(var4);
   }

   public int getSlot() {
      return this.slot;
   }

   public String BoxShaderRenderer() {
      return this.string61;
   }

   public boolean customDrawContext() {
      return this.boolean122;
   }

   public Set<String> float22() {
      return Set.copyOf(this.set11);
   }

   public boolean matrixStack() {
      if (this.string61 != null && !this.string61.isBlank()) {
         if (this.set11 == null) {
            this.set11 = new LinkedHashSet<>();
         } else {
            this.set11.removeIf(Objects::isNull);
         }

         return true;
      } else {
         return false;
      }
   }
}
