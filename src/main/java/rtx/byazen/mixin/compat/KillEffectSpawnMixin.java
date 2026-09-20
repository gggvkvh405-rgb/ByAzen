package rtx.byazen.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Visuals.KillEffect3D;

/**
 * Blocks spawning of 3D kill effects of the bundled "Kill Effect" mod while the
 * ByAzen "Kill Effect 3D" module is disabled.
 */
@Mixin(targets = "com.killeffect.client.EffectManager", remap = false)
public class KillEffectSpawnMixin {

    @Inject(method = "spawn", at = @At("HEAD"), cancellable = true, require = 0)
    private void byazen_blockDisabled(CallbackInfo callbackInfo) {
        KillEffect3D module = ModuleManager.get().get(KillEffect3D.class);
        if (module != null && !module.isEnabled()) {
            callbackInfo.cancel();
        }
    }
}
