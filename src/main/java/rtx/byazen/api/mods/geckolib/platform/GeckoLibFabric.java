package rtx.byazen.api.mods.geckolib.platform;
import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import rtx.byazen.api.mods.geckolib.GeckoLibConstants;
import rtx.byazen.api.mods.geckolib.service.GeckoLibPlatform;

public final class GeckoLibFabric
implements GeckoLibPlatform {
    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public <T> Supplier<ComponentType<T>> registerDataComponent(String string, UnaryOperator<ComponentType.Builder<T>> unaryOperator) {
        ComponentType componentType = (ComponentType)Registry.register((Registry)Registries.DATA_COMPONENT_TYPE, (String)GeckoLibConstants.id(string).toString(), (Object)((ComponentType.Builder)unaryOperator.apply(ComponentType.builder())).build());
        return () -> componentType;
    }

    @Override
    public boolean isPhysicalClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}

