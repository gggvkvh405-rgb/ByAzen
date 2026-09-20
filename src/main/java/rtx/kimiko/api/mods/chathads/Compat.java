package rtx.kimiko.api.mods.chathads;

import net.fabricmc.loader.api.FabricLoader;

public final class Compat {
    private Compat() {}

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
