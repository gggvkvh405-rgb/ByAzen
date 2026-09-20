package rtx.kimiko.mixin.chathads;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import rtx.kimiko.api.mods.chathads.ChatHeads;
import rtx.kimiko.api.mods.chathads.HeadData;
import rtx.kimiko.api.mods.chathads.config.RenderPosition;

@Mixin(targets = {"net.minecraft.client.gui.hud.ChatHud$ChatState"})

public abstract class ChatComponentInnerMixin {
    @ModifyArgs(method={"accept"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_338$class_12233;method_75807(IFLnet/minecraft/class_5481;)Z"), require = 0)
    private void chatheads_renderChatHeadAndOffsetChatMessage(Args args, @Local(argsOnly=true) ChatHudLine.Visible line, @Share(value="chatOffset") LocalIntRef chatOffset) {
        if (ChatHeads.CONFIG.renderPosition() == RenderPosition.BEFORE_LINE) {
            int y = (Integer)args.get(0);
            float opacity = ((Float)args.get(1)).floatValue();
            HeadData headData = ChatHeads.getHeadData(line);
            chatOffset.set(ChatHeads.getChatOffset(headData));
            if (ChatHeads.guiGraphics != null && headData != HeadData.EMPTY) {
                ChatHeads.renderChatHead(ChatHeads.guiGraphics, 0, y, headData.playerInfo, opacity);
            }
            ChatHeads.chatGraphicsAccess.updatePose(matrix3x2f -> matrix3x2f.translate((float)chatOffset.get(), 0.0f));
        }
    }

    @Inject(method={"accept"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_338$class_12233;method_75807(IFLnet/minecraft/class_5481;)Z", shift=At.Shift.AFTER)}, require = 0)
    private void chatheads_undoChatMessageOffset(CallbackInfo ci, @Share(value="chatOffset") LocalIntRef chatOffset) {
        if (ChatHeads.CONFIG.renderPosition() == RenderPosition.BEFORE_LINE) {
            ChatHeads.chatGraphicsAccess.updatePose(matrix3x2f -> matrix3x2f.translate((float)(-chatOffset.get()), 0.0f));
        }
    }

    @Inject(method={"accept"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_338$class_12233;method_75810(IIZLnet/minecraft/class_7591;Lnet/minecraft/class_7591$class_7592;)V")}, require = 0)
    private void chatheads_offsetTagIcon(CallbackInfo ci, @Share(value="chatOffset") LocalIntRef chatOffset) {
        if (ChatHeads.CONFIG.renderPosition() == RenderPosition.BEFORE_LINE) {
            ChatHeads.chatGraphicsAccess.updatePose(matrix3x2f -> matrix3x2f.translate((float)chatOffset.get(), 0.0f));
        }
    }

    @Inject(method={"accept"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_338$class_12233;method_75810(IIZLnet/minecraft/class_7591;Lnet/minecraft/class_7591$class_7592;)V", shift=At.Shift.AFTER)}, require = 0)
    private void chatheads_undoTagIconOffset(CallbackInfo ci, @Share(value="chatOffset") LocalIntRef chatOffset) {
        if (ChatHeads.CONFIG.renderPosition() == RenderPosition.BEFORE_LINE) {
            ChatHeads.chatGraphicsAccess.updatePose(matrix3x2f -> matrix3x2f.translate((float)(-chatOffset.get()), 0.0f));
        }
    }
}

