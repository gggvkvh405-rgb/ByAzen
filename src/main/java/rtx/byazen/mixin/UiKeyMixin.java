package rtx.byazen.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.ClickGui;
import rtx.byazen.api.ui.UI;
import rtx.byazen.utils.sounds.Sounds;

@Mixin(net.minecraft.client.Keyboard.class)
public abstract class UiKeyMixin {
    @Inject(method = "onKey", at = @At("HEAD"), require = 0, cancellable = true)
    private void byazen_uiKey(long window, int action, KeyInput keyEvent, CallbackInfo ci) {
        if (action != 1) {
            return;
        }
        ClickGui clickGui = ModuleManager.get().get(ClickGui.class);
        if (clickGui == null) {
            return;
        }
        int toggleKey = clickGui.getBind().getCode();
        if (keyEvent.key() != toggleKey) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) {
            return;
        }
        if (mc.currentScreen == null) {
            mc.setScreen((Screen)UI.INSTANCE);
            Sounds.play("gui_open");
            ci.cancel();
        }
    }
}
