package rtx.byazen.mixin.chathads;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.mods.chathads.ChatHeads;

@Mixin(net.minecraft.network.ClientConnection.class)

public abstract class ConnectionMixin {
    // Важно: у ClientConnection.connect несколько перегрузок, часть возвращает значения.
    // Селектор без дескриптора цеплял их все — для возвращающих нужен CallbackInfoReturnable,
    // и игра падала при загрузке класса. Поэтому дескриптор указан точно (вход в игру).
    @Inject(method="connect(Ljava/lang/String;ILnet/minecraft/network/listener/ClientLoginPacketListener;)V", at={@At(value="HEAD")}, require = 0)
    public void chatheads_resetServerKnowledge(CallbackInfo ci) {
        ChatHeads.serverSentUuid = false;
        ChatHeads.serverDisabledChatHeads = false;
    }
}

