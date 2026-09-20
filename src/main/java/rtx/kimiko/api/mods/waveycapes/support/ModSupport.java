package rtx.kimiko.api.mods.waveycapes.support;

import rtx.kimiko.api.mods.waveycapes.CapeRenderer;
import rtx.kimiko.api.mods.waveycapes.compat.PlayerWrapper;

public interface ModSupport {
    boolean shouldBeUsed(PlayerWrapper playerWrapper);
    CapeRenderer getRenderer();
    default boolean blockFeatureRenderer(Object featureRenderer) {
        return false;
    }
}