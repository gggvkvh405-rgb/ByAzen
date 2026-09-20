package rtx.kimiko.mixin.accessor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("handSwingTicks")
    public void kimiko_setSwingTime(int var1);

    @Accessor("handSwingProgress")
    public void kimiko_setAttackAnim(float var1);

    @Accessor("handSwinging")
    public void kimiko_setSwinging(boolean var1);

    @Accessor("jumpingCooldown")
    public void kimiko_setNoJumpDelay(int var1);

    @Accessor("preferredHand")
    public void kimiko_setSwingingArm(Hand var1);
}
