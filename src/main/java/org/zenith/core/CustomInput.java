package org.zenith.core;

import net.minecraft.client.input.Input;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.util.MovementUtils;

public class CustomInput extends Input {
   public boolean boolean79 = false;
   public float float62;
   public float float63;
   public PlayerInput playerInput;
   public static final double double31 = 0.121;

   public CustomInput(PlayerInput var1) {
      this.playerInput = var1;
   }

   public void update() {
      if (this.playerInput.forward() != this.playerInput.backward()) {
         this.float62 = this.playerInput.forward() ? 1.0F : -1.0F;
      } else {
         this.float62 = 0.0F;
      }

      if (this.playerInput.left() == this.playerInput.right()) {
         this.float63 = 0.0F;
      } else {
         this.float63 = this.playerInput.left() ? 1.0F : -1.0F;
      }
   }

   public String toString() {
      return "SimulatedPlayerInput(forwards={"
         + this.playerInput.forward()
         + "}, backwards={"
         + this.playerInput.backward()
         + "}, left={"
         + this.playerInput.left()
         + "}, right={"
         + this.playerInput.right()
         + "}, jumping={"
         + this.playerInput.jump()
         + "}, sprinting="
         + this.playerInput.sprint()
         + ", slowDown="
         + this.playerInput.sneak()
         + ")";
   }

   public static CustomInput Easing(PlayerInput var0) {
      return new CustomInput(var0);
   }

   public static CustomInput ItemServiceBase(PlayerEntity var0) {
      Vec3d vec3d = var0.getEntityPos().subtract(new Vec3d(var0.lastX, var0.lastY, var0.lastZ));
      double d0 = vec3d.horizontalLengthSquared();
      PlayerInput playerinput = new PlayerInput(false, false, false, false, !var0.isOnGround(), var0.isSneaking(), d0 >= 0.014641);
      if (d0 > 0.0025000000000000005) {
         double d1 = MovementUtils.UiAnimation(vec3d, var0.getYaw());
         double d2 = MathHelper.wrapDegrees(d1);
         playerinput = MovementUtils.on23(playerinput, d2);
      }

      return new CustomInput(playerinput);
   }
}
