package rtx.kimiko.mixin.accessor;

import java.util.Set;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPacketListenerAccessor {
    @Accessor("worldKeys")
    public void kimiko_setLevels(Set<RegistryKey<World>> var1);

    @Accessor("worldProperties")
    public ClientWorld.Properties kimiko_getLevelData();

    @Accessor("world")
    public void kimiko_setLevel(ClientWorld var1);

    @Accessor("worldProperties")
    public void kimiko_setLevelData(ClientWorld.Properties var1);

    @Accessor("chunkLoadDistance")
    public void kimiko_setServerChunkRadius(int var1);

    @Accessor("simulationDistance")
    public void kimiko_setServerSimulationDistance(int var1);

    @Accessor("simulationDistance")
    public int kimiko_getServerSimulationDistance();

    @Accessor("chunkLoadDistance")
    public int kimiko_getServerChunkRadius();
}
