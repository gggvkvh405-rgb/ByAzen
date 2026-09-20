package rtx.kimiko.api.mods.geckolib.loading.object;
import java.util.Map;
import rtx.kimiko.api.mods.geckolib.cache.animation.Animation;

public record BakedAnimations(Map<String, Animation> animations) {
    public Animation getAnimation(String string) {
        return this.animations.get(string);
    }
}

