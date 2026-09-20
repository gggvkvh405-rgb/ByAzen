package org.zenith.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextScanner {
   public static final Map<String, String> GameMessageEvent;

   public static List<ItemSpec> UiAnimation(String var0) {
      List<ItemSpec> arraylist = new ArrayList<>();
      String s = var0.trim();
      if (s.startsWith("[") && s.endsWith("]")) {
         s = s.substring(1, s.length() - 1);
      }

      Pattern pattern = Pattern.compile("(EnchantCustom|EnchantVanilla) \\[checked=([^,]+), level=(\\d+)]");
      Matcher matcher = pattern.matcher(s);

      while (matcher.find()) {
         String s1 = matcher.group(1);
         String s2 = matcher.group(2);
         int i = Integer.parseInt(matcher.group(3));
         String s3 = GameMessageEvent.getOrDefault(s2, s2);
         ItemSpec object;
         if (s1.equals("EnchantCustom")) {
            object = new NbtItemSpec(s3, s2, i);
         } else {
            object = new EnchantItemSpec(s3, s2, i);
         }

         arraylist.add(object);
      }

      return arraylist;
   }

   static {
      Map<String, String> hashmap = new HashMap<>();
      hashmap.put("minecraft:luck_of_the_sea", "Везучий рыбак");
      hashmap.put("minecraft:thorns", "Шипы");
      hashmap.put("pinger", "Пингер");
      hashmap.put("minecraft:feather_falling", "Невесомость (Падение)");
      hashmap.put("minecraft:flame", "Воспламенение");
      hashmap.put("minecraft:binding_curse", "Проклятие несъёмности");
      hashmap.put("minecraft:projectile_protection", "Защита от снарядов");
      hashmap.put("minecraft:smite", "Небесная кара");
      hashmap.put("minecraft:frost_walker", "Ледоход");
      hashmap.put("minecraft:punch", "Откидывание (Удар стрелой)");
      hashmap.put("minecraft:sharpness", "Острота");
      hashmap.put("minecraft:efficiency", "Эффективность");
      hashmap.put("demolishing", "Разрушение");
      hashmap.put("minecraft:lure", "Приманка");
      hashmap.put("minecraft:fire_aspect", "Заговор огня");
      hashmap.put("minecraft:piercing", "Точность");
      hashmap.put("minecraft:unbreaking", "Прочность");
      hashmap.put("minecraft:quick_charge", "Быстрая перезарядка");
      hashmap.put("minecraft:riptide", "Замедление");
      hashmap.put("minecraft:fortune", "Удача");
      hashmap.put("minecraft:fire_protection", "Огнеупорность");
      hashmap.put("minecraft:knockback", "Отбрасывание");
      hashmap.put("minecraft:mending", "Починка");
      hashmap.put("minecraft:power", "Сила");
      hashmap.put("stupor", "Ступор");
      hashmap.put("minecraft:aqua_affinity", "Подводник (Ускорение добычи под водой)");
      hashmap.put("skilled", "Опытный");
      hashmap.put("minecraft:sweeping_edge", "Разящий клинок");
      hashmap.put("web", "Паутина");
      hashmap.put("buldozing", "Бульдозер");
      hashmap.put("minecraft:vanishing_curse", "Проклятие утраты");
      hashmap.put("smelting", "Автоплавка");
      hashmap.put("minecraft:blast_protection", "Взрывоустойчивость");
      hashmap.put("minecraft:impaling", "Пронзатель");
      hashmap.put("minecraft:bane_of_arthropods", "Бич членистоногих");
      hashmap.put("detection", "Детекция");
      hashmap.put("poison", "Яд");
      hashmap.put("minecraft:silk_touch", "Шёлковое касание");
      hashmap.put("oxidation", "Окисление");
      hashmap.put("minecraft:looting", "Добыча");
      hashmap.put("minecraft:depth_strider", "Подводник (Подводная ходьба)");
      hashmap.put("minecraft:soul_speed", "Скорость души");
      hashmap.put("vampirism", "Вампиризм");
      hashmap.put("magnet", "Магнит");
      hashmap.put("minecraft:respiration", "Подводное дыхание");
      hashmap.put("minecraft:loyalty", "Верность");
      hashmap.put("returning", "Возврат");
      hashmap.put("pulling", "Притягивание");
      hashmap.put("minecraft:protection", "Защита");
      hashmap.put("minecraft:infinity", "Бесконечность");
      hashmap.put("scout", "Скаут");
      hashmap.put("minecraft:multishot", "Многострельность");
      hashmap.put("minecraft:channeling", "Громовержец");
      GameMessageEvent = hashmap;
   }
}
