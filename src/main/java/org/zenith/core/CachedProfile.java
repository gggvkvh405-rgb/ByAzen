package org.zenith.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CachedProfile {
   public String name;
   public long RotationDelta;
   public List<SlotRenderRule> list56;

   public CachedProfile(String var1, long var2, Collection<SlotRenderRule> var4) {
      this.name = var1;
      this.RotationDelta = var2;
      this.list56 = new ArrayList<>(var4);
   }

   public String getName() {
      return this.name;
   }

   public long getVar125() {
      return this.RotationDelta;
   }

   public List<SlotRenderRule> WorldTweaks() {
      return List.copyOf(this.list56);
   }

   public boolean matrixStack() {
      if (this.name != null && !this.name.isBlank()) {
         if (this.list56 == null) {
            this.list56 = new ArrayList<>();
         } else {
            this.list56.removeIf(var0 -> var0 == null || !var0.matrixStack());
         }

         return true;
      } else {
         return false;
      }
   }
}
