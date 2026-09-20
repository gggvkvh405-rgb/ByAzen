package rtx.byazen.mixin.accessor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("handSwingTicks")
    public void byazen_setSwingTime(int var1);

    @Accessor("handSwingProgress")
    public void byazen_setAttackAnim(float var1);

    @Accessor("handSwinging")
    public void byazen_setSwinging(boolean var1);

    @Accessor("jumpingCooldown")
    public void byazen_setNoJumpDelay(int var1);

    @Accessor("preferredHand")
    public void byazen_setSwingingArm(Hand var1);
}
