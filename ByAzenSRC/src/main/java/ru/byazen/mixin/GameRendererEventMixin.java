/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_4587
 *  net.minecraft.class_757
 *  net.minecraft.class_9779
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.byazen.mixin;

import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_9779;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.byazen.ByAzenClient;
import ru.byazen.event.AspectRatioEvent;
import ru.byazen.event.EventBus;
import ru.byazen.event.OverlayRenderEvent;
import ru.byazen.event.OverlayType;
import ru.byazen.misc.ChamsRenderer;
import ru.byazen.misc.GlowEspRenderer;
import ru.byazen.module.render.NoRenderModule;
import ru.byazen.render.ClientEventBridge;
import ru.byazen.render.HologramImpostorRenderer;
import ru.byazen.ui.ByAzenScreen;

@Mixin(value={class_757.class})
public abstract class GameRendererEventMixin {
    @Shadow
    @Final
    private class_310 field_4015;
    @Shadow
    private float field_4019;
    @Shadow
    private float field_3999;

    @Shadow
    public abstract float method_32796();

    @Inject(method={"method_3192"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_11228;method_70890(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", shift=At.Shift.BEFORE)})
    private void renderByAzenHud(class_9779 tickCounter, boolean tick, CallbackInfo callback) {
        ClientEventBridge.renderHudAfterVanillaGui();
    }

    @Inject(method={"method_3192"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_11228;method_70890(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", shift=At.Shift.AFTER)})
    private void renderByAzenPanel(class_9779 tickCounter, boolean tick, CallbackInfo callback) {
        class_437 class_4372 = this.field_4015.field_1755;
        if (class_4372 instanceof ByAzenScreen) {
            ByAzenScreen screen = (ByAzenScreen)class_4372;
            screen.renderPanel(tickCounter.method_60636());
        }
    }

    @Inject(method={"method_3188"}, at={@At(value="TAIL")})
    private void byazen$finishDeferredWorldEffects(class_9779 tickCounter, CallbackInfo callback) {
        HologramImpostorRenderer.flush();
        ChamsRenderer.presentPendingChams();
        GlowEspRenderer.renderPendingGlow();
    }

    @Inject(method={"method_3198"}, at={@At(value="HEAD")}, cancellable=true)
    private void allowHurtCameraCancellation(class_4587 matrices, float tickDelta, CallbackInfo callback) {
        EventBus events = ByAzenClient.getEventBus();
        if (events == null) {
            return;
        }
        OverlayRenderEvent event = new OverlayRenderEvent(OverlayType.CAMERA_HURT);
        events.post(event);
        if (event.isCancelled()) {
            callback.cancel();
        }
    }

    @Inject(method={"method_3186"}, at={@At(value="HEAD")}, cancellable=true)
    private void disableViewBobbing(class_4587 matrices, float tickDelta, CallbackInfo callback) {
        if (NoRenderModule.isViewBobbingDisabled()) {
            callback.cancel();
        }
    }

    @Inject(method={"method_3199"}, at={@At(value="HEAD")}, cancellable=true)
    private void disableDynamicFov(CallbackInfo callback) {
        if (!NoRenderModule.isEnabled()) {
            return;
        }
        this.field_3999 = 1.0f;
        this.field_4019 = 1.0f;
        callback.cancel();
    }

    @Inject(method={"method_22973"}, at={@At(value="HEAD")}, cancellable=true)
    private void useConfiguredAspectRatio(float fov, CallbackInfoReturnable<Matrix4f> callback) {
        EventBus events = ByAzenClient.getEventBus();
        if (events == null) {
            return;
        }
        float vanillaRatio = (float)this.field_4015.method_22683().method_4489() / (float)Math.max(1, this.field_4015.method_22683().method_4506());
        AspectRatioEvent event = new AspectRatioEvent(vanillaRatio);
        events.post(event);
        if (Float.compare(event.getAspectRatio(), vanillaRatio) == 0) {
            return;
        }
        callback.setReturnValue(new Matrix4f().perspective(fov * ((float)Math.PI / 180), event.getAspectRatio(), 0.05f, this.method_32796()));
    }
}

