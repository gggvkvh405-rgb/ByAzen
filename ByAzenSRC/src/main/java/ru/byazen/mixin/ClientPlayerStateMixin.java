/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.byazen.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.byazen.misc.PlayerShieldStateAccessor;
import ru.byazen.module.render.HitParticlesModule;

@Mixin(value={class_746.class})
public abstract class ClientPlayerStateMixin
implements PlayerShieldStateAccessor {
    private boolean byazen$shieldDesynchronized;

    @Override
    public boolean isShieldUseForced() {
        return this.byazen$shieldDesynchronized;
    }

    @Override
    public void setShieldUseForced(boolean state) {
        this.byazen$shieldDesynchronized = state;
    }

    @Inject(method={"method_7277"}, at={@At(value="HEAD")}, cancellable=true)
    private void byazen$replaceCriticalParticles(class_1297 target, CallbackInfo callback) {
        if (HitParticlesModule.isEnabled()) {
            callback.cancel();
        }
    }

    @Inject(method={"method_7304"}, at={@At(value="HEAD")}, cancellable=true)
    private void byazen$replaceEnchantedParticles(class_1297 target, CallbackInfo callback) {
        if (HitParticlesModule.isEnabled()) {
            callback.cancel();
        }
    }
}

