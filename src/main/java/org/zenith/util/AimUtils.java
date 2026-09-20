package org.zenith.util;

import baritone.api.BaritoneAPI;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.core.ClientProvider;
import org.zenith.event.EventTick;
import org.zenith.event.PacketEvent;

public final class AimUtils {
   public static final float float287 = 47.0F;
   public static final float float288 = 1.2F;
   public static final long long154 = 200L;
   public static float float289;
   public static float float290;
   public static long long155;
   public static float float291;
   public static float float292;
   public static boolean boolean180;
   public static float float293;
   public static float float294;
   public static float float295;
   public static float float296;
   public static boolean boolean181;
   public static float float297;
   public static float float298;
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static void AnalyticsTracker(Vec3d var0) {
      float[] afloat = ConfigJsonUtil(var0);
      EmoteMetadata(afloat[0], afloat[1]);
   }

   public static boolean on23(Vec3d var0, float var1) {
      float[] afloat = ConfigJsonUtil(var0);
      float f = MathHelper.wrapDegrees(afloat[0] - float289);
      float f1 = afloat[1] - float290;
      return Math.sqrt(f * f + f1 * f1) <= var1;
   }

   public static float[] ConfigJsonUtil(Vec3d var0) {
      Vec3d vec3d = minecraftClient3.player.getEyePos();
      double d0 = var0.x - vec3d.x;
      double d1 = var0.y - vec3d.y;
      double d2 = var0.z - vec3d.z;
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      return new float[]{(float)Math.toDegrees(Math.atan2(d2, d0)) - 90.0F, (float)(-Math.toDegrees(Math.atan2(d1, d3)))};
   }

   public static void EmoteMetadata(float var0, float var1) {
      long i = System.currentTimeMillis();
      if (long155 == 0L || !boolean181 && i - long155 > 400L) {
         float289 = minecraftClient3.player.getYaw();
         float290 = minecraftClient3.player.getPitch();
      }

      long155 = i;
      float[] afloat = ModuleStateStore(float289, float290, var0, var1);
      float289 = afloat[0];
      float290 = afloat[1];
   }

   public static void CloudResponse(Vec3d var0) {
      float[] afloat = ConfigJsonUtil(var0);
      float[] afloat1 = ModuleStateStore(
         minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch(), afloat[0], afloat[1]
      );
      minecraftClient3.player.setYaw(afloat1[0]);
      minecraftClient3.player.setPitch(afloat1[1]);
      float289 = afloat1[0];
      float290 = afloat1[1];
      long155 = System.currentTimeMillis();
   }

   public static void BotRespawnEvent(boolean var0) {
      boolean181 = var0;
      if (var0) {
         long155 = 0L;
      }
   }

   public static float[] ModuleStateStore(float var0, float var1, float var2, float var3) {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      float297 = MathHelper.clamp(float297 * 0.96F + (float)threadlocalrandom.nextGaussian() * 0.15F, -1.2F, 1.2F);
      float298 = MathHelper.clamp(float298 * 0.96F + (float)threadlocalrandom.nextGaussian() * 0.1F, -0.9F, 0.9F);
      float f = MathHelper.wrapDegrees(var2 + float297 - var0);
      float f1 = MathHelper.clamp(var3 + float298, -90.0F, 90.0F) - var1;
      float f2 = (float)Math.sqrt(f * f + f1 * f1);
      if (f2 < 0.08F) {
         return new float[]{var0, var1};
      }

      float f3 = 0.31F + threadlocalrandom.nextFloat() * 0.28F;
      float f4 = MathHelper.clamp(f2 * f3, Math.min(f2, 1.2F), 47.0F);
      if (f2 < 5.0F && f2 > 1.5F && threadlocalrandom.nextFloat() < 0.1F) {
         f4 = Math.min(f2 * 1.2F, 47.0F);
      }

      float f5 = f4 / f2;
      float f6 = f * f5 + (float)threadlocalrandom.nextGaussian() * 0.27F;
      float f7 = f1 * f5 * 0.85F + (float)threadlocalrandom.nextGaussian() * 0.18F;
      return new float[]{var0 + StopUsingItemEvent(f6), MathHelper.clamp(var1 + StopUsingItemEvent(f7), -90.0F, 90.0F)};
   }

   public static void TradeGuardService(Vec3d var0) {
      double d0 = var0.x - minecraftClient3.player.getX();
      double d1 = var0.z - minecraftClient3.player.getZ();
      float f = (float)Math.toDegrees(Math.atan2(d1, d0)) - 90.0F;
      float f1 = MathHelper.wrapDegrees(f - minecraftClient3.player.getYaw());
      boolean flag = f1 > -67.5F && f1 < 67.5F;
      boolean flag1 = f1 > 112.5F || f1 < -112.5F;
      boolean flag2 = f1 < -22.5F && f1 > -157.5F;
      boolean flag3 = f1 > 22.5F && f1 < 157.5F;
      minecraftClient3.options.forwardKey.setPressed(flag);
      minecraftClient3.options.backKey.setPressed(flag1);
      minecraftClient3.options.leftKey.setPressed(flag2);
      minecraftClient3.options.rightKey.setPressed(flag3);
      minecraftClient3.options.sprintKey.setPressed(flag);
   }

   public static void call007() {
      minecraftClient3.options.forwardKey.setPressed(false);
      minecraftClient3.options.backKey.setPressed(false);
      minecraftClient3.options.leftKey.setPressed(false);
      minecraftClient3.options.rightKey.setPressed(false);
      minecraftClient3.options.sprintKey.setPressed(false);
   }

   public static boolean double60() {
      return boolean180 && minecraftClient3.player != null;
   }

   public static float EventDead(float var0) {
      return float295 + MathHelper.wrapDegrees(float293 - float295) * var0;
   }

   public static float HotbarInputEvent(float var0) {
      return MathHelper.lerp(var0, float296, float294);
   }

   public static void EmoteManager(float var0, float var1) {
      float f = MathHelper.wrapDegrees(var0 - float289);
      if (Math.abs(f) > 30.0F) {
         float289 = var0 - Math.copySign(30.0F, f);
      }

      float f1 = var1 - float290;
      if (Math.abs(f1) > 30.0F) {
         float290 = MathHelper.clamp(var1 - Math.copySign(30.0F, f1), -90.0F, 90.0F);
      }
   }

   public static boolean double61() {
      try {
         return BaritoneAPI.getProvider() != null && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
      } catch (Throwable throwable) {
         return false;
      }
   }

   public static float StopUsingItemEvent(float var0) {
      double d0 = (Double)minecraftClient3.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
      double d1 = d0 * d0 * d0 * 1.2;
      return d1 <= 0.0 ? var0 : (float)(Math.round(var0 / d1) * d1);
   }

   static {
      EventManager.register(new PacketRotationListener());
   }

   private static final class PacketRotationListener {
      @EventTarget
      public void onPacket(PacketEvent event) {
         if (event.AntiInvisible()
            && ClientProvider.minecraftClient3.player != null
            && event.ItemScroller() instanceof PlayerMoveC2SPacket packet
            && packet.changeLook) {
            if (System.currentTimeMillis() - AimUtils.long155 <= 200L) {
               packet.yaw = AimUtils.float289;
               packet.pitch = AimUtils.float290;
            } else {
               float yawDelta = MathHelper.wrapDegrees(packet.yaw - AimUtils.float289);
               float pitchDelta = packet.pitch - AimUtils.float290;
               if (AimUtils.double61()) {
                  AimUtils.float289 = packet.yaw;
                  AimUtils.float290 = packet.pitch;
               } else if (AimUtils.boolean181 && AimUtils.long155 != 0L) {
                  packet.yaw = AimUtils.float289;
                  packet.pitch = AimUtils.float290;
               } else if (Math.abs(yawDelta) <= 8.0F && Math.abs(pitchDelta) <= 8.0F) {
                  AimUtils.float289 = packet.yaw;
                  AimUtils.float290 = packet.pitch;
               } else {
                  AimUtils.float289 += MathHelper.clamp(yawDelta, -35.0F, 35.0F);
                  AimUtils.float290 = MathHelper.clamp(AimUtils.float290 + MathHelper.clamp(pitchDelta, -35.0F, 35.0F), -90.0F, 90.0F);
                  AimUtils.EmoteManager(packet.yaw, packet.pitch);
                  packet.yaw = AimUtils.float289;
                  packet.pitch = AimUtils.float290;
               }

               AimUtils.float291 = packet.yaw;
               AimUtils.float292 = packet.pitch;
               if (!AimUtils.boolean180) {
                  AimUtils.boolean180 = true;
                  AimUtils.float293 = AimUtils.float295 = AimUtils.float291;
                  AimUtils.float294 = AimUtils.float296 = AimUtils.float292;
               }
            }
         }
      }

      @EventTarget
      public void onUpdate(EventTick event) {
         if (ClientProvider.minecraftClient3.player != null) {
            boolean recentlyModified = System.currentTimeMillis() - AimUtils.long155 <= 200L;
            float yaw = recentlyModified ? AimUtils.float289 : (AimUtils.boolean180 ? AimUtils.float291 : ClientProvider.minecraftClient3.player.getYaw());
            float pitch = recentlyModified ? AimUtils.float290 : (AimUtils.boolean180 ? AimUtils.float292 : ClientProvider.minecraftClient3.player.getPitch());
            AimUtils.float295 = AimUtils.float293;
            AimUtils.float296 = AimUtils.float294;
            float yawDelta = MathHelper.wrapDegrees(yaw - AimUtils.float293);
            float pitchDelta = pitch - AimUtils.float294;
            AimUtils.float293 += MathHelper.clamp(yawDelta * 0.45F, -40.0F, 40.0F);
            AimUtils.float294 = MathHelper.clamp(AimUtils.float294 + MathHelper.clamp(pitchDelta * 0.45F, -40.0F, 40.0F), -90.0F, 90.0F);
         }
      }
   }
}
