package org.zenith.utility.mixin.screen;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatInputSuggestor.SuggestionWindow;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.zenith.ZenithClient;

@Mixin(ChatInputSuggestor.class)
public abstract class MixinChatInputSuggestor {
   @Final
   @Shadow
   TextFieldWidget textField;
   @Shadow
   boolean completingSuggestions;
   @Shadow
   public ParseResults<CommandSource> parse;
   @Shadow
   public CompletableFuture<Suggestions> pendingSuggestions;
   @Shadow
   public SuggestionWindow window;

   @Shadow
   protected abstract void showCommandSuggestions();

   @Inject(
      method = "refresh",
      at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false),
      cancellable = true,
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   public void refreshHook(CallbackInfo var1, String var2, StringReader var3) {
      if (var3.canRead(ZenithClient.on23().CloudResponse().getPrefix().length())
         && var3.getString().startsWith(ZenithClient.on23().CloudResponse().getPrefix(), var3.getCursor())) {
         var3.setCursor(var3.getCursor() + 1);
         if (this.parse == null) {
            this.parse = ZenithClient.on23().CloudResponse().getDispatcher().parse(var3, ZenithClient.on23().CloudResponse().getSource());
         }

         int i = this.textField.getCursor();
         if (i >= 1 && (this.window == null || !this.completingSuggestions)) {
            this.pendingSuggestions = ZenithClient.on23().CloudResponse().getDispatcher().getCompletionSuggestions(this.parse, i);
            this.pendingSuggestions.thenRun(() -> {
               if (this.pendingSuggestions.isDone()) {
                  this.showCommandSuggestions();
               }
            });
         }

         var1.cancel();
      }
   }
}
