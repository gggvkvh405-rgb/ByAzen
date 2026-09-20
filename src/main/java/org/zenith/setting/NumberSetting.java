package org.zenith.setting;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import org.zenith.ZenithClient;

public class NumberSetting extends Setting {
   public final String suffix;
   public final String description;
   public float current;
   public final float min;
   public final float max;
   public final float increment;
   public NumberSetting.Formatter edit;

   @Override
   public String getDescription() {
      return ZenithClient.on23().Easing().translate(this.description);
   }

   public NumberSetting(
      String var1, float var2, float var3, float var4, float var5, String var6, String var7, Supplier<Boolean> var8, NumberSetting.Formatter var9
   ) {
      super(var1, var6);
      this.min = var3;
      this.max = var4;
      this.current = var2;
      this.increment = var5;
      this.description = var6 != null ? var6 : "";
      this.suffix = var7 != null ? var7 : "";
      this.edit = var9;
      if (var8 != null) {
         this.setVisible(var8);
      }
   }

   public NumberSetting(String var1, float var2, float var3, float var4, float var5) {
      this(var1, var2, var3, var4, var5, "", "%", null, null);
   }

   public NumberSetting(String var1, float var2, float var3, float var4, float var5, String var6) {
      this(var1, var2, var3, var4, var5, var6, "%", null, null);
   }

   public NumberSetting(String var1, float var2, float var3, float var4, float var5, String var6, String var7) {
      this(var1, var2, var3, var4, var5, var6, var7, null, null);
   }

   public NumberSetting(String var1, float var2, float var3, float var4, float var5, Supplier<Boolean> var6) {
      this(var1, var2, var3, var4, var5, "", "%", var6, null);
   }

   public NumberSetting(String var1, float var2, float var3, float var4, float var5, NumberSetting.Formatter var6) {
      this(var1, var2, var3, var4, var5, "", "%", null, var6);
   }

   public NumberSetting(String var1, float var2, float var3, float var4, float var5, Supplier<Boolean> var6, NumberSetting.Formatter var7) {
      this(var1, var2, var3, var4, var5, "", "%", var6, var7);
   }

   public NumberSetting(String var1, float var2, float var3, float var4, float var5, String var6, String var7, Supplier<Boolean> var8) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, null);
   }

   public NumberSetting(String var1, float var2, float var3, float var4, float var5, String var6, Supplier<Boolean> var7, NumberSetting.Formatter var8) {
      this(var1, var2, var3, var4, var5, "", var6, var7, var8);
   }

   public void setCurrent(float var1) {
      float f = this.current;
      this.current = var1;
      if (this.edit != null) {
         this.edit.apply(f, var1);
      }
   }

   @Override
   public void safe(JsonObject var1) {
      var1.addProperty(String.valueOf(this.key), this.getCurrent());
   }

   @Override
   public void load(JsonObject var1) {
      this.setCurrent(var1.get(String.valueOf(this.key)).getAsFloat());
   }

   public String getSuffix() {
      return this.suffix;
   }

   public float getCurrent() {
      return this.current;
   }

   public float getMin() {
      return this.min;
   }

   public float getMax() {
      return this.max;
   }

   public float getIncrement() {
      return this.increment;
   }

   public NumberSetting.Formatter getEdit() {
      return this.edit;
   }


   public interface Formatter {
      void apply(float var1, float var2);
   }
}
