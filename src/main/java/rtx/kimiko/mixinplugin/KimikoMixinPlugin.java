package rtx.kimiko.mixinplugin;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public final class KimikoMixinPlugin implements IMixinConfigPlugin {
    private static final Map<String, String> DISABLED_BY_MOD = Map.of(
        "rtx.kimiko.mixin.LevelRendererChunkFadeMixin", "sodium"
    );
    private static final Map<String, String> REQUIRED_MOD = Map.of(
        "rtx.kimiko.mixin.compat.IrisHandRendererHandsMixin", "iris",
        "rtx.kimiko.mixin.compat.IrisRenderingPipelineHandsMixin", "iris"
    );
    private static final Set<String> SKIPPED = new HashSet<String>();

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String blockedBy = DISABLED_BY_MOD.get(mixinClassName);
        if (blockedBy != null && KimikoMixinPlugin.isLoaded(blockedBy)) {
            if (SKIPPED.add(mixinClassName)) {
                System.out.println("[Kimiko] Skipping mixin " + mixinClassName + " because '" + blockedBy + "' is installed");
            }
            return false;
        }

        String required = REQUIRED_MOD.get(mixinClassName);
        if (required != null && !KimikoMixinPlugin.isLoaded(required)) {
            return false;
        }

        return true;
    }

    private static boolean isLoaded(String modId) {
        try {
            return FabricLoader.getInstance().isModLoaded(modId);
        } catch (Throwable throwable) {
            return false;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
