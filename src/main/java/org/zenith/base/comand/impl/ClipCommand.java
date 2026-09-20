package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.CoordinateArgumentType;
import org.zenith.core.StyledTextBuilder;
import org.zenith.core.TextAccent;

public class ClipCommand extends CommandAbstract {
   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(literal("vclip").then(arg("distance", CoordinateArgumentType.create()).executes(var0 -> {
         double d0 = (Double)var0.getArgument("distance", Double.class);
         ClientPlayerEntity clientplayerentity = MinecraftClient.getInstance().player;
         if (clientplayerentity != null) {
            clientplayerentity.setPosition(clientplayerentity.getX(), clientplayerentity.getY() + d0 + 0.1, clientplayerentity.getZ());
            StyledTextBuilder.on23(TextAccent.call002, "Вертикальный вклип на " + d0 + " блоков");
         }

         return 1;
      })));
      var1.then(
         literal("hclip")
            .then(
               arg("distance", CoordinateArgumentType.create())
                  .executes(
                     var0 -> {
                        double d0 = (Double)var0.getArgument("distance", Double.class);
                        ClientPlayerEntity clientplayerentity = MinecraftClient.getInstance().player;
                        if (clientplayerentity != null) {
                           double d1 = Math.toRadians(clientplayerentity.getYaw());
                           clientplayerentity.setPosition(
                              clientplayerentity.getX() - Math.sin(d1) * d0,
                              clientplayerentity.getY() + 0.1,
                              clientplayerentity.getZ() + Math.cos(d1) * d0
                           );
                           StyledTextBuilder.on23(TextAccent.call002, "Горизонтальный вклип на " + d0 + " блоков");
                        }

                        return 1;
                     }
                  )
            )
      );
      var1.then(literal("up").then(arg("distance", CoordinateArgumentType.create()).executes(var0 -> {
         double d0 = (Double)var0.getArgument("distance", Double.class);
         ClientPlayerEntity clientplayerentity = MinecraftClient.getInstance().player;
         if (clientplayerentity != null) {
            clientplayerentity.setPosition(clientplayerentity.getX(), clientplayerentity.getY() + d0 + 0.1, clientplayerentity.getZ());
            StyledTextBuilder.on23(TextAccent.call002, "Вклип вверх на " + d0 + " блоков");
         }

         return 1;
      })));
      var1.then(literal("down").then(arg("distance", CoordinateArgumentType.create()).executes(var0 -> {
         double d0 = (Double)var0.getArgument("distance", Double.class);
         ClientPlayerEntity clientplayerentity = MinecraftClient.getInstance().player;
         if (clientplayerentity != null) {
            clientplayerentity.setPosition(clientplayerentity.getX(), clientplayerentity.getY() - d0 + 0.1, clientplayerentity.getZ());
            StyledTextBuilder.on23(TextAccent.call002, "Вклип вниз на " + d0 + " блоков");
         }

         return 1;
      })));
      var1.then(
         literal("forward")
            .then(
               arg("distance", CoordinateArgumentType.create())
                  .executes(
                     var0 -> {
                        double d0 = (Double)var0.getArgument("distance", Double.class);
                        ClientPlayerEntity clientplayerentity = MinecraftClient.getInstance().player;
                        if (clientplayerentity != null) {
                           double d1 = Math.toRadians(clientplayerentity.getYaw());
                           clientplayerentity.setPosition(
                              clientplayerentity.getX() - Math.sin(d1) * d0,
                              clientplayerentity.getY() + 0.1,
                              clientplayerentity.getZ() + Math.cos(d1) * d0
                           );
                           StyledTextBuilder.on23(TextAccent.call002, "Вклип вперед на " + d0 + " блоков");
                        }

                        return 1;
                     }
                  )
            )
      );
      var1.then(
         literal("back")
            .then(
               arg("distance", CoordinateArgumentType.create())
                  .executes(
                     var0 -> {
                        double d0 = (Double)var0.getArgument("distance", Double.class);
                        ClientPlayerEntity clientplayerentity = MinecraftClient.getInstance().player;
                        if (clientplayerentity != null) {
                           double d1 = Math.toRadians(clientplayerentity.getYaw());
                           clientplayerentity.setPosition(
                              clientplayerentity.getX() + Math.sin(d1) * d0,
                              clientplayerentity.getY() + 0.1,
                              clientplayerentity.getZ() - Math.cos(d1) * d0
                           );
                           StyledTextBuilder.on23(TextAccent.call002, "Вклип назад на " + d0 + " блоков");
                        }

                        return 1;
                     }
                  )
            )
      );
      var1.then(literal("help").executes(var0 -> {
         StyledTextBuilder.on23(TextAccent.call002, "Использование: .clip <vclip/hclip/up/down/forward/back/help> [расстояние]");
         StyledTextBuilder.on23(TextAccent.call002, "Примеры:");
         StyledTextBuilder.on23(TextAccent.call002, ".clip vclip 10 - вклип вверх на 10 блоков");
         StyledTextBuilder.on23(TextAccent.call002, ".clip hclip 5 - горизонтальный вклип на 5 блоков");
         StyledTextBuilder.on23(TextAccent.call002, ".clip up 3 - вклип вверх на 3 блока");
         return 1;
      }));
   }

   public ClipCommand() {
      super("clip");
   }
}
