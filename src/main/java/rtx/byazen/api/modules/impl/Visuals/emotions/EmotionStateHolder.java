package rtx.byazen.api.modules.impl.Visuals.emotions;
import rtx.byazen.api.modules.impl.Visuals.emotions.Emotion;

public interface EmotionStateHolder {
    public float byazen_getEmotionTime();

    public void byazen_setEmotion(Emotion var1, float var2, float var3);

    public Emotion byazen_getEmotion();

    public float byazen_getEmotionWeight();
}

