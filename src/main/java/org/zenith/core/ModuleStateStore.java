package org.zenith.core;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import org.zenith.ZenithClient;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.module.Module;

public class ModuleStateStore {
   public final String string98;
   public final File file4;
   public boolean priority;

   public ModuleStateStore(String var1) {
      this.string98 = var1;
      this.file4 = new File(CloudPoller.file7, var1 + "." + "Zenith".toLowerCase());
      if (!this.file4.exists()) {
         try {
            this.file4.createNewFile();
         } catch (IOException ioexception) {
            ioexception.printStackTrace();
         }
      }
   }

   public void TriggerBot() {
      this.priority = !this.priority;
   }

   public JsonObject save() {
      try {
         JsonObject jsonobject = new JsonObject();
         JsonObject jsonobject1 = new JsonObject();
         jsonobject1.addProperty("name", this.string98);
         jsonobject1.addProperty("priority", this.priority);
         jsonobject.add("ConfigData", jsonobject1);
         jsonobject.add("Styles", ZenithClient.on23().TextScanner().save());
         JsonObject jsonobject2 = new JsonObject();
         jsonobject2.addProperty("name", UserdataManager.ProfileItemBuilder(ZenithClient.on23().EnchantItemSpec().HitParticles()));
         jsonobject2.addProperty("weapon", UserdataManager.ProfileItemBuilder(ZenithClient.on23().EnchantItemSpec().JumpCircle()));
         jsonobject.add("FiguraData", jsonobject2);
         jsonobject.add("PetData", ZenithClient.on23().ItemServiceBase().save());
         JsonObject jsonobject3 = new JsonObject();

         for (Module lii1lll1l1li1ii1iiillii : ZenithClient.on23().ColorAnimator().PacketDispatcher()) {
            jsonobject3.add(lii1lll1l1li1ii1iiillii.getId(), lii1lll1l1li1ii1iiillii.save());
         }

         jsonobject.add("Modules", jsonobject3);
         JsonObject jsonobject4 = new JsonObject();
         jsonobject4.addProperty("language", ZenithClient.on23().Easing().getLanguageName());
         jsonobject.add("Language", jsonobject4);
         jsonobject.add("BotData", HeadlessBots.saveState());
         JsonObject jsonobject5 = new JsonObject();
         jsonobject.add("Interface", jsonobject5);
         return jsonobject;
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public void load(JsonObject var1) {
      if (var1.has("ConfigData")) {
         JsonObject jsonobject = var1.getAsJsonObject("ConfigData");
         if (jsonobject.has("priority")) {
            this.priority = jsonobject.get("priority").getAsBoolean();
         }
      }

      loadState(var1);
   }

   public static void loadState(JsonObject var0) {
      InventoryUtils(var0);
      if (var0.has("Styles")) {
         ZenithClient.on23().TextScanner().load(var0.getAsJsonObject("Styles"));
      }

      if (var0.has("FiguraData")) {
         JsonObject jsonobject = var0.getAsJsonObject("FiguraData");
         if (jsonobject.has("name")) {
            ZenithClient.on23().EnchantItemSpec().on23(UserdataManager.EventUpdateHealth(jsonobject.get("name").getAsString()));
         }

         if (jsonobject.has("weapon")) {
            ZenithClient.on23().EnchantItemSpec().UiAnimation(UserdataManager.EventUpdateHealth(jsonobject.get("weapon").getAsString()));
         }
      }

      if (var0.has("PetData")) {
         JsonObject jsonobject2 = var0.getAsJsonObject("PetData");
         ZenithClient.on23().ItemServiceBase().load(jsonobject2);
      }

      if (var0.has("Language")) {
         JsonObject jsonobject3 = var0.getAsJsonObject("Language");
         if (jsonobject3.has("language")) {
            ZenithClient.on23().Easing().CloseScreenEvent(jsonobject3.get("language").getAsString());
         }
      }

      if (var0.has("Modules")) {
         JsonObject jsonobject4 = var0.getAsJsonObject("Modules");

         for (Module lii1lll1l1li1ii1iiillii : ZenithClient.on23().ColorAnimator().PacketDispatcher()) {
            try {
               JsonObject jsonobject1 = jsonobject4.getAsJsonObject(lii1lll1l1li1ii1iiillii.getId());
               if (jsonobject1 == null && lii1lll1l1li1ii1iiillii.getId().startsWith("zenith:")) {
                  jsonobject1 = jsonobject4.getAsJsonObject(lii1lll1l1li1ii1iiillii.getName());
               }

               lii1lll1l1li1ii1iiillii.load(jsonobject1);
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }
      }
   }

   public static void InventoryUtils(JsonObject var0) {
      try {
         if (var0.has("BotData") && var0.get("BotData").isJsonObject()) {
            HeadlessBots.loadState(var0.getAsJsonObject("BotData"));
         }
      } catch (Exception var2) {
      }
   }

   public String getName() {
      return this.string98;
   }

   public File getFile() {
      return this.file4;
   }

   public boolean isPriority() {
      return this.priority;
   }
}
