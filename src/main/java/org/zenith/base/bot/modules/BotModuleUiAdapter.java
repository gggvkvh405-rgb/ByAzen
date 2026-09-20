package org.zenith.base.bot.modules;

import java.util.List;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.setting.Setting;

@ModuleInfo(name = "BotModuleUiAdapter", category = Category.PLAYER, description = "", long120 = true)
public final class BotModuleUiAdapter extends Module {
   public final String botName;
   public final BotModule module;

   public BotModuleUiAdapter(String var1, BotModule var2) {
      this.botName = var1;
      this.module = var2;
      this.setName(var2.getName());
   }

   @Override
   public boolean isEnabled() {
      return this.module.isEnabled();
   }

   @Override
   public boolean isEnabledRaw() {
      return this.module.isEnabled();
   }

   @Override
   public void toggle() {
      HeadlessBots.setModuleEnabled(this.botName, this.module.getName(), !this.module.isEnabled());
   }

   @Override
   public void setToggled(boolean var1) {
      if (var1 != this.module.isEnabled()) {
         this.toggle();
      }
   }

   @Override
   public List<Setting> getSettings() {
      return this.module.getUiSettings();
   }
}
