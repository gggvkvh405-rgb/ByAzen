package org.zenith.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;

public class ServerConfigStore {
   public static final String string88 = "HolyWorld";
   public final String isFalse;
   public final File file3;
   public String server;
   public List<ItemStackStore> list56 = new ArrayList<>();

   public ServerConfigStore(String var1) {
      this(var1, "HolyWorld", new File(DiskStorage.file5, var1 + "." + "Zenith".toLowerCase()));
   }

   public ServerConfigStore(String var1, String var2) {
      this(var1, var2, new File(DiskStorage.file5, EnchantItemSpec(var1, var2) + "." + "Zenith".toLowerCase()));
   }

   public ServerConfigStore(String var1, String var2, File var3) {
      this.isFalse = var1;
      this.server = EventInjectPlaced(var2);
      this.file3 = var3;
      if (!var3.exists()) {
         try {
            var3.createNewFile();
         } catch (IOException ioexception) {
            ioexception.printStackTrace();
         }
      }
   }

   public JsonObject save() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", this.isFalse);
      jsonobject.addProperty("server", this.server);
      JsonArray jsonarray = new JsonArray();

      for (ItemStackStore ll1l11l11l1lli1 : this.list56) {
         jsonarray.add(ll1l11l11l1lli1.save());
      }

      jsonobject.add("items", jsonarray);
      return jsonobject;
   }

   public void load(JsonObject var1) {
      this.list56.clear();
      if (var1.has("server")) {
         this.server = EventInjectPlaced(var1.get("server").getAsString());
      }

      if (var1.has("items")) {
         JsonArray jsonarray = var1.getAsJsonArray("items");

         for (int i = 0; i < jsonarray.size(); i++) {
            JsonObject jsonobject = jsonarray.get(i).getAsJsonObject();
            ItemStackStore ll1l11l11l1lli1 = new ItemStackStore();
            ll1l11l11l1lli1.load(jsonobject);
            this.list56.add(ll1l11l11l1lli1);
         }
      }
   }

   public List<ItemStack> WorldParticles() {
      List<ItemStack> arraylist = new ArrayList<>();

      for (ItemStackStore ll1l11l11l1lli1 : this.list56) {
         arraylist.add(ll1l11l11l1lli1.BlockOverLay());
      }

      return arraylist;
   }

   public boolean DataChangedEvent(String var1) {
      return this.server.equals(EventInjectPlaced(var1));
   }

   public static String EventInjectPlaced(String var0) {
      return var0 != null && !var0.isBlank() ? var0 : "HolyWorld";
   }

   public static String EnchantItemSpec(String var0, String var1) {
      String s = EventInjectPlaced(var1).replaceAll("[^\\p{L}\\p{N}._-]", "_");
      return s + "_" + var0;
   }

   public String getName() {
      return this.isFalse;
   }

   public File getFile() {
      return this.file3;
   }

   public String getServer() {
      return this.server;
   }

   public List<ItemStackStore> WorldTweaks() {
      return this.list56;
   }

   public void ChatMessageEvent(String var1) {
      this.server = var1;
   }

   public void Easing(List<ItemStackStore> var1) {
      this.list56 = var1;
   }
}
