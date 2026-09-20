package rtx.kimiko.api.mods.geckolib.animatable.stateless;

import rtx.kimiko.api.mods.geckolib.animatable.GeoAnimatable;
import rtx.kimiko.api.mods.geckolib.animation.RawAnimation;

public interface StatelessAnimatable<T extends GeoAnimatable> extends GeoAnimatable {
    default void handleClientAnimationPlay(GeoAnimatable animatable, long id, RawAnimation animation) {}
    default void handleClientAnimationStop(GeoAnimatable animatable, long id) {}
    default void handleClientAnimationStop(GeoAnimatable animatable, long id, String animation) {}
}
