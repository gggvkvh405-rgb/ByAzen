/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_437
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.wexside.mixin;

import net.minecraft.class_437;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.WexSideClient;
import ru.wexside.event.EventBus;
import ru.wexside.event.OutgoingChatEvent;

@Mixin(value={class_437.class})
public abstract class ScreenCommandMixin {
    @Inject(method={"method_71844"}, at={@At(value="HEAD")}, cancellable=true)
    private static void wexside$dispatchClientCommand(class_746 player, String command, class_437 screen, CallbackInfo callback) {
        if (command == null || !command.startsWith(".")) {
            return;
        }
        EventBus eventBus = WexSideClient.getEventBus();
        if (eventBus == null) {
            return;
        }
        eventBus.post(new OutgoingChatEvent(command));
        callback.cancel();
    }
}

