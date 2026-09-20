package org.zenith.core;

import net.minecraft.util.math.Vec3d;
import org.zenith.util.ArgbColor;

public interface PositionProvider {
   Vec3d WallBypass();

   Vec3d getModeSetting3();

   float getSize();

   float var11927();

   default float EventPushOutOfBlocks(float var1) {
      return this.var11927();
   }

   ArgbColor getColor();

   String var111();

   float getRotation();

   boolean float304();
}
