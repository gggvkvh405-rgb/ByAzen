/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11403
 *  net.minecraft.class_4184
 *  net.minecraft.class_638
 *  net.minecraft.class_7285
 *  net.minecraft.class_9779
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.byazen.mixin;

import net.minecraft.class_11403;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_7285;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.byazen.module.render.NoRenderModule;

@Mixin(value={class_11403.class})
public abstract class WaterFogModifierMixin {
    @Inject(method={"method_42591"}, at={@At(value="TAIL")})
    private void byazen$removeWaterFog(class_7285 fog, class_4184 camera, class_638 world, float viewDistance, class_9779 tickCounter, CallbackInfo callback) {
        if (NoRenderModule.isFluidZoomDisabled()) {
            WaterFogModifierMixin.clearFog(fog);
        }
    }

    private static void clearFog(class_7285 fog) {
        fog.field_60582 = 1.0E7f;
        fog.field_60583 = 1.0E7f;
        fog.field_60584 = 1.0E8f;
        fog.field_60585 = 1.0E8f;
        fog.field_60099 = 1.0E8f;
        fog.field_60100 = 1.0E8f;
    }
}

