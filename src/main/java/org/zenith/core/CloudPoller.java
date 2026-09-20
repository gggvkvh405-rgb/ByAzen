package org.zenith.core;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import org.zenith.ZenithClient;
import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.util.CryptoUtils;

public class CloudPoller implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final File file7 = new File(ZenithClient.ColorAnimator, "configs");
   public static final ScheduledExecutorService scheduledExecutorService2 = Executors.newSingleThreadScheduledExecutor(var0 -> {
      Thread thread = new Thread(var0, "Config-AutoSave");
      thread.setDaemon(true);
      thread.setPriority(1);
      return thread;
   });
   public final String string130 = "siMunids";

   public CloudPoller() {
      file7.mkdirs();
      this.BotFeaturesDto("current_config");
      EventManager.register(this);
      scheduledExecutorService2.scheduleAtFixedRate(() -> {
         try {
            this.save();
         } catch (Exception var2) {
         }
      }, 5L, 5L, TimeUnit.MINUTES);
   }

   public boolean BotFeaturesDto(String var1) {
      return this.on23(var1, PollMode.call107);
   }

   public boolean on23(String var1, PollMode var2) {
      try {
         if (var1 == null) {
            return false;
         }

         ModuleStateStore illlll11i11i1illi1l1ii1i111 = this.CloudPoller(var1);
         if (illlll11i11i1illi1l1ii1i111 == null) {
            return false;
         }

         try (BufferedReader bufferedreader = new BufferedReader(new FileReader(illlll11i11i1illi1l1ii1i111.getFile()))) {
            String s = bufferedreader.readLine();
            return s == null || s.isBlank() ? false : this.on23(this.CommandManager(s), var2, illlll11i11i1illi1l1ii1i111);
         } catch (Exception exception) {
            exception.printStackTrace();
            return false;
         }
      } catch (Exception exception1) {
         return false;
      }
   }

   public boolean on23(byte[] var1, PollMode var2) {
      if (var1 != null && var1.length != 0) {
         try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(var1), StandardCharsets.UTF_8))) {
            String s = bufferedreader.readLine();
            return s == null || s.isBlank() ? false : this.on23(this.CommandManager(s), var2, null);
         } catch (Exception exception) {
            return false;
         }
      } else {
         return false;
      }
   }

   public JsonObject CommandManager(String var1) throws Exception {
      byte[] abyte = Base64.getDecoder().decode(var1);
      byte[] abyte1 = CryptoUtils.UiAnimation(abyte, "siMunids");
      String s = new String(abyte1, StandardCharsets.UTF_8);
      return JsonParser.parseString(s).getAsJsonObject();
   }

   public boolean on23(JsonObject var1, PollMode var2, ModuleStateStore var3) {
      if (var2 == PollMode.getThis3) {
         JsonObject jsonobject = new JsonObject();
         if (var1.has("Styles")) {
            jsonobject.add("Styles", var1.get("Styles"));
         }

         this.on23(var3, jsonobject);
         this.AutoAccept();
         return true;
      } else {
         if (var2 != PollMode.call137) {
            this.on23(var3, var1);
            this.AutoAccept();
            return true;
         }

         HashMap hashmap = new HashMap();

         for (Module lii1lll1l1li1ii1iiilliix : ZenithClient.on23().ColorAnimator().PacketDispatcher()) {
            hashmap.put(lii1lll1l1li1ii1iiilliix.getId(), lii1lll1l1li1ii1iiilliix.getKeyCode());
         }

         this.on23(var3, var1);

         for (Module lii1lll1l1li1ii1iiillii : ZenithClient.on23().ColorAnimator().PacketDispatcher()) {
            Integer integer = (Integer)hashmap.get(lii1lll1l1li1ii1iiillii.getId());
            if (integer != null) {
               lii1lll1l1li1ii1iiillii.setKeyCode(integer);
            }
         }

         this.AutoAccept();
         return true;
      }
   }

   public void on23(ModuleStateStore var1, JsonObject var2) {
      if (var1 == null) {
         ModuleStateStore.loadState(var2);
      } else {
         var1.load(var2);
      }
   }

   public void AutoAccept() {
      if (minecraftClient3.player != null && ZenithClient.on23().CloudResponse() != null) {
         try {
            ZenithClient.on23().CloudResponse().getDispatcher().execute("binds list", ZenithClient.on23().CloudResponse().getSource());
         } catch (Exception var2) {
         }
      }
   }

   public boolean ModuleStateStore(String var1) {
      return this.on23(var1, true, true, true);
   }

   public boolean on23(String var1, boolean var2, boolean var3, boolean var4) {
      try {
         if (var1 == null) {
            return false;
         }

         ModuleStateStore illlll11i11i1illi1l1ii1i111 = this.CloudPoller(var1);
         if (illlll11i11i1illi1l1ii1i111 == null) {
            illlll11i11i1illi1l1ii1i111 = new ModuleStateStore(var1);
         }

         JsonObject jsonobject = illlll11i11i1illi1l1ii1i111.save();
         if (jsonobject == null) {
            return false;
         }

         this.on23(jsonobject, var2, var3, var4);
         String s = new GsonBuilder().setPrettyPrinting().create().toJson(jsonobject);
         String s1 = Base64.getEncoder().encodeToString(CryptoUtils.on23(s.getBytes(), "siMunids"));

         try (FileWriter filewriter = new FileWriter(illlll11i11i1illi1l1ii1i111.getFile())) {
            filewriter.write(s1);
         }

         return true;
      } catch (Exception exception) {
         return false;
      }
   }

   public void on23(JsonObject var1, boolean var2, boolean var3, boolean var4) {
      if (!var2) {
         var1.remove("Styles");
         var1.remove("FiguraData");
         var1.remove("PetData");
         var1.remove("Language");
         var1.remove("Interface");
      }

      if (!var3) {
         var1.remove("BotData");
      }

      JsonObject jsonobject = var1.has("Modules") && var1.get("Modules").isJsonObject() ? var1.getAsJsonObject("Modules") : null;
      if (jsonobject != null) {
         for (Module lii1lll1l1li1ii1iiillii : ZenithClient.on23().ColorAnimator().PacketDispatcher()) {
            Category i1i1lillillll11 = lii1lll1l1li1ii1iiillii.getCategory();
            boolean flag = i1i1lillillll11 == Category.RENDER || i1i1lillillll11 == Category.THEMES;
            if ((!flag || var2) && (flag || var3)) {
               if (!var4 && jsonobject.has(lii1lll1l1li1ii1iiillii.getId()) && jsonobject.get(lii1lll1l1li1ii1iiillii.getId()).isJsonObject()) {
                  jsonobject.getAsJsonObject(lii1lll1l1li1ii1iiillii.getId()).remove("keyCode");
               }
            } else {
               jsonobject.remove(lii1lll1l1li1ii1iiillii.getId());
            }
         }
      }
   }

   public ModuleStateStore CloudPoller(String var1) {
      if (var1 == null) {
         return null;
      }

      var1 = var1.replace("." + "Zenith".toLowerCase(), "");
      File file1 = new File(file7, var1 + "." + "Zenith".toLowerCase());
      return file1.exists() ? new ModuleStateStore(var1) : null;
   }

   public List<String> AutoAuth() {
      File[] afile = file7.listFiles();
      List<String> arraylist = new ArrayList<>();
      if (afile != null) {
         for (File file1 : afile) {
            arraylist.add(file1.getName());
         }
      }

      return arraylist;
   }

   public boolean EmoteMetadata(String var1) {
      if (var1 == null) {
         return false;
      }

      ModuleStateStore illlll11i11i1illi1l1ii1i111 = this.CloudPoller(var1);
      if (illlll11i11i1illi1l1ii1i111 == null) {
         return false;
      }

      File file1 = illlll11i11i1illi1l1ii1i111.getFile();
      return file1.exists() && file1.delete();
   }

   public synchronized boolean NbtItemSpec(String var1, String var2) {
      if (var1 != null && var2 != null) {
         String s = var1.replace("." + "Zenith".toLowerCase(), "").trim();
         String s1 = var2.replace("." + "Zenith".toLowerCase(), "").trim();
         if (s.isEmpty() || s1.isEmpty()) {
            return false;
         }

         if (s.equalsIgnoreCase(s1)) {
            return true;
         }

         ModuleStateStore illlll11i11i1illi1l1ii1i111 = this.CloudPoller(s);
         if (illlll11i11i1illi1l1ii1i111 != null && this.CloudPoller(s1) == null) {
            try {
               ModuleStateStore illlll11i11i1illi1l1ii1i1111 = new ModuleStateStore(s1);
               Files.copy(illlll11i11i1illi1l1ii1i111.getFile().toPath(), illlll11i11i1illi1l1ii1i1111.getFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
               if (!illlll11i11i1illi1l1ii1i111.getFile().delete()) {
                  illlll11i11i1illi1l1ii1i1111.getFile().delete();
                  return false;
               } else {
                  return true;
               }
            } catch (Exception exception) {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void save() {
      this.ModuleStateStore("current_config");
   }

   public String AutoCapcha() {
      return "siMunids";
   }
}
