package org.zenith.base.font;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.zenith.module.misc.NameProtect;
import org.zenith.util.ColorUtils;

public class FormattedTextProcessor {
   public static List<FormattedTextProcessor_TextSegment> processText(Text var0, int var1) {
      List<FormattedTextProcessor_TextSegment> arraylist = new ArrayList<>();
      var0.visit((var2x, var3) -> {
         if (!var3.isEmpty()) {
            int i = extractColor(var2x, var1);
            if (MinecraftClient.getInstance().player != null && var3.contains(MinecraftClient.getInstance().player.getNameForScoreboard())) {
               var3 = var3.replace(MinecraftClient.getInstance().player.getNameForScoreboard(), NameProtect.call029());
            }

            boolean flag = var2x.isBold();
            boolean flag1 = var2x.isItalic();
            boolean flag2 = var2x.isUnderlined();
            boolean flag3 = var2x.isStrikethrough();
            arraylist.add(new FormattedTextProcessor_TextSegment(var3, i, flag, flag1, flag2, flag3));
         }

         return Optional.empty();
      }, Style.EMPTY);
      return arraylist;
   }

   public static int extractColor(Style var0, int var1) {
      TextColor textcolor = var0.getColor();
      if (textcolor != null) {
         int i = textcolor.getRgb() | 0xFF000000;
         return i != -1 ? ColorUtils.ColorAnimator(i, ColorUtils.Item(var1)) : var1;
      } else {
         return var1;
      }
   }
}
