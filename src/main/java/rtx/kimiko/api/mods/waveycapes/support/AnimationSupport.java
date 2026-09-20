package rtx.kimiko.api.mods.waveycapes.support;

import net.minecraft.entity.LivingEntity;
import rtx.kimiko.api.mods.waveycapes.versionless.util.Vector3;

public interface AnimationSupport {
    Vector3 applyAnimationChanges(LivingEntity player, float delta, Vector3 movement);
}
