package rtx.kimiko.api.modules.impl.Visuals.emotions;

import java.util.UUID;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;

public class EmotionPlayback {
    private static Emotion activeEmotion = null;
    private static boolean playing = false;
    private static float speed = 1.0f;
    private static boolean looping = false;
    private static float progress = 0.0f;

    public static void play(Emotion emotion) {
        activeEmotion = emotion;
        playing = true;
        progress = 0.0f;
    }

    public static void stop() {
        playing = false;
        activeEmotion = null;
        progress = 0.0f;
    }

    public static void cancel() {
        stop();
    }

    public static void clearRemote() {
    }

    public static void setRemote(UUID uuid, Emotion emotion, long startedAt, float speed, boolean looping) {
    }

    public static void setSpeed(float s) {
        speed = s;
    }

    public static void setLooping(boolean l) {
        looping = l;
    }

    public static void update() {
    }

    public static boolean isPlaying() {
        return playing;
    }

    public static Emotion active() {
        return activeEmotion;
    }

    public static Emotion currentEmotion() {
        return activeEmotion;
    }

    public static float currentProgress() {
        return progress;
    }

    public static void fill(EmotionStateHolder holder, PlayerLikeEntity entity) {
    }

    public static void applyTo(BipedEntityModel<?> model, EmotionStateHolder holder, float limbSwingAmplitude) {
    }

    public static void beginPreview(Emotion emotion, float time) {
    }

    public static void endPreview() {
    }
}
