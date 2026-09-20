package org.zenith.module.render;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import com.darkmagician6.eventapi.EventTarget;
import org.zenith.event.VelocityChangeEvent;
import org.zenith.setting.ColorSetting;
import org.zenith.util.ArgbColor;

@ModuleInfo(name = "Anti Invisible", category = Category.RENDER, description = "Видно инвизок")
public final class AntiInvisible extends Module {
   public static final AntiInvisible antiInvisible = new AntiInvisible();
   public final ColorSetting colorSetting = new ColorSetting(
      "module.antiInvisible.colorSetting", "module.antiInvisible.colorSetting.desc", ArgbColor.var11934.SprintStateEvent(0.5F)
   );

   @EventTarget
   public void on23(VelocityChangeEvent var1) {
      var1.setColor(this.colorSetting.getColor().call001());
      var1.cancel();
   }

   public ColorSetting float317() {
      return this.colorSetting;
   }
}
