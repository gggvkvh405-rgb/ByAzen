package org.zenith.core;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

final class TargetSnapshot {
   public final float[] call108;
   public final Vec3d vec3d32;
   public final Box box7;
   public final float float142;

   public TargetSnapshot(float[] var1, Vec3d var2, Box var3, float var4) {
      this.call108 = (float[])var1.clone();
      this.vec3d32 = var2;
      this.box7 = var3;
      this.float142 = var4;
   }
}
