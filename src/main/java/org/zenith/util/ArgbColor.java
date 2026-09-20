package org.zenith.util;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Objects;
import net.minecraft.util.math.MathHelper;

public class ArgbColor {
   public static final ArgbColor var11934 = new ArgbColor(255, 255, 255, 255);
   public static final ArgbColor var11935 = new ArgbColor(0, 0, 0, 255);
   public static final ArgbColor var11936 = new ArgbColor(0, 255, 0, 255);
   public static final ArgbColor var11937 = new ArgbColor(255, 0, 0, 255);
   public static final ArgbColor var11938 = new ArgbColor(0, 0, 255, 255);
   public static final ArgbColor var11939 = new ArgbColor(255, 255, 0, 255);
   public static final ArgbColor var11940 = new ArgbColor(88, 87, 93, 255);
   public static final ArgbColor var11941 = new ArgbColor(0, 0, 0, 0);
   public static final ArgbColor var11942 = new ArgbColor(14, 14, 16, 255);
   private transient float[] val338;
   public final int int334;
   public final int int335;
   public final int int336;
   public final int int337;
   public static final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);

   public ArgbColor(String var1) {
      if (var1.startsWith("#")) {
         var1 = var1.substring(1);
      }

      long i = Long.parseLong(var1, 16);
      int j;
      int k;
      int l;
      int i1;
      if (var1.length() == 8) {
         j = (int)(i >> 24 & 255L);
         k = (int)(i >> 16 & 255L);
         l = (int)(i >> 8 & 255L);
         i1 = (int)(i & 255L);
      } else {
         if (var1.length() != 6) {
            throw new IllegalArgumentException("Invalid hex color: " + var1);
         }

         j = (int)(i >> 16 & 255L);
         k = (int)(i >> 8 & 255L);
         l = (int)(i & 255L);
         i1 = 255;
      }

      this.int334 = j;
      this.int335 = k;
      this.int336 = l;
      this.int337 = i1;
   }

   public ArgbColor(int var1) {
      this(ColorUtils.Event18Ext3(var1), ColorUtils.EventRenderScreenHook(var1), ColorUtils.GameMessageEvent(var1), ColorUtils.PacketEvent(var1));
   }

   public ArgbColor(Color var1) {
      this(var1.getRed(), var1.getGreen(), var1.getBlue(), var1.getAlpha());
   }

   public ArgbColor(int var1, int var2, int var3) {
      this(var1, var2, var3, 255);
   }

   public ArgbColor(int var1, int var2, int var3, int var4) {
      var1 = MathHelper.clamp(var1, 0, 255);
      var2 = MathHelper.clamp(var2, 0, 255);
      var3 = MathHelper.clamp(var3, 0, 255);
      var4 = MathHelper.clamp(var4, 0, 255);
      this.int334 = var1;
      this.int335 = var2;
      this.int336 = var3;
      this.int337 = var4;
   }

   public int call001() {
      int i = Math.round(this.EventImpl(this.int337));
      int j = Math.round(this.EventImpl(this.int334));
      int k = Math.round(this.EventImpl(this.int335));
      int l = Math.round(this.EventImpl(this.int336));
      return (i & 0xFF) << 24 | (j & 0xFF) << 16 | (k & 0xFF) << 8 | l & 0xFF;
   }

   public int EventImpl(float var1) {
      return (int)Math.max(0.0F, Math.min(255.0F, var1));
   }

   public static ArgbColor HudElementValue(String var0) {
      String s = var0.startsWith("#") ? var0.substring(1) : var0;
      if (s.length() != 6 && s.length() != 8) {
         throw new IllegalArgumentException("Hex color must be in the format #RRGGBB or #RRGGBBAA");
      }

      int i = Integer.parseInt(s.substring(0, 2), 16);
      int j = Integer.parseInt(s.substring(2, 4), 16);
      int k = Integer.parseInt(s.substring(4, 6), 16);
      int l = s.length() == 8 ? Integer.parseInt(s.substring(6, 8), 16) : 255;
      return new ArgbColor(i, j, k, l);
   }

   public static ArgbColor on23(ArgbColor var0, ArgbColor var1, float var2) {
      float f = Math.max(0.0F, Math.min(1.0F, var2));
      int i = (int)(var0.float240() + (var1.float240() - var0.float240()) * f);
      int j = (int)(var0.var14323() + (var1.var14323() - var0.var14323()) * f);
      int k = (int)(var0.var14324() + (var1.var14324() - var0.var14324()) * f);
      int l = (int)(var0.var14325() + (var1.var14325() - var0.var14325()) * f);
      return new ArgbColor(i, j, k, l);
   }

   public static ArgbColor HudRenderEvent(int var0) {
      int i = var0 >> 24 & 0xFF;
      int j = var0 >> 16 & 0xFF;
      int k = var0 >> 8 & 0xFF;
      int l = var0 & 0xFF;
      return new ArgbColor(j, k, l, i);
   }

   public ArgbColor ItemUseEvent(float var1) {
      return new ArgbColor(this.int334, this.int335, this.int336, this.EventImpl((int)(255.0F * var1)));
   }

   public ArgbColor EventHookWorldRender(int var1) {
      return new ArgbColor(this.int334, this.int335, this.int336, var1);
   }

   public ArgbColor SprintStateEvent(float var1) {
      return this.EventHookWorldRender((int)(this.int337 * var1));
   }

   public ArgbColor Easing(ArgbColor var1, float var2) {
      var2 = Math.min(1.0F, Math.max(0.0F, var2));
      return new ArgbColor(
         (int)MathUtils.SimpleItemBuilder(this.float240(), var1.float240(), var2),
         (int)MathUtils.SimpleItemBuilder(this.var14323(), var1.var14323(), var2),
         (int)MathUtils.SimpleItemBuilder(this.var14324(), var1.var14324(), var2),
         (int)MathUtils.SimpleItemBuilder(this.var14325(), var1.var14325(), var2)
      );
   }

   public ArgbColor SprintPacketEvent(float var1) {
      var1 = MathHelper.clamp(var1, 0.0F, 1.0F);
      return new ArgbColor((int)(this.int334 * (1.0F - var1)), (int)(this.int335 * (1.0F - var1)), (int)(this.int336 * (1.0F - var1)), this.int337);
   }

   public static ArgbColor FileLogger(float var0, float var1, float var2) {
      if (var1 == 0.0F) {
         int i = (int)(var2 * 255.0F + 0.5F);
         return new ArgbColor(i, i, i);
      }

      float f = (var0 - (float)Math.floor(var0)) * 6.0F;
      float f1 = f - (float)Math.floor(f);
      float f2 = var2 * (1.0F - var1);
      float f3 = var2 * (1.0F - var1 * f1);
      float f4 = var2 * (1.0F - var1 * (1.0F - f1));
      float f5 = 0.0F;
      float f6 = 0.0F;
      float f7 = 0.0F;
      switch ((int)f) {
         case 0:
            f5 = var2;
            f6 = f4;
            f7 = f2;
            break;
         case 1:
            f5 = f3;
            f6 = var2;
            f7 = f2;
            break;
         case 2:
            f5 = f2;
            f6 = var2;
            f7 = f4;
            break;
         case 3:
            f5 = f2;
            f6 = f3;
            f7 = var2;
            break;
         case 4:
            f5 = f4;
            f6 = f2;
            f7 = var2;
            break;
         case 5:
            f5 = var2;
            f6 = f2;
            f7 = f3;
      }

      return new ArgbColor((int)(f5 * 255.0F), (int)(f6 * 255.0F), (int)(f7 * 255.0F));
   }

   public float call210() {
      return this.call032()[0];
   }

   public float call024() {
      return this.call032()[2];
   }

   public float call064() {
      return this.call032()[1];
   }

   public float[] call032() {
      if (this.val338 == null) {
         this.val338 = this.float239();
      }

      return this.val338;
   }

   public float[] float239() {
      float f = this.int334 / 255.0F;
      float f1 = this.int335 / 255.0F;
      float f2 = this.int336 / 255.0F;
      float f3 = Math.max(f, Math.max(f1, f2));
      float f4 = Math.min(f, Math.min(f1, f2));
      float f5 = f3 - f4;
      float f6 = 0.0F;
      if (f5 != 0.0F) {
         if (f3 == f) {
            f6 = (f1 - f2) / f5;
         } else if (f3 == f1) {
            f6 = (f2 - f) / f5 + 2.0F;
         } else {
            f6 = (f - f1) / f5 + 4.0F;
         }

         f6 /= 6.0F;
         if (f6 < 0.0F) {
            f6++;
         }
      }

      float f7 = f3 == 0.0F ? 0.0F : f5 / f3;
      return new float[]{f6, f7, f3};
   }

   public ArgbColor EventTick(float var1) {
      var1 = MathHelper.clamp(var1, 0.0F, 1.0F);
      return new ArgbColor(
         (int)(this.int334 + (255.0F - this.int334) * var1),
         (int)(this.int335 + (255.0F - this.int335) * var1),
         (int)(this.int336 + (255.0F - this.int336) * var1),
         this.int337
      );
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ArgbColor i11ii1llliilllii1i1 = (ArgbColor)var1;
         return Float.compare(this.int334, i11ii1llliilllii1i1.int334) == 0
            && Float.compare(this.int335, i11ii1llliilllii1i1.int335) == 0
            && Float.compare(this.int336, i11ii1llliilllii1i1.int336) == 0
            && Float.compare(this.int337, i11ii1llliilllii1i1.int337) == 0;
      } else {
         return false;
      }
   }

   public float EmoteMetadata(ArgbColor var1) {
      return Math.abs(this.call210() - var1.call210()) + Math.abs(this.call064() - var1.call064()) + Math.abs(this.call024() - var1.call024());
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.int334, this.int335, this.int336, this.int337);
   }

   public int float240() {
      return this.int334;
   }

   public int var14323() {
      return this.int335;
   }

   public int var14324() {
      return this.int336;
   }

   public int var14325() {
      return this.int337;
   }
}
