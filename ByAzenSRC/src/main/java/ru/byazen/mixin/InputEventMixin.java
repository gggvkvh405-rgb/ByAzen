/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11908
 *  net.minecraft.class_11910
 *  net.minecraft.class_309
 *  net.minecraft.class_312
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.byazen.mixin;

import net.minecraft.class_11908;
import net.minecraft.class_11910;
import net.minecraft.class_309;
import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.byazen.ByAzenClient;
import ru.byazen.event.EventBus;
import ru.byazen.event.KeyPressedEvent;
import ru.byazen.event.KeyReleasedEvent;
import ru.byazen.event.MousePressedEvent;
import ru.byazen.event.MouseReleasedEvent;

public final class InputEventMixin {
    private InputEventMixin() {
    }

    @Mixin(value={class_312.class})
    public static class MouseHook {
        @Inject(method={"method_1601"}, at={@At(value="TAIL")})
        private void byazen$onMouseButton(long window, class_11910 input, int action, CallbackInfo callback) {
            EventBus eventBus = ByAzenClient.getEventBus();
            if (eventBus == null) {
                return;
            }
            if (action == 1) {
                eventBus.post(new MousePressedEvent(input.comp_4801()));
            } else if (action == 0) {
                eventBus.post(new MouseReleasedEvent(input.comp_4801()));
            }
        }
    }

    @Mixin(value={class_309.class})
    public static class KeyboardHook {
        @Inject(method={"method_1466"}, at={@At(value="TAIL")})
        private void byazen$onKey(long window, int action, class_11908 input, CallbackInfo callback) {
            EventBus eventBus = ByAzenClient.getEventBus();
            if (eventBus == null) {
                return;
            }
            if (action == 1) {
                eventBus.post(new KeyPressedEvent(input.comp_4795(), input.comp_4796()));
            } else if (action == 0) {
                eventBus.post(new KeyReleasedEvent(input.comp_4795(), input.comp_4796()));
            }
        }
    }
}

