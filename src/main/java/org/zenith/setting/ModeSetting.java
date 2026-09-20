package org.zenith.setting;


import java.util.Objects;
import org.zenith.ZenithClient;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class ModeSetting extends Setting {
   public final List<ModeSetting.Option> values = new ArrayList<>();
   public ModeSetting.Option value;

   public ModeSetting(String var1, String var2) {
      super(var1, var2);
   }

   public ModeSetting(String var1, String var2, String... var3) {
      super(var1, var2);

      for (String s : var3) {
         if (!s.isEmpty()) {
            new ModeSetting.Option(this, s);
         }
      }

      if (!this.values.isEmpty()) {
         this.value = this.values.getFirst();
      }
   }

   public ModeSetting(String var1, Supplier<Boolean> var2, String... var3) {
      this(var1, "", var2, var3);
   }

   public ModeSetting(String var1, String var2, Supplier<Boolean> var3, String... var4) {
      super(var1, var2);

      for (String s : var4) {
         if (!s.isEmpty()) {
            new ModeSetting.Option(this, s);
         }
      }

      if (!this.values.isEmpty()) {
         this.value = this.values.getFirst();
      }

      this.setVisible(var3);
   }

   public void set(String var1) {
      this.values.stream().filter(var1xx -> var1xx.getKey().equals(var1)).findFirst().ifPresent(var1x -> this.value = var1x);
   }

   public String get() {
      return this.value != null ? this.value.getKey() : "";
   }

   public boolean is(int var1) {
      return this.values.get(var1).isSelected();
   }

   public boolean is(String var1) {
      return this.value.getKey().equals(var1);
   }

   public ModeSetting.Option getRandomEnabledElement() {
      List<ModeSetting.Option> list = this.values.stream().filter(ModeSetting.Option::isSelected).toList();
      return !list.isEmpty() ? list.get(new Random().nextInt(list.size())) : null;
   }

   @Override
   public void safe(JsonObject var1) {
      var1.addProperty(String.valueOf(this.key), this.get());
   }

   @Override
   public void load(JsonObject var1) {
      this.set(var1.get(String.valueOf(this.key)).getAsString());
   }

   public int getIndex() {
      return this.values.indexOf(this.value);
   }

   public List<ModeSetting.Option> getValues() {
      return this.values;
   }

   public ModeSetting.Option getValue() {
      return this.value;
   }

   public void setValue(ModeSetting.Option var1) {
      this.value = var1;
   }


   public static class Option {
      public final ModeSetting modeSetting33;
      public final String string33;
      public final String string34;

      public Option(ModeSetting var1, String var2) {
         this.modeSetting33 = var1;
         this.string33 = var2;
         this.string34 = "";
         if (var1.values.isEmpty()) {
            this.int210();
         }

         var1.values.add(this);
      }

      public Option(ModeSetting var1, String var2, String var3) {
         this.modeSetting33 = var1;
         this.string33 = var2;
         this.string34 = var3;
         if (var1.values.isEmpty()) {
            this.int210();
         }

         var1.values.add(this);
      }

      public String getName() {
         return ZenithClient.on23().Easing().translate(this.string33);
      }

      public Option int210() {
         this.modeSetting33.setValue(this);
         return this;
      }

      public boolean isSelected() {
         return this.modeSetting33.getValue() == this;
      }

      @Override
      public String toString() {
         return this.string33;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (var1 != null && var1.getClass() == this.getClass()) {
            Option ill11ii1ilil1liili1iliil_ii1il11l111ii11iil = (Option)var1;
            return Objects.equals(this.modeSetting33, ill11ii1ilil1liili1iliil_ii1il11l111ii11iil.modeSetting33)
               && Objects.equals(this.string33, ill11ii1ilil1liili1iliil_ii1il11l111ii11iil.string33)
               && Objects.equals(this.string34, ill11ii1ilil1liili1iliil_ii1il11l111ii11iil.string34);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.modeSetting33, this.string33, this.string34);
      }

      public ModeSetting int211() {
         return this.modeSetting33;
      }

      public String getKey() {
         return this.string33;
      }

      public String getDescription() {
         return this.string34;
      }
   }
}
