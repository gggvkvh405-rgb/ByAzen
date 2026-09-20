package org.zenith.core;

import net.minecraft.util.math.Vec3d;
import org.zenith.util.ArgbColor;

public class SpinMarkerDot {
   public final Vec3d vec3d24;
   public final Vec3d vec3d25;
   public final ArgbColor var11929;
   public int int143;
   public static final int int144 = 20;

   public SpinMarkerDot(Vec3d var1, Vec3d var2, ArgbColor var3) {
      this.vec3d24 = var1;
      this.vec3d25 = var2;
      this.var11929 = var3;
      this.int143 = 20;
   }

   public void update() {
      this.int143--;
   }

   public boolean float304() {
      return this.int143 <= 0;
   }

   public float var11927() {
      return this.int143 / 20.0F;
   }

   public Vec3d var1199() {
      return this.vec3d24;
   }

   public Vec3d var11910() {
      return this.vec3d25;
   }

   public ArgbColor getColor() {
      return this.var11929;
   }

   public int boolean87() {
      return this.int143;
   }
}
