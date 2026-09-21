/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.byazen.mixin;

import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.byazen.ByAzenClient;
import ru.byazen.event.AttackWindowEvent;
import ru.byazen.event.EventBus;
import ru.byazen.misc.TickPhase;

@Mixin(value={class_310.class})
public abstract class MinecraftAttackWindowMixin {
    @Inject(method={"method_1508"}, at={@At(value="HEAD")})
    private void beforeInputActions(CallbackInfo callback) {
        this.postAttackWindow(TickPhase.PRE);
    }

    @Inject(method={"method_1508"}, at={@At(value="RETURN")})
    private void afterInputActions(CallbackInfo callback) {
        this.postAttackWindow(TickPhase.POST);
    }

    private void postAttackWindow(TickPhase phase) {
        EventBus events = ByAzenClient.getEventBus();
        if (events != null) {
            events.post(new AttackWindowEvent(phase));
        }
    }
}

