package org.zenith.addon.internal;

import org.zenith.setting.NumberSetting;

final class AddonBackedModule_ExternalNumberSetting extends NumberSetting {
   public final AddonBackedModule this_0;
   public final String id;

   public AddonBackedModule_ExternalNumberSetting(
      AddonBackedModule var1, String var2, String var3, String var4, double var5, double var7, double var9, double var11, String var13
   ) {
      super(var3, (float)var5, (float)var7, (float)var9, (float)var11, var4, var13);
      this.this_0 = var1;
      this.id = var2;
   }

   @Override
   public void setCurrent(float var1) {
      super.setCurrent(var1);
      if (this.id != null) {
         this.this_0.registered.setting(this.id, (double)var1);
      }
   }
}
