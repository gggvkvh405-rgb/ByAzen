package rtx.byazen.api.mods.geckolib.cache.animation;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import rtx.byazen.api.mods.geckolib.cache.animation.KeyframeStack;
import rtx.byazen.api.mods.geckolib.loading.math.value.Variable;

public record BoneAnimation(String boneName, KeyframeStack rotationKeyFrames, KeyframeStack positionKeyFrames, KeyframeStack scaleKeyFrames) {
    public Set<Variable> getUsedVariables() {
        ReferenceOpenHashSet referenceOpenHashSet = new ReferenceOpenHashSet();
        referenceOpenHashSet.addAll(this.rotationKeyFrames.getUsedVariables());
        referenceOpenHashSet.addAll(this.positionKeyFrames.getUsedVariables());
        referenceOpenHashSet.addAll(this.scaleKeyFrames.getUsedVariables());
        return referenceOpenHashSet;
    }
}

