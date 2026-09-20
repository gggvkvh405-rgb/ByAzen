package org.zenith.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import org.zenith.ZenithClient;
import org.zenith.setting.Setting;
import org.zenith.util.Item;

public class FriendStore extends Item<String> {
   public static final Type type = (new TypeToken<Map<String, JsonObject>>() {}).getType();
   public final List<CloudUserProfile> list88 = new CopyOnWriteArrayList<>();
   public final Map<String, String> map39 = new ConcurrentHashMap<>();
   public Map<String, JsonObject> map40 = new HashMap<>();

   public FriendStore() {
      super("friends.json", "", new FriendSetToken().getType(), ConcurrentHashMap::newKeySet);
      KeySetView keysetview = ConcurrentHashMap.newKeySet();
      keysetview.addAll(this.items);
      this.items = keysetview;
      this.NoRender();
   }

   public boolean UiAnimation(Entity var1) {
      if (!(var1 instanceof PlayerEntity playerentity)) {
         return false;
      } else {
         String s = playerentity.getGameProfile().name();
         return this.getItems().contains(s) || this.BotDisconnectEvent(s);
      }
   }

   public boolean isFriend(String var1) {
      return this.getItems().contains(var1) || this.BotDisconnectEvent(var1);
   }

   public boolean BotDisconnectEvent(String var1) {
      return var1 != null && !var1.isEmpty() ? this.map39.containsValue(var1) : false;
   }

   public void on23(CloudUserProfile var1, BotFeatureRegistry var2) {
      if (var1 != null && var1.id() != null && !var1.id().isEmpty() && var2 != null) {
         String s = var2.PacketSendEvent();
         if (s != null && !s.isEmpty()) {
            this.map39.put(var1.id(), s);
         } else {
            this.map39.remove(var1.id());
         }
      }
   }

   public void ItemServiceBase(String var1) {
      this.getItems().remove(var1);
   }

   public void UiAnimation(List<CloudUserProfile> var1) {
      if (var1 == null) {
         var1 = List.of();
      }

      HashSet hashset = new HashSet();

      for (CloudUserProfile li1ilil1i11ii111l11lxxx : var1) {
         if (li1ilil1i11ii111l11lxxx != null && li1ilil1i11ii111l11lxxx.id() != null && !li1ilil1i11ii111l11lxxx.id().isEmpty()) {
            hashset.add(li1ilil1i11ii111l11lxxx.id());
         }
      }

      this.list88.removeIf(var1x -> {
         boolean flag = !hashset.contains(var1x.id());
         if (flag) {
            var1x.ItemUseEvent();
         }

         return flag;
      });
      this.map39.keySet().removeIf(var1x -> !hashset.contains(var1x));

      for (CloudUserProfile li1ilil1i11ii111l11lx : var1) {
         if (li1ilil1i11ii111l11lx != null && li1ilil1i11ii111l11lx.id() != null && !li1ilil1i11ii111l11lx.id().isEmpty()) {
            CloudUserProfile li1ilil1i11ii111l11lxx = this.BotPacketEvent(li1ilil1i11ii111l11lx.id());
            if (li1ilil1i11ii111l11lxx != null) {
               li1ilil1i11ii111l11lxx.setUsername(li1ilil1i11ii111l11lx.username());
               li1ilil1i11ii111l11lxx.setRole(li1ilil1i11ii111l11lx.Event29());
               this.on23(li1ilil1i11ii111l11lxx);
            } else {
               CloudUserProfile li1ilil1i11ii111l11lxxx = new CloudUserProfile(
                  li1ilil1i11ii111l11lx.id(), li1ilil1i11ii111l11lx.username(), li1ilil1i11ii111l11lx.Event29()
               );
               this.on23(li1ilil1i11ii111l11lxxx);
               this.list88.add(li1ilil1i11ii111l11lxxx);
            }
         }
      }
   }

   public void on23(String var1, String var2, String var3) {
      if (var1 != null && !var1.isEmpty() && this.BotPacketEvent(var1) == null) {
         CloudUserProfile li1ilil1i11ii111l11l = new CloudUserProfile(var1, var2 != null && !var2.isEmpty() ? var2 : "UID " + var1, var3 == null ? "" : var3);
         this.on23(li1ilil1i11ii111l11l);
         this.list88.add(li1ilil1i11ii111l11l);
      }
   }

   public boolean BotWorldJoinEvent(String var1) {
      if (var1 != null) {
         this.map39.remove(var1);
      }

      return this.list88.removeIf(var1xx -> {
         boolean flag = var1 != null && var1.equalsIgnoreCase(var1xx.id());
         if (flag) {
            var1xx.ItemUseEvent();
         }

         return flag;
      });
   }

   public CloudUserProfile BotPacketEvent(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         for (CloudUserProfile li1ilil1i11ii111l11l : this.list88) {
            if (var1.equalsIgnoreCase(li1ilil1i11ii111l11l.id())) {
               return li1ilil1i11ii111l11l;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @Override
   public void save() {
      super.save();
      this.Particles();
   }

   public void NoRender() {
      File file1 = new File(ZenithClient.ColorAnimator, "cloud_friend_settings.json");
      if (!file1.exists()) {
         this.map40 = new HashMap<>();
      } else {
         try (BufferedReader bufferedreader = new BufferedReader(new FileReader(file1))) {
            Gson gson = new Gson();
            Map<String, JsonObject> map = (Map<String, JsonObject>)gson.fromJson(bufferedreader, type);
            this.map40 = map != null ? map : new HashMap<>();
         } catch (Exception exception) {
            this.map40 = new HashMap<>();
         }
      }
   }

   public void Particles() {
      HashMap hashmap = new HashMap();

      for (CloudUserProfile li1ilil1i11ii111l11l : this.list88) {
         if (li1ilil1i11ii111l11l.id() != null && !li1ilil1i11ii111l11l.id().isEmpty()) {
            JsonObject jsonobject = new JsonObject();

            for (Setting l1illl1lllllll1l1l1l1ili11l1 : li1ilil1i11ii111l11l.getSettings()) {
               try {
                  l1illl1lllllll1l1l1l1ili11l1.safe(jsonobject);
               } catch (Exception var11) {
               }
            }

            hashmap.put(li1ilil1i11ii111l11l.id(), jsonobject);
         }
      }

      try (FileWriter filewriter = new FileWriter(new File(ZenithClient.ColorAnimator, "cloud_friend_settings.json"))) {
         new Gson().toJson(hashmap, filewriter);
      } catch (Exception var10) {
      }
   }

   public void on23(CloudUserProfile var1) {
      if (var1.id() != null && !var1.id().isEmpty()) {
         JsonObject jsonobject = this.map40.get(var1.id());
         if (jsonobject != null) {
            for (Setting l1illl1lllllll1l1l1l1ili11l1 : var1.getSettings()) {
               try {
                  if (jsonobject.has(l1illl1lllllll1l1l1l1ili11l1.getKey())) {
                     l1illl1lllllll1l1l1l1ili11l1.load(jsonobject);
                  }
               } catch (Exception var6) {
               }
            }
         }
      }
   }

   public List<CloudUserProfile> ShaderHand() {
      return this.list88;
   }

   public Map<String, String> SwingAnimation() {
      return this.map39;
   }
}
