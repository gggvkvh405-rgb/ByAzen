package org.zenith.core;

import java.lang.ref.WeakReference;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.managers.BotEntity;
import org.zenith.util.ArgbColor;

public class BotGotoEntity extends BotEntity {
   public static final MinecraftClient minecraftClient6 = MinecraftClient.getInstance();
   public double double146;
   public double double147;
   public double double148;
   public WeakReference<Entity> weakReference;
   public float float353;
   public float float354;
   public boolean boolean191;
   public static final float float355 = 0.3F;
   public static final float float356 = 0.2F;

   public BotGotoEntity(Vec3d var1, Vec3d var2, int var3, float var4, ArgbColor var5, String var6, float var7, float var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void on23(Entity var1, float var2, float var3) {
      this.weakReference = new WeakReference<>(var1);
      this.float353 = var2;
      this.float354 = var3;
      Vec3d vec3d = var1.getEntityPos();
      this.double146 = Math.atan2(this.TriggerBot.z - vec3d.z, this.TriggerBot.x - vec3d.x);
      this.double147 = Math.sqrt(Math.pow(this.TriggerBot.x - vec3d.x, 2.0) + Math.pow(this.TriggerBot.z - vec3d.z, 2.0));
      this.double148 = this.TriggerBot.y - vec3d.y;
   }

   public void UiAnimation(Vec3d var1, int var2) {
      this.int426 = this.int143;
      this.BotPacketEvent(var1);
      if (this.int143 > 0) {
         this.ModeSetting = this.TriggerBot;
         float f = 1.0F - (float)this.int143 / this.int444;
         switch (var2) {
            case 1:
               this.EventHookPacketProcess(f);
               break;
            case 2:
               this.var11912();
               break;
            default:
               this.var11911();
         }

         this.var11928();
      }
   }

   public void var11911() {
      this.TriggerBot = this.TriggerBot.add(this.vec3d37);
      this.vec3d37 = new Vec3d(this.vec3d37.x * 0.98, this.vec3d37.y * 0.98 - 3.0E-4, this.vec3d37.z * 0.98);
      if (minecraftClient6.world != null) {
         BlockPos blockpos = BlockPos.ofFloored(this.TriggerBot.x, this.TriggerBot.y - 0.1, this.TriggerBot.z);
         if (!minecraftClient6.world.getBlockState(blockpos).isAir()) {
            this.vec3d37 = new Vec3d(this.vec3d37.x / 1.1, -this.vec3d37.y / 1.1, this.vec3d37.z / 1.1);
         }
      }

      if (this.TriggerBot.y <= minecraftClient6.world.getBottomY()) {
         this.int143 = 0;
      }
   }

   public void EventHookPacketProcess(float var1) {
      float f = (float)Math.sin(var1 * Math.PI);
      f = 0.3F + f * 1.2F;
      this.TriggerBot = this.TriggerBot.add(this.vec3d37.multiply(f));
      this.vec3d37 = this.vec3d37.multiply(0.96);
   }

   public void var11912() {
      float f = 1.0F - (float)this.int143 / this.int444;
      Entity entity = this.weakReference != null ? this.weakReference.get() : null;
      if (entity != null && !entity.isRemoved()) {
         if (!(f > 0.5F) && !this.boolean191) {
            this.double146 = this.double146 + this.float353 / 10.0;
            this.double147 = this.double147 + this.float354 / 100.0;
            double d0 = entity.getX();
            double d1 = entity.getZ();
            double d2 = entity.getY();
            double d3 = d0 + Math.cos(this.double146) * this.double147;
            double d4 = d1 + Math.sin(this.double146) * this.double147;
            double d5 = d2 + this.double148 + this.float357 * 0.1;
            if (f < 0.3F) {
               this.TriggerBot = new Vec3d(d3, d5, d4);
            } else {
               float f1 = (f - 0.3F) / 0.2F;
               f1 = f1 * f1 * (3.0F - 2.0F * f1);
               if (this.vec3d37.equals(Vec3d.ZERO)) {
                  this.var11913();
               }

               Vec3d vec3d = this.TriggerBot.add(this.vec3d37);
               this.vec3d37 = new Vec3d(this.vec3d37.x * 0.98, this.vec3d37.y * 0.98 - 3.0E-4, this.vec3d37.z * 0.98);
               this.TriggerBot = new Vec3d(
                  MathHelper.lerp(f1, d3, vec3d.x),
                  MathHelper.lerp(f1, d5, vec3d.y),
                  MathHelper.lerp(f1, d4, vec3d.z)
               );
            }
         } else {
            if (!this.boolean191) {
               this.boolean191 = true;
               this.var11913();
            }

            this.var11911();
         }
      } else {
         if (!this.boolean191) {
            this.boolean191 = true;
            this.var11913();
         }

         this.var11911();
      }
   }

   public void var11913() {
      double d0 = -Math.sin(this.double146) * this.float353 * 0.02;
      double d1 = Math.cos(this.double146) * this.float353 * 0.02;
      double d2 = Math.cos(this.double146) * this.float354 * 0.005;
      double d3 = Math.sin(this.double146) * this.float354 * 0.005;
      this.vec3d37 = new Vec3d(d0 + d2 + (Math.random() - 0.5) * 0.02, 0.015 + Math.random() * 0.025, d1 + d3 + (Math.random() - 0.5) * 0.02);
   }

   @Override
   public float var11927() {
      return this.EventPushOutOfBlocks(1.0F);
   }

   @Override
   public float EventPushOutOfBlocks(float var1) {
      float f = this.EventInjectAddEntity(var1);
      if (f < 0.1F) {
         return f / 0.1F;
      }

      float f1 = 0.5F;
      if (!this.boolean191 && !(f > f1)) {
         return 1.0F;
      }

      float f2 = (f - f1) / (1.0F - f1);
      return 1.0F - f2 * f2;
   }
}
