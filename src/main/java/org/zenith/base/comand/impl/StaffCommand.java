package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.zenith.ZenithClient;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.PlayerArgumentType;
import org.zenith.base.comand.impl.args.StaffArgumentType;
import org.zenith.core.StyledTextBuilder;
import org.zenith.core.TextAccent;

public class StaffCommand extends CommandAbstract {
   public StaffCommand() {
      super("staff");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(literal("add").then(arg("player", PlayerArgumentType.create()).executes(var0 -> {
         String s = (String)var0.getArgument("player", String.class);
         if (ZenithClient.on23().CloudUserProfile().getItems().contains(s)) {
            StyledTextBuilder.on23(TextAccent.call013, "Уже добавлен " + s);
            return 1;
         } else {
            ZenithClient.on23().CloudUserProfile().add(s);
            StyledTextBuilder.on23(TextAccent.call002, "Добавили " + s);
            return 1;
         }
      })));
      var1.then(literal("remove").then(arg("player", StaffArgumentType.create()).executes(var0 -> {
         String s = (String)var0.getArgument("player", String.class);
         ZenithClient.on23().CloudUserProfile().remove(s);
         StyledTextBuilder.on23(TextAccent.call002, s + " удален из стаффа");
         return 1;
      })));
      var1.then(literal("list").executes(var0 -> {
         StyledTextBuilder.on23(TextAccent.call002, ZenithClient.on23().CloudUserProfile().getItems().toString());
         return 1;
      }));
   }
}
