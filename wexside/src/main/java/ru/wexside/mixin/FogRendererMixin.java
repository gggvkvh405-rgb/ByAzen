/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4184
 *  net.minecraft.class_638
 *  net.minecraft.class_758
 *  org.joml.Vector4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.wexside.mixin;

import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_758;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.module.render.AmbientModule;
import ru.wexside.util.ColorUtils;

@Mixin(value={class_758.class})
public abstract class FogRendererMixin {
    @Inject(method={"method_62185"}, at={@At(value="RETURN")}, cancellable=true)
    private void wexside$applyConfiguredFogColor(class_4184 camera, float tickProgress, class_638 world, int viewDistance, float skyDarkness, CallbackInfoReturnable<Vector4f> callback) {
        if (!AmbientModule.isEnabled4()) {
            return;
        }
        float[] color = ColorUtils.toNormalizedRgba(AmbientModule.getIntType2());
        callback.setReturnValue(new Vector4f(color[0], color[1], color[2], 1.0f));
    }
}

