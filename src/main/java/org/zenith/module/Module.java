package org.zenith.module;

import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.zenith.ZenithClient;
import org.zenith.core.GameService;
import org.zenith.core.HolyWorldClient;
import org.zenith.core.SessionFlag;
import org.zenith.event.ModuleToggleEvent;
import org.zenith.rotation.RotationManager;
import org.zenith.setting.SettingGroup;
import org.zenith.setting.Setting;

public class Module implements Comparable<Module>, GameService {
   protected ModuleInfo info = this.getClass().getAnnotation(ModuleInfo.class);
   public final String id;
   public String name;
   public final String nameLower;
   public final Category category;
   public final String description;
   public final boolean botModule;
   public volatile boolean enabled;
   public boolean priority = false;
   public int keyCode;

   protected Module() {
      if (this.info == null) {
         throw new IllegalStateException("ModuleAnnotation is required for " + this.getClass().getName());
      }

      this.id = "zenith:" + this.getClass().getSimpleName().toLowerCase(Locale.ROOT);
      this.name = this.info.name();
      this.nameLower = this.name.toLowerCase(Locale.ROOT);
      this.category = this.info.category();
      this.description = this.info.description();
      this.botModule = this.info.long120();
      this.enabled = false;
      this.keyCode = -1;
   }

   protected Module(String var1, String var2, String var3, Category var4) {
      this.info = null;
      this.id = var1;
      this.name = var2;
      this.nameLower = var2.toLowerCase(Locale.ROOT);
      this.category = var4;
      this.description = var3 == null ? "" : var3;
      this.botModule = false;
      this.enabled = false;
      this.keyCode = -1;
   }

   public boolean isEnabledRaw() {
      return this.enabled;
   }

   public boolean isBotModule() {
      return this.botModule;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setToggled(boolean var1) {
      if (var1 && !this.isEnabledRaw()) {
         this.toggle();
      }

      if (!var1 && this.isEnabledRaw()) {
         this.toggle();
      }
   }

   protected final void disableSelf() {
      this.setToggled(false);
   }

   public void toggle() {
      this.enabled = !this.enabled;
      if (this.enabled) {
         this.onEnable();
      } else {
         this.onDisable();
      }
   }

   protected final RotationManager scopedRotationManager() {
      return ZenithClient.on23().CloudRouter();
   }

   protected final HolyWorldClient scopedRCTRepository() {
      return ZenithClient.on23().UiAnimation();
   }

   public void onEnable() {
      if (this.isPremium() && ZenithClient.on23().CommandManager().EmoteManager() == SessionFlag.BotRespawnEvent) {
         this.setToggled(false);
         ZenithClient.on23()
            .ConfigJsonUtil()
            .on23(
               this.category.getIcon(),
               Text.of(
                  Text.of("Работает только")
                     .copy()
                     .setStyle(Style.EMPTY.withColor(val003.TextScanner().getCurrentStyle().getPrimaryColor().getColor().call001()))
                     .append(
                        Text.of("с Альфой")
                           .copy()
                           .setStyle(Style.EMPTY.withColor(val003.TextScanner().getCurrentStyle().getTextEnable().getColor().call001()))
                     )
               )
            );
      } else {
         EventManager.register(this);
         EventManager.call(new ModuleToggleEvent(this, this.enabled));
      }
   }

   public void onDisable() {
      EventManager.unregister(this);
      EventManager.call(new ModuleToggleEvent(this, this.enabled));
   }

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(var1 -> {
         try {
            var1.setAccessible(true);
            return var1.get(this);
         } catch (IllegalAccessException illegalaccessexception) {
            illegalaccessexception.printStackTrace();
            return null;
         }
      }).filter(var0 -> var0 instanceof Setting).map(var0 -> (Setting)var0).collect(Collectors.toList());
   }

   public boolean isRenderSetting() {
      return true;
   }

   public JsonObject save() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("enabled", this.enabled);
      jsonobject.addProperty("priority", this.priority);
      jsonobject.addProperty("keyCode", this.keyCode);
      JsonObject jsonobject1 = new JsonObject();

      for (Setting l1illl1lllllll1l1l1l1ili11l1 : this.getSettings()) {
         l1illl1lllllll1l1l1l1ili11l1.safe(jsonobject1);
      }

      jsonobject.add("Settings", jsonobject1);
      return jsonobject;
   }

   public void load(JsonObject var1) {
      try {
         if (var1 != null) {
            Boolean obool = var1.has("enabled") ? var1.get("enabled").getAsBoolean() : null;
            if (var1.has("priority")) {
               this.priority = var1.get("priority").getAsBoolean();
            }

            if (var1.has("keyCode")) {
               this.setKeyCode(var1.get("keyCode").getAsInt());
            }

            for (Setting l1illl1lllllll1l1l1l1ili11l1 : this.getSettings()) {
               String s = l1illl1lllllll1l1l1l1ili11l1.getKey();
               JsonObject jsonobject = var1.getAsJsonObject("Settings");
               if (jsonobject != null && (jsonobject.has(s) || l1illl1lllllll1l1l1l1ili11l1 instanceof SettingGroup)) {
                  l1illl1lllllll1l1l1l1ili11l1.load(jsonobject);
               }
            }

            if (obool != null) {
               if (obool && !this.isEnabledRaw()) {
                  this.toggle();
               }

               if (!obool && this.isEnabledRaw()) {
                  this.toggle();
               }
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public int compareTo(Module var1) {
      return var1.getName().compareTo(this.name);
   }

   public boolean isPremium() {
      return false;
   }

   public ModuleInfo getInfo() {
      return this.info;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getNameLower() {
      return this.nameLower;
   }

   public Category getCategory() {
      return this.category;
   }

   public String getDescription() {
      return this.description;
   }

   public boolean isPriority() {
      return this.priority;
   }

   public int getKeyCode() {
      return this.keyCode;
   }

   public void setInfo(ModuleInfo var1) {
      this.info = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setEnabled(boolean var1) {
      this.enabled = var1;
   }

   public void setPriority(boolean var1) {
      this.priority = var1;
   }

   public void setKeyCode(int var1) {
      this.keyCode = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      }

      if (var1 instanceof Module lii1lll1l1li1ii1iiillii) {
         if (!lii1lll1l1li1ii1iiillii.canEqual(this)) {
            return false;
         }

         if (this.isBotModule() != lii1lll1l1li1ii1iiillii.isBotModule()) {
            return false;
         }

         if (this.isEnabled() != lii1lll1l1li1ii1iiillii.isEnabled()) {
            return false;
         }

         if (this.isPriority() != lii1lll1l1li1ii1iiillii.isPriority()) {
            return false;
         }

         if (this.getKeyCode() != lii1lll1l1li1ii1iiillii.getKeyCode()) {
            return false;
         }

         ModuleInfo i111ii1l11 = this.getInfo();
         ModuleInfo i111ii1l111 = lii1lll1l1li1ii1iiillii.getInfo();
         if (i111ii1l11 == null ? i111ii1l111 == null : i111ii1l11.equals(i111ii1l111)) {
            String s = this.getId();
            String s1 = lii1lll1l1li1ii1iiillii.getId();
            if (s == null ? s1 == null : s.equals(s1)) {
               String s2 = this.getName();
               String s3 = lii1lll1l1li1ii1iiillii.getName();
               if (s2 == null ? s3 == null : s2.equals(s3)) {
                  String s4 = this.getNameLower();
                  String s5 = lii1lll1l1li1ii1iiillii.getNameLower();
                  if (s4 == null ? s5 == null : s4.equals(s5)) {
                     Category i1i1lillillll11 = this.getCategory();
                     Category i1i1lillillll111 = lii1lll1l1li1ii1iiillii.getCategory();
                     if (i1i1lillillll11 == null ? i1i1lillillll111 == null : i1i1lillillll11.equals(i1i1lillillll111)) {
                        String s6 = this.getDescription();
                        String s7 = lii1lll1l1li1ii1iiillii.getDescription();
                        return s6 == null ? s7 == null : s6.equals(s7);
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Module;
   }

   @Override
   public int hashCode() {
      byte b0 = 59;
      int i = 1;
      i = i * 59 + (this.isBotModule() ? 79 : 97);
      i = i * 59 + (this.isEnabled() ? 79 : 97);
      i = i * 59 + (this.isPriority() ? 79 : 97);
      i = i * 59 + this.getKeyCode();
      ModuleInfo i111ii1l11 = this.getInfo();
      i = i * 59 + (i111ii1l11 == null ? 43 : i111ii1l11.hashCode());
      String s = this.getId();
      i = i * 59 + (s == null ? 43 : s.hashCode());
      String s1 = this.getName();
      i = i * 59 + (s1 == null ? 43 : s1.hashCode());
      String s2 = this.getNameLower();
      i = i * 59 + (s2 == null ? 43 : s2.hashCode());
      Category i1i1lillillll11 = this.getCategory();
      i = i * 59 + (i1i1lillillll11 == null ? 43 : i1i1lillillll11.hashCode());
      String s3 = this.getDescription();
      return i * 59 + (s3 == null ? 43 : s3.hashCode());
   }

   @Override
   public String toString() {
      return "Module(info="
         + this.getInfo()
         + ", id="
         + this.getId()
         + ", name="
         + this.getName()
         + ", nameLower="
         + this.getNameLower()
         + ", category="
         + this.getCategory()
         + ", description="
         + this.getDescription()
         + ", botModule="
         + this.isBotModule()
         + ", enabled="
         + this.isEnabled()
         + ", priority="
         + this.isPriority()
         + ", keyCode="
         + this.getKeyCode()
         + ")";
   }
}
