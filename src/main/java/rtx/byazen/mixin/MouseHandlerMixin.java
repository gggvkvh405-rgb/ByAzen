package rtx.byazen.mixin;

import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.impl.input.HotBarScrollEvent;
import rtx.byazen.api.events.impl.input.MouseButtonEvent;

@Mixin(net.minecraft.client.Mouse.class)
public abstract class MouseHandlerMixin {
    @Inject(method = "onMouseButton(JLnet/minecraft/client/input/MouseInput;I)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void byazen_onMouseButton(long window, MouseInput info, int action, CallbackInfo ci) {
        MouseButtonEvent event = EventBus.get().post(new MouseButtonEvent(info.button(), info.modifiers(), MouseButtonEvent.Action.of(action)));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "onMouseScroll(JDD)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void byazen_onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        HotBarScrollEvent event = EventBus.get().post(new HotBarScrollEvent(vertical));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
