package org.zenith.core;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.math.Vec3d;
import org.zenith.managers.BotEntity;
import org.zenith.util.ArgbColor;

public class BotGuardEntity extends BotEntity {
   public static final String string133 = "particle.texture.spaceStar";
   public final double double165;
   public final double double166;
   public final double double167;
   public final double double168;
   public final double double169;
   public final double double170;
   public final double double171;
   public final double double172;
   public final double double173;
   public final float float372;
   public final float float373;
   public final float float374;
   public double double174;
   public float float375 = 1.0F;

   public BotGuardEntity(
      Vec3d var1,
      int var2,
      float var3,
      ArgbColor var4,
      float var5,
      float var6,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23,
      float var25,
      float var26,
      float var27
   ) {
      super(var1, Vec3d.ZERO, var2, var3, var4, "particle.texture.spaceStar", var5, var6);
      this.double165 = var7;
      this.double166 = var9;
      this.double167 = var11;
      this.double168 = var13;
      this.double169 = var15;
      this.double170 = var17;
      this.double171 = var19;
      this.double172 = var21;
      this.double173 = var23;
      this.float372 = var25;
      this.float373 = var26;
      this.float374 = var27;
   }

   public static BotGuardEntity on23(Vec3d var0, float var1, int var2, float var3, ArgbColor var4) {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      double d0 = Math.pow(threadlocalrandom.nextDouble(0.08, 1.0), 0.72);
      double d1 = Math.max(6.0, var1 * d0);
      double d2 = threadlocalrandom.nextDouble(-var1 * 0.34, var1 * 0.42);
      double d3 = threadlocalrandom.nextDouble(Math.PI * 2);
      double d4 = threadlocalrandom.nextInt(4) * (Math.PI / 2);
      double d5 = threadlocalrandom.nextDouble(0.0015, 0.0075) * (threadlocalrandom.nextBoolean() ? 1.0 : -1.0);
      double d6 = threadlocalrandom.nextDouble(Math.PI * 2);
      double d7 = threadlocalrandom.nextDouble(0.015, 0.035);
      double d8 = threadlocalrandom.nextDouble(1.2, 4.8);
      double d9 = threadlocalrandom.nextDouble(0.8, 3.6);
      float f = var3 * threadlocalrandom.nextFloat(0.55F, 1.65F);
      float f1 = threadlocalrandom.nextFloat(0.0F, 360.0F);
      float f2 = threadlocalrandom.nextFloat(-0.7F, 0.7F);
      float f3 = threadlocalrandom.nextFloat(0.65F, 1.35F);
      float f4 = threadlocalrandom.nextFloat(0.05F, 0.12F);
      float f5 = threadlocalrandom.nextFloat(0.0F, (float) (Math.PI * 2));
      BotGuardEntity ll1i1il1111liiil = new BotGuardEntity(var0, var2, f, var4, f1, f2, d1, d2, d3, d4, d5, d6, d7, d8, d9, f3, f4, f5);
      ll1i1il1111liiil.double174 = threadlocalrandom.nextDouble(0.0, 260.0);
      ll1i1il1111liiil.int143 = threadlocalrandom.nextInt(Math.max(2, var2 / 3), var2);
      ll1i1il1111liiil.int426 = ll1i1il1111liiil.int143;
      ll1i1il1111liiil.TriggerBot = ll1i1il1111liiil.CrosshairTargetUpdateEvent(var0);
      ll1i1il1111liiil.ModeSetting = ll1i1il1111liiil.TriggerBot;
      return ll1i1il1111liiil;
   }

   public void ColorAnimator(Vec3d var1, float var2) {
      this.int426 = this.int143;
      this.ModeSetting = this.TriggerBot;
      this.int143--;
      if (this.int143 > 0) {
         this.float375 = Math.max(0.05F, var2);
         this.double174 = this.double174 + this.float375;
         this.TriggerBot = this.CrosshairTargetUpdateEvent(var1);
         this.vec3d37 = this.TriggerBot.subtract(this.ModeSetting);
         this.var11928();
      }
   }

   public Vec3d CrosshairTargetUpdateEvent(Vec3d var1) {
      double d0 = this.double174;
      double d1 = this.double165 * 0.055;
      double d2 = this.double167 + this.double168 + d1 + d0 * this.double169;
      double d3 = this.double165 + Math.sin(d0 * this.double171 + this.double170) * this.double172;
      double d4 = Math.cos(d2) * d3;
      double d5 = Math.sin(d2) * d3;
      double d6 = 4.5 + this.double166 + Math.sin(d0 * this.double171 * 1.7 + this.double170) * this.double173 + Math.sin(d2 * 2.0 + this.double170) * 0.85;
      return var1.add(d4, d6, d5);
   }

   @Override
   public float var11927() {
      return this.EventPushOutOfBlocks(1.0F);
   }

   @Override
   public float EventPushOutOfBlocks(float var1) {
      float f = this.EventInjectAddEntity(var1);
      float f1 = Math.min(f / 0.12F, 1.0F);
      float f2 = f > 0.82F ? 1.0F - (f - 0.82F) / 0.18F : 1.0F;
      float f3 = 0.62F + 0.38F * this.GuiWalkEvent(var1);
      return Math.max(0.0F, f1 * f2 * f3);
   }

   public float GuiWalkEvent(float var1) {
      return (float)((Math.sin((this.double174 + var1 * this.float375) * this.float373 + this.float374) + 1.0) * 0.5);
   }

   public float EventWindowSizeChanged(float var1) {
      return this.float357 * (4.0F + this.GuiWalkEvent(var1) * 2.5F) * this.float372;
   }

   public float AttackEntityEvent(float var1) {
      return this.float357 * (0.65F + this.GuiWalkEvent(var1) * 0.35F);
   }

   public float Event18Ext5(float var1) {
      return this.float357 * (1.35F + this.GuiWalkEvent(var1) * 0.45F);
   }

   public float Event05(float var1) {
      return Math.min(10.0F, (float)this.vec3d37.length() * 18.0F * var1);
   }
}
