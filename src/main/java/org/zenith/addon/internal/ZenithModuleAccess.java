package org.zenith.addon.internal;

import java.util.List;
import java.util.Objects;
import org.zenith.addon.api.frontend.ModuleAccess;
import org.zenith.addon.api.frontend.SettingAccess;
import org.zenith.module.Module;

final class ZenithModuleAccess implements ModuleAccess {
   public final Module module;

   ZenithModuleAccess(Module var1) {
      this.module = Objects.requireNonNull(var1, "module");
   }

   public String id() {
      return this.module.getId();
   }

   public String addonId() {
      if (this.module instanceof AddonBackedModule addonbackedmodule) {
         return addonbackedmodule.getAddonId();
      } else {
         int i = this.module.getId().indexOf(58);
         return i > 0 ? this.module.getId().substring(0, i) : "zenith";
      }
   }

   public String name() {
      return this.module.getName();
   }

   public String description() {
      return this.module.getDescription();
   }

   public String category() {
      return this.module.getCategory().name();
   }

   public boolean enabled() {
      return this.module.isEnabled();
   }

   public void enabled(boolean var1) {
      this.module.setToggled(var1);
   }

   public int keyCode() {
      return this.module.getKeyCode();
   }

   public void keyCode(int var1) {
      this.module.setKeyCode(var1);
   }

   public List<? extends SettingAccess> settings() {
      return this.module.getSettings().stream().map(var1 -> new ZenithSettingAccess(this.module, var1)).toList();
   }
}
