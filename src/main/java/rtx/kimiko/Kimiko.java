package rtx.kimiko;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rtx.kimiko.api.mods.geckolib.GeckoLib;
import rtx.kimiko.manager.Manager;

public class Kimiko
implements ModInitializer,
ClientModInitializer {
    public static final String MOD_ID = "kimiko";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"kimiko");

    public void onInitializeClient() {
        Manager.init();
    }

    public void onInitialize() {
        new GeckoLib().onInitialize();
    }
}

