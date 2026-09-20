package org.zenith.addon.internal;

import java.util.List;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.ModeSetting;
final class AddonBackedModule_ExternalChoiceSetting extends ModeSetting {
   public final AddonBackedModule this_0;
   public final String id;

   public AddonBackedModule_ExternalChoiceSetting(AddonBackedModule var1, String var2, String var3, String var4, String var5, List<String> var6) {
      super(var3, var4, var6.toArray(String[]::new));
      this.this_0 = var1;
      this.id = var2;
      super.set(var5);
   }

   @Override
   public void set(String var1) {
      super.set(var1);
      if (this.id != null) {
         this.this_0.registered.setting(this.id, var1);
      }
   }

   @Override
   public void setValue(ModeSetting.Option var1) {
      super.setValue(var1);
      if (this.id != null && var1 != null) {
         this.this_0.registered.setting(this.id, var1.getKey());
      }
   }
}
