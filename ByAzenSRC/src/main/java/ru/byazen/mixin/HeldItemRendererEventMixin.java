/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11659
 *  net.minecraft.class_1268
 *  net.minecraft.class_1306
 *  net.minecraft.class_1799
 *  net.minecraft.class_4587
 *  net.minecraft.class_742
 *  net.minecraft.class_746
 *  net.minecraft.class_759
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.byazen.mixin;

import net.minecraft.class_11659;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_742;
import net.minecraft.class_746;
import net.minecraft.class_759;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.byazen.ByAzenClient;
import ru.byazen.event.EventBus;
import ru.byazen.event.FirstPersonItemTransformEvent;
import ru.byazen.event.FirstPersonSwingTransformEvent;
import ru.byazen.event.HandRenderEvent;
import ru.byazen.event.HandRenderPhase;

@Mixin(value={class_759.class})
public abstract class HeldItemRendererEventMixin {
    @Unique
    private class_1268 byazen$currentHand;
    @Unique
    private float byazen$currentEquipProgress;

    @Shadow
    private void method_3217(class_4587 matrices, class_1306 arm, float swingProgress) {
        throw new AssertionError();
    }

    @Inject(method={"method_3228"}, at={@At(value="HEAD")})
    private void beforeFirstPersonItem(class_742 player, float tickDelta, float pitch, class_1268 hand, float swingProgress, class_1799 stack, float equipProgress, class_4587 matrices, class_11659 queue, int light, CallbackInfo callback) {
        this.byazen$currentHand = hand;
        this.byazen$currentEquipProgress = equipProgress;
        EventBus events = ByAzenClient.getEventBus();
        if (events == null) {
            return;
        }
        FirstPersonItemTransformEvent event = new FirstPersonItemTransformEvent(matrices, stack, hand);
        events.post(event);
        float scale = event.getScale();
        if (scale != 1.0f) {
            matrices.method_22905(scale, scale, scale);
        }
    }

    @Redirect(method={"method_65816"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_759;method_3217(Lnet/minecraft/class_4587;Lnet/minecraft/class_1306;F)V"))
    private void replaceSwingTransform(class_759 renderer, class_4587 matrices, class_1306 arm, float swingProgress) {
        EventBus events = ByAzenClient.getEventBus();
        if (events != null && this.byazen$currentHand != null) {
            FirstPersonSwingTransformEvent event = new FirstPersonSwingTransformEvent(matrices, this.byazen$currentHand, swingProgress, this.byazen$currentEquipProgress);
            events.post(event);
            if (event.isCancelled()) {
                return;
            }
        }
        this.method_3217(matrices, arm, swingProgress);
    }

    @Inject(method={"method_22976"}, at={@At(value="HEAD")})
    private void beforeHandRender(float tickDelta, class_4587 matrices, class_11659 queue, class_746 player, int light, CallbackInfo callback) {
        EventBus events = ByAzenClient.getEventBus();
        if (events != null) {
            events.post(new HandRenderEvent(HandRenderPhase.BEFORE, matrices, tickDelta));
        }
    }

    @Inject(method={"method_22976"}, at={@At(value="RETURN")})
    private void afterHandRender(float tickDelta, class_4587 matrices, class_11659 queue, class_746 player, int light, CallbackInfo callback) {
        EventBus events = ByAzenClient.getEventBus();
        if (events != null) {
            events.post(new HandRenderEvent(HandRenderPhase.AFTER, matrices, tickDelta));
        }
    }
}

