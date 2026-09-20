package rtx.byazen.api.mods.waveycapes.support;

import net.minecraft.entity.LivingEntity;
import rtx.byazen.api.mods.waveycapes.versionless.util.Vector3;

public interface AnimationSupport {
    Vector3 applyAnimationChanges(LivingEntity player, float delta, Vector3 movement);
}
