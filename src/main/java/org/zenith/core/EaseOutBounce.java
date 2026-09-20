package org.zenith.core;

import org.zenith.util.MathUtils;

public class EaseOutBounce extends EaseSineBase {
   public EaseOutBounce(float var1, float var2) {
      super(var1, var2);
   }

   public EaseOutBounce() {
   }

   @Override
   public float ease(float var1, float var2, float var3, float var4) {
      float f = this.BotRespawnEvent();
      float f1 = this.BotTickEvent();
      if (var1 == 0.0F) {
         return var2;
      }

      if ((var1 = var1 / var4) == 1.0F) {
         return var2 + var3;
      }

      if (f1 == 0.0F) {
         f1 = var4 * 0.3F;
      }

      float f2 = 0.0F;
      if (f < Math.abs(var3)) {
         f = var3;
         f2 = f1 / 4.0F;
      } else {
         f2 = f1 / (float) (Math.PI * 2) * (float)Math.asin(var3 / f);
      }

      return f * (float)Math.pow(2.0, -10.0F * var1) * (float)MathUtils.sin((var1 * var4 - f2) * (Math.PI * 2) / f1) + var3 + var2;
   }
}
