package org.zenith.rotation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class RotationMath {
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static Rotation boolean122() {
      return new Rotation(minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch());
   }

   public static Rotation Event08(Vec3d var0) {
      return new Rotation(
         (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var0.z, var0.x)) - 90.0),
         (float)MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(var0.y, Math.hypot(var0.x, var0.z))))
      );
   }

   public static Rotation BotChatEvent(Vec3d var0) {
      return Event08(var0.subtract(minecraftClient3.player.getEyePos()));
   }

   public RotationMath() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
