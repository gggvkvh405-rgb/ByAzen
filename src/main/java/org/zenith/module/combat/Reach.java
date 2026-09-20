package org.zenith.module.combat;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.NumberSetting;

@ModuleInfo(name = "Reach", category = Category.COMBAT, description = "")
public final class Reach extends Module {
   public static final Reach reach2 = new Reach();
   public final MultiSelectSetting mods2 = new MultiSelectSetting("module.reach.mods", "module.reach.mods.desc");
   public final MultiSelectSetting.Option modeSettingVar15914 = new MultiSelectSetting.Option(this.mods2, "module.reach.defoult", true);
   public final NumberSetting reach = new NumberSetting(
      "module.reach.reach", 3.0F, 3.0F, 6.0F, 0.05F, "module.reach.reach.desc", "b", this.modeSettingVar15914::isEnabled, null
   );
   public final NumberSetting reachBlock = new NumberSetting(
      "module.reach.reachBlock", 3.0F, 3.0F, 20.0F, 0.05F, "module.reach.reachBlock.desc", "b", this.modeSettingVar15914::isEnabled, null
   );
}
