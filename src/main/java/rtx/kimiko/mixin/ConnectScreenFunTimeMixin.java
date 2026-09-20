package rtx.kimiko.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.kimiko.api.ui.vanilla.FunTimeWarningScreen;
import rtx.kimiko.utils.network.FunTimeJoinGuard;

@Mixin(ConnectScreen.class)
public abstract class ConnectScreenFunTimeMixin {
    @Inject(method="connect(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;ZLnet/minecraft/client/network/CookieStorage;)V", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private static void kimiko$funTimeWarning(Screen screen, MinecraftClient minecraft, ServerAddress serverAddress, ServerInfo serverData, boolean quickPlay, CookieStorage transferState, CallbackInfo ci) {
        if (FunTimeJoinGuard.bypass) {
            return;
        }
        if (minecraft.currentScreen instanceof ConnectScreen) {
            return;
        }
        if (!FunTimeJoinGuard.matches(serverAddress, serverData, transferState != null)) {
            return;
        }
        ci.cancel();
        minecraft.setScreen(new FunTimeWarningScreen(screen, serverAddress, serverData, quickPlay, transferState));
    }
}
