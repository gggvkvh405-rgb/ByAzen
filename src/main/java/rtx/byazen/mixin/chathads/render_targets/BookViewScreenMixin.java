package rtx.byazen.mixin.chathads.render_targets;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.mods.chathads.ChatHeads;

@Mixin(net.minecraft.client.gui.screen.ingame.BookScreen.class)

public abstract class BookViewScreenMixin {
    @Inject(method="render", at={@At(value="HEAD")}, require = 0)
    public void chatheads_isInsideBook(CallbackInfo ci) {
        ChatHeads.customHeadRendering = true;
    }

    @Inject(method="render", at={@At(value="RETURN")}, require = 0)
    public void chatheads_isOutsideBook(CallbackInfo ci) {
        ChatHeads.customHeadRendering = false;
    }
}

