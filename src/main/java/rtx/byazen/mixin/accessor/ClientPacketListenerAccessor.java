package rtx.byazen.mixin.accessor;

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
    public void byazen_setLevels(Set<RegistryKey<World>> var1);

    @Accessor("worldProperties")
    public ClientWorld.Properties byazen_getLevelData();

    @Accessor("world")
    public void byazen_setLevel(ClientWorld var1);

    @Accessor("worldProperties")
    public void byazen_setLevelData(ClientWorld.Properties var1);

    @Accessor("chunkLoadDistance")
    public void byazen_setServerChunkRadius(int var1);

    @Accessor("simulationDistance")
    public void byazen_setServerSimulationDistance(int var1);

    @Accessor("simulationDistance")
    public int byazen_getServerSimulationDistance();

    @Accessor("chunkLoadDistance")
    public int byazen_getServerChunkRadius();
}
