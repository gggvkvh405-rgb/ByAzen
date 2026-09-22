package rtx.byazen.api.modules.impl.Visuals.emotions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Проигрывание эмоций (идея №125 из IDEAS.md).
 * <p>
 * Здесь живёт вся анимация: локальный проигрыш с нарастанием и затуханием, состояния удалённых
 * игроков (эмоция приходит с их прогрессом, поэтому у всех на экране она выглядит синхронно) и
 * мягкая посадка — поза подмешивается к обычной анимации модели с весом, поэтому переход не рвётся.
 */
public class EmotionPlayback {

    /** Состояние чужой эмоции: что играет, когда началось и с какой скоростью. */
    private static final class Remote {

        final Emotion emotion;
        final long startedAt;
        final float speed;
        final boolean looping;

        Remote(Emotion emotion, long startedAt, float speed, boolean looping) {
            this.emotion = emotion;
            this.startedAt = startedAt;
            this.speed = speed <= 0.05f ? 1.0f : speed;
            this.looping = looping;
        }
    }

    private static final float FADE_IN = 0.12f;
    private static final float FADE_OUT = 0.18f;
    private static final Map<UUID, Remote> REMOTE = new HashMap<UUID, Remote>();
    private static final EmotionPose POSE = new EmotionPose();

    private static Emotion activeEmotion;
    private static boolean playing;
    private static float speed = 1.0f;
    private static boolean looping;
    private static float progress;
    private static float weight;
    private static long lastUpdateMs;
    private static Emotion previewEmotion;
    private static float previewTime;

    public static void play(Emotion emotion) {
        activeEmotion = emotion;
        playing = emotion != null;
        progress = 0.0f;
        weight = 0.0f;
        lastUpdateMs = System.currentTimeMillis();
    }

    public static void stop() {
        playing = false;
        activeEmotion = null;
        progress = 0.0f;
        weight = 0.0f;
    }

    public static void cancel() {
        stop();
    }

    public static void clearRemote() {
        REMOTE.clear();
    }

    public static void setRemote(UUID uuid, Emotion emotion, long startedAt, float speed, boolean looping) {
        if (uuid == null || emotion == null) {
            return;
        }
        REMOTE.put(uuid, new Remote(emotion, startedAt, speed, looping));
    }

    public static void setSpeed(float value) {
        speed = value <= 0.05f ? 0.05f : value;
    }

    public static void setLooping(boolean value) {
        looping = value;
    }

    /** Продвигает локальную эмоцию: прогресс, нарастание, затухание и остановка в конце. */
    public static void update() {
        long now = System.currentTimeMillis();
        float delta = lastUpdateMs == 0L ? 0.016f : Math.min(0.12f, (float)(now - lastUpdateMs) / 1000.0f);
        lastUpdateMs = now;
        if (!playing || activeEmotion == null) {
            weight = Math.max(0.0f, weight - delta / FADE_OUT);
            return;
        }
        progress += delta * speed / Math.max(0.2f, activeEmotion.duration());
        if (progress >= 1.0f) {
            if (looping) {
                progress -= (float)Math.floor((double)progress);
            } else {
                progress = 1.0f;
                playing = false;
                weight = Math.max(0.0f, weight - delta / FADE_OUT);
                return;
            }
        }
        float target = progress < FADE_IN ? progress / FADE_IN : progress > 1.0f - FADE_OUT ? (1.0f - progress) / FADE_OUT : 1.0f;
        target = MathHelper.clamp(target, 0.0f, 1.0f);
        weight += (target - weight) * Math.min(1.0f, delta * 9.0f);
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

    public static float currentWeight() {
        return weight;
    }

    /** Заполняет состояние модели: своё для локального игрока, чужое — по приходу с сервера. */
    public static void fill(EmotionStateHolder holder, PlayerLikeEntity entity) {
        if (holder == null || entity == null) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && entity == client.player) {
            if (previewEmotion != null) {
                holder.byazen_setEmotion(previewEmotion, previewTime, 1.0f);
                return;
            }
            float currentWeight = EmotionPlayback.isPlaying() ? weight : weight;
            if (activeEmotion == null || currentWeight <= 0.01f) {
                holder.byazen_setEmotion(null, 0.0f, 0.0f);
                return;
            }
            holder.byazen_setEmotion(activeEmotion, progress, currentWeight);
            return;
        }
        Remote remote = REMOTE.get(entity.getUuid());
        if (remote == null) {
            holder.byazen_setEmotion(null, 0.0f, 0.0f);
            return;
        }
        float normalized = EmotionPlayback.normalized(remote);
        if (normalized < 0.0f) {
            holder.byazen_setEmotion(null, 0.0f, 0.0f);
            return;
        }
        float remoteWeight = normalized < FADE_IN ? normalized / FADE_IN
                : normalized > 1.0f - FADE_OUT ? (1.0f - normalized) / FADE_OUT : 1.0f;
        holder.byazen_setEmotion(remote.emotion, normalized, MathHelper.clamp(remoteWeight, 0.0f, 1.0f));
    }

    /** Прогресс чужой эмоции: отрицательное значение — она уже закончилась. */
    private static float normalized(Remote remote) {
        float duration = Math.max(0.2f, remote.emotion.duration());
        float elapsed = (float)(System.currentTimeMillis() - remote.startedAt) / 1000.0f * remote.speed;
        if (elapsed < 0.0f) {
            return 0.0f;
        }
        float value = elapsed / duration;
        if (remote.looping) {
            return value - (float)Math.floor((double)value);
        }
        return value > 1.0f ? -1.0f : value;
    }

    /** Подмешивает позу эмоции к обычной анимации модели с учётом веса. */
    public static void applyTo(BipedEntityModel<?> model, EmotionStateHolder holder, float limbSwingAmplitude) {
        if (model == null || holder == null) {
            return;
        }
        Emotion emotion = holder.byazen_getEmotion();
        float time = holder.byazen_getEmotionTime();
        float currentWeight = MathHelper.clamp(holder.byazen_getEmotionWeight(), 0.0f, 1.0f);
        if (emotion == null || currentWeight <= 0.01f) {
            return;
        }
        EmotionPose pose = POSE;
        pose.reset();
        emotion.apply(pose, MathHelper.clamp(time, 0.0f, 1.0f));
        float w = currentWeight;
        // плавное покачивание на месте, чтобы тело не «застывало»
        float sway = (float)Math.sin((double)time * 6.283185307179586) * 0.04f * w;
        EmotionPlayback.blend(model, pose, w, sway);
    }

    private static void blend(BipedEntityModel<?> model, EmotionPose pose, float weight, float sway) {
        float w = weight;
        model.head.pitch += pose.headX * w;
        model.head.yaw += pose.headY * w;
        model.head.roll += pose.headZ * w;
        model.body.pitch += pose.bodyX * w;
        model.body.yaw += pose.bodyY * w + sway;
        model.body.roll += pose.bodyZ * w;
        model.rightArm.pitch += pose.rightArmX * w;
        model.rightArm.yaw += pose.rightArmY * w;
        model.rightArm.roll += pose.rightArmZ * w;
        model.leftArm.pitch += pose.leftArmX * w;
        model.leftArm.yaw += pose.leftArmY * w;
        model.leftArm.roll += pose.leftArmZ * w;
        model.rightLeg.pitch += pose.rightLegX * w;
        model.rightLeg.yaw += pose.rightLegY * w;
        model.rightLeg.roll += pose.rightLegZ * w;
        model.leftLeg.pitch += pose.leftLegX * w;
        model.leftLeg.yaw += pose.leftLegY * w;
        model.leftLeg.roll += pose.leftLegZ * w;
    }

    /** Превью в колесе эмоций: показывает позу, не запуская проигрыш. */
    public static void beginPreview(Emotion emotion, float time) {
        previewEmotion = emotion;
        previewTime = time;
    }

    public static void endPreview() {
        previewEmotion = null;
        previewTime = 0.0f;
    }

    /** Идёт ли проигрыш чужой эмоции: нужно синхрону команды. */
    public static boolean seesRemote(UUID uuid) {
        return uuid != null && REMOTE.containsKey(uuid);
    }
}
