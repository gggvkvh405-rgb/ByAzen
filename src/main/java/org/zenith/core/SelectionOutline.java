package org.zenith.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.util.ArgbColor;

public class SelectionOutline implements PositionProvider {
   public Vec3d TriggerBot;
   public Vec3d ModeSetting;
   public Vec3d vec3d37;
   public int int411;
   public int int412;
   public int int413;
   public final int int414;
   public final float float299;
   public final ArgbColor var11943;
   public final String string115;
   public float rotation;
   public final float float300;
   public boolean onGround;
   public static final MinecraftClient minecraftClient5 = MinecraftClient.getInstance();

   public SelectionOutline(Vec3d var1, Vec3d var2, int var3, float var4, ArgbColor var5, String var6, float var7, float var8) {
      this.TriggerBot = var1;
      this.ModeSetting = var1;
      this.vec3d37 = var2;
      this.int411 = 0;
      this.int412 = 0;
      this.int414 = var3;
      this.float299 = var4;
      this.var11943 = var5;
      this.string115 = var6;
      this.rotation = var7;
      this.float300 = var8;
      this.onGround = false;
   }

   public void update() {
      this.int411++;
      this.ModeSetting = this.TriggerBot;
      this.int413 = this.int412;
      if (this.onGround) {
         this.int412++;
      } else if (minecraftClient5.world != null) {
         float f = this.float299 / 2.0F;
         double d0 = this.TriggerBot.x;
         double d1 = this.TriggerBot.y;
         double d2 = this.TriggerBot.z;
         Vec3d vec3d = this.vec3d37;
         Box box = new Box(d0 - f, d1 - f * 2.0F + vec3d.y, d2 - f, d0 + f, d1 + f + vec3d.y, d2 + f);
         if (!minecraftClient5.world.isSpaceEmpty(null, box)) {
            if (vec3d.y < 0.0) {
               this.onGround = true;
            }

            vec3d = new Vec3d(vec3d.x, 0.0, vec3d.z);
         } else {
            d1 += vec3d.y;
         }

         Box box1 = new Box(d0 - f + vec3d.x, d1 - f, d2 - f, d0 + f + vec3d.x, d1 + f, d2 + f);
         if (!minecraftClient5.world.isSpaceEmpty(null, box1)) {
            vec3d = new Vec3d(0.0, vec3d.y, vec3d.z);
         } else {
            d0 += vec3d.x;
         }

         Box box2 = new Box(d0 - f, d1 - f, d2 - f + vec3d.z, d0 + f, d1 + f, d2 + f + vec3d.z);
         if (!minecraftClient5.world.isSpaceEmpty(null, box2)) {
            vec3d = new Vec3d(vec3d.x, vec3d.y, 0.0);
         } else {
            d2 += vec3d.z;
         }

         this.vec3d37 = vec3d;
         this.TriggerBot = new Vec3d(d0, d1, d2);
         if (this.onGround) {
            this.vec3d37 = Vec3d.ZERO;
         }
      }
   }

   @Override
   public boolean float304() {
      return this.int411 >= this.int414 * 2 || this.int412 >= this.int414;
   }

   @Override
   public float var11927() {
      return this.EventPushOutOfBlocks(1.0F);
   }

   @Override
   public float EventPushOutOfBlocks(float var1) {
      if (!this.onGround) {
         return 1.0F;
      }

      float f = this.int413 + (this.int412 - this.int413) * var1;
      return 1.0F - f / this.int414;
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

   public int boolean85() {
      return this.int411;
   }

   public int var1113() {
      return this.int412;
   }

   public int var1114() {
      return this.int413;
   }

   public int var14360() {
      return this.int414;
   }

   @Override
   public float getSize() {
      return this.float299;
   }

   @Override
   public ArgbColor getColor() {
      return this.var11943;
   }

   @Override
   public String var111() {
      return this.string115;
   }

   @Override
   public float getRotation() {
      return this.rotation;
   }

   public float var1112() {
      return this.float300;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public void UiAnimation(Vec3d var1) {
      this.TriggerBot = var1;
   }

   public void BotRespawnEvent(Vec3d var1) {
      this.ModeSetting = var1;
   }

   public void BotTickEvent(Vec3d var1) {
      this.vec3d37 = var1;
   }

   public void SprintPacketEvent(int var1) {
      this.int411 = var1;
   }

   public void EventTick(int var1) {
      this.int412 = var1;
   }

   public void EventTickEnd(int var1) {
      this.int413 = var1;
   }

   public void EventHookTickEvent(float var1) {
      this.rotation = var1;
   }

   public void setOnGround(boolean var1) {
      this.onGround = var1;
   }
}
