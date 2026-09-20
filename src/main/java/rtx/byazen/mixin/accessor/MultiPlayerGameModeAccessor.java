package rtx.byazen.mixin.accessor;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerInteractionManager.class)
public interface MultiPlayerGameModeAccessor {
    @Invoker("syncSelectedSlot")
    public void byazen_ensureHasSentCarriedItem();

    @Accessor("breakingBlock")
    public boolean byazen_isDestroying();

    @Accessor("currentBreakingPos")
    public BlockPos byazen_getDestroyBlockPos();

    @Accessor("currentBreakingProgress")
    public float byazen_getDestroyProgress();

    @Accessor("breakingBlock")
    public void byazen_setDestroying(boolean var1);

    @Accessor("blockBreakingCooldown")
    public void byazen_setDestroyDelay(int var1);

    @Accessor("currentBreakingProgress")
    public void byazen_setDestroyProgress(float var1);
}
