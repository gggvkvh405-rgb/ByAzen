package org.zenith.managers;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.text.Text;
import org.zenith.ZenithClient;
import org.zenith.core.EffectEngine;
import org.zenith.core.HudStatusPanel;
import org.zenith.event.ModuleToggleEvent;
import org.zenith.module.render.Interface;
import org.zenith.module.render.Menu;
import org.zenith.module.Module;

public class CloudApi {
   public static CloudApi zClass010 = new CloudApi();
   public HudStatusPanel var72;

   public CloudApi() {
      EventManager.register(this);
   }

   public static CloudApi TextUtils() {
      if (zClass010 == null) {
         zClass010 = new CloudApi();
      }

      return zClass010;
   }

   public void on23(HudStatusPanel var1) {
      this.var72 = var1;
   }

   @EventTarget
   public void on23(ModuleToggleEvent var1) {
      if (this.var72 != null
         && this.var72.boolean148()
         && var1.getModule() != Menu.menu
         && !EffectEngine.double69()
         && Interface.interfaceField.isEnabled()
         && Interface.interfaceField.boolean67()) {
         this.var72.on23(var1.getModule(), var1.isEnabled());

         try {
            ZenithClient.on23()
               .NbtItemSpec()
               .on23(var1.isEnabled() ? ZenithClient.on23().NbtItemSpec().soundEvent3 : ZenithClient.on23().NbtItemSpec().soundEvent4);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   public void on23(Module var1, boolean var2) {
      if (this.var72 != null
         && this.var72.boolean148()
         && !EffectEngine.double69()
         && Interface.interfaceField.isEnabled()
         && Interface.interfaceField.boolean67()) {
         ZenithClient.on23().NbtItemSpec().on23(var2 ? ZenithClient.on23().NbtItemSpec().soundEvent3 : ZenithClient.on23().NbtItemSpec().soundEvent4);
         this.var72.on23(var1, var2);
      }
   }

   public void on23(String var1, Text var2) {
      if (this.var72 != null && !EffectEngine.double69() && Interface.interfaceField.isEnabled() && Interface.interfaceField.boolean67()) {
         this.var72.UiAnimation(var1, var2);
      }
   }

   public void on23(String var1, Text var2, long var3) {
      if (this.var72 != null && !EffectEngine.double69() && Interface.interfaceField.isEnabled() && Interface.interfaceField.boolean67()) {
         this.var72.UiAnimation(var1, var2, var3);
      }
   }
}
