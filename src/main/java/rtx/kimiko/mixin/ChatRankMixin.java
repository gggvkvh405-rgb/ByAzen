package rtx.kimiko.mixin;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import rtx.kimiko.api.modules.impl.Utils.StreamerMode;

@Mixin(net.minecraft.client.network.message.MessageHandler.class)

public abstract class ChatRankMixin {
    @ModifyArg(method="processChatMessageInternal", at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/hud/ChatHud;method_44811(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V", ordinal=0), require = 0)
    private Text kimiko_playerChatRank(Text message) {
        return StreamerMode.applySelfRankInChat(message);
    }

    @ModifyArg(method="onGameMessage", at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/hud/ChatHud;method_1812(Lnet/minecraft/class_2561;)V", ordinal=0), require = 0)
    private Text kimiko_systemChatRank(Text message) {
        return StreamerMode.applySelfRankInChat(message);
    }
}

