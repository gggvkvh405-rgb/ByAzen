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

import java.util.List;
import org.zenith.setting.MultiSelectSetting;

@ModuleInfo(name = "NoRender", category = Category.RENDER, description = "Убирает лишние элементы с экрана")
public final class NoRender extends Module {
   public static final NoRender noRender = new NoRender();
   public final MultiSelectSetting modeSetting13 = MultiSelectSetting.on23(
      "module.noRender.settings",
      "module.noRender.settings.desc",
      List.of("module.noRender.fire", "module.noRender.badEffects", "module.noRender.blockOverlay", "module.noRender.scoreBoard")
   );

   public boolean float377() {
      return this.isEnabled() && this.modeSetting13.ConfigJsonUtil(2);
   }

   public boolean float378() {
      return this.isEnabled() && this.modeSetting13.ConfigJsonUtil(3);
   }

   public boolean float379() {
      return this.isEnabled() && this.modeSetting13.ConfigJsonUtil(0);
   }

   public boolean float380() {
      return this.isEnabled() && this.modeSetting13.ConfigJsonUtil(1);
   }
}
