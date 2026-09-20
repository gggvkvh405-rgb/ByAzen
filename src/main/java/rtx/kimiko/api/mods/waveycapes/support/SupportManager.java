package rtx.kimiko.api.mods.waveycapes.support;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import rtx.kimiko.api.mods.waveycapes.support.AnimationSupport;
import rtx.kimiko.api.mods.waveycapes.support.ModSupport;

public class SupportManager {
    public static Set<ModSupport> mods = new HashSet<ModSupport>();
    public static Set<AnimationSupport> animationSupport = new HashSet<AnimationSupport>();
    public static Supplier<Float> alphaSupplier = () -> Float.valueOf(1.0f);

    public static Supplier<Float> getAlphaSupplier() {
        return alphaSupplier;
    }

    public static Set<ModSupport> getSupportedMods() {
        return mods;
    }

    public static void setAlphaSupplier(Supplier<Float> supplier) {
        alphaSupplier = supplier;
    }
}

