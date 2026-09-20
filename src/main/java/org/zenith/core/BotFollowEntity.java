package org.zenith.core;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.zenith.managers.BotEntity;
import org.zenith.util.ArgbColor;

public class BotFollowEntity extends BotEntity {
   public static final MinecraftClient minecraftClient2 = MinecraftClient.getInstance();
   public static final int int369 = 10;
   public final Vec3d vec3d41;
   public float float238 = 1.0F;

   public BotFollowEntity(Vec3d var1, Vec3d var2, int var3, float var4, ArgbColor var5, String var6, float var7, float var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      double d0 = Math.toRadians(threadlocalrandom.nextDouble() * 80.0);
      double d1 = threadlocalrandom.nextDouble() * Math.PI * 2.0;
      double d2 = Math.sin(d0);
      double d3 = -Math.cos(d0);
      this.vec3d41 = new Vec3d(Math.cos(d1) * d2, d3, Math.sin(d1) * d2).normalize();
   }

   public void on23(Vec3d var1, boolean var2, float var3) {
      this.int426 = this.int143;
      this.BotPacketEvent(var1);
      if (this.int143 > 0) {
         this.ModeSetting = this.TriggerBot;
         Vec3d vec3d = this.vec3d37.multiply(var3);
         if (minecraftClient2.world != null) {
            int i = -1;
            Vec3d vec3d1 = this.TriggerBot;

            for (int j = 1; j <= 10; j++) {
               vec3d1 = vec3d1.add(vec3d);
               if (!minecraftClient2.world.getBlockState(BlockPos.ofFloored(vec3d1)).isAir()) {
                  i = j;
                  break;
               }
            }

            if (i > 0) {
               float f1 = i / 10.0F;
               this.float238 = f1 * f1;
               if (i <= 1) {
                  this.int143 = 0;
                  return;
               }
            } else {
               this.float238 = Math.min(this.float238 + 0.1F, 1.0F);
            }
         }

         this.TriggerBot = this.TriggerBot.add(vec3d);
         if (var2) {
            this.vec3d37 = this.vec3d37.multiply(0.9);
            this.vec3d37 = new Vec3d(this.vec3d37.x * 0.85, this.vec3d37.y - 0.008, this.vec3d37.z * 0.85);
            if (this.vec3d37.y < -0.5) {
               this.vec3d37 = new Vec3d(this.vec3d37.x, -0.5, this.vec3d37.z);
            }

            if (this.TriggerBot.y <= minecraftClient2.world.getBottomY()) {
               this.int143 = 0;
            }
         } else {
            float f = 1.0F - (float)this.int143 / this.int444;
            if (f < 0.7F) {
               this.vec3d37 = this.vec3d37.multiply(0.995);
            } else {
               this.vec3d37 = this.vec3d37.multiply(0.92);
            }

            if (this.TriggerBot.y <= minecraftClient2.world.getBottomY() || this.TriggerBot.y > 320.0) {
               this.int143 = 0;
            }
         }

         this.var11928();
      }
   }

   @Override
   public float var11927() {
      return this.EventPushOutOfBlocks(1.0F);
   }

   @Override
   public float EventPushOutOfBlocks(float var1) {
      float f = this.EventInjectAddEntity(var1);
      float f1;
      if (f < 0.1F) {
         f1 = f / 0.1F;
      } else if (f > 0.7F) {
         f1 = 1.0F - (f - 0.7F) / 0.3F;
      } else {
         f1 = 1.0F;
      }

      return f1 * this.float238;
   }
}
