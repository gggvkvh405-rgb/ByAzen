package rtx.kimiko.mixin.accessor;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.client.network.ClientRegistries;
import net.minecraft.client.resource.ClientDataPackManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientConfigurationNetworkHandler.class)
public interface ClientConfigurationPacketListenerAccessor {
    @Accessor("dataPackManager")
    public ClientDataPackManager kimiko_getKnownPacks();

    @Accessor("enabledFeatures")
    public FeatureSet kimiko_getEnabledFeatures();

    @Accessor("chatState")
    public ChatHud.ChatState chatState();

    @Accessor("clientRegistries")
    public ClientRegistries kimiko_getRegistryDataCollector();

    @Accessor("registryManager")
    public DynamicRegistryManager.Immutable kimiko_getReceivedRegistries();
}
