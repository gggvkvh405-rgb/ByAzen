package org.zenith.setting;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import org.zenith.util.ArgbColor;

public class ColorSetting extends Setting {
   public ArgbColor color;
   public final ColorSetting.ColorProvider colorGetter;

   public ColorSetting(String var1, ArgbColor var2, Supplier<Boolean> var3, ColorSetting.ColorProvider var4) {
      this(var1, "", var2, var4);
      this.setVisible(var3);
   }

   public ColorSetting(String var1, ArgbColor var2, ColorSetting.ColorProvider var3) {
      this(var1, "", var2, var3);
   }

   public ColorSetting(String var1, String var2, ArgbColor var3, ColorSetting.ColorProvider var4) {
      super(var1, var2);
      if (var3 == null) {
         throw new RuntimeException(var1 + " color is null");
      }

      this.color = var3;
      this.setColor(var3);
      this.colorGetter = var4;
   }

   public ColorSetting(String var1, ArgbColor var2) {
      this(var1, "", var2, () -> var2);
   }

   public ColorSetting(String var1, String var2, ArgbColor var3) {
      this(var1, var2, var3, () -> var3);
   }

   public ColorSetting(String var1, ColorSetting.ColorProvider var2) {
      this(var1, "", var2.getDefaultColor(), var2);
   }

   public ColorSetting(String var1, ArgbColor var2, Supplier<Boolean> var3) {
      this(var1, "", var2, () -> var2);
      this.setVisible(var3);
   }

   public ColorSetting(String var1, String var2, ArgbColor var3, Supplier<Boolean> var4) {
      this(var1, var2, var3, () -> var3);
      this.setVisible(var4);
   }

   public int getIntColor() {
      return this.color.call001();
   }

   public void setColor(int var1) {
      this.color = new ArgbColor(var1);
   }

   public void setColor(ArgbColor var1) {
      this.color = var1;
   }

   public void update() {
   }

   public void reset() {
      this.color = this.colorGetter.getDefaultColor();
   }

   public ArgbColor getColor(float var1) {
      return this.color.SprintStateEvent(var1);
   }

   @Override
   public void safe(JsonObject var1) {
      var1.addProperty(String.valueOf(this.key), this.getIntColor());
   }

   @Override
   public void load(JsonObject var1) {
      this.setColor(var1.get(String.valueOf(this.key)).getAsInt());
   }

   public ArgbColor getColor() {
      return this.color;
   }


   public interface ColorProvider {
      ArgbColor getDefaultColor();
   }
}
