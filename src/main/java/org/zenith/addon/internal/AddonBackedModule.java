package org.zenith.addon.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.zenith.addon.api.ActionSettingDefinition;
import org.zenith.addon.api.BooleanSettingDefinition;
import org.zenith.addon.api.ChoiceSettingDefinition;
import org.zenith.addon.api.ColorSettingDefinition;
import org.zenith.addon.api.NumberSettingDefinition;
import org.zenith.addon.api.SettingDefinition;
import org.zenith.addon.api.StringSettingDefinition;
import org.zenith.addon.api.setting.BooleanSetting;
import org.zenith.addon.api.setting.ChoiceSetting;
import org.zenith.addon.api.setting.ColorSetting;
import org.zenith.addon.api.setting.NumberSetting;
import org.zenith.addon.api.setting.StringSetting;
import org.zenith.addon.runtime.RegisteredModule;
import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.setting.Setting;

public final class AddonBackedModule extends Module {
   public final RegisteredModule registered;
   public final List<Setting> settings = new ArrayList<>();
   public final Map<Setting, String> settingIds = new IdentityHashMap<>();

   public AddonBackedModule(RegisteredModule var1) {
      super(var1.id(), var1.definition().name(), var1.definition().description(), Category.valueOf(var1.definition().category().name()));
      this.registered = var1;

      for (SettingDefinition settingdefinition : var1.settingDefinitions()) {
         Setting l1illl1lllllll1l1l1l1ili11l1 = this.adapt(settingdefinition);
         this.settings.add(l1illl1lllllll1l1l1l1ili11l1);
         this.settingIds.put(l1illl1lllllll1l1l1l1ili11l1, settingdefinition.id());
      }

      super.setKeyCode(var1.keyCode());
   }

   public String getAddonId() {
      return this.registered.addonId();
   }

   public String getSettingId(Setting var1) {
      return this.settingIds.getOrDefault(var1, var1.getKey());
   }

   @Override
   public List<Setting> getSettings() {
      return List.copyOf(this.settings);
   }

   @Override
   public JsonObject save() {
      JsonObject jsonobject = super.save();
      JsonObject jsonobject1 = new JsonObject();
      this.registered.behaviorState().forEach((var1x, var2x) -> jsonobject1.add(var1x, primitive(var2x)));
      jsonobject.add("AddonBehavior", jsonobject1);
      return jsonobject;
   }

   @Override
   public void load(JsonObject var1) {
      if (var1 != null && var1.has("AddonBehavior") && var1.get("AddonBehavior").isJsonObject()) {
         LinkedHashMap linkedhashmap = new LinkedHashMap();

         for (Entry<String, JsonElement> entry : var1.getAsJsonObject("AddonBehavior").entrySet()) {
            if (entry.getValue().isJsonPrimitive()) {
               linkedhashmap.put(entry.getKey(), value(entry.getValue()));
            }
         }

         this.registered.loadBehaviorState(linkedhashmap);
      }

      super.load(var1);
   }

   @Override
   public void setKeyCode(int var1) {
      super.setKeyCode(var1);
      if (this.registered != null) {
         this.registered.keyCode(var1);
      }
   }

   @Override
   public void onEnable() {
      this.registered.enabled(true);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.registered.enabled(false);
      super.onDisable();
   }

   public Setting adapt(SettingDefinition var1) {
      if (var1 instanceof BooleanSettingDefinition booleansettingdefinition) {
         return new AddonBackedModule_ExternalBooleanSetting(
            this,
            booleansettingdefinition.id(),
            booleansettingdefinition.name(),
            booleansettingdefinition.description(),
            booleansettingdefinition.defaultEnabled()
         );
      } else if (var1 instanceof BooleanSetting booleansetting) {
         return new AddonBackedModule_ExternalBooleanSetting(
            this, booleansetting.id(), booleansetting.name(), booleansetting.description(), booleansetting.enabled()
         );
      } else if (var1 instanceof NumberSettingDefinition numbersettingdefinition) {
         return new AddonBackedModule_ExternalNumberSetting(
            this,
            numbersettingdefinition.id(),
            numbersettingdefinition.name(),
            numbersettingdefinition.description(),
            numbersettingdefinition.defaultNumber(),
            numbersettingdefinition.min(),
            numbersettingdefinition.max(),
            numbersettingdefinition.step(),
            numbersettingdefinition.suffix()
         );
      } else if (var1 instanceof NumberSetting numbersetting) {
         return new AddonBackedModule_ExternalNumberSetting(
            this,
            numbersetting.id(),
            numbersetting.name(),
            numbersetting.description(),
            (Double)numbersetting.get(),
            numbersetting.min(),
            numbersetting.max(),
            numbersetting.step(),
            numbersetting.suffix()
         );
      } else if (var1 instanceof ChoiceSettingDefinition choicesettingdefinition) {
         return new AddonBackedModule_ExternalChoiceSetting(
            this,
            choicesettingdefinition.id(),
            choicesettingdefinition.name(),
            choicesettingdefinition.description(),
            choicesettingdefinition.defaultChoice(),
            choicesettingdefinition.choices()
         );
      } else if (var1 instanceof ChoiceSetting choicesetting) {
         return new AddonBackedModule_ExternalChoiceSetting(
            this, choicesetting.id(), choicesetting.name(), choicesetting.description(), (String)choicesetting.get(), choicesetting.choices()
         );
      } else if (var1 instanceof StringSettingDefinition stringsettingdefinition) {
         return new AddonBackedModule_ExternalStringSetting(
            this,
            stringsettingdefinition.id(),
            stringsettingdefinition.name(),
            stringsettingdefinition.description(),
            stringsettingdefinition.defaultText(),
            stringsettingdefinition.placeholder(),
            stringsettingdefinition.maxLength(),
            stringsettingdefinition.secret()
         );
      } else if (var1 instanceof StringSetting stringsetting) {
         return new AddonBackedModule_ExternalStringSetting(
            this,
            stringsetting.id(),
            stringsetting.name(),
            stringsetting.description(),
            (String)stringsetting.get(),
            stringsetting.placeholder(),
            stringsetting.maxLength(),
            stringsetting.secret()
         );
      } else if (var1 instanceof ColorSettingDefinition colorsettingdefinition) {
         return new AddonBackedModule_ExternalColorSetting(
            this, colorsettingdefinition.id(), colorsettingdefinition.name(), colorsettingdefinition.description(), colorsettingdefinition.defaultArgb()
         );
      } else if (var1 instanceof ColorSetting colorsetting) {
         return new AddonBackedModule_ExternalColorSetting(this, colorsetting.id(), colorsetting.name(), colorsetting.description(), colorsetting.argb());
      } else if (var1 instanceof ActionSettingDefinition actionsettingdefinition) {
         return new org.zenith.setting.ButtonSetting(
            actionsettingdefinition.name(),
            actionsettingdefinition.icon(),
            actionsettingdefinition.description(),
            () -> this.registered.setting(actionsettingdefinition.id(), Boolean.TRUE)
         );
      } else {
         throw new IllegalArgumentException("Unsupported addon setting: " + var1.getClass().getName());
      }
   }

   public static JsonPrimitive primitive(Object var0) {
      if (var0 instanceof Boolean obool) {
         return new JsonPrimitive(obool);
      } else {
         return var0 instanceof Number number ? new JsonPrimitive(number) : new JsonPrimitive(String.valueOf(var0));
      }
   }

   public static Object value(JsonElement var0) {
      JsonPrimitive jsonprimitive = var0.getAsJsonPrimitive();
      if (jsonprimitive.isBoolean()) {
         return jsonprimitive.getAsBoolean();
      }

      if (jsonprimitive.isString()) {
         return jsonprimitive.getAsString();
      }

      double d0 = jsonprimitive.getAsDouble();
      return Math.rint(d0) == d0 ? jsonprimitive.getAsLong() : d0;
   }
}
