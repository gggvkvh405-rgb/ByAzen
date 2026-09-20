package org.zenith.setting;

import com.google.gson.JsonObject;

public class ButtonSetting extends Setting {
   public String string67;
   public Runnable runnable2;

   public ButtonSetting(String var1, Runnable var2) {
      this(var1, "t", var2);
   }

   public ButtonSetting(String var1, String var2, Runnable var3) {
      this(var1, var2, "", var3);
   }

   public ButtonSetting(String var1, String var2, String var3, Runnable var4) {
      super(var1, var3);
      this.runnable2 = var4;
      this.string67 = var2;
   }

   public void toggle() {
      this.runnable2.run();
   }

   @Override
   public void safe(JsonObject var1) {
   }

   @Override
   public void load(JsonObject var1) {
   }

   public String getIcon() {
      return this.string67;
   }

   public Runnable classMethod() {
      return this.runnable2;
   }

   public void Event18Ext(String var1) {
      this.string67 = var1;
   }

   public void ItemRegistry(Runnable var1) {
      this.runnable2 = var1;
   }
}
