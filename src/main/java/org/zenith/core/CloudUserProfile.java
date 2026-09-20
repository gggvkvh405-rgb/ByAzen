package org.zenith.core;

import java.util.List;
import java.util.UUID;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.managers.EmoteRegistry;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.Setting;

public class CloudUserProfile {
   public final String BooleanSetting;
   public volatile String username;
   public volatile String role;
   public volatile BotFeatureRegistry ButtonSetting;
   public volatile InventoryUtils ColorSetting;
   public volatile long StringListSetting;
   public volatile boolean HudInfoBoxPrimary;
   public volatile boolean KeySetting;
   public volatile Vec3d ModeSetting;
   public volatile Vec3d MultiSelectSetting;
   public volatile long NumberSetting;
   public volatile long TextSetting;
   public UUID SettingGroup;
   public String AimAssist = "";
   public String AntiBot = "";
   public UUID Aura;
   public final BooleanSetting AutoExplosion = new BooleanSetting("Render Cosmetics", true);
   public final BooleanSetting AutoSwap = new BooleanSetting("Render Inventory", true);

   public CloudUserProfile(String var1, String var2, String var3) {
      this.BooleanSetting = var1;
      this.username = var2 == null ? "" : var2;
      this.role = var3 == null ? "" : var3;
   }

   public String id() {
      return this.BooleanSetting;
   }

   public String username() {
      return this.username;
   }

   public String Event29() {
      return this.role;
   }

   public void setUsername(String var1) {
      this.username = var1 == null ? "" : var1;
   }

   public void setRole(String var1) {
      this.role = var1 == null ? "" : var1;
   }

   public BotFeatureRegistry RotationUpdateStartEvent() {
      return this.ButtonSetting;
   }

   public InventoryUtils TargetAcquireEvent() {
      return this.ColorSetting;
   }

   public long EventImpl() {
      return this.StringListSetting;
   }

   public void UiAnimation(InventoryUtils var1) {
      this.ColorSetting = var1;
      this.StringListSetting = System.currentTimeMillis();
   }

   public void UiAnimation(BotFeatureRegistry var1) {
      this.KeySetting = false;
      this.ColorAnimator(var1);
      this.Easing(var1);
      if (var1 == null) {
         this.ButtonSetting = null;
         this.ModeSetting = null;
         this.MultiSelectSetting = null;
         this.NumberSetting = 0L;
         this.TextSetting = 0L;
      } else {
         if (this.ButtonSetting != null && this.AutoExplosion.isEnabled()) {
            String s = var1.ServerConfigStore();
            String s1 = this.ButtonSetting.ServerConfigStore();
            if (!s.equals(s1)) {
               ZenithClient.on23().ItemServiceBase().on23(var1.uuid(), s);
            }
         }

         long i = System.currentTimeMillis();
         if (this.ButtonSetting != null && this.ButtonSetting.VisualSettingsStore() != null) {
            this.ModeSetting = this.ButtonSetting.VisualSettingsStore();
            this.MultiSelectSetting = this.ButtonSetting.ItemStackStore();
            this.NumberSetting = this.TextSetting;
         } else {
            this.ModeSetting = var1.VisualSettingsStore();
            this.MultiSelectSetting = var1.ItemStackStore();
            this.NumberSetting = i;
         }

         this.TextSetting = i;
         this.ButtonSetting = var1;
      }
   }

   public void ItemUseEvent() {
      if (this.SettingGroup != null) {
         UserdataManager.StringCodec(this.SettingGroup);
         this.SettingGroup = null;
         this.AimAssist = "";
         this.AntiBot = "";
      }
   }

   public void Easing(BotFeatureRegistry var1) {
      if (var1 != null && var1.uuid() != null) {
         if (this.Aura != null && !this.Aura.equals(var1.uuid())) {
            EmoteRegistry.ItemSpec(this.Aura);
         }

         this.Aura = var1.uuid();
         EmoteRegistry.on23(var1.uuid(), var1.ModuleManager(), var1.CloudApi(), var1.FriendFilter());
      } else {
         this.SprintStateEvent();
      }
   }

   public void SprintStateEvent() {
      if (this.Aura != null) {
         EmoteRegistry.ItemSpec(this.Aura);
         this.Aura = null;
      }
   }

   public void ColorAnimator(BotFeatureRegistry var1) {
      if (var1 != null && this.AutoExplosion.isEnabled()) {
         UUID uuid = var1.uuid();
         String s = var1.MacroManager() == null ? "" : var1.MacroManager();
         String s1 = var1.UsageStatStore() == null ? "" : var1.UsageStatStore();
         if (uuid != null && (!s.isBlank() || !s1.isBlank())) {
            if (!uuid.equals(this.SettingGroup)) {
               this.ItemUseEvent();
            }

            this.SettingGroup = uuid;
            if (!s.equals(this.AimAssist)) {
               if (s.isBlank()) {
                  UserdataManager.FileLogger(uuid);
                  this.AimAssist = "";
               } else {
                  UserdataManager.ColorAnimator(uuid, s);
                  if (UserdataManager.PotionItemBuilder(uuid)) {
                     this.AimAssist = s;
                  }
               }
            }

            if (!s1.equals(this.AntiBot)) {
               if (s1.isBlank()) {
                  UserdataManager.CloudApiClient(uuid);
                  this.AntiBot = "";
               } else {
                  UserdataManager.ItemRegistry(uuid, s1);
                  if (UserdataManager.ProfileItemBuilder(uuid)) {
                     this.AntiBot = s1;
                  }
               }
            }
         } else {
            this.ItemUseEvent();
         }
      } else {
         this.ItemUseEvent();
      }
   }

   public Vec3d SprintPacketEvent() {
      if (this.ButtonSetting != null && this.ButtonSetting.VisualSettingsStore() != null) {
         Vec3d vec3d = this.ButtonSetting.VisualSettingsStore();
         if (this.ModeSetting == null) {
            return vec3d;
         } else {
            long i = Math.max(1L, this.TextSetting - this.NumberSetting);
            double d0 = (double)(System.currentTimeMillis() - this.TextSetting) / i;
            if (d0 <= 0.0) {
               return this.ModeSetting;
            } else {
               return d0 >= 1.0
                  ? vec3d
                  : new Vec3d(
                     this.ModeSetting.x + (vec3d.x - this.ModeSetting.x) * d0,
                     this.ModeSetting.y + (vec3d.y - this.ModeSetting.y) * d0,
                     this.ModeSetting.z + (vec3d.z - this.ModeSetting.z) * d0
                  );
            }
         }
      } else {
         return null;
      }
   }

   public Vec3d EventTick() {
      if (this.ButtonSetting != null && this.ButtonSetting.ItemStackStore() != null) {
         Vec3d vec3d = this.ButtonSetting.ItemStackStore();
         if (this.MultiSelectSetting == null) {
            return vec3d;
         } else {
            long i = Math.max(1L, this.TextSetting - this.NumberSetting);
            double d0 = (double)(System.currentTimeMillis() - this.TextSetting) / i;
            if (d0 <= 0.0) {
               return this.MultiSelectSetting;
            } else {
               return d0 >= 1.0
                  ? vec3d
                  : new Vec3d(
                     this.MultiSelectSetting.x + (vec3d.x - this.MultiSelectSetting.x) * d0,
                     this.MultiSelectSetting.y + (vec3d.y - this.MultiSelectSetting.y) * d0,
                     this.MultiSelectSetting.z + (vec3d.z - this.MultiSelectSetting.z) * d0
                  );
            }
         }
      } else {
         return null;
      }
   }

   public boolean EventTickEnd() {
      return this.HudInfoBoxPrimary;
   }

   public boolean EventGetBasicProjectionMatrixHook() {
      return this.KeySetting;
   }

   public void ItemRegistry(BotFeatureRegistry var1) {
      BotFeatureRegistry ili1ll11li1ili11l1i1l11l1 = this.ButtonSetting;
      this.KeySetting = this.HudInfoBoxPrimary
         && var1 != null
         && ili1ll11li1ili11l1i1l11l1 != null
         && ColorAnimator(var1.PacketReceiveEvent(), ili1ll11li1ili11l1i1l11l1.PacketReceiveEvent())
         && ColorAnimator(var1.FriendStore(), ili1ll11li1ili11l1i1l11l1.FriendStore());
   }

   public static boolean ColorAnimator(String var0, String var1) {
      return var0 != null && var1 != null && !var0.isBlank() && !var1.isBlank() && var0.equalsIgnoreCase(var1);
   }

   @Override
   public String toString() {
      String s = this.role != null && !this.role.isEmpty() ? this.role : "USER";
      return this.BooleanSetting + " | " + this.username + " [" + s + "]";
   }

   public void SprintEvent() {
      this.HudInfoBoxPrimary = true;
   }

   public void EventPosHook() {
      this.HudInfoBoxPrimary = false;
      this.KeySetting = false;
      this.UiAnimation((BotFeatureRegistry)null);
      this.ColorSetting = null;
      this.StringListSetting = 0L;
   }

   public List<Setting> getSettings() {
      return List.of(this.AutoExplosion, this.AutoSwap);
   }

   public BooleanSetting EventGetFogColorHook() {
      return this.AutoExplosion;
   }

   public BooleanSetting FovEvent() {
      return this.AutoSwap;
   }
}
