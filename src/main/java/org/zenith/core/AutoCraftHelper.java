package org.zenith.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.zenith.module.misc.AutoCraft;

public final class AutoCraftHelper {
   public static final int int479 = 2;
   public static final int int480 = 2;
   public final AutoCraft autoCraft4;
   public String string135 = "";
   public int int481 = 0;
   public final Set<String> set23 = new HashSet<>();
   public final Map<String, Integer> map60 = new HashMap<>();

   public AutoCraftHelper(AutoCraft var1) {
      this.autoCraft4 = var1;
   }

   public void reset() {
      this.set23.clear();
      this.map60.clear();
      this.call114();
   }

   public void call114() {
      this.string135 = "";
      this.int481 = 0;
   }

   public int call239() {
      return 2;
   }

   public boolean UiAnimation(ItemFilterRules var1, String var2) {
      String s = this.TextScanner(var1, var2);
      if (!s.isBlank() && !s.equals(this.string135)) {
         this.string135 = s;
         this.int481 = 2;
      }

      if (this.int481 > 0) {
         this.int481--;
         return true;
      } else {
         return false;
      }
   }

   public boolean Easing(ItemFilterRules var1, String var2) {
      String s = this.TextScanner(var1, var2);
      return !s.isBlank() && this.set23.contains(s);
   }

   public boolean ColorAnimator(ItemFilterRules var1, String var2) {
      String s = this.TextScanner(var1, var2);
      return !s.isBlank() && this.map60.getOrDefault(s, 0) >= 2;
   }

   public int ItemRegistry(ItemFilterRules var1, String var2) {
      String s = this.TextScanner(var1, var2);
      if (s.isBlank()) {
         return 0;
      }

      int i = this.map60.getOrDefault(s, 0) + 1;
      this.map60.put(s, i);
      return i;
   }

   public void ItemSpec(ItemFilterRules var1, String var2) {
      String s = this.TextScanner(var1, var2);
      if (!s.isBlank()) {
         this.map60.remove(s);
         this.set23.remove(s);
      }
   }

   public void on23(ItemFilterRules var1, String var2, String var3, String var4, boolean var5) {
      String s = this.TextScanner(var1, var2);
      if (!s.isBlank()) {
         boolean flag = this.set23.add(s);
         this.map60.put(s, 2);
         if (var5 && flag) {
            this.autoCraft4.VisualSettingsStore("Source empty: " + this.autoCraft4.ProtocolMessage(var3, var4));
         }
      }
   }

   public String TextScanner(ItemFilterRules var1, String var2) {
      if (var1 != null && var2 != null && !var2.isBlank()) {
         BlockPosEntry iili1i11ii1l1l11il = var1.PlayerStateService(var2);
         long i = iili1i11ii1l1l11il != null && iili1i11ii1l1l11il.isPresent() ? iili1i11ii1l1l11il.string108().asLong() : Long.MIN_VALUE;
         return var1.string112().toLowerCase(Locale.ROOT) + ":" + var1.getId().toLowerCase(Locale.ROOT) + ":" + var2 + ":" + i;
      } else {
         return "";
      }
   }
}
