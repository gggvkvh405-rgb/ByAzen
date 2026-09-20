package rtx.byazen.api.mods.geckolib.animatable;
import rtx.byazen.api.mods.geckolib.animatable.instance.AnimatableInstanceCache;
import rtx.byazen.api.mods.geckolib.animatable.manager.AnimatableManager;
import rtx.byazen.api.mods.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;

public interface GeoAnimatable {
    public AnimatableInstanceCache getAnimatableInstanceCache();

    default public AnimatableInstanceCache animatableCacheOverride() {
        return null;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar var1);
}

