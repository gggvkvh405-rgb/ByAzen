package org.zenith.core;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Kernel;

public class ConvolveKernel {
   protected float float271;
   protected Kernel kernel;

   public ConvolveKernel(float var1) {
      this.EventUpdateHealth(var1);
   }

   public static void on23(Kernel var0, int[] var1, int[] var2, int var3, int var4, boolean var5, boolean var6, boolean var7, int var8) {
      float[] afloat = var0.getKernelData(null);
      int i = var0.getWidth();
      int j = i / 2;

      for (int k = 0; k < var4; k++) {
         int l = k;
         int i1 = k * var3;

         for (int j1 = 0; j1 < var3; j1++) {
            float f = 0.0F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            float f3 = 0.0F;
            int k1 = j;

            for (int l1 = -j; l1 <= j; l1++) {
               float f4 = afloat[k1 + l1];
               if (f4 != 0.0F) {
                  int i2 = j1 + l1;
                  if (i2 < 0) {
                     if (var8 == 1) {
                        i2 = 0;
                     } else if (var8 == 2) {
                        i2 = (j1 + var3) % var3;
                     }
                  } else if (i2 >= var3) {
                     if (var8 == 1) {
                        i2 = var3 - 1;
                     } else if (var8 == 2) {
                        i2 = (j1 + var3) % var3;
                     }
                  }

                  int j2 = var1[i1 + i2];
                  int k2 = j2 >> 24 & 0xFF;
                  int l2 = j2 >> 16 & 0xFF;
                  int i3 = j2 >> 8 & 0xFF;
                  int j3 = j2 & 0xFF;
                  if (var6) {
                     float f5 = k2 * 0.003921569F;
                     l2 = (int)(l2 * f5);
                     i3 = (int)(i3 * f5);
                     j3 = (int)(j3 * f5);
                  }

                  f3 += f4 * k2;
                  f += f4 * l2;
                  f1 += f4 * i3;
                  f2 += f4 * j3;
               }
            }

            if (var7 && f3 != 0.0F && f3 != 255.0F) {
               float f6 = 255.0F / f3;
               f *= f6;
               f1 *= f6;
               f2 *= f6;
            }

            int k3 = var5 ? SprintEvent((int)(f3 + 0.5)) : 255;
            int l3 = SprintEvent((int)(f + 0.5));
            int i4 = SprintEvent((int)(f1 + 0.5));
            int j4 = SprintEvent((int)(f2 + 0.5));
            var2[l] = k3 << 24 | l3 << 16 | i4 << 8 | j4;
            l += var4;
         }
      }
   }

   public static int SprintEvent(int var0) {
      return var0 < 0 ? 0 : Math.min(var0, 255);
   }

   public static Kernel Event37(float var0) {
      int i = (int)Math.ceil(var0);
      int j = i * 2 + 1;
      float[] afloat = new float[j];
      float f = var0 / 3.0F;
      float f1 = 2.0F * f * f;
      float f2 = (float) (Math.PI * 2) * f;
      float f3 = (float)Math.sqrt(f2);
      float f4 = var0 * var0;
      float f5 = 0.0F;
      int k = 0;

      for (int l = -i; l <= i; l++) {
         float f6 = l * l;
         if (f6 > f4) {
            afloat[k] = 0.0F;
         } else {
            afloat[k] = (float)Math.exp(-f6 / f1) / f3;
         }

         f5 += afloat[k];
         k++;
      }

      for (int i1 = 0; i1 < j; i1++) {
         afloat[i1] /= f5;
      }

      return new Kernel(j, 1, afloat);
   }

   public void EventUpdateHealth(float var1) {
      this.float271 = var1;
      this.kernel = Event37(var1);
   }

   public BufferedImage on23(BufferedImage var1, BufferedImage var2) {
      int i = var1.getWidth();
      int j = var1.getHeight();
      if (var2 == null) {
         var2 = this.on23(var1, (ColorModel)null);
      }

      int[] aint = new int[i * j];
      int[] aint1 = new int[i * j];
      var1.getRGB(0, 0, i, j, aint, 0, i);
      if (this.float271 > 0.0F) {
         on23(this.kernel, aint, aint1, i, j, true, true, false, 1);
         on23(this.kernel, aint1, aint, j, i, true, false, true, 1);
      }

      var2.setRGB(0, 0, i, j, aint, 0, i);
      return var2;
   }

   public BufferedImage on23(BufferedImage var1, ColorModel var2) {
      if (var2 == null) {
         var2 = var1.getColorModel();
      }

      return new BufferedImage(var2, var2.createCompatibleWritableRaster(var1.getWidth(), var1.getHeight()), var2.isAlphaPremultiplied(), null);
   }
}
