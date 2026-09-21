/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10071
 *  net.minecraft.class_11659
 *  net.minecraft.class_4587
 *  net.minecraft.class_8138$class_8141
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.byazen.mixin;

import net.minecraft.class_10071;
import net.minecraft.class_11659;
import net.minecraft.class_4587;
import net.minecraft.class_8138;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.byazen.render.HologramImpostorRenderer;

@Mixin(value={class_8138.class_8141.class})
public abstract class TextDisplayHologramMixin {
    @Inject(method={"method_49056"}, at={@At(value="HEAD")}, cancellable=true)
    private void byazen$captureTextDisplay(class_10071 state, class_4587 matrices, class_11659 queue, int light, float tickProgress, CallbackInfo callback) {
        if (HologramImpostorRenderer.captureTextDisplay(state, matrices, tickProgress)) {
            callback.cancel();
        }
    }
}

