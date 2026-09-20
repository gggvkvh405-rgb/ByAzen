package org.zenith.addon.internal;

import org.zenith.setting.ColorSetting;
import org.zenith.util.ArgbColor;

final class AddonBackedModule_ExternalColorSetting extends ColorSetting {
   public final AddonBackedModule this_0;
   public final String id;

   public AddonBackedModule_ExternalColorSetting(AddonBackedModule var1, String var2, String var3, String var4, int var5) {
      super(var3, var4, new ArgbColor(var5));
      this.this_0 = var1;
      this.id = var2;
   }

   @Override
   public void setColor(int var1) {
      super.setColor(var1);
      if (this.id != null) {
         this.this_0.registered.setting(this.id, var1);
      }
   }

   @Override
   public void setColor(ArgbColor var1) {
      super.setColor(var1);
      if (this.id != null && var1 != null) {
         this.this_0.registered.setting(this.id, var1.call001());
      }
   }
}
