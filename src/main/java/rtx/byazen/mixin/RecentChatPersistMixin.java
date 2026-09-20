package rtx.byazen.mixin;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.utils.chat.ChatHistory;

@Mixin(net.minecraft.client.gui.hud.ChatHud.class)

public class RecentChatPersistMixin {
    @Inject(method="addToMessageHistory", at={@At(value="HEAD")}, require = 0)
    private void byazen_persist(String message, CallbackInfo ci) {
        if (!ChatHistory.seeding) {
            ChatHistory.add(message);
        }
    }
}

