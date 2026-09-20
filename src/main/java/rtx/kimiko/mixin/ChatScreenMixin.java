package rtx.kimiko.mixin;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.kimiko.api.chat.commands.CommandManager;
import rtx.kimiko.api.chat.commands.suggestion.ClientCommandSuggestions;
import rtx.kimiko.utils.chat.ChatHistory;

@Mixin(net.minecraft.client.gui.screen.ChatScreen.class)

public abstract class ChatScreenMixin
extends Screen {
    @Shadow
    protected TextFieldWidget chatField;
    @Shadow
    ChatInputSuggestor chatInputSuggestor;
    @Unique
    private static boolean kimiko_historySeeded;

    @Shadow
    public abstract String method_44054(String var1);

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method="init", at={@At(value="TAIL")}, require = 0)
    private void kimiko_replaceSuggestions(CallbackInfo ci) {
        CommandManager.get().refreshRuntimeState();
        this.chatInputSuggestor = new ClientCommandSuggestions(this.client, this, this.chatField, this.client.textRenderer, false, false, 1, 10, true, -805306368);
        this.chatInputSuggestor.setCanLeave(false);
        this.chatInputSuggestor.refresh();
        this.kimiko_seedHistory();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Unique
    private void kimiko_seedHistory() {
        if (kimiko_historySeeded) {
            return;
        }
        kimiko_historySeeded = true;
        if (this.client == null || this.client.inGameHud == null) {
            return;
        }
        ChatHistory.seeding = true;
        try {
            ChatHud chat = this.client.inGameHud.getChatHud();
            for (String line : ChatHistory.entries()) {
                chat.addToMessageHistory(line);
            }
        }
        finally {
            ChatHistory.seeding = false;
        }
    }

    @Inject(method="sendMessage", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void kimiko_handleClientCommand(String message, boolean addToHistory, CallbackInfo ci) {
        CommandManager.get().refreshRuntimeState();
        String normalized = this.method_44054(message);
        if (!CommandManager.get().isClientCommand(normalized)) {
            return;
        }
        if (addToHistory) {
            this.client.inGameHud.getChatHud().addToMessageHistory(normalized);
        }
        String prefix = CommandManager.get().getPrefix();
        CommandManager.get().executeRaw(normalized.substring(prefix.length()));
        ci.cancel();
    }
}

