package org.zenith.base.comand.impl;

import com.google.gson.JsonElement;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.JsonOps;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.PlayerArgumentType;
import org.zenith.core.StyledTextBuilder;
import org.zenith.module.misc.NameProtect;
import org.zenith.module.misc.StreamerMode;
import org.zenith.util.TextReplaceUtils;

public class NameProtectCommand extends CommandAbstract {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public NameProtectCommand() {
      super("nameprotect");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      ((LiteralArgumentBuilder)var1.then(literal("parse").then(arg("player", PlayerArgumentType.createDisplayed()).executes(var0 -> {
         List<PlayerListEntry> list = minecraftClient3.inGameHud.getPlayerListHud().collectPlayerEntries();
         String s = (String)var0.getArgument("player", String.class);

         for (int i = 0; i < list.size(); i++) {
            PlayerListEntry playerlistentry = list.get(i);
            String s1 = playerlistentry.getProfile().name();
            String s2 = StreamerMode.streamerMode.LocaleEntry(s1);
            if (s1.equals(s) || s2.equals(s)) {
               Text text = minecraftClient3.inGameHud.getPlayerListHud().getPlayerName(playerlistentry);
               MutableText mutabletext = TextReplaceUtils.ColorAnimator(text, s2);
               if (mutabletext.getString().isEmpty()) {
                  StyledTextBuilder.RefreshCacheEvent("У данного игрока видимо нету доната");
                  return 1;
               }

               JsonElement jsonelement = (JsonElement)TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, mutabletext).result().get();
               NameProtect.nameProtect.UiAnimation(jsonelement, i);
            }
         }

         return 1;
      })))).executes(var0 -> {
         StyledTextBuilder.RefreshCacheEvent("Использование .nameprotect parse nick и .nameprotect clear ");
         return 1;
      });
      var1.then(literal("clear").executes(var0 -> {
         NameProtect.nameProtect.call105();
         return 1;
      }));
   }
}
