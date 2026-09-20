package rtx.kimiko.mixin;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import rtx.kimiko.api.chat.commands.helpers.AnimatedChatText;

@Mixin(net.minecraft.client.gui.hud.ChatHud.class)

public abstract class AnimatedChatHeaderMixin {
    @ModifyArgs(method="accept", at=@At(value="INVOKE", target="Lnet/minecraft/class_338$class_12233;method_75807(IFLnet/minecraft/class_5481;)Z"), require = 0)
    private void kimiko_animateHeader(Args args) {
        OrderedText text = (OrderedText)args.get(2);
        if (text != null && AnimatedChatText.hasSentinel(text)) {
            args.set(2, (Object)AnimatedChatText.animate(text));
        }
    }
}

