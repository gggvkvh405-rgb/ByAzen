/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_1309
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.byazen.mixin;

import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.byazen.ByAzenClient;
import ru.byazen.event.EventBus;
import ru.byazen.event.HandSwingSpeedEvent;
import ru.byazen.event.PreAttackEvent;

@Mixin(value={class_1309.class})
public abstract class LivingEntityEventMixin {
    @Inject(method={"method_6028"}, at={@At(value="HEAD")}, cancellable=true)
    private void applySwingDurationOverride(CallbackInfoReturnable<Integer> callback) {
        if ((Object)this != class_310.method_1551().field_1724) {
            return;
        }
        EventBus events = ByAzenClient.getEventBus();
        if (events == null) {
            return;
        }
        HandSwingSpeedEvent event = new HandSwingSpeedEvent();
        events.post(event);
        if (event.isCancelled()) {
            callback.setReturnValue(Math.max(1, Math.round(6.0f * event.getSpeedMultiplier())));
        }
    }

    @Inject(method={"method_23667"}, at={@At(value="HEAD")}, cancellable=true)
    private void beforeLocalHandSwing(class_1268 hand, boolean fromServer, CallbackInfo callback) {
        class_1309 entity = (class_1309)((Object)this);
        if (entity != class_310.method_1551().field_1724 || fromServer) {
            return;
        }
        EventBus events = ByAzenClient.getEventBus();
        if (events == null) {
            return;
        }
        PreAttackEvent event = new PreAttackEvent(entity.method_36454());
        events.post(event);
        if (event.isCancelled()) {
            callback.cancel();
        }
    }
}

