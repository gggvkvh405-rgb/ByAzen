package org.zenith.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationMath;
import org.zenith.util.MathUtils;

public final class ScreenProjection {
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static Vec3d BotDisconnectEvent(Vec3d var0) {
      Vector3f vector3f = var0.subtract(minecraftClient3.getEntityRenderDispatcher().camera.getCameraPos()).toVector3f();
      int[] aint = new int[4];
      GL11.glGetIntegerv(2978, aint);
      Vector3f vector3f1 = new Vector3f();
      Vector4f vector4f = new Vector4f(vector3f.x, vector3f.y, vector3f.z, 1.0F).mul(WorldRender.itemStack7());
      Matrix4f matrix4f = new Matrix4f(WorldRender.string39());
      matrix4f.project(vector4f.x(), vector4f.y(), vector4f.z(), aint, vector3f1);
      return new Vec3d(
         vector3f1.x / minecraftClient3.getWindow().getScaleFactor(),
         (minecraftClient3.getWindow().getHeight() - vector3f1.y) / minecraftClient3.getWindow().getScaleFactor(),
         vector3f1.z
      );
   }

   public static boolean BotWorldJoinEvent(Vec3d var0) {
      Camera camera = minecraftClient3.getEntityRenderDispatcher().camera;
      Rotation ililiiili1ll1li11 = RotationMath.BotChatEvent(var0);
      return Math.abs(MathHelper.wrapDegrees(ililiiili1ll1li11.GrimGlide() - camera.getYaw())) < 90.0F
            && Math.abs(MathHelper.wrapDegrees(ililiiili1ll1li11.GuiWalk() - camera.getPitch())) < 60.0F
         || NbtEditor(new Box(BlockPos.ofFloored(var0)));
   }

   public static boolean NbtEditor(Box var0) {
      if (var0 == null) {
         return false;
      }

      Camera camera = minecraftClient3.gameRenderer.getCamera();
      Frustum frustum = new Frustum(WorldRender.itemStack7(), WorldRender.string39());
      Vec3d cameraPos = camera.getCameraPos();
      frustum.setPosition(cameraPos.x, cameraPos.y, cameraPos.z);
      return frustum.isVisible(var0);
   }

   public static boolean on23(Vector4d var0) {
      return var0 == null || var0.x < 0.0 && var0.z < 1.0 || var0.y < 0.0 && var0.w < 1.0;
   }

   public static double UiAnimation(Vector4d var0) {
      return var0.x + (var0.z - var0.x) / 2.0;
   }

   public static Vec3d[] on23(Entity var0, Vec3d var1) {
      Box box = var0.getBoundingBox();
      Box box1 = new Box(
         box.minX - var0.getX() + var1.x - 0.1F,
         box.minY - var0.getY() + var1.y - 0.1F,
         box.minZ - var0.getZ() + var1.z - 0.1F,
         box.maxX - var0.getX() + var1.x + 0.1F,
         box.maxY - var0.getY() + var1.y + 0.1F,
         box.maxZ - var0.getZ() + var1.z + 0.1F
      );
      return new Vec3d[]{
         new Vec3d(box1.minX, box1.minY, box1.minZ),
         new Vec3d(box1.minX, box1.maxY, box1.minZ),
         new Vec3d(box1.maxX, box1.minY, box1.minZ),
         new Vec3d(box1.maxX, box1.maxY, box1.minZ),
         new Vec3d(box1.minX, box1.minY, box1.maxZ),
         new Vec3d(box1.minX, box1.maxY, box1.maxZ),
         new Vec3d(box1.maxX, box1.minY, box1.maxZ),
         new Vec3d(box1.maxX, box1.maxY, box1.maxZ)
      };
   }

   public static Vector4d TradeGuardService(Entity var0) {
      Vector4d vector4d = null;

      for (Vec3d vec3d : on23(var0, MathUtils.CloudResponse(var0))) {
         vec3d = BotDisconnectEvent(new Vec3d(vec3d.x, vec3d.y, vec3d.z));
         if (vec3d.z > 0.0 && vec3d.z < 1.0) {
            if (vector4d == null) {
               vector4d = new Vector4d(vec3d.x, vec3d.y, vec3d.z, 0.0);
            }

            vector4d.x = Math.min(vec3d.x, vector4d.x);
            vector4d.y = Math.min(vec3d.y, vector4d.y);
            vector4d.z = Math.max(vec3d.x, vector4d.z);
            vector4d.w = Math.max(vec3d.y, vector4d.w);
         }
      }

      return vector4d;
   }

   public ScreenProjection() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
