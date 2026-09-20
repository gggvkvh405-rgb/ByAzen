package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.zenith.ZenithClient;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.core.HolyWorldClient;
import org.zenith.managers.CloudApi;

public class RCTCommand extends CommandAbstract {
   public RCTCommand() {
      super("rct");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var1x -> {
         HolyWorldClient l1llilll1iiiill11l1l11lii = this.repository();
         if (!l1llilll1iiiill11l1l11lii.isHolyWorldHere()) {
            CloudApi.TextUtils().on23("0", Text.literal(" Не работает на этом " + Formatting.RED + "сервере"));
            return 1;
         } else if (l1llilll1iiiill11l1l11lii.SpinMarker()) {
            CloudApi.TextUtils().on23("️0", Text.literal(" Вы находитесь в режиме " + Formatting.RED + "пвп"));
            return 1;
         } else {
            l1llilll1iiiill11l1l11lii.reconnect(l1llilll1iiiill11l1l11lii.currentAnarchyHere());
            return 1;
         }
      });
      var1.then(CommandAbstract.arg("anarchy", IntegerArgumentType.integer(1, 69)).executes(var1x -> {
         HolyWorldClient l1llilll1iiiill11l1l11lii = this.repository();
         if (!l1llilll1iiiill11l1l11lii.isHolyWorldHere()) {
            CloudApi.TextUtils().on23("0", Text.literal(" Не работает на этом " + Formatting.RED + "сервере"));
            return 1;
         } else if (l1llilll1iiiill11l1l11lii.SpinMarker()) {
            CloudApi.TextUtils().on23("0", Text.literal(" Вы находитесь в режиме " + Formatting.RED + "пвп"));
            return 1;
         } else {
            int i = (Integer)var1x.getArgument("anarchy", Integer.class);
            l1llilll1iiiill11l1l11lii.reconnect(i);
            return 1;
         }
      }));
   }

   public HolyWorldClient repository() {
      return ZenithClient.on23().UiAnimation();
   }
}
