package org.zenith.setting;


import org.zenith.ZenithClient;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MultiSelectSetting extends Setting {
   public final List<MultiSelectSetting.Option> list57;

   public MultiSelectSetting(String var1) {
      this(var1, "");
   }

   public MultiSelectSetting(String var1, String var2) {
      super(var1, var2);
      this.list57 = new ArrayList<>();
   }

   public MultiSelectSetting(String var1, MultiSelectSetting.Option... var2) {
      this(var1, "", var2);
   }

   public MultiSelectSetting(String var1, String var2, MultiSelectSetting.Option... var3) {
      super(var1, var2);
      this.list57 = new ArrayList<>(Arrays.asList(var3));
   }

   public MultiSelectSetting.Option Event29(String var1) {
      return this.list57.stream().filter(var1xx -> var1xx.getKey().equalsIgnoreCase(var1)).findFirst().orElse(null);
   }

   public static MultiSelectSetting UiAnimation(String var0, List<String> var1) {
      return on23(var0, "", var1);
   }

   public static MultiSelectSetting on23(String var0, String var1, List<String> var2) {
      MultiSelectSetting.Option[] ai1i1lll1liii1il1llll1_ii1il11l111ii11iil = var2.stream()
         .map(var0x -> new MultiSelectSetting.Option(var0x, true))
         .toArray(MultiSelectSetting.Option[]::new);
      return new MultiSelectSetting(var0, var1, ai1i1lll1liii1il1llll1_ii1il11l111ii11iil);
   }

   public MultiSelectSetting.Option AnalyticsTracker(int var1) {
      return this.list57.get(var1);
   }

   public boolean RotationUpdateStartEvent(String var1) {
      MultiSelectSetting.Option i1i1lll1liii1il1llll1_ii1il11l111ii11iil = this.Event29(var1);
      return i1i1lll1liii1il1llll1_ii1il11l111ii11iil != null && i1i1lll1liii1il1llll1_ii1il11l111ii11iil.isEnabled();
   }

   public boolean ConfigJsonUtil(int var1) {
      if (var1 >= this.int212().size()) {
         return false;
      }

      MultiSelectSetting.Option i1i1lll1liii1il1llll1_ii1il11l111ii11iil = this.AnalyticsTracker(var1);
      return i1i1lll1liii1il1llll1_ii1il11l111ii11iil != null && i1i1lll1liii1il1llll1_ii1il11l111ii11iil.isEnabled();
   }

   public List<MultiSelectSetting.Option> class2() {
      return this.list57.stream().filter(MultiSelectSetting.Option::isEnabled).collect(Collectors.toList());
   }

   @Override
   public void safe(JsonObject var1) {
      StringBuilder stringbuilder = new StringBuilder();
      int i = 0;

      for (MultiSelectSetting.Option i1i1lll1liii1il1llll1_ii1il11l111ii11iil : this.int212()) {
         if (this.Event29(i1i1lll1liii1il1llll1_ii1il11l111ii11iil.getKey()).isEnabled()) {
            stringbuilder.append(i1i1lll1liii1il1llll1_ii1il11l111ii11iil.getKey()).append("\n");
         }

         i++;
      }

      var1.addProperty(this.key, stringbuilder.toString());
   }

   @Override
   public void load(JsonObject var1) {
      this.int212().forEach(var0 -> var0.setEnabled(false));
      String[] astring = var1.get(String.valueOf(this.key)).getAsString().split("\n");

      for (String s : astring) {
         MultiSelectSetting.Option i1i1lll1liii1il1llll1_ii1il11l111ii11iil = this.Event29(s);
         if (i1i1lll1liii1il1llll1_ii1il11l111ii11iil != null) {
            this.Event29(s).setEnabled(true);
         }
      }
   }

   public List<String> zClass100Var143Var143() {
      return this.list57.stream().filter(MultiSelectSetting.Option::isEnabled).map(MultiSelectSetting.Option::getKey).toList();
   }

   public List<MultiSelectSetting.Option> int212() {
      return this.list57;
   }


   public static class Option {
      public boolean enabled;
      public final String string17;

      public String getName() {
         return ZenithClient.on23().Easing().translate(this.string17);
      }

      public String getKey() {
         return this.string17;
      }

      public Option(String var1, boolean var2) {
         this.enabled = var2;
         this.string17 = var1;
      }

      public Option(MultiSelectSetting var1, String var2, boolean var3) {
         this.enabled = var3;
         this.string17 = var2;
         var1.list57.add(this);
      }

      public static Option UiAnimation(String var0, boolean var1) {
         return new Option(var0, var1);
      }

      public static Option EventImpl(String var0) {
         return new Option(var0, true);
      }

      public void toggle() {
         this.enabled = !this.enabled;
      }

      public boolean isEnabled() {
         return this.enabled;
      }

      public void setEnabled(boolean var1) {
         this.enabled = var1;
      }
   }
}
