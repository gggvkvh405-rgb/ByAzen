/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_12076
 *  net.minecraft.class_4184
 *  net.minecraft.class_638
 *  net.minecraft.class_9975
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.byazen.mixin;

import net.minecraft.class_12076;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_9975;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.byazen.module.render.AmbientModule;

@Mixin(value={class_9975.class})
public abstract class SkyRenderingMixin {
    @Inject(method={"method_74926"}, at={@At(value="TAIL")})
    private void byazen$applyConfiguredSkyColor(class_638 world, float tickProgress, class_4184 camera, class_12076 state, CallbackInfo callback) {
        if (AmbientModule.isEnabled5()) {
            state.field_63097 = AmbientModule.getIntType();
        }
    }
}

