/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.byazen.mixin;

import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.byazen.module.render.AmbientModule;

@Mixin(value={class_1937.class})
public abstract class WorldAmbientMixin {
    @Inject(method={"method_8532"}, at={@At(value="HEAD")}, cancellable=true)
    private void byazen$useConfiguredTime(CallbackInfoReturnable<Long> callback) {
        if (AmbientModule.isEnabled2()) {
            callback.setReturnValue(AmbientModule.getLongType());
        }
    }
}

