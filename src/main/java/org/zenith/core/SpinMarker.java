package org.zenith.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.zenith.util.ArgbColor;

public class SpinMarker implements PositionProvider {
   public Vec3d TriggerBot;
   public Vec3d ModeSetting;
   public Vec3d vec3d37;
   public Vec3d vec3d43;
   public int int143;
   public int int426;
   public final int int427;
   public final float float319;
   public ArgbColor color;
   public final List<SpinMarkerDot> list106 = new ArrayList<>();
   public static final int int428 = 15;
   public int int429 = -1;
   public float float238 = 1.0F;

   public SpinMarker(Vec3d var1, Vec3d var2, int var3, float var4, ArgbColor var5) {
      this.TriggerBot = var1;
      this.ModeSetting = var1;
      this.vec3d37 = Vec3d.ZERO;
      this.vec3d43 = var2;
      this.int143 = var3;
      this.int426 = var3;
      this.int427 = var3;
      this.float319 = var4;
      this.color = var5;
   }

   @Override
   public String var111() {
      return "particle.texture.firefly";
   }

   @Override
   public float getRotation() {
      return 0.0F;
   }

   public void VelocityChangeEvent(Vec3d var1) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      this.int426 = this.int143;
      double d0 = this.TriggerBot.distanceTo(var1);
      this.int143 -= d0 > 64.0 ? 8 : 1;
      if (this.int143 > 0) {
         Vec3d vec3d = this.TriggerBot;
         this.ModeSetting = this.TriggerBot;
         float f = 1.0F - (float)this.int143 / this.int427;
         if (f < 0.35F) {
            float f1 = 1.0F - (float)Math.pow(1.0F - f / 0.35F, 3.0);
            this.vec3d37 = this.vec3d43.multiply(f1);
         } else if (f > 0.6F) {
            float f2 = 1.0F - (float)Math.pow((f - 0.6F) / 0.4F, 3.0);
            this.vec3d37 = this.vec3d37.multiply(0.92 + 0.08 * f2);
         } else {
            this.vec3d37 = this.vec3d37.multiply(0.998);
         }

         if (minecraftclient.world != null) {
            this.int429 = -1;
            Vec3d vec3d1 = this.TriggerBot;

            for (int i = 1; i <= 15; i++) {
               vec3d1 = vec3d1.add(this.vec3d37);
               if (!minecraftclient.world.getBlockState(BlockPos.ofFloored(vec3d1)).isAir()) {
                  this.int429 = i;
                  break;
               }
            }

            if (this.int429 > 0) {
               this.float238 = this.int429 / 15.0F;
               this.float238 = this.float238 * this.float238;
               if (this.int429 <= 1) {
                  this.int143 = 0;
                  return;
               }
            } else {
               this.float238 = Math.min(this.float238 + 0.1F, 1.0F);
            }
         }

         this.TriggerBot = this.TriggerBot.add(this.vec3d37);
         this.list106.add(new SpinMarkerDot(vec3d, this.TriggerBot, this.color));
         this.list106.removeIf(var0 -> {
            var0.update();
            return var0.float304();
         });
      }
   }

   @Override
   public boolean float304() {
      return this.int143 <= 0;
   }

   @Override
   public float var11927() {
      return this.EventPushOutOfBlocks(1.0F);
   }

   @Override
   public float EventPushOutOfBlocks(float var1) {
      float f = this.int426 + (this.int143 - this.int426) * var1;
      float f1 = f / this.int427;
      return f1 * this.float238;
   }

   public void ProfileItemBuilder(Vec3d var1, Vec3d var2) {
      this.ModeSetting = this.TriggerBot;
      this.TriggerBot = var1;
      this.vec3d37 = var2;
      this.vec3d43 = var2;
      this.list106.add(new SpinMarkerDot(this.ModeSetting, this.TriggerBot, this.color));
      this.list106.removeIf(var0 -> {
         var0.update();
         return var0.float304();
      });
   }

   public void var1193() {
      this.int143 = this.int427;
   }

   public void var1194() {
      this.int143 = this.int427 / 2;
   }

   @Override
   public Vec3d WallBypass() {
      return this.TriggerBot;
   }

   @Override
   public Vec3d getModeSetting3() {
      return this.ModeSetting;
   }

   public Vec3d boolean86() {
      return this.vec3d37;
   }

   public Vec3d var1195() {
      return this.vec3d43;
   }

   public int boolean87() {
      return this.int143;
   }

   public int boolean88() {
      return this.int426;
   }

   public int boolean149() {
      return this.int427;
   }

   @Override
   public float getSize() {
      return this.float319;
   }

   @Override
   public ArgbColor getColor() {
      return this.color;
   }

   public List<SpinMarkerDot> var1196() {
      return this.list106;
   }

   public int var1197() {
      return this.int429;
   }

   public float var1198() {
      return this.float238;
   }

   public void BotTickEvent(Vec3d var1) {
      this.vec3d37 = var1;
   }

   public void setColor(ArgbColor var1) {
      this.color = var1;
   }
}
