package rtx.byazen.mixin.chatanim;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.mods.chatanim.config.ModConfig;
import rtx.byazen.api.modules.impl.Visuals.BetterMinecraft;

@Mixin(ChatHud.class)
public abstract class ChatComponentMixin {
    @Shadow
    private int scrolledLines;

    @Shadow
    public abstract int getLineHeight();

    @Unique
    private long byazen_chatAnimLastMessageTime = 0L;

    @Unique
    private float byazen_chatAnimCalculateDisplacement() {
        ModConfig config = ModConfig.getConfig();
        if (!BetterMinecraft.chatAnimationsEnabled() || !config.enableMessageAnimation || this.scrolledLines != 0) {
            return 0.0f;
        }
        float fadeTime = config.fadeTimeMessage;
        float maxDisplacement = (float)this.getLineHeight() * 0.8f;
        long lifetime = System.currentTimeMillis() - this.byazen_chatAnimLastMessageTime;
        float alpha = Math.min((float)lifetime / fadeTime, 1.0f);
        return maxDisplacement - alpha * maxDisplacement;
    }

    @WrapOperation(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;IIIZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/hud/ChatHud$Backend;IIZ)V"), require = 0)
    private void byazen_chatAnimWrapRender(ChatHud instance, ChatHud.Backend queueMessage, int restrictedMessageWidth, int restrictedMessage, boolean focused, Operation<Void> original, @Local(argsOnly = true) DrawContext graphics) {
        float displacement = this.byazen_chatAnimCalculateDisplacement();
        if (displacement != 0.0f) {
            graphics.getMatrices().pushMatrix();
            graphics.getMatrices().translate(0.0f, displacement);
        }
        original.call(instance, queueMessage, restrictedMessageWidth, restrictedMessage, focused);
        if (displacement != 0.0f) {
            graphics.getMatrices().popMatrix();
        }
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("TAIL"), require = 0)
    private void byazen_chatAnimAddMessage(Text contents, CallbackInfo ci) {
        if (BetterMinecraft.chatAnimationsEnabled()) {
            this.byazen_chatAnimLastMessageTime = System.currentTimeMillis();
        }
    }
}
