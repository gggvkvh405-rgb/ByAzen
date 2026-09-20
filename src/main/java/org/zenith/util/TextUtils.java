package org.zenith.util;


import net.minecraft.text.Style;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.text.Text;

public final class TextUtils {
   public static final Pattern NUMBER = Pattern.compile("(\\d{1,6})");

   public static boolean isActive() {
      return false;
   }

   public static Text TextScanner(Text var0) {
      return var0;
   }

   public static Text ItemSpec(Text var0) {
      return var0;
   }

   public static List PotionItemBuilder(List var0) {
      return var0;
   }

   public static Text ItemServiceBase(Text var0) {
      return var0 != null && !var0.getString().isBlank() ? var0 : null;
   }

   public static Text PotionItemBuilder(Text var0) {
      return var0 != null && NUMBER.matcher(var0.getString()).find() ? var0 : null;
   }

   public static int ProfileItemBuilder(Text var0) {
      if (var0 == null) {
         return 0;
      }

      Matcher matcher = NUMBER.matcher(var0.getString());
      if (!matcher.find()) {
         return 0;
      }

      try {
         return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException numberformatexception) {
         return 0;
      }
   }

   public static boolean EnchantItemSpec(Text var0) {
      return var0 != null && !var0.getString().isBlank() && !NUMBER.matcher(var0.getString()).find();
   }

   public static boolean SimpleItemBuilder(Text var0) {
      return var0 != null && NUMBER.matcher(var0.getString()).find();
   }

   public static String NbtEditor(Text var0) {
      if (var0 == null) {
         return null;
      }

      String s = var0.getString().trim();
      return s.isEmpty() ? null : s;
   }

   public static int EventUpdateHealth(int var0) {
      return 0;
   }

   public static int JumpEvent(int var0) {
      return 0;
   }


   public record StyledCharacter(char charField, Style style) {
      public char float135() {
         return this.charField;
      }
   }
}
