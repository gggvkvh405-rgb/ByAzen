package rtx.byazen.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.modules.impl.Visuals.FakePlayer;

@Mixin(PlayerEntity.class)
public abstract class PlayerAttackSoundMixin {
    @Unique
    private boolean byazen_attackingFakePlayer;
    @Unique
    private boolean byazen_criticalFakePlayerAttack;
    @Unique
    private boolean byazen_vanillaCriticalSound;

    @Inject(method="attack", at={@At(value="HEAD")}, require = 0)
    private void byazen_beginFakePlayerAttack(Entity target, CallbackInfo ci) {
        this.byazen_attackingFakePlayer = FakePlayer.isFakePlayer(target);
        PlayerEntity player = (PlayerEntity)(Object)this;
        boolean airborne = !player.isOnGround() || player.fallDistance > 0.0 || player.getVelocity().y > 0.05;
        this.byazen_criticalFakePlayerAttack = this.byazen_attackingFakePlayer && airborne && player.getAttackCooldownProgress(0.5f) > 0.9f && !player.isClimbing() && !player.isTouchingWater() && !player.hasBlindnessEffect() && !player.hasVehicle() && !player.isSprinting();
        this.byazen_vanillaCriticalSound = false;
    }

    @Inject(method="attack", at={@At(value="RETURN")}, require = 0)
    private void byazen_endFakePlayerAttack(Entity target, CallbackInfo ci) {
        if (this.byazen_criticalFakePlayerAttack && !this.byazen_vanillaCriticalSound) {
            ((PlayerEntity)(Object)this).addCritParticles(target);
        }
        this.byazen_attackingFakePlayer = false;
        this.byazen_criticalFakePlayerAttack = false;
    }
}
