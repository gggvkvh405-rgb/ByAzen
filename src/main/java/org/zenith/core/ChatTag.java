package org.zenith.core;

import java.util.Locale;

public enum ChatTag {
   call214("Груз", "C"),
   call418("Босс", "B"),
   call442("Кубик", "B"),
   call443("Контейнер", "D"),
   call419("Золотая лихорадка", "E"),
   call420("Посылка", "F"),
   call444("Корабль", "G"),
   call421("Цветочная поляна", "H"),
   call422("Цветочная поляна", "H"),
   call445("Смертельная шахта", "I"),
   call423("Смертельная шахта", "I"),
   call177("Опытный Тыпо", "J"),
   call446("Голосование", "K"),
   call215("Неизвестно", "A");

   public final String string66;
   public final String string67;

   ChatTag(String var3, String var4) {
      this.string66 = var3;
      this.string67 = var4;
   }

   public static ChatTag GuiWalkEvent(String var0) {
      if (var0 == null) {
         return call215;
      }

      String s = var0.toUpperCase(Locale.ROOT);
      switch (s) {
         case "GOLDEN_FORTRESS":
            return call419;
         case "PARCELS":
            return call420;
         case "SNOWQUARRY":
            return call422;
         case "JAYCOB":
            return call177;
         default:
            try {
               return valueOf(s);
            } catch (IllegalArgumentException illegalargumentexception) {
               return call215;
            }
      }
   }

   public String getDisplayName() {
      return this.string66;
   }

   public String getIcon() {
      return this.string67;
   }
}
