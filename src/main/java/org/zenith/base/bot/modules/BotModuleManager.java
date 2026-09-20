package org.zenith.base.bot.modules;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.modules.impl.BotAutoCapcha;
import org.zenith.base.bot.modules.impl.BotAutoMine;
import org.zenith.base.bot.modules.impl.BotWarpFarm;
import org.zenith.module.ModuleInfo;

public final class BotModuleManager {
   public static final Logger LOGGER = LogUtils.getLogger();
   public static final List<String> SUPPORTED_MODULE_NAMES = List.of(
      BotAutoMine.class.getAnnotation(ModuleInfo.class).name(),
      BotWarpFarm.class.getAnnotation(ModuleInfo.class).name(),
      BotAutoCapcha.class.getAnnotation(ModuleInfo.class).name()
   );
   public final BotClient client;
   public final Map<String, BotModule> modules = new LinkedHashMap<>();

   public static List<String> supportedModuleNames() {
      return SUPPORTED_MODULE_NAMES;
   }

   public BotModuleManager(BotClient var1) {
      this.client = var1;
      this.register(new BotAutoMine());
      this.register(new BotWarpFarm());
      BotAutoCapcha botautocapcha = new BotAutoCapcha();
      this.register(botautocapcha);
      botautocapcha.setToggled(true);
   }

   public void register(BotModule var1) {
      var1.bind(this.client);
      this.modules.put(var1.getNameLower(), var1);
   }

   public Collection<BotModule> getModules() {
      return this.modules.values();
   }

   public BotModule getModule(String var1) {
      return this.modules.get(var1.toLowerCase(Locale.ROOT));
   }

   public <T extends BotModule> T getModule(Class<T> var1) {
      for (BotModule botmodule : this.modules.values()) {
         if (var1.isInstance(botmodule)) {
            return (T)botmodule;
         }
      }

      return null;
   }

   public boolean setEnabled(String var1, boolean var2) {
      BotModule botmodule = this.getModule(var1);
      if (botmodule == null) {
         return false;
      }

      botmodule.setToggled(var2);
      return true;
   }

   public void applySettings(String var1, JsonObject var2) {
      BotModule botmodule = this.getModule(var1);
      if (botmodule != null) {
         botmodule.load(var2);
      }
   }

   public void disableAll() {
      for (BotModule botmodule : this.modules.values()) {
         try {
            botmodule.setToggled(false);
         } catch (RuntimeException runtimeexception) {
            LOGGER.error("Bot {}: failed to disable module {}", new Object[]{this.client.getName(), botmodule.getName(), runtimeexception});
         }
      }
   }
}
