package org.zenith.core;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import org.zenith.ZenithClient;
import org.zenith.event.EventTick;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.Setting;

public class VisualSettingsStore implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public Path path8 = null;
   public Path path9 = null;
   public Path path10 = null;
   public Path path11 = null;
   public final MultiSelectSetting render2 = new MultiSelectSetting("cosmetics.figura.render");

   public void EntityESP() {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient != null && minecraftclient.world != null && minecraftclient.player != null) {
         if (!this.Chams()) {
            for (CloudUserProfile li1ilil1i11ii111l11l : ZenithClient.on23().MediaTrackInfo().ShaderHand()) {
               BotFeatureRegistry ili1ll11li1ili11l1i1l11l11 = li1ilil1i11ii111l11l.RotationUpdateStartEvent();
               if (ili1ll11li1ili11l1i1l11l11 != null) {
                  UserdataManager.StringCodec(ili1ll11li1ili11l1i1l11l11.uuid());
               }

               li1ilil1i11ii111l11l.ItemUseEvent();
            }
         } else {
            for (CloudUserProfile li1ilil1i11ii111l11lx : ZenithClient.on23().MediaTrackInfo().ShaderHand()) {
               BotFeatureRegistry ili1ll11li1ili11l1i1l11l1 = li1ilil1i11ii111l11lx.RotationUpdateStartEvent();
               if (ili1ll11li1ili11l1i1l11l1 != null) {
                  if (!li1ilil1i11ii111l11lx.EventGetFogColorHook().isEnabled()) {
                     UserdataManager.StringCodec(ili1ll11li1ili11l1i1l11l1.uuid());
                     li1ilil1i11ii111l11lx.ItemUseEvent();
                  } else {
                     if (!ili1ll11li1ili11l1i1l11l1.MacroManager().isBlank() && !UserdataManager.PotionItemBuilder(ili1ll11li1ili11l1i1l11l1.uuid())) {
                        UserdataManager.ColorAnimator(ili1ll11li1ili11l1i1l11l1.uuid(), ili1ll11li1ili11l1i1l11l1.MacroManager());
                     }

                     if (!ili1ll11li1ili11l1i1l11l1.UsageStatStore().isBlank() && !UserdataManager.ProfileItemBuilder(ili1ll11li1ili11l1i1l11l1.uuid())) {
                        UserdataManager.ItemRegistry(ili1ll11li1ili11l1i1l11l1.uuid(), ili1ll11li1ili11l1i1l11l1.UsageStatStore());
                     }
                  }
               }
            }
         }
      }
   }

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

   public boolean CameraTweaks() {
      return !this.render2.Event29("cosmetics.figura.render.self").isEnabled() && !this.render2.Event29("cosmetics.figura.render.friends").isEnabled();
   }

   public boolean Cape() {
      return this.render2.Event29("cosmetics.figura.render.self").isEnabled();
   }

   public boolean Chams() {
      return this.render2.Event29("cosmetics.figura.render.friends").isEnabled();
   }

   public VisualSettingsStore() {
      new MultiSelectSetting.Option(this.render2, "cosmetics.figura.render.self", true);
      new MultiSelectSetting.Option(this.render2, "cosmetics.figura.render.friends", true);
      EventManager.register(this);
   }

   @EventTarget
   public void UiAnimation(EventTick var1) {
      this.EntityESP();
      this.Crosshair();
   }

   public void Crosshair() {
      if (minecraftClient3.player == null) {
         this.path9 = null;
         this.path11 = null;
      } else {
         UUID uuid = minecraftClient3.player.getUuid();
         this.path9 = this.updateAvatarSelection(uuid, false, this.path8, this.path9);
         this.path11 = this.updateAvatarSelection(uuid, true, this.path10, this.path11);
      }
   }

   public Path updateAvatarSelection(UUID var1, boolean weapon, Path var3, Path var4) {
      boolean flag1 = weapon ? UserdataManager.ProfileItemBuilder(var1) : UserdataManager.PotionItemBuilder(var1);
      if (this.Cape() && var3 != null && this.Easing(var3)) {
         if (flag1 && samePath(var3, var4)) {
            return var4;
         }

         if (weapon) {
            UserdataManager.ColorAnimator(var1, var3);
         } else {
            UserdataManager.Easing(var1, var3);
         }

         return var3;
      } else {
         if (flag1) {
            clearAvatarSelection(var1, weapon);
         }

         return null;
      }
   }

   public static void clearAvatarSelection(UUID var0, boolean weapon) {
      if (weapon) {
         UserdataManager.CloudApiClient(var0);
      } else {
         UserdataManager.FileLogger(var0);
      }
   }

   public void on23(Path var1) {
      if (!samePath(this.path8, var1)) {
         this.path8 = var1;
         this.path9 = null;
         if (minecraftClient3.player != null) {
            UserdataManager.MediaTrackInfo(minecraftClient3.player.getUuid());
            if (var1 == null) {
               UserdataManager.FileLogger(minecraftClient3.player.getUuid());
            }
         }
      }
   }

   public void UiAnimation(Path var1) {
      if (!samePath(this.path10, var1)) {
         this.path10 = var1;
         this.path11 = null;
         if (minecraftClient3.player != null) {
            UserdataManager.CloudUserProfile(minecraftClient3.player.getUuid());
            if (var1 == null) {
               UserdataManager.CloudApiClient(minecraftClient3.player.getUuid());
            }
         }
      }
   }

   public String FireWorkESP() {
      return this.Easing(this.path8) ? UserdataManager.ProfileItemBuilder(this.path8) : "";
   }

   public String HandFire() {
      return this.Easing(this.path10) ? UserdataManager.ProfileItemBuilder(this.path10) : "";
   }

   public boolean Easing(Path var1) {
      if (var1 == null) {
         return false;
      }

      String s = UserdataManager.ProfileItemBuilder(var1);
      return ZenithClient.on23().getCloudClient() != null && ZenithClient.on23().getCloudClient().NbtItemSpec(s);
   }

   public static boolean samePath(Path var0, Path var1) {
      return var0 != null && var1 != null ? var0.toAbsolutePath().normalize().equals(var1.toAbsolutePath().normalize()) : var0 == var1;
   }

   public Path HitParticles() {
      return this.path8;
   }

   public Path Interface() {
      return this.path9;
   }

   public Path JumpCircle() {
      return this.path10;
   }

   public Path KillEffect() {
      return this.path11;
   }

   public MultiSelectSetting Menu() {
      return this.render2;
   }
}
