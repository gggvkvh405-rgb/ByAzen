/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.byazen.mixin;

import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.byazen.ByAzenClient;
import ru.byazen.event.EventBus;
import ru.byazen.event.MovementSlowdownEvent;

@Mixin(value={class_746.class})
public abstract class ClientPlayerMovementEventMixin {
    @Inject(method={"method_20303"}, at={@At(value="HEAD")}, cancellable=true)
    private void allowSlowdownCancellation(CallbackInfoReturnable<Boolean> callback) {
        EventBus events = ByAzenClient.getEventBus();
        if (events == null) {
            return;
        }
        MovementSlowdownEvent event = new MovementSlowdownEvent();
        events.post(event);
        if (event.isCancelled()) {
            callback.setReturnValue(false);
        }
    }
}

