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

import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;

@ModuleInfo(name = "Cape", category = Category.RENDER, description = "module.cape.desc")
public final class Cape extends Module {
   public static final Cape cape = new Cape();
   public ModeSetting animationMode = new ModeSetting(
      "module.swingAnimation.animationMode",
      "module.cape.animationMode.desc",
      "module.swingAnimation.first",
      "module.swingAnimation.second",
      "module.swingAnimation.third"
   );
   public final NumberSetting windStrength = new NumberSetting("module.cape.windStrength", 1.0F, 0.0F, 3.0F, 0.1F, "module.cape.windStrength.desc", "x");
   public final NumberSetting gravity = new NumberSetting("module.cape.gravity", 1.0F, 0.0F, 2.0F, 0.1F, "module.cape.gravity.desc", "x");
   public final NumberSetting stiffness = new NumberSetting("module.cape.stiffness", 0.5F, 0.1F, 2.0F, 0.1F, "module.cape.stiffness.desc", "x");
   public final NumberSetting sensitivity = new NumberSetting("module.cape.sensitivity", 1.5F, 0.5F, 3.0F, 0.1F, "module.cape.sensitivity.desc", "x");

   @Override
   public boolean isPremium() {
      return true;
   }

   public float boolean105() {
      return this.windStrength.getCurrent();
   }

   public float boolean106() {
      return this.gravity.getCurrent();
   }

   public float boolean107() {
      return this.stiffness.getCurrent();
   }

   public float int398() {
      return this.sensitivity.getCurrent();
   }
}
