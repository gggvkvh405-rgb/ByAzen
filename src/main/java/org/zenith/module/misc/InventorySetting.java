package org.zenith.module.misc;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import org.zenith.setting.BooleanSetting;

@ModuleInfo(name = "InventorySetting", description = "", category = Category.MISC)
public final class InventorySetting extends Module {
   public final BooleanSetting stoping = new BooleanSetting("module.inventorySetting.stoping", "module.inventorySetting.stoping.desc", true);
   public final BooleanSetting delayMoveItem = new BooleanSetting("module.inventorySetting.delayMoveItem", "module.inventorySetting.delayMoveItem.desc", true);
   public final BooleanSetting updateSlot = new BooleanSetting("module.inventorySetting.updateSlot", "module.inventorySetting.updateSlot.desc", false);
   public static final InventorySetting inventorySetting = new InventorySetting();

   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   public boolean call099() {
      return !this.delayMoveItem.isEnabled();
   }

   public boolean string104() {
      return this.stoping.isEnabled();
   }

   public boolean path12() {
      return this.updateSlot.isEnabled();
   }
}
