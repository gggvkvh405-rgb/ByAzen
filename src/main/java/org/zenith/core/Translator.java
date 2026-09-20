package org.zenith.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Translator {
   public final Map<String, LocaleEntry> map58 = new HashMap<>();
   public final List<String> list112 = new ArrayList<>();
   public LocaleEntry zClass050;
   public String string131;

   public Translator() {
      this.EventClickSlotHook("ru");
      this.EventClickSlotHook("en");
      this.EventClickSlotHook("pl");
      this.EventClickSlotHook("tr");
   }

   public void EventClickSlotHook(String var1) {
      if (!this.list112.contains(var1)) {
         this.list112.add(var1);
      }

      if (this.string131 == null) {
         this.CloseScreenEvent(var1);
      }
   }

   public void CloseScreenEvent(String var1) {
      if (this.list112.contains(var1)) {
         LocaleEntry illl1ilili1il11ll11 = this.EventDead(var1);
         if (illl1ilili1il11ll11 != null) {
            this.map58.clear();
            this.map58.put(var1, illl1ilili1il11ll11);
            this.zClass050 = illl1ilili1il11ll11;
            this.string131 = var1;
         } else if (this.string131 == null) {
            this.string131 = var1;
         }
      }
   }

   public void TranslationKey() {
      this.StringCodec(true);
   }

   public void StringCodec(boolean var1) {
      if (!this.list112.isEmpty()) {
         int i = this.list112.indexOf(this.string131);
         if (i < 0) {
            i = 0;
         }

         int j;
         if (var1) {
            j = (i + 1) % this.list112.size();
         } else {
            j = (i - 1 + this.list112.size()) % this.list112.size();
         }

         this.CloseScreenEvent(this.list112.get(j));
      }
   }

   public void ClickFxController() {
      this.StringCodec(false);
   }

   public String getLanguageName() {
      return this.string131 == null ? "ru" : this.string131;
   }

   public String translate(String var1) {
      if (this.zClass050 == null && this.string131 != null) {
         this.zClass050 = this.EventDead(this.string131);
      }

      return this.zClass050 == null ? var1 : this.zClass050.get(var1);
   }

   public LocaleEntry EventDead(String var1) {
      LocaleEntry illl1ilili1il11ll11 = this.map58.get(var1);
      if (illl1ilili1il11ll11 != null) {
         return illl1ilili1il11ll11;
      }

      try (InputStream inputstream = Translator.class.getResourceAsStream("/assets/zenith/languages/" + var1 + ".language")) {
         if (inputstream == null) {
            return null;
         }

         LocaleEntry illl1ilili1il11ll111 = new LocaleEntry(var1, inputstream);
         this.map58.put(var1, illl1ilili1il11ll111);
         return illl1ilili1il11ll111;
      } catch (IOException ioexception) {
         ioexception.printStackTrace();
         return null;
      }
   }

   public String CryptoUtils() {
      return this.string131;
   }
}
