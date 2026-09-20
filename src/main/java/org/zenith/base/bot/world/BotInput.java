package org.zenith.base.bot.world;

import net.minecraft.client.input.Input;
import net.minecraft.util.math.Vec2f;

/** Mutable movement input used by a headless player. */
public final class BotInput extends Input {
   public float movementForward;
   public float movementSideways;

   @Override
   public void tick() {
      this.movementForward = axis(this.playerInput.forward(), this.playerInput.backward());
      this.movementSideways = axis(this.playerInput.left(), this.playerInput.right());
      this.movementVector = new Vec2f(this.movementSideways, this.movementForward);
   }

   private static float axis(boolean positive, boolean negative) {
      return positive == negative ? 0.0F : positive ? 1.0F : -1.0F;
   }
}
