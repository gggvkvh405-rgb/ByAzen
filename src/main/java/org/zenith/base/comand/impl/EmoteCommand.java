package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.core.StyledTextBuilder;
import org.zenith.core.TextAccent;
import org.zenith.managers.EmoteRegistry;

public final class EmoteCommand extends CommandAbstract {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public EmoteCommand() {
      super("emote");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var0 -> {
         StyledTextBuilder.on23(TextAccent.call002, "Использование: .emote <id>, .emote stop или .emote list");
         return 1;
      });
      var1.then(literal("list").executes(var0 -> {
         StyledTextBuilder.on23(TextAccent.call002, "Доступные анимации: " + String.join(", ", EmoteRegistry.map56()));
         return 1;
      }));
      var1.then(literal("stop").executes(var0 -> {
         if (minecraftClient3.player != null) {
            EmoteRegistry.ItemSpec(minecraftClient3.player.getUuid());
         }

         return 1;
      }));
      var1.then(arg("id", StringArgumentType.word()).executes(var0 -> {
         String s = StringArgumentType.getString(var0, "id");
         if (!EmoteRegistry.BotChatEvent(s)) {
            StyledTextBuilder.on23(TextAccent.call417, "Неизвестная анимация: " + s + ". Используйте .emote list");
         }

         return 1;
      }));
   }
}
