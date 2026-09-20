package org.zenith.rotation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Rotation {
   public final float float209;
   public final float float210;
   public boolean boolean151;
   public static final Rotation var1189 = new Rotation(0.0F, 0.0F);

   public Rotation(float var1, float var2) {
      this(var1, var2, false);
   }

   public Rotation(float var1, float var2, boolean var3) {
      this.float209 = var1;
      this.float210 = MathHelper.clamp(var2, -90.0F, 90.0F);
      this.boolean151 = var3;
   }

   public static Rotation ItemServiceBase(Vec3d var0, Vec3d var1) {
      return CancellableEvent(var0.subtract(var1));
   }

   public static Rotation CancellableEvent(Vec3d var0) {
      double d0 = var0.x;
      double d1 = var0.y;
      double d2 = var0.z;
      return new Rotation(
         (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(d2, d0)) - 90.0),
         (float)MathHelper.wrapDegrees(-Math.toDegrees(Math.atan2(d1, Math.sqrt(d0 * d0 + d2 * d2))))
      );
   }

   public float EmoteMetadata(Rotation var1) {
      return Math.min(this.EmoteManager(var1).gson2(), 180.0F);
   }

   public RotationDelta EmoteManager(Rotation var1) {
      return new RotationDelta(this.CancellableEvent(var1.float209, this.float209), this.CancellableEvent(var1.float210, this.float210));
   }

   public float CancellableEvent(float var1, float var2) {
      return MathHelper.wrapDegrees(var1 - var2);
   }

   public boolean on23(Rotation var1, float var2) {
      return this.EmoteMetadata(var1) <= var2;
   }

   public boolean call494() {
      return this.boolean151;
   }

   public Vec3d int202() {
      return Vec3d.fromPolar(this.float210, this.float209);
   }

   public Rotation on23(Rotation var1, float var2, float var3) {
      RotationDelta liiilliiilil1l1i1111li1ii11 = this.EmoteManager(var1);
      float f = liiilliiilil1l1i1111li1ii11.gson2();
      float f1 = Math.abs(liiilliiilil1l1i1111li1ii11.type2() / f) * var2;
      float f2 = Math.abs(liiilliiilil1l1i1111li1ii11.path15() / f) * var3;
      float f3 = MathHelper.clamp(liiilliiilil1l1i1111li1ii11.type2(), -f1, f1);
      float f4 = MathHelper.clamp(liiilliiilil1l1i1111li1ii11.path15(), -f2, f2);
      return new Rotation(this.float209 + f3, this.float210 + f4);
   }

   public boolean string68() {
      return Float.isInfinite(this.float209) || Float.isNaN(this.float209) || Float.isInfinite(this.float210) || Float.isNaN(this.float210);
   }

   public static float logger2() {
      double d0 = (Double)MinecraftClient.getInstance().options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
      return (float)(d0 * d0 * d0 * 8.0 * 0.15F);
   }

   public Rotation CosmeticManager(Rotation var1) {
      if (!this.boolean151 && !this.equals(var1)) {
         RotationDelta liiilliiilil1l1i1111li1ii11 = var1.EmoteManager(this);
         float f = logger2();
         if (!Float.isNaN(f) && !Float.isInfinite(f) && !(f <= 0.0F)) {
            float f1 = Math.round(liiilliiilil1l1i1111li1ii11.type2() / f) * f;
            float f2 = Math.round(liiilliiilil1l1i1111li1ii11.path15() / f) * f;
            return new Rotation(var1.GrimGlide() + f1, var1.GuiWalk() + f2, true);
         } else {
            return this;
         }
      } else {
         return this;
      }
   }

   public Rotation Event08(float var1, float var2) {
      return new Rotation(this.float209 + var1, MathHelper.clamp(this.float210 + var2, -90.0F, 90.0F));
   }

   public Rotation on23(RotationDelta var1) {
      return new Rotation(this.float209 + var1.type2(), MathHelper.clamp(this.float210 + var1.path15(), -90.0F, 90.0F));
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof Rotation ililiiili1ll1li11 ? ililiiili1ll1li11.float209 == this.float209 && ililiiili1ll1li11.float210 == this.float210 : false;
   }

   public float GrimGlide() {
      return this.float209;
   }

   public float GuiWalk() {
      return this.float210;
   }
}
