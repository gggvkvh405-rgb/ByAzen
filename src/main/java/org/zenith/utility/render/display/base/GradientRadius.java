package org.zenith.utility.render.display.base;

import java.util.List;
import org.zenith.util.ArgbColor;

public class GradientRadius {
   protected final ArgbColor var11944;
   protected final ArgbColor var11945;
   protected final ArgbColor var11946;
   protected final ArgbColor var11947;

   protected GradientRadius(ArgbColor var1, ArgbColor var2, ArgbColor var3, ArgbColor var4) {
      this.var11944 = var1;
      this.var11945 = var2;
      this.var11946 = var3;
      this.var11947 = var4;
   }

   public static GradientRadius on23(ArgbColor var0, ArgbColor var1, ArgbColor var2, ArgbColor var3) {
      return new GradientRadius(var0, var1, var2, var3);
   }

   public static GradientRadius CloudPoller(ArgbColor var0) {
      return new GradientRadius(var0, var0, var0, var0);
   }

   public static GradientRadius CloudRouter(List<ArgbColor> var0) {
      return new GradientRadius(var0.get(0), var0.get(1), var0.get(2), var0.get(3));
   }

   public GradientRadius getThis4() {
      return this;
   }

   public GradientRadius TargetAcquireEvent(float var1) {
      return new GradientRadius(
         this.var11944.SprintStateEvent(var1), this.var11945.SprintStateEvent(var1), this.var11946.SprintStateEvent(var1), this.var11947.SprintStateEvent(var1)
      );
   }

   public ArgbColor call010() {
      return this.var11944;
   }

   public ArgbColor call014() {
      return this.var11945;
   }

   public ArgbColor call052() {
      return this.var11946;
   }

   public ArgbColor call017() {
      return this.var11947;
   }
}
