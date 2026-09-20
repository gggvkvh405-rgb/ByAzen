package org.zenith.base.bot.modules.api;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotEventBus;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotInteractionManager;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.module.Category;
import org.zenith.module.ModuleInfo;
import org.zenith.setting.Setting;

public abstract class BotModule {
   protected final ModuleInfo info = this.getClass().getAnnotation(ModuleInfo.class);
   public final String name = this.info.name();
   public final String nameLower = this.name.toLowerCase(Locale.ROOT);
   public final Category category = this.info.category();
   public final String description = this.info.description();
   public volatile boolean enabled;
   public BotClient bot;
   public BotEventBus eventBus;

   protected BotModule() {
   }

   public final void bind(BotClient var1) {
      this.bot = var1;
      this.eventBus = var1.getEventBus();
   }

   public void toggle() {
      this.enabled = !this.enabled;
      if (this.enabled) {
         this.onEnable();
      } else {
         this.onDisable();
      }
   }

   public void setToggled(boolean var1) {
      if (var1 && !this.enabled) {
         this.toggle();
      }

      if (!var1 && this.enabled) {
         this.toggle();
      }
   }

   protected final void disableSelf() {
      this.setToggled(false);
   }

   public void onEnable() {
      this.eventBus.register(this);
   }

   public void onDisable() {
      this.eventBus.unregister(this);
   }

   public final BotClient bot() {
      return this.bot;
   }

   protected final BotWorld world() {
      return this.bot.getWorld();
   }

   protected final BotPlayer player() {
      return this.bot.getPlayer();
   }

   protected final BotPlayHandler handler() {
      return this.bot.getPlayHandler();
   }

   protected final BotInteractionManager interaction() {
      BotPlayHandler botplayhandler = this.bot.getPlayHandler();
      return botplayhandler != null && botplayhandler.getWorld() != null ? botplayhandler.getInteractionManager() : null;
   }

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(var1 -> {
         try {
            var1.setAccessible(true);
            return var1.get(this);
         } catch (IllegalAccessException illegalaccessexception) {
            illegalaccessexception.printStackTrace();
            return null;
         }
      }).filter(var0 -> var0 instanceof Setting).map(var0 -> (Setting)var0).collect(Collectors.toList());
   }

   public List<Setting> getUiSettings() {
      return this.getSettings();
   }

   public JsonObject save() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("enabled", this.enabled);
      JsonObject jsonobject1 = new JsonObject();

      for (Setting l1illl1lllllll1l1l1l1ili11l1 : this.getSettings()) {
         l1illl1lllllll1l1l1l1ili11l1.safe(jsonobject1);
      }

      jsonobject.add("Settings", jsonobject1);
      return jsonobject;
   }

   public void load(JsonObject var1) {
      try {
         if (var1 == null) {
            return;
         }

         JsonObject jsonobject = var1.getAsJsonObject("Settings");
         if (jsonobject != null) {
            for (Setting l1illl1lllllll1l1l1l1ili11l1 : this.getSettings()) {
               if (jsonobject.has(l1illl1lllllll1l1l1l1ili11l1.getKey())) {
                  l1illl1lllllll1l1l1l1ili11l1.load(jsonobject);
               }
            }
         }

         if (var1.has("enabled")) {
            this.setToggled(var1.get("enabled").getAsBoolean());
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public ModuleInfo getInfo() {
      return this.info;
   }

   public String getName() {
      return this.name;
   }

   public String getNameLower() {
      return this.nameLower;
   }

   public Category getCategory() {
      return this.category;
   }

   public String getDescription() {
      return this.description;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public BotClient getBot() {
      return this.bot;
   }

   public BotEventBus getEventBus() {
      return this.eventBus;
   }
}
