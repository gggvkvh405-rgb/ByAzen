package org.zenith.setting;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import org.zenith.util.ScoreboardUtils;

public class KeySetting extends Setting {
   public String string79;
   public int keyCode;

   public void setKeyCode(int var1) {
      this.keyCode = var1;
      this.string79 = ScoreboardUtils.EventPosHook(var1);
   }

   public KeySetting(String var1, Supplier<Boolean> var2) {
      this(var1, "", var2);
   }

   public KeySetting(String var1, String var2, Supplier<Boolean> var3) {
      super(var1, var2);
      this.setVisible(var3);
      this.keyCode = -1;
      this.string79 = ScoreboardUtils.EventPosHook(this.keyCode);
   }

   public KeySetting(String var1, int var2, Supplier<Boolean> var3) {
      this(var1, "", var2, var3);
   }

   public KeySetting(String var1, String var2, int var3, Supplier<Boolean> var4) {
      super(var1, var2);
      this.setVisible(var4);
      this.keyCode = var3;
      this.string79 = ScoreboardUtils.EventPosHook(var3);
   }

   public KeySetting(String var1, int var2) {
      this(var1, "", var2);
   }

   public KeySetting(String var1, String var2, int var3) {
      super(var1, var2);
      this.keyCode = var3;
      this.string79 = ScoreboardUtils.EventPosHook(var3);
   }

   public KeySetting(String var1) {
      this(var1, "");
   }

   public KeySetting(String var1, String var2) {
      super(var1, var2);
      this.keyCode = -1;
      this.string79 = "";
   }

   @Override
   public void safe(JsonObject var1) {
      var1.addProperty(String.valueOf(this.key), this.getKeyCode());
   }

   @Override
   public void load(JsonObject var1) {
      this.setKeyCode(var1.get(String.valueOf(this.key)).getAsInt());
   }

   public String linkedList() {
      return this.string79;
   }

   public int getKeyCode() {
      return this.keyCode;
   }
}
