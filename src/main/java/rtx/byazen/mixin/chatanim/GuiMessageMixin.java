package rtx.byazen.mixin.chatanim;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rtx.byazen.api.mods.chatanim.config.ModConfig;
import rtx.byazen.api.modules.impl.Visuals.BetterMinecraft;

@Mixin(net.minecraft.client.gui.hud.ChatHudLine.Visible.class)

public abstract class GuiMessageMixin {
    @Inject(method="comp_897", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void byazen_chatAnimRemoveIndicator(CallbackInfoReturnable<MessageIndicator> cir) {
        if (BetterMinecraft.chatAnimationsEnabled() && ModConfig.getConfig().removeMessageIndicator) {
            cir.setReturnValue(null);
        }
    }
}

