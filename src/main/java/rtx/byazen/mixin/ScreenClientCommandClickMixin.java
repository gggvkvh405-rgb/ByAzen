package rtx.byazen.mixin;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.chat.commands.CommandManager;

@Mixin(ClientPlayNetworkHandler.class)

public abstract class ScreenClientCommandClickMixin {
    @Inject(method="runClickEventCommand", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void byazen_routeUnattendedClick(String command, Screen screen, CallbackInfo ci) {
        if (ScreenClientCommandClickMixin.byazen_dispatchLocal(command)) {
            ci.cancel();
        }
    }

    @Inject(method="sendChatCommand", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void byazen_routeSendCommand(String command, CallbackInfo ci) {
        if (ScreenClientCommandClickMixin.byazen_dispatchLocal(command)) {
            ci.cancel();
        }
    }

    @Inject(method="sendChatMessage", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void byazen_routeSendChat(String message, CallbackInfo ci) {
        if (ScreenClientCommandClickMixin.byazen_dispatchLocal(message)) {
            ci.cancel();
        }
    }

    private static boolean byazen_dispatchLocal(String text) {
        if (text == null) {
            return false;
        }
        CommandManager manager = CommandManager.get();
        if (!manager.isClientCommand(text)) {
            return false;
        }
        manager.executeRaw(text.substring(manager.getPrefix().length()));
        return true;
    }
}

