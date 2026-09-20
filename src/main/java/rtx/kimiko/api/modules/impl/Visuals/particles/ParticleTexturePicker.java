package rtx.kimiko.api.modules.impl.Visuals.particles;
import java.util.Random;
import net.minecraft.util.Identifier;
import rtx.kimiko.api.modules.impl.Visuals.particles.ParticleConstants;
import rtx.kimiko.api.modules.impl.Visuals.particles.ParticleTexture;
import rtx.kimiko.api.modules.settings.impl.SelectSetting;

public final class ParticleTexturePicker {
    public static final String SHOW_ALL = "\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c \u0432\u0441\u0451";
    private static final String[] MODE_OPTIONS = new String[]{"\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c \u0432\u0441\u0451", "\u0422\u043e\u0447\u043a\u0430", "\u0417\u0432\u0435\u0437\u0434\u0430", "\u041c\u043e\u043b\u043d\u0438\u044f", "\u041a\u0440\u0435\u0441\u0442", "\u041a\u043e\u0440\u043e\u043d\u0430", "\u0421\u0435\u0440\u0434\u0446\u0435", "\u041b\u0438\u043d\u0438\u044f", "\u0420\u043e\u043c\u0431", "\u0414\u043e\u043b\u043b\u0430\u0440", "\u0421\u043d\u0435\u0436\u0438\u043d\u043a\u0430", "\u0422\u0440\u0435\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a"};

    private ParticleTexturePicker() {
    }

    public static Identifier pick(SelectSetting selectSetting, Random random) {
        for (ParticleTexture particleTexture : ParticleConstants.TEXTURES) {
            if (!selectSetting.is(particleTexture.mode())) continue;
            return particleTexture.id();
        }
        ParticleTexture[] particleTextureArray = ParticleConstants.TEXTURES;
        return particleTextureArray[random.nextInt(particleTextureArray.length)].id();
    }

    public static String[] modeOptions() {
        return (String[])MODE_OPTIONS.clone();
    }
}

