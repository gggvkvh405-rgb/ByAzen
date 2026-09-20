package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.command.CommandSource;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.core.StyledTextBuilder;
import org.zenith.core.TextAccent;

public class CalcCommand extends CommandAbstract {
   public CalcCommand() {
      super("calc");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var0 -> {
         StyledTextBuilder.on23(TextAccent.call002, "Использование: .calc <выражение>");
         StyledTextBuilder.on23(TextAccent.call002, "Пример: .calc (2 + 2) * 2 или .calc 64 * 3.5кк, подробнее: .calc help");
         return 1;
      });
      var1.then(literal("help").executes(var0 -> {
         StyledTextBuilder.on23(TextAccent.call002, "Операторы: + - * / ^ и скобки, умножение можно писать как x или х");
         StyledTextBuilder.on23(TextAccent.call002, "Числа: 1.5 или 1,5; суффиксы к/кк/ккк: 1кк = 1 000 000");
         StyledTextBuilder.on23(TextAccent.call002, "Процент от числа: .calc 250 + 10% = 275, .calc 1кк - 15% = 850 000");
         StyledTextBuilder.on23(TextAccent.call002, "Функции: sqrt, cbrt, abs, round, floor, ceil, sin, cos, tan, ln, log; константы pi, e");
         StyledTextBuilder.on23(TextAccent.call002, "Пример: .calc sqrt(16) + 2 ^ 10 или .calc (27 * 1.8кк) - 10%");
         return 1;
      }));
      var1.then(arg("expression", StringArgumentType.greedyString()).executes(var1x -> {
         String s = ((String)var1x.getArgument("expression", String.class)).trim();

         try {
            double d0 = new CalcCommand_ExpressionParser(s).parse();
            StyledTextBuilder.on23(TextAccent.call002, s + " = " + this.format(d0));
         } catch (CalcCommand_CalcException calccommand_calcexception) {
            StyledTextBuilder.on23(TextAccent.call417, calccommand_calcexception.getMessage());
         }

         return 1;
      }));
   }

   public String format(double var1) {
      BigDecimal bigdecimal = BigDecimal.valueOf(var1).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros();
      if (bigdecimal.compareTo(BigDecimal.ZERO) == 0) {
         bigdecimal = BigDecimal.ZERO;
      }

      String s = bigdecimal.toPlainString();
      boolean flag = s.startsWith("-");
      if (flag) {
         s = s.substring(1);
      }

      int i = s.indexOf(46);
      String s1 = i == -1 ? s : s.substring(0, i);
      String s2 = i == -1 ? "" : s.substring(i);
      StringBuilder stringbuilder = new StringBuilder();

      for (int j = 0; j < s1.length(); j++) {
         if (j > 0 && (s1.length() - j) % 3 == 0) {
            stringbuilder.append(' ');
         }

         stringbuilder.append(s1.charAt(j));
      }

      return (flag ? "-" : "") + stringbuilder + s2;
   }
}
