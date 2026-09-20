package org.zenith.core;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.zenith.ZenithClient;
import org.zenith.util.CryptoUtils;

public class DiskStorage {
   public static final File file5 = new File(ZenithClient.ColorAnimator, "kits");
   public final String string116 = "kit";
   public final List<ServerConfigStore> list104 = new ArrayList<>();

   public DiskStorage() {
      file5.mkdirs();
      this.ShaderFog();
   }

   public void ShaderFog() {
      this.list104.clear();
      File[] afile = file5.listFiles();
      if (afile != null) {
         for (File file1 : afile) {
            if (file1.getName().endsWith("." + "Zenith".toLowerCase())) {
               ServerConfigStore l1ili1lll = this.on23(file1);
               if (l1ili1lll != null) {
                  this.list104.add(l1ili1lll);
               }
            }
         }
      }
   }

   public ServerConfigStore EventMouseButton(String var1) {
      if (var1 == null) {
         return null;
      }

      File file1 = new File(file5, var1 + "." + "Zenith".toLowerCase());
      return !file1.exists() ? null : this.on23(file1);
   }

   public ServerConfigStore on23(File var1) {
      if (var1 != null && var1.exists()) {
         try (BufferedReader bufferedreader = new BufferedReader(new FileReader(var1))) {
            JsonParser jsonparser = new JsonParser();
            String s = bufferedreader.readLine();
            byte[] abyte = Base64.getDecoder().decode(s);
            byte[] abyte1 = CryptoUtils.UiAnimation(abyte, "kit");
            String s1 = new String(abyte1, StandardCharsets.UTF_8);
            JsonObject jsonobject = (JsonObject)jsonparser.parse(s1);
            String s2 = var1.getName().replace("." + "Zenith".toLowerCase(), "");
            String s3 = jsonobject.has("name") ? jsonobject.get("name").getAsString() : s2;
            String s4 = jsonobject.has("server") ? jsonobject.get("server").getAsString() : "HolyWorld";
            ServerConfigStore l1ili1lllx = new ServerConfigStore(s3, s4, var1);
            l1ili1lllx.load(jsonobject);
            return l1ili1lllx;
         } catch (Exception exception) {
            exception.printStackTrace();
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean on23(ServerConfigStore var1) {
      try {
         if (var1 == null) {
            return false;
         }

         String s = new GsonBuilder().setPrettyPrinting().create().toJson(var1.save());
         s = Base64.getEncoder().encodeToString(CryptoUtils.on23(s.getBytes(), "kit"));

         try {
            FileWriter filewriter = new FileWriter(var1.getFile());
            filewriter.write(s);
            filewriter.close();
            return true;
         } catch (IOException ioexception) {
            return false;
         }
      } catch (Exception exception) {
         return false;
      }
   }

   public ServerConfigStore on23(String var1, List<ItemStackStore> var2) {
      ServerConfigStore l1ili1lll = new ServerConfigStore(var1);
      l1ili1lll.Easing(var2);
      if (this.on23(l1ili1lll)) {
         this.list104.add(l1ili1lll);
         return l1ili1lll;
      } else {
         return null;
      }
   }

   public ServerConfigStore on23(String var1, List<ItemStackStore> var2, String var3) {
      ServerConfigStore l1ili1lll = new ServerConfigStore(var1, var3);
      l1ili1lll.Easing(var2);
      if (this.on23(l1ili1lll)) {
         this.list104.add(l1ili1lll);
         return l1ili1lll;
      } else {
         return null;
      }
   }

   public boolean EventModifyMouseRotationInput(String var1) {
      if (var1 == null) {
         return false;
      }

      ServerConfigStore l1ili1lll;
      if ((l1ili1lll = this.EventMixin_modifySetScreenArg(var1)) != null) {
         File file1 = l1ili1lll.getFile();
         if (file1.exists() && file1.delete()) {
            this.list104.remove(l1ili1lll);
            return true;
         }
      }

      return false;
   }

   public boolean UiAnimation(ServerConfigStore var1) {
      if (var1 == null) {
         return false;
      } else {
         File file1 = var1.getFile();
         if (file1.exists() && file1.delete()) {
            this.list104.remove(var1);
            return true;
         } else {
            return false;
         }
      }
   }

   public ServerConfigStore EventMixin_modifySetScreenArg(String var1) {
      return var1 == null ? null : this.list104.stream().filter(var1xx -> var1xx.getName().equals(var1)).findFirst().orElse(null);
   }

   public ServerConfigStore SimpleItemBuilder(String var1, String var2) {
      return var1 == null
         ? null
         : this.list104.stream().filter(var2xx -> var2xx.getName().equals(var1) && var2xx.DataChangedEvent(var2)).findFirst().orElse(null);
   }

   public List<ServerConfigStore> BlockInteractEvent(String var1) {
      return this.list104.stream().filter(var1xx -> var1xx.DataChangedEvent(var1)).toList();
   }

   public List<String> ShaderPostProcess() {
      List<String> arraylist = new ArrayList<>();

      for (ServerConfigStore l1ili1lll : this.list104) {
         if (!arraylist.contains(l1ili1lll.getName())) {
            arraylist.add(l1ili1lll.getName());
         }
      }

      return arraylist;
   }

   public String AutoCapcha() {
      return "kit";
   }

   public List<ServerConfigStore> getKits() {
      return this.list104;
   }
}
