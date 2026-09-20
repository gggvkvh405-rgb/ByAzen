package rtx.kimiko.mixin.accessor;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerInteractionManager.class)
public interface MultiPlayerGameModeAccessor {
    @Invoker("syncSelectedSlot")
    public void kimiko_ensureHasSentCarriedItem();

    @Accessor("breakingBlock")
    public boolean kimiko_isDestroying();

    @Accessor("currentBreakingPos")
    public BlockPos kimiko_getDestroyBlockPos();

    @Accessor("currentBreakingProgress")
    public float kimiko_getDestroyProgress();

    @Accessor("breakingBlock")
    public void kimiko_setDestroying(boolean var1);

    @Accessor("blockBreakingCooldown")
    public void kimiko_setDestroyDelay(int var1);

    @Accessor("currentBreakingProgress")
    public void kimiko_setDestroyProgress(float var1);
}
