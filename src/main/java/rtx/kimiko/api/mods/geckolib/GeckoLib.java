package rtx.kimiko.api.mods.geckolib;
import net.fabricmc.api.ModInitializer;
import rtx.kimiko.api.mods.geckolib.GeckoLibConstants;
import rtx.kimiko.api.mods.geckolib.service.GeckoLibNetworking;

public final class GeckoLib
implements ModInitializer {
    public void onInitialize() {
        GeckoLibConstants.init();
        GeckoLibNetworking.init();
    }
}

