/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1113
 *  net.minecraft.class_1140$class_11518
 *  net.minecraft.class_1144
 *  net.minecraft.class_2960
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.byazen.mixin;

import net.minecraft.class_1113;
import net.minecraft.class_1140;
import net.minecraft.class_1144;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.byazen.module.misc.CustomSoundsModule;
import ru.byazen.module.misc.SoundRemoverModule;

@Mixin(value={class_1144.class})
public abstract class SoundManagerEventMixin {
    @Inject(method={"method_4873"}, at={@At(value="HEAD")}, cancellable=true)
    private void byazen$filterImmediateSound(class_1113 sound, CallbackInfoReturnable<class_1140.class_11518> callback) {
        if (sound == null) {
            return;
        }
        class_2960 soundId = sound.method_4775();
        boolean muted = SoundRemoverModule.compute4(soundId);
        SoundRemoverModule.handle(soundId, muted);
        if (muted) {
            callback.setReturnValue(class_1140.class_11518.field_60956);
            return;
        }
        if (CustomSoundsModule.compute8(sound, 0L)) {
            callback.setReturnValue(class_1140.class_11518.field_60956);
        }
    }

    @Inject(method={"method_4872"}, at={@At(value="HEAD")}, cancellable=true)
    private void byazen$filterDelayedSound(class_1113 sound, int delay, CallbackInfo callback) {
        if (sound == null) {
            return;
        }
        class_2960 soundId = sound.method_4775();
        boolean muted = SoundRemoverModule.compute4(soundId);
        SoundRemoverModule.handle(soundId, muted);
        if (muted) {
            callback.cancel();
            return;
        }
        if (CustomSoundsModule.compute8(sound, (long)delay * 50L)) {
            callback.cancel();
        }
    }
}

