package org.zenith.utility.mixin;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/** Keeps optional compatibility mixins out of the transform set when their mod is absent. */
public final class ZenithMixinPlugin implements IMixinConfigPlugin {
   private static final String SODIUM_MIXIN_PREFIX = "org.zenith.utility.mixin.render.MixinSodium";

   @Override
   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      return !mixinClassName.startsWith(SODIUM_MIXIN_PREFIX) || FabricLoader.getInstance().isModLoaded("sodium");
   }

   @Override
   public void onLoad(String mixinPackage) {
   }

   @Override
   public String getRefMapperConfig() {
      return null;
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
