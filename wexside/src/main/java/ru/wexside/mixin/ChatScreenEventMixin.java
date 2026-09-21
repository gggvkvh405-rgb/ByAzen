/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_408
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.wexside.mixin;

import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_408;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.WexSideClient;
import ru.wexside.event.EventBus;
import ru.wexside.event.OutgoingChatEvent;
import ru.wexside.misc.NativeHandle;
import ru.wexside.module.hud.AnimateModule;

@Mixin(value={class_408.class})
public abstract class ChatScreenEventMixin
implements NativeHandle {
    @Shadow
    protected class_342 field_2382;
    @Unique
    private long wexside$openedAt;

    @Inject(method={"method_25426"}, at={@At(value="TAIL")})
    private void wexside$startInputAnimation(CallbackInfo callback) {
        this.wexside$openedAt = System.currentTimeMillis();
    }

    @Inject(method={"method_25394"}, at={@At(value="HEAD")})
    private void wexside$animateInputWidth(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo callback) {
        if (this.field_2382 == null) {
            return;
        }
        float fullWidth = Math.max(1.0f, (float)context.method_51421() - 4.0f);
        this.field_2382.method_25358(Math.max(1, Math.round(AnimateModule.compute4(fullWidth, this))));
    }

    @Override
    public long getLongType() {
        return this.wexside$openedAt;
    }

    @Inject(method={"method_44056"}, at={@At(value="HEAD")}, cancellable=true)
    private void wexside$beforeSendMessage(String original, boolean addToHistory, CallbackInfo callback) {
        EventBus eventBus = WexSideClient.getEventBus();
        if (eventBus == null) {
            return;
        }
        OutgoingChatEvent event = new OutgoingChatEvent(original);
        eventBus.post(event);
        if (event.isCancelled()) {
            callback.cancel();
            return;
        }
        String message = event.getMessage();
        if (message == null || message.equals(original)) {
            return;
        }
        callback.cancel();
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || client.field_1724.field_3944 == null) {
            return;
        }
        if (addToHistory) {
            client.field_1705.method_1743().method_1803(message);
        }
        if (message.startsWith("/")) {
            client.field_1724.field_3944.method_45730(message.substring(1));
        } else {
            client.field_1724.field_3944.method_45729(message);
        }
    }
}

