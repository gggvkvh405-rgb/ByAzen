package org.zenith.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LocaleEntry {
   public final String string121;
   public final Map<String, String> map55 = new HashMap<>();

   public LocaleEntry(String var1, InputStream var2) throws IOException {
      this.string121 = var1;
      this.on23(var2);
   }

   public void on23(InputStream var1) throws IOException {
      String s;
      try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(var1))) {
         while ((s = bufferedreader.readLine()) != null) {
            s = s.trim();
            if (!s.isEmpty() && !s.startsWith("#")) {
               String[] astring = s.split("=", 2);
               if (astring.length == 2) {
                  this.map55.put(astring[0].trim(), astring[1].trim());
               }
            }
         }
      }
   }

   public String get(String var1) {
      return this.map55.getOrDefault(var1, var1);
   }

   public String getName() {
      return this.string121;
   }
}
