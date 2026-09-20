package org.zenith.managers;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.core.BotFeatureRegistry;
import org.zenith.core.ClientProvider;
import org.zenith.core.CloudUserProfile;
import org.zenith.core.NpcCloneManager;
import org.zenith.core.UserdataManager;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.JumpEvent;
import org.zenith.event.EventTick;
import org.zenith.module.combat.AimAssist;
import org.zenith.module.combat.Aura;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.setting.Setting;

public class FriendFilter implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final float float308 = 0.4F;
   public static final int int422 = 8;
   public final MultiSelectSetting render3 = new MultiSelectSetting("cosmetics.pet.render");
   public final NumberSetting getEvent11;
   public Path path13;
   public NpcCloneManager var0;
   public final Map<UUID, NpcCloneManager> map56;
   public static final Set<UUID> set19 = ConcurrentHashMap.newKeySet();
   public static final Set<Integer> set20 = ConcurrentHashMap.newKeySet();

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(var1 -> {
         try {
            var1.setAccessible(true);
            return var1.get(this);
         } catch (Exception exception) {
            return null;
         }
      }).filter(var0 -> var0 instanceof Setting).map(var0 -> (Setting)var0).collect(Collectors.toList());
   }

   public boolean TextReplaceUtils() {
      return !this.render3.Event29("cosmetics.pet.render.self").isEnabled() && !this.render3.Event29("cosmetics.pet.render.friends").isEnabled();
   }

   public boolean TimerSpeed() {
      return this.render3.Event29("cosmetics.pet.render.self").isEnabled();
   }

   public boolean BaritoneBridge() {
      return this.render3.Event29("cosmetics.pet.render.friends").isEnabled();
   }

   public FriendFilter() {
      new MultiSelectSetting.Option(this.render3, "cosmetics.pet.render.self", true);
      new MultiSelectSetting.Option(this.render3, "cosmetics.pet.render.friends", true);
      this.getEvent11 = new NumberSetting("cosmetics.pet.scale", 0.4F, 0.1F, 2.0F, 0.05F, (var1, var2) -> this.MovementController());
      this.map56 = new ConcurrentHashMap<>();
      EventManager.register(this);
   }

   public void ColorAnimator(Path var1) {
      this.path13 = var1;
      if (var1 != null && this.Easing(var1) && minecraftClient3.player != null && minecraftClient3.world != null) {
         this.RaycastUtils();
         this.var0.ItemRegistry(var1);
      } else if (var1 == null && this.var0 != null) {
         this.ActionSequencePlayer();
      }
   }

   public String Pathfinder() {
      return this.Easing(this.path13) ? UserdataManager.ProfileItemBuilder(this.path13) : "";
   }

   public Vec3d AimUtils() {
      return this.var0 != null && this.var0.RotationMath() ? this.var0.ClientWindowProvider() : null;
   }

   public float MovementUtils() {
      return this.var0 != null ? this.var0.CloudResult() : 0.0F;
   }

   public boolean EffectEngine() {
      return minecraftClient3.player != null && minecraftClient3.player.isSneaking();
   }

   public static boolean SimpleItemBuilder(UUID var0) {
      return var0 != null && set19.contains(var0);
   }

   public static boolean PotionItemBuilder(int var0) {
      return set20.contains(var0);
   }

   public static void on23(NpcCloneManager var0) {
      if (var0 != null && var0.GameService() != null) {
         set19.add(var0.DrawContextSink());
         set20.add(var0.GameService().getId());
      }
   }

   public static void UiAnimation(NpcCloneManager var0) {
      if (var0 != null) {
         set19.remove(var0.DrawContextSink());
         if (var0.GameService() != null) {
            set20.remove(var0.GameService().getId());
         }
      }
   }

   @EventTarget
   public void UiAnimation(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         this.TaskScheduler();
         this.ScreenUtils();
      } else {
         this.cleanup();
      }
   }

   @EventTarget
   public void on23(JumpEvent var1) {
      if (this.var0 != null && this.var0.RotationMath()) {
         this.var0.ItemCountUtils();
      }
   }

   @EventTarget
   public void on23(EventHookWorldRender var1) {
      if (this.var0 != null && this.var0.RotationMath()) {
         Vec3d vec3d = this.var0.FileLogger(var1.CropFarmer());
         if (vec3d != null && this.var0.GameService() != null) {
            this.var0.GameService().updateTrackedPosition(vec3d.x, vec3d.y, vec3d.z);
         }
      }

      for (NpcCloneManager ll1i1ii1il : this.map56.values()) {
         if (ll1i1ii1il.RotationMath()) {
            Vec3d vec3d1 = ll1i1ii1il.FileLogger(var1.CropFarmer());
            if (vec3d1 != null && ll1i1ii1il.GameService() != null) {
               ll1i1ii1il.GameService().updateTrackedPosition(vec3d1.x, vec3d1.y, vec3d1.z);
            }
         }
      }
   }

   public void TaskScheduler() {
      if (this.path13 != null && this.TimerSpeed() && this.Easing(this.path13)) {
         this.RaycastUtils();
         if (!this.var0.RotationMath()) {
            Vec3d vec3d = minecraftClient3.player.getEntityPos().add(2.0, 0.0, 0.0);
            this.var0.on23(minecraftClient3.world, vec3d);
            this.var0.ItemRegistry(this.path13);
            on23(this.var0);
         }

         this.var0.FileLogger(this.RandomSource());
         LivingEntity livingentity = null;

         try {
            Aura lli1i11i1i1l11il1i111i1llliii1 = Aura.aura;
            livingentity = lli1i11i1i1l11il1i111i1llliii1.zClass054() == null ? AimAssist.aimAssist.zClass054() : lli1i11i1i1l11il1i111i1llliii1.zClass054();
         } catch (Exception var3) {
         }

         this.var0
            .on23(minecraftClient3.player.getEntityPos(), minecraftClient3.player.bodyYaw, minecraftClient3.player.isSneaking(), livingentity);
         this.var0.on23(minecraftClient3.player);
         if (this.var0.GameService() != null) {
            this.var0.GameService().handSwingProgress = minecraftClient3.player.handSwingProgress;
            this.var0.GameService().handSwinging = minecraftClient3.player.handSwinging;
            this.var0.GameService().handSwingTicks = minecraftClient3.player.handSwingTicks;
            this.var0.GameService().preferredHand = minecraftClient3.player.preferredHand;
            this.var0.GameService().setSneaking(minecraftClient3.player.isSneaking());
            this.var0.GameService().setPose(minecraftClient3.player.isSneaking() ? EntityPose.CROUCHING : EntityPose.STANDING);
         }
      } else if (this.var0 != null) {
         this.ActionSequencePlayer();
      }
   }

   public boolean Easing(Path var1) {
      return var1 != null && ZenithClient.on23().getCloudClient() != null
         ? ZenithClient.on23().getCloudClient().NbtItemSpec(UserdataManager.ProfileItemBuilder(var1))
         : false;
   }

   public void ScreenUtils() {
      if (minecraftClient3.world != null && minecraftClient3.player != null) {
         if (!this.BaritoneBridge()) {
            for (NpcCloneManager ll1i1ii1il : this.map56.values()) {
               UiAnimation(ll1i1ii1il);
               ll1i1ii1il.RotationDelta();
            }

            this.map56.clear();
         } else {
            String s = ZenithClient.on23().CloudApiClient().getServer();
            String s1 = minecraftClient3.player.getEntityWorld() != null
               ? minecraftClient3.player.getEntityWorld().getRegistryKey().getValue().toString()
               : "";
            HashSet hashset = new HashSet();
            int i = 0;

            for (CloudUserProfile li1ilil1i11ii111l11l : ZenithClient.on23().MediaTrackInfo().ShaderHand()) {
               if (i >= 8) {
                  break;
               }

               BotFeatureRegistry ili1ll11li1ili11l1i1l11l1 = li1ilil1i11ii111l11l.RotationUpdateStartEvent();
               if (ili1ll11li1ili11l1i1l11l1 != null && li1ilil1i11ii111l11l.EventTickEnd() && li1ilil1i11ii111l11l.EventGetFogColorHook().isEnabled()) {
                  String s2 = ili1ll11li1ili11l1i1l11l1.ServerConfigStore();
                  if (s2 != null
                     && !s2.isBlank()
                     && s.equals(ili1ll11li1ili11l1i1l11l1.PacketReceiveEvent())
                     && s1.equals(ili1ll11li1ili11l1i1l11l1.FriendStore())) {
                     UUID uuid = ili1ll11li1ili11l1i1l11l1.uuid();
                     hashset.add(uuid);
                     NpcCloneManager ll1i1ii1ilx = this.map56.computeIfAbsent(uuid, var0 -> new NpcCloneManager(var0));
                     if (!ll1i1ii1ilx.RotationMath()) {
                        ll1i1ii1ilx.on23(minecraftClient3.world, ili1ll11li1ili11l1i1l11l1.VisualSettingsStore().add(2.0, 0.0, 0.0));
                        on23(ll1i1ii1ilx);
                     }

                     ll1i1ii1ilx.StopUsingItemEvent(s2);
                     ll1i1ii1ilx.StringCodec(ili1ll11li1ili11l1i1l11l1.DiskStorage());
                     Vec3d vec3d = li1ilil1i11ii111l11l.EventTick();
                     if (vec3d == null) {
                        vec3d = ili1ll11li1ili11l1i1l11l1.ItemStackStore();
                     }

                     if (vec3d != null) {
                        ll1i1ii1ilx.on23(vec3d, ili1ll11li1ili11l1i1l11l1.LocaleEntry(), ili1ll11li1ili11l1i1l11l1.Translator());
                     }

                     PlayerEntity playerentity = minecraftClient3.world.getPlayerByUuid(uuid);
                     if (playerentity != null) {
                        ll1i1ii1ilx.on23(playerentity);
                     }

                     i++;
                  }
               }
            }

            Iterator<Entry<UUID, NpcCloneManager>> iterator = this.map56.entrySet().iterator();

            while (iterator.hasNext()) {
               Entry entry = iterator.next();
               if (!hashset.contains(entry.getKey())) {
                  UiAnimation((NpcCloneManager)entry.getValue());
                  ((NpcCloneManager)entry.getValue()).RotationDelta();
                  iterator.remove();
               }
            }
         }
      }
   }

   public boolean RandomSource() {
      return minecraftClient3.player == null ? false : minecraftClient3.player.getAbilities().flying || minecraftClient3.player.isGliding();
   }

   public void RaycastUtils() {
      if (this.var0 == null) {
         this.var0 = new NpcCloneManager(minecraftClient3.player.getUuid());
      }
   }

   public void ActionSequencePlayer() {
      if (this.var0 != null) {
         UiAnimation(this.var0);
         this.var0.RotationDelta();
         this.var0 = null;
      }
   }

   public void on23(UUID var1, String var2) {
   }

   public float TickGate() {
      return this.getEvent11.getCurrent();
   }

   public void MovementController() {
      if (this.var0 != null) {
         this.var0.StringCodec(this.TickGate());
      }
   }

   public JsonObject save() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", UserdataManager.ProfileItemBuilder(this.path13));
      JsonObject jsonobject1 = new JsonObject();

      for (Setting l1illl1lllllll1l1l1l1ili11l1 : this.getSettings()) {
         l1illl1lllllll1l1l1l1ili11l1.safe(jsonobject1);
      }

      jsonobject.add("settings", jsonobject1);
      return jsonobject;
   }

   public void load(JsonObject var1) {
      if (var1 != null) {
         if (var1.has("settings") && var1.get("settings").isJsonObject()) {
            JsonObject jsonobject = var1.getAsJsonObject("settings");

            for (Setting l1illl1lllllll1l1l1l1ili11l1 : this.getSettings()) {
               if (jsonobject.has(l1illl1lllllll1l1l1l1ili11l1.getKey())) {
                  l1illl1lllllll1l1l1l1ili11l1.load(jsonobject);
               }
            }
         }

         if (var1.has("name")) {
            String s = var1.get("name").isJsonNull() ? "" : var1.get("name").getAsString();
            this.ColorAnimator(s != null && !s.isBlank() ? UserdataManager.EventUpdateHealth(s) : null);
         }
      }
   }

   public void cleanup() {
      this.ActionSequencePlayer();

      for (NpcCloneManager ll1i1ii1il : this.map56.values()) {
         UiAnimation(ll1i1ii1il);
         ll1i1ii1il.RotationDelta();
      }

      this.map56.clear();
   }

   public MultiSelectSetting TargetSelector() {
      return this.render3;
   }

   public NumberSetting LegitRotationUtils() {
      return this.getEvent11;
   }

   public Path Rotation() {
      return this.path13;
   }
}
