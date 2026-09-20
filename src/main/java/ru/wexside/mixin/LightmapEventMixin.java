/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_765
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.wexside.mixin;

import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.WexSideClient;
import ru.wexside.event.BrightnessEvent;
import ru.wexside.event.EventBus;

@Mixin(value={class_765.class})
public abstract class LightmapEventMixin {
    @Inject(method={"method_62226"}, at={@At(value="RETURN")}, cancellable=true)
    private static void applyBrightnessOverride(float ambientLight, int lightLevel, CallbackInfoReturnable<Float> callback) {
        EventBus events = WexSideClient.getEventBus();
        if (events == null) {
            return;
        }
        BrightnessEvent event = new BrightnessEvent(callback.getReturnValueF());
        events.post(event);
        callback.setReturnValue(Float.valueOf(event.getBrightness()));
    }
}

