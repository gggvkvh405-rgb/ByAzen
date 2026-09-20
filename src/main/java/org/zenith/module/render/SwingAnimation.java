package org.zenith.module.render;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;

@ModuleInfo(name = "SwingAnimation", category = Category.RENDER, description = "Кастомные анимации замаха")
public final class SwingAnimation extends Module {
   public static final SwingAnimation swingAnimation = new SwingAnimation();
   public ModeSetting animationMode = new ModeSetting(
      "module.swingAnimation.animationMode",
      "module.swingAnimation.animationMode.desc",
      "module.swingAnimation.normal",
      "module.swingAnimation.first",
      "module.swingAnimation.second",
      "module.swingAnimation.third",
      "module.swingAnimation.fourth",
      "module.swingAnimation.fifth",
      "module.swingAnimation.sixth",
      "module.swingAnimation.seventh",
      "module.swingAnimation.eighth",
      "module.swingAnimation.lunge",
      "module.swingAnimation.jelly",
      "module.swingAnimation.slap",
      "module.swingAnimation.bonk",
      "module.swingAnimation.realism"
   );
   public final NumberSetting swingPower = new NumberSetting(
      "module.swingAnimation.swingPower", 5.0F, 1.0F, 20.0F, 0.1F, "module.swingAnimation.swingPower.desc", "x"
   );
   public final NumberSetting swingStrength = new NumberSetting(
      "module.swingAnimation.swingStrength", 1.0F, 0.1F, 3.0F, 0.1F, "module.swingAnimation.swingStrength.desc", "x"
   );
   public final BooleanSetting onlyAura2 = new BooleanSetting("module.swingAnimation.onlyAura", "module.swingAnimation.onlyAura.desc", false);
   public int int307 = -1;
   public boolean boolean143 = true;

   public void on23(MatrixStack var1, float var2, float var3, Arm var4) {
      float f = this.zClass101Var159();
      if (this.animationMode.is(0)) {
         var1.translate(0.56F, -0.52F, -0.72F);
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F + MathHelper.sin(var2 * var2 * (float) Math.PI) * -20.0F * f));
         float f1 = MathHelper.sin(MathHelper.sqrt(var2) * (float) Math.PI);
         var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f1 * -20.0F * f));
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f1 * -80.0F * f));
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45.0F));
      } else if (this.animationMode.is(1)) {
         if (var2 > 0.0F) {
            float f4 = MathHelper.sin(MathHelper.sqrt(var2) * (float) Math.PI);
            var1.translate(0.56F, var3 * -0.2F - 0.5F, -0.7F);
            var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F));
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f4 * -85.0F * f));
            var1.translate(-0.1F, 0.28F, 0.2F);
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-85.0F));
         } else {
            float f5 = -0.4F * MathHelper.sin(MathHelper.sqrt(var2) * (float) Math.PI) * f;
            float f2 = 0.2F * MathHelper.sin(MathHelper.sqrt(var2) * (float) (Math.PI * 2)) * f;
            float f3 = -0.2F * MathHelper.sin(var2 * (float) Math.PI) * f;
            var1.translate(f5, f2, f3);
            this.applyEquipOffset(var1, var4, var3);
            this.applySwingOffset(var1, var4, var2);
         }
      } else if (this.animationMode.is(2)) {
         float f6 = MathHelper.sin(MathHelper.sqrt(var2) * (float) Math.PI);
         this.applyEquipOffset(var1, var4, 0.0F);
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(50.0F));
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-60.0F));
         var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(110.0F + 20.0F * f6 * f));
      } else if (this.animationMode.is(3)) {
         float f7 = MathHelper.sin(MathHelper.sqrt(var2) * (float) Math.PI);
         this.applyEquipOffset(var1, var4, 0.0F);
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(50.0F));
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-60.0F + 30.0F * f7 * f));
         var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(110.0F));
      } else if (this.animationMode.is(4)) {
         float f8 = MathHelper.sin(var2 * (float) Math.PI);
         this.applyEquipOffset(var1, var4, 0.0F);
         var1.translate(0.1F, -0.2F, -0.3F);
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-30.0F * f8 * f - 36.0F));
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(25.0F * f8 * f));
         var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(12.0F));
      } else if (this.animationMode.is(5)) {
         float f9 = MathHelper.sin(MathHelper.sqrt(var2) * (float) Math.PI);
         this.applyEquipOffset(var1, var4, 0.0F);
         var1.translate(0.0F, -0.2F, -0.4F);
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-120.0F * f9 * f - 3.0F));
      } else if (this.animationMode.is(6)) {
         var1.translate(0.56F, -0.52F + Math.sin((var2 > 0.0F ? var2 : 1.0F) * var3) / 6.0 * f, -0.72F);
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F + MathHelper.sin(var2 * var2 * (float) Math.PI) * -20.0F * f));
         float f10 = MathHelper.sin(MathHelper.sqrt(var2) * (float) Math.PI);
         var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f10 * -20.0F * f));
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f10 * -80.0F * f));
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45.0F));
      } else if (this.animationMode.is(7)) {
         this.applyEquipOffset(var1, var4, 0.0F);
         float f11 = 360.0F * var2 + MathHelper.sin(var2 * (float) Math.PI) * 180.0F * (f - 1.0F);
         var1.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(f11));
      } else if (this.animationMode.is(8)) {
         float f12 = (float)Math.sin(var2 * (Math.PI / 2) * 2.0);
         int i = var4 == Arm.RIGHT ? 1 : -1;
         var1.translate(i * 0.56F, -0.52F, -(1.0F + f12 * 0.2F * f));
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-180.0F));
         var1.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(30.0F));
         var1.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(-180.0F * f12 * f));
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(50.0F));
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(60.0F * f12 * f));
      } else if (this.animationMode.is(9)) {
         this.on23(var1, var2, var4);
      } else if (this.animationMode.is(10)) {
         this.UiAnimation(var1, var2, var4);
      } else if (this.animationMode.is(11)) {
         this.Easing(var1, var2, var4);
      } else if (this.animationMode.is(12)) {
         this.ColorAnimator(var1, var2, var4);
      } else if (this.animationMode.is(13)) {
         this.ItemRegistry(var1, var2, var4);
      }
   }

   public void on23(MatrixStack var1, float var2, Arm var3) {
      int i = this.on23(var3);
      float f = this.zClass101Var159();
      float f1 = MathHelper.sin(MathHelper.sqrt(var2) * (float) Math.PI);
      var1.translate(i * 1.35F, -0.55F, -1.3F);
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * 45.0F));
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * f1 * -16.0F * f));
      var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * f1 * -16.0F * f));
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(i * f1 * -32.0F * f));
      var1.translate(i * -0.5F, 0.2F, -0.3F);
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * 30.0F));
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0F));
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * 60.0F));
   }

   public void UiAnimation(MatrixStack var1, float var2, Arm var3) {
      int i = this.on23(var3);
      float f = this.zClass101Var159();
      float f1 = (float)Math.sin(var2 * Math.PI);
      var1.translate(i * 0.75F, -0.45F, -(1.0F + f1 * 0.2F * f));
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * 35.0F));
      var1.scale(1.0F, 1.0F, f1 * var2 * 0.65F * f * 0.2F + 1.0F);
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
   }

   public void Easing(MatrixStack var1, float var2, Arm var3) {
      int i = this.on23(var3);
      float f = this.zClass101Var159();
      float f1 = MathHelper.sin(var2 * var2 * (float) Math.PI);
      float f2 = MathHelper.sin(MathHelper.sqrt(var2) * (float) Math.PI);
      var1.translate(i * 0.56F, -0.52F, -0.72F);
      var1.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(f2 * 55.0F));
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f2 * f1 * -120.0F * f));
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * f2 * 60.0F * f));
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f2 * 55.0F));
   }

   public void ColorAnimator(MatrixStack var1, float var2, Arm var3) {
      int i = this.on23(var3);
      float f = this.zClass101Var159();
      float f1 = this.ChatMessageEvent(var2 * var2);
      var1.translate(i * 0.56F, -0.52F, -0.72F);
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(15.0F * i));
      var1.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(95.0F * f1 * f));
   }

   public void ItemRegistry(MatrixStack var1, float var2, Arm var3) {
      if (var2 < 0.12F && this.boolean143) {
         this.int307 *= -1;
         this.boolean143 = false;
      } else if (var2 > 0.78F) {
         this.boolean143 = true;
      }

      int i = this.on23(var3);
      float f = (float)Math.sin(var2 * Math.PI);
      float f1 = this.zClass101Var159();
      float f2 = i * this.int307 * 22.0F * f * MathHelper.clamp(f1, 0.5F, 2.0F);
      float f3 = -90.0F * f * f1 * 0.3F;
      float f4 = 45.0F * this.ChatMessageEvent(var2 * var2) * f * f1;
      var1.translate(i * 0.55F, -0.5F, -(0.7F + f * 0.002F));
      var1.scale(1.0F, 1.0F, f * f1 + 1.0F);
      var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f2));
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f3));
      var1.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(f4));
   }

   public float zClass101Var159() {
      return this.swingStrength.getCurrent();
   }

   public int on23(Arm var1) {
      return var1 == Arm.RIGHT ? 1 : -1;
   }

   public float ChatMessageEvent(float var1) {
      if (var1 == 0.0F) {
         return 0.0F;
      }

      if (var1 == 1.0F) {
         return 1.0F;
      }

      double d0 = Math.PI * 2.0 / 3.0;
      return (float)(Math.pow(2.0, -10.0 * var1) * Math.sin((var1 * 10.0 - 0.75) * d0) + 1.0);
   }

   public void applyEquipOffset(MatrixStack var1, Arm var2, float var3) {
      int i = var2 == Arm.RIGHT ? 1 : -1;
      var1.translate(i * 0.56F, -0.52F + var3 * -0.6F, -0.72F);
   }

   public void applySwingOffset(MatrixStack var1, Arm var2, float var3) {
      int i = var2 == Arm.RIGHT ? 1 : -1;
      float f = this.zClass101Var159();
      float f1 = MathHelper.sin(var3 * var3 * (float) Math.PI);
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * (45.0F + f1 * -20.0F * f)));
      float f2 = MathHelper.sin(MathHelper.sqrt(var3) * (float) Math.PI);
      var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * f2 * -20.0F * f));
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f2 * -80.0F * f));
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * -45.0F));
   }
}
