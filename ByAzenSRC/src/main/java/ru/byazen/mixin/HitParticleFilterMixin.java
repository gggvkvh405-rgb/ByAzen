/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2394
 *  net.minecraft.class_2396
 *  net.minecraft.class_2398
 *  net.minecraft.class_702
 *  net.minecraft.class_703
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.byazen.mixin;

import net.minecraft.class_2394;
import net.minecraft.class_2396;
import net.minecraft.class_2398;
import net.minecraft.class_702;
import net.minecraft.class_703;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.byazen.module.render.HitParticlesModule;

@Mixin(value={class_702.class})
public abstract class HitParticleFilterMixin {
    @Inject(method={"method_3056"}, at={@At(value="HEAD")}, cancellable=true)
    private void byazen$removeVanillaHitParticles(class_2394 effect, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<class_703> callback) {
        if (effect != null && HitParticlesModule.isEnabled() && HitParticleFilterMixin.isVanillaHitParticle(effect.method_10295())) {
            callback.setReturnValue(null);
        }
    }

    private static boolean isVanillaHitParticle(class_2396<?> type) {
        return type == class_2398.field_11205 || type == class_2398.field_11208 || type == class_2398.field_11209;
    }
}

