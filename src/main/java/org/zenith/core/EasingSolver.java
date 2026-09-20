package org.zenith.core;

class EasingSolver implements Easing {
   public final double SprintStateEvent;
   public final double SprintPacketEvent;
   public final double ItemUseEvent;
   public final double EventImpl;

   @Override
   public float ease(float var1, float var2, float var3, float var4) {
      if (var4 <= 0.0F || var1 <= 0.0F) {
         return var2;
      }

      if (var1 >= var4) {
         return var2 + var3;
      }

      float f = var1 / var4;
      float f1 = this.on23((float)this.EventImpl, (float)this.ItemUseEvent, f);
      float f2 = this.ColorAnimator(f1, (float)this.SprintStateEvent, (float)this.SprintPacketEvent);
      return var2 + var3 * f2;
   }

   public float on23(float var1, float var2, float var3) {
      float f = var3;
      byte b0 = 8;
      float f1 = 1.0E-5F;

      for (int i = 0; i < 8; i++) {
         float f2 = this.UiAnimation(f, var1, var2);
         float f3 = this.Easing(f, var1, var2);
         if (Math.abs(f2 - var3) < 1.0E-5F || Math.abs(f3) < 1.0E-6F) {
            break;
         }

         f -= (f2 - var3) / f3;
         f = Math.max(0.0F, Math.min(1.0F, f));
      }

      return f;
   }

   public float UiAnimation(float var1, float var2, float var3) {
      return 3.0F * (1.0F - var1) * (1.0F - var1) * var1 * var2 + 3.0F * (1.0F - var1) * var1 * var1 * var3 + var1 * var1 * var1;
   }

   public float Easing(float var1, float var2, float var3) {
      return 3.0F * ((1.0F - var1) * (1.0F - 3.0F * var1) * var2 + (2.0F * var1 - 3.0F * var1 * var1) * var3) + 3.0F * var1 * var1;
   }

   public float ColorAnimator(float var1, float var2, float var3) {
      return 3.0F * (1.0F - var1) * (1.0F - var1) * var1 * var2 + 3.0F * (1.0F - var1) * var1 * var1 * var3 + var1 * var1 * var1;
   }

   EasingSolver(double var1, double var3, double var5, double var7) {
      this.EventImpl = var1;
      this.ItemUseEvent = var3;
      this.SprintStateEvent = var5;
      this.SprintPacketEvent = var7;
   }
}
