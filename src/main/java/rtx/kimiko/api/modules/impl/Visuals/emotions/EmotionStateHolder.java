package rtx.kimiko.api.modules.impl.Visuals.emotions;
import rtx.kimiko.api.modules.impl.Visuals.emotions.Emotion;

public interface EmotionStateHolder {
    public float kimiko_getEmotionTime();

    public void kimiko_setEmotion(Emotion var1, float var2, float var3);

    public Emotion kimiko_getEmotion();

    public float kimiko_getEmotionWeight();
}

