package org.zenith.core;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Hand;
import org.zenith.module.combat.Criticals;
import org.zenith.util.MathUtils;

public final class PlayerStateService implements GameService {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static void Easing(Entity var0) {
      minecraftClient3.interactionManager.attackEntity(minecraftClient3.player, var0);
      minecraftClient3.player.swingHand(Hand.MAIN_HAND);
   }

   public static boolean ScreenProjection() {
      return minecraftClient3.player.hasStatusEffect(StatusEffects.BLINDNESS)
         || minecraftClient3.player.hasStatusEffect(StatusEffects.LEVITATION)
         || EffectEngine.ItemSpec(Blocks.COBWEB)
         || minecraftClient3.player.isSubmergedInWater()
         || minecraftClient3.player.isSwimming()
         || minecraftClient3.player.isInLava()
         || minecraftClient3.player.isClimbing()
         || !EffectEngine.UiAnimation(EntityPose.STANDING) && minecraftClient3.player.isInSneakingPose()
         || minecraftClient3.player.getAbilities().flying;
   }

   public static boolean on23(MovementController var0) {
      return var0.ItemSpec(StatusEffects.BLINDNESS)
         || var0.ItemSpec(StatusEffects.LEVITATION)
         || EffectEngine.on23(var0.box9, Blocks.COBWEB)
         || var0.call177()
         || var0.boolean160
         || var0.call214()
         || var0.call142()
         || !EffectEngine.UiAnimation(EntityPose.STANDING) && minecraftClient3.player.isInSneakingPose()
         || minecraftClient3.player.getAbilities().flying;
   }

   public static boolean RandomUtils() {
      boolean flag = minecraftClient3.player.fallDistance > (CooldownTimer() ? MathUtils.EnchantItemSpec(0.15F, 0.7F) : 0.0F)
         && (minecraftClient3.player.fallDistance < 0.08 || !MovementController.TargetAcquireEvent(1).onGround);
      return !minecraftClient3.player.isOnGround() && flag || Criticals.criticals.isEnabled() && Criticals.criticals.call125();
   }

   public static boolean StopWatch() {
      return minecraftClient3.player.isOnGround() || minecraftClient3.player.fallDistance < 0.12F || minecraftClient3.player.isSwimming();
   }

   public static boolean CooldownTimer() {
      return false;
   }

   public static boolean UiAnimation(MovementController var0) {
      boolean flag = var0.float75 > 0.0F && (var0.float75 < 0.08 || !MovementController.TargetAcquireEvent(2).onGround);
      return !var0.onGround && flag || Criticals.criticals.isEnabled() && Criticals.criticals.call125();
   }

   public PlayerStateService() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
