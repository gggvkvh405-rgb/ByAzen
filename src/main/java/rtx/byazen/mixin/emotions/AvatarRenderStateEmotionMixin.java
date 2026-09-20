package rtx.byazen.mixin.emotions;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import rtx.byazen.api.modules.impl.Visuals.emotions.Emotion;
import rtx.byazen.api.modules.impl.Visuals.emotions.EmotionStateHolder;

@Mixin(net.minecraft.client.render.entity.state.PlayerEntityRenderState.class)

public abstract class AvatarRenderStateEmotionMixin
implements EmotionStateHolder {
    @Unique
    private Emotion byazen_emotion;
    @Unique
    private float byazen_emotionTime;
    @Unique
    private float byazen_emotionWeight;

    @Override
    public Emotion byazen_getEmotion() {
        return this.byazen_emotion;
    }

    @Override
    public float byazen_getEmotionTime() {
        return this.byazen_emotionTime;
    }

    @Override
    public float byazen_getEmotionWeight() {
        return this.byazen_emotionWeight;
    }

    @Override
    public void byazen_setEmotion(Emotion emotion, float time, float weight) {
        this.byazen_emotion = emotion;
        this.byazen_emotionTime = time;
        this.byazen_emotionWeight = weight;
    }
}

