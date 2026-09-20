package org.zenith.rotation;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.zenith.module.combat.Aura;
import org.zenith.util.RaycastUtils;

public class RotationSmoothStrategy extends RotationStrategyBase {
   public float float301 = 1.0F;
   public float float302;
   public float float303;
   public float float304;
   public float float305;

   public Rotation Easing(Rotation var1) {
      Rotation ililiiili1ll1li11 = val002.LineShader();
      if (var1 != null && !var1.string68()) {
         RotationDelta liiilliiilil1l1i1111li1ii11 = ililiiili1ll1li11.EmoteManager(var1);
         float f = liiilliiilil1l1i1111li1ii11.type2();
         float f1 = liiilliiilil1l1i1111li1ii11.path15();
         float f2 = liiilliiilil1l1i1111li1ii11.gson2();
         if (f2 <= 1.0E-4F) {
            return ililiiili1ll1li11;
         }

         float f3 = this.NbtEditor(25.0F, 360.0F);
         float f4 = this.NbtEditor(4.0F, 10.0F);
         float f5 = (float)(5.0 * Math.cos(System.currentTimeMillis() / 70.0));
         float f6 = (float)(4.0 * Math.sin(System.currentTimeMillis() / 70.0));
         LivingEntity livingentity = Aura.aura.zClass054();
         if (livingentity != null && RaycastUtils.on23(16.0, ililiiili1ll1li11, livingentity)) {
            f3 = 0.0F;
            f4 = 0.0F;
            f5 = 0.0F;
            f6 = 0.0F;
         }

         float f7 = Math.abs(f / f2) * f3;
         float f8 = Math.abs(f1 / f2) * f4;
         float f9 = ililiiili1ll1li11.GrimGlide() + MathHelper.clamp(f, -f7, f7);
         float f10 = MathHelper.clamp(ililiiili1ll1li11.GuiWalk() + MathHelper.clamp(f1, -f8, f8), -89.0F, 89.0F);
         return this.on23(ililiiili1ll1li11, f9 + f5, f10 + f6, this.NbtEditor(0.1F, 0.4F));
      } else {
         return ililiiili1ll1li11;
      }
   }

   public Rotation on23(Rotation var1, float var2, float var3, float var4) {
      float f = Math.abs(MathHelper.wrapDegrees(var2 - this.float304));
      float f1 = Math.abs(var3 - this.float305);
      if (this.float301 >= 1.0F || f > 6.0F || f1 > 6.0F) {
         this.float302 = var1.GrimGlide();
         this.float303 = var1.GuiWalk();
         this.float304 = var2;
         this.float305 = var3;
         this.float301 = 0.0F;
      }

      this.float301 = MathHelper.clamp(this.float301 + var4, 0.0F, 1.0F);
      float f2 = this.ModuleSnapshotDto(this.float301);
      float f3 = MathHelper.wrapDegrees(this.float304 - this.float302);
      float f4 = this.float305 - this.float303;
      return new Rotation(this.float302 + f3 * f2, MathHelper.clamp(this.float303 + f4 * f2, -89.0F, 89.0F));
   }

   public float ModuleSnapshotDto(float var1) {
      float f = 1.70158F;
      float f1 = f + 1.0F;
      float f2 = var1 - 1.0F;
      return 1.0F + f1 * f2 * f2 * f2 - f * f2 * f2;
   }

   public float NbtEditor(float var1, float var2) {
      return ThreadLocalRandom.current().nextFloat(var1, var2);
   }
}
