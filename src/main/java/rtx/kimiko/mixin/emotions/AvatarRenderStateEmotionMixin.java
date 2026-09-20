package rtx.kimiko.mixin.emotions;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import rtx.kimiko.api.modules.impl.Visuals.emotions.Emotion;
import rtx.kimiko.api.modules.impl.Visuals.emotions.EmotionStateHolder;

@Mixin(net.minecraft.client.render.entity.state.PlayerEntityRenderState.class)

public abstract class AvatarRenderStateEmotionMixin
implements EmotionStateHolder {
    @Unique
    private Emotion kimiko_emotion;
    @Unique
    private float kimiko_emotionTime;
    @Unique
    private float kimiko_emotionWeight;

    @Override
    public Emotion kimiko_getEmotion() {
        return this.kimiko_emotion;
    }

    @Override
    public float kimiko_getEmotionTime() {
        return this.kimiko_emotionTime;
    }

    @Override
    public float kimiko_getEmotionWeight() {
        return this.kimiko_emotionWeight;
    }

    @Override
    public void kimiko_setEmotion(Emotion emotion, float time, float weight) {
        this.kimiko_emotion = emotion;
        this.kimiko_emotionTime = time;
        this.kimiko_emotionWeight = weight;
    }
}

