package rtx.byazen.utils.cosmetics;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.sound.SoundEvent;
import rtx.byazen.utils.sounds.SoundManager;

/**
 * Звуки косметики и питомцев (идея №126 из IDEAS.md).
 * <p>
 * Все файлы лежат в сборке как настоящие .ogg и уже нормализованы: общая громкость по энергии
 * одинаковая, пик ограничен. Дополнительно здесь хранится измеренный пик каждого файла, и при
 * воспроизведении громкость делится на него — поэтому ни один звук не «стреляет» громче остальных,
 * как бы сильно ни был сжат при генерации.
 */
public final class CosmeticSounds {

    private static final float TARGET_PEAK = 0.86f;

    /** Пики, измеренные при генерации файлов (см. tools/cosmetic_sound_report.txt). */
    private static final Map<String, Float> PEAKS = new LinkedHashMap<String, Float>();

    private CosmeticSounds() {
    }

    static {
        PEAKS.put("cosmetic_equip", 0.589f);
        PEAKS.put("cosmetic_unequip", 0.622f);
        PEAKS.put("cosmetic_favorite", 0.649f);
        PEAKS.put("cosmetic_claim_set", 0.477f);
        PEAKS.put("cosmetic_set_winter", 0.640f);
        PEAKS.put("cosmetic_set_neon", 0.546f);
        PEAKS.put("cosmetic_set_space", 0.534f);
        PEAKS.put("pet_feed", 0.860f);
        PEAKS.put("pet_pet", 0.394f);
        PEAKS.put("pet_toy", 0.404f);
        PEAKS.put("pet_level", 0.540f);
    }

    /** Громкость для ровного звучания: чем выше пик файла, тем тише играем. */
    public static float gain(String key) {
        Float peak = PEAKS.get(key);
        if (peak == null || peak <= 0.01f) {
            return 1.0f;
        }
        return Math.min(1.35f, TARGET_PEAK / peak);
    }

    public static SoundEvent event(String key) {
        if (key == null) {
            return SoundManager.COSMETIC_EQUIP;
        }
        switch (key) {
            case "cosmetic_unequip":
                return SoundManager.COSMETIC_UNEQUIP;
            case "cosmetic_favorite":
                return SoundManager.COSMETIC_FAVORITE;
            case "cosmetic_claim_set":
                return SoundManager.COSMETIC_CLAIM_SET;
            case "cosmetic_set_winter":
                return SoundManager.COSMETIC_SET_WINTER;
            case "cosmetic_set_neon":
                return SoundManager.COSMETIC_SET_NEON;
            case "cosmetic_set_space":
                return SoundManager.COSMETIC_SET_SPACE;
            case "pet_feed":
                return SoundManager.PET_FEED;
            case "pet_pet":
                return SoundManager.PET_PET;
            case "pet_toy":
                return SoundManager.PET_TOY;
            case "pet_level":
                return SoundManager.PET_LEVEL;
            default:
                return SoundManager.COSMETIC_EQUIP;
        }
    }

    /** Звук набора: у каждого набора свой характер. */
    public static String setKey(String flavor) {
        if (Cosmetic.FLAVOR_SNOW.equals(flavor)) {
            return "cosmetic_set_winter";
        }
        if (Cosmetic.FLAVOR_NEON.equals(flavor)) {
            return "cosmetic_set_neon";
        }
        if (Cosmetic.FLAVOR_SPACE.equals(flavor)) {
            return "cosmetic_set_space";
        }
        return "cosmetic_favorite";
    }

    /**
     * Играет звук косметики.
     *
     * @param key    имя файла без расширения
     * @param volume пользовательская громкость (0…1.5)
     */
    public static void play(String key, float volume) {
        try {
            float volume2 = Math.max(0.0f, volume) * CosmeticSounds.gain(key);
            SoundManager.playSound(CosmeticSounds.event(key), volume2, 1.0f);
        }
        catch (Throwable ignored) {
            // звук не критичен
        }
    }

    /** Играет звук события косметики: надел или снял. */
    public static void playEquipped(boolean equipped, float volume) {
        CosmeticSounds.play(equipped ? "cosmetic_equip" : "cosmetic_unequip", volume);
    }

    /** Играет звук набора: используется при надевании и при заборе всего набора. */
    public static void playSet(String flavor, float volume) {
        CosmeticSounds.play(CosmeticSounds.setKey(flavor), volume);
    }

    /** Есть ли звук с таким именем в таблице нормализации. */
    public static boolean exists(String key) {
        return PEAKS.containsKey(key);
    }

    public static int count() {
        return PEAKS.size();
    }
}
