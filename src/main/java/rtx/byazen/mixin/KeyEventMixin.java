package rtx.byazen.mixin;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.impl.input.KeyPressEvent;
import rtx.byazen.api.events.impl.input.KeyPressEvent.Action;

@Mixin(net.minecraft.client.Keyboard.class)

public abstract class KeyEventMixin {
    @Inject(method="onKey", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void byazen_onKeyPress(long window, int action, KeyInput keyEvent, CallbackInfo ci) {
        KeyPressEvent event = EventBus.get().post(new KeyPressEvent(keyEvent.key(), keyEvent.scancode(), keyEvent.modifiers(), KeyPressEvent.Action.of(action)));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}

