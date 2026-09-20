package rtx.kimiko.mixin.accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftAccessor {
    @Invoker("setWorld")
    public void kimiko_updateLevelInEngines(ClientWorld var1);

    @Invoker("doAttack")
    public boolean kimiko_startAttack();

    @Invoker("doItemUse")
    public void kimiko_startUseItem();

    @Accessor("itemUseCooldown")
    public int kimiko_getRightClickDelay();

    @Accessor("itemUseCooldown")
    public void kimiko_setRightClickDelay(int var1);
}
