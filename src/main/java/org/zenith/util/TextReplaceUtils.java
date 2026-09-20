package org.zenith.util;

import java.util.Optional;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public final class TextReplaceUtils {
   public static MutableText Easing(Text var0, String var1, Text var2) {
      if (var0 == null) {
         return null;
      } else if (var1 != null && !var1.isEmpty()) {
         Text text = (Text)(var2 == null ? Text.empty() : var2);
         MutableText mutabletext = Text.empty();
         var0.visit((style, s) -> {
            int i = 0;

            int j;
            while ((j = s.indexOf(var1, i)) >= 0) {
               if (j > i) {
                  mutabletext.append(Text.literal(s.substring(i, j)).setStyle(style));
               }

               mutabletext.append(text.copy());
               i = j + var1.length();
            }

            if (i < s.length()) {
               mutabletext.append(Text.literal(s.substring(i)).setStyle(style));
            }

            return Optional.empty();
         }, Style.EMPTY);
         return mutabletext;
      } else {
         return var0.copy();
      }
   }

   public static MutableText UiAnimation(Text var0, String var1, String var2) {
      return Easing(var0, var1, var2 == null ? Text.empty() : Text.literal(var2));
   }

   public static MutableText UiAnimation(Text var0, String var1, Text var2) {
      return Easing(var0, var1, var2);
   }

   public static MutableText on23(Text var0, String var1, MutableText var2) {
      return Easing(var0, var1, var2);
   }

   public static MutableText ColorAnimator(Text var0, String var1) {
      MutableText mutabletext = Easing(var0, var1, Text.empty());
      return mutabletext == null ? Text.empty() : mutabletext;
   }

   public static MutableText ItemRegistry(MutableText var0, String var1) {
      if (var0 == null) {
         return null;
      }

      String s = var0.getString();
      int i = s.length();

      while (i > 0 && Character.isWhitespace(s.charAt(i - 1))) {
         i--;
      }

      int j = i;
      int[] aint = new int[]{0};
      MutableText mutabletext = Text.empty();
      var0.visit((style, s1) -> {
         int k = aint[0];
         aint[0] = k + s1.length();
         if (k < j) {
            String s2 = s1.substring(0, Math.min(s1.length(), j - k));
            if (!s2.isEmpty()) {
               mutabletext.append(Text.literal(s2).setStyle(style));
            }
         }

         return Optional.empty();
      }, Style.EMPTY);
      if (var1 != null && !var1.isEmpty()) {
         mutabletext.append(Text.literal(var1));
      }

      return mutabletext;
   }

   public static Text CloudApiClient(Text var0) {
      return var0 == null ? Text.empty() : var0.copy();
   }

   public static Text on23(Text var0, String var1, boolean var2) {
      return ColorAnimator(var0, var1);
   }

   public static Text on23(Text var0, boolean var1) {
      return CloudApiClient(var0);
   }


   public record Replacement(String string36, Style style2) {
      public String text() {
         return this.string36;
      }

      public Style style() {
         return this.style2;
      }
   }
}
