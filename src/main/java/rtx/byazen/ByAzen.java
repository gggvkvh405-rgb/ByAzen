package rtx.byazen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rtx.byazen.api.mods.geckolib.GeckoLib;
import rtx.byazen.manager.Manager;

public class ByAzen
implements ModInitializer,
ClientModInitializer {
    public static final String MOD_ID = "byazen";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"byazen");

    public void onInitializeClient() {
        Manager.init();
    }

    public void onInitialize() {
        new GeckoLib().onInitialize();
    }
}

