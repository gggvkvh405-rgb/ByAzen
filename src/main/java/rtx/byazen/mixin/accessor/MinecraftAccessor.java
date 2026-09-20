package rtx.byazen.mixin.accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftAccessor {
    @Invoker("setWorld")
    public void byazen_updateLevelInEngines(ClientWorld var1);

    @Invoker("doAttack")
    public boolean byazen_startAttack();

    @Invoker("doItemUse")
    public void byazen_startUseItem();

    @Accessor("itemUseCooldown")
    public int byazen_getRightClickDelay();

    @Accessor("itemUseCooldown")
    public void byazen_setRightClickDelay(int var1);
}
