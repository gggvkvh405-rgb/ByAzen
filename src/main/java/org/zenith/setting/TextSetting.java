package org.zenith.setting;


import java.util.function.Predicate;
import com.google.gson.JsonObject;
import java.util.function.Supplier;

public class TextSetting extends Setting {
   public final String emptyText;
   public boolean secret;
   public final TextSetting.Validator validator;
   public String value;

   public TextSetting(String var1, String var2, String var3) {
      this(var1, "", var2, var3);
   }

   public TextSetting secret() {
      this.secret = true;
      return this;
   }

   public TextSetting(String var1, String var2, String var3, String var4) {
      super(var1, var2);
      this.value = var3;
      this.emptyText = var4;
      this.validator = TextSetting.Validator.boolean120();
   }

   public TextSetting(String var1, String var2, String var3, TextSetting.Validator var4) {
      this(var1, "", var2, var3, var4);
   }

   public TextSetting(String var1, String var2, String var3, String var4, TextSetting.Validator var5) {
      super(var1, var2);
      this.value = var3;
      this.emptyText = var4;
      this.validator = var5 == null ? TextSetting.Validator.boolean120() : var5;
   }

   public TextSetting(String var1, String var2, String var3, Supplier<Boolean> var4) {
      this(var1, "", var2, var3, var4);
   }

   public TextSetting(String var1, String var2, String var3, String var4, Supplier<Boolean> var5) {
      super(var1, var2);
      this.value = var3;
      this.emptyText = var4;
      this.validator = TextSetting.Validator.boolean120();
      this.setVisible(var5);
   }

   public TextSetting(String var1, String var2, String var3, Supplier<Boolean> var4, TextSetting.Validator var5) {
      this(var1, "", var2, var3, var4, var5);
   }

   public TextSetting(String var1, String var2, String var3, String var4, Supplier<Boolean> var5, TextSetting.Validator var6) {
      super(var1, var2);
      this.value = var3;
      this.emptyText = var4;
      this.validator = var6 == null ? TextSetting.Validator.boolean120() : var6;
      this.setVisible(var5);
   }

   public boolean setValueSafe(String var1) {
      if (this.validator.ItemUseEvent(var1)) {
         this.value = var1;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void safe(JsonObject var1) {
      var1.addProperty(String.valueOf(this.key), this.value);
   }

   @Override
   public void load(JsonObject var1) {
      if (var1.has(String.valueOf(this.key))) {
         String s = var1.get(String.valueOf(this.key)).getAsString();
         this.setValueSafe(s);
      }
   }

   public TextSetting.Validator getValidator() {
      return this.validator;
   }

   public String getValue() {
      return this.value;
   }

   public String getEmptyText() {
      return this.emptyText;
   }

   public boolean isSecret() {
      return this.secret;
   }

   public void setValue(String var1) {
      this.value = var1;
   }


   public static abstract class Validator {
      public final int int120;

      public Validator() {
         this.int120 = Integer.MAX_VALUE;
      }

      public abstract boolean ItemUseEvent(String var1);

      public static Validator boolean120() {
         return new AcceptAnyValidator(Integer.MAX_VALUE);
      }

      public static Validator TradeGuardService(int var0) {
         return new LengthValidator(var0, var0);
      }

      public static Validator on23(int var0, Predicate<String> var1) {
         return new PredicateValidator(var0, var1);
      }

      public Validator(int var1) {
         this.int120 = var1;
      }

      public int getMaxLength() {
         return this.int120;
      }
   }

   public static class AcceptAnyValidator extends Validator {
      AcceptAnyValidator(int var1) {
         super(var1);
      }

      @Override
      public boolean ItemUseEvent(String var1) {
         return var1 != null;
      }
   }

   public static class LengthValidator extends Validator {
      public final int val501;

      LengthValidator(int var1, int var2) {
         super(var1);
         this.val501 = var2;
      }

      @Override
      public boolean ItemUseEvent(String var1) {
         return var1 != null && var1.length() <= this.val501;
      }
   }

   public static class PredicateValidator extends Validator {
      public final Predicate val502;

      PredicateValidator(int var1, Predicate var2) {
         super(var1);
         this.val502 = var2;
      }

      @Override
      public boolean ItemUseEvent(String var1) {
         return var1 != null && this.val502.test(var1);
      }
   }
}
