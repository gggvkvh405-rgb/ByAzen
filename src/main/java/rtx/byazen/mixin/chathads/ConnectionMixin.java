package rtx.byazen.mixin.chathads;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.mods.chathads.ChatHeads;

@Mixin(net.minecraft.network.ClientConnection.class)

public abstract class ConnectionMixin {
    @Inject(method="connect", at={@At(value="HEAD")}, require = 0)
    public void chatheads_resetServerKnowledge(CallbackInfo ci) {
        ChatHeads.serverSentUuid = false;
        ChatHeads.serverDisabledChatHeads = false;
    }
}

