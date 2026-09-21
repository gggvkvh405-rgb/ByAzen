/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10017
 *  net.minecraft.class_11659
 *  net.minecraft.class_12075
 *  net.minecraft.class_1297
 *  net.minecraft.class_4587
 *  net.minecraft.class_897
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.byazen.mixin;

import net.minecraft.class_10017;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_1297;
import net.minecraft.class_4587;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.byazen.render.HologramImpostorRenderer;
import ru.byazen.util.NameplateRenderer;

@Mixin(value={class_897.class})
public abstract class EntityRenderStateMixin<T extends class_1297, S extends class_10017> {
    @Inject(method={"method_62354"}, at={@At(value="TAIL")})
    private void byazen$hideVanillaNameplate(T entity, S state, float tickProgress, CallbackInfo callback) {
        if (NameplateRenderer.process16(entity)) {
            ((class_10017)state).field_53337 = null;
            ((class_10017)state).field_53338 = null;
        }
    }

    @Inject(method={"method_3926"}, at={@At(value="HEAD")}, cancellable=true)
    private void byazen$captureHologramLabel(S state, class_4587 matrices, class_11659 queue, class_12075 camera, CallbackInfo callback) {
        if (HologramImpostorRenderer.captureEntityLabel(state, matrices, camera)) {
            callback.cancel();
        }
    }
}

