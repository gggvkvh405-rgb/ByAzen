package org.zenith.addon.internal;

import org.zenith.setting.TextSetting;
import org.zenith.setting.TextSetting;
final class AddonBackedModule_ExternalStringSetting extends TextSetting {
   public final AddonBackedModule this_0;
   public final String id;

   public AddonBackedModule_ExternalStringSetting(
      AddonBackedModule var1, String var2, String var3, String var4, String var5, String var6, int var7, boolean var8
   ) {
      super(var3, var4, var5, var6, TextSetting.Validator.TradeGuardService(var7));
      this.this_0 = var1;
      this.id = var2;
      if (var8) {
         this.secret();
      }
   }

   @Override
   public boolean setValueSafe(String var1) {
      boolean flag = super.setValueSafe(var1);
      if (flag && this.id != null) {
         this.this_0.registered.setting(this.id, var1);
      }

      return flag;
   }
}
