package org.zenith.config;

import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.animation.layered.AnimationContainer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.zenith.core.EmotePlayback;
import org.zenith.managers.EmoteManager;
import org.zenith.managers.EmoteMetadata;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.Setting;

public final class CosmeticManager {
   public static final int int371 = 1000;
   public static final long long147 = 30000000000L;
   public final EmoteManager zClass114 = new EmoteManager();
   public final AtomicLong atomicLong3 = new AtomicLong();
   public final AtomicLong atomicLong4 = new AtomicLong();
   public final Map<UUID, EmoteSyncState> map41 = new ConcurrentHashMap<>();
   public final Map<UUID, ActiveEmote> map42 = new ConcurrentHashMap<>();
   public final Map<UUID, Long> map43 = new ConcurrentHashMap<>();
   public ClientWorld clientWorld2;
   public final MultiSelectSetting render = new MultiSelectSetting("cosmetics.emotes.render");

   public CosmeticManager() {
      new MultiSelectSetting.Option(this.render, "cosmetics.emotes.render.self", true);
      new MultiSelectSetting.Option(this.render, "cosmetics.emotes.render.friends", true);
      ClientTickEvents.END_CLIENT_TICK.register(this::on23);
   }

   public EmoteManager AutoTool() {
      return this.zClass114;
   }

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(var1 -> {
         try {
            var1.setAccessible(true);
            return var1.get(this);
         } catch (ReflectiveOperationException reflectiveoperationexception) {
            return null;
         }
      }).filter(Setting.class::isInstance).map(Setting.class::cast).collect(Collectors.toList());
   }

   public boolean BotChatEvent(String var1) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      return minecraftclient.player != null && this.on23(minecraftclient.player.getUuid(), var1, 0, EmoteLoopMode.call009);
   }

   public boolean on23(UUID var1, String var2, int var3, EmoteLoopMode var4) {
      if (var1 != null && var4 != null) {
         EmoteMetadata li1ll1i111l1l1iilli1111il = this.zClass114.find(var2).orElse(null);
         if (li1ll1i111l1l1iilli1111il == null) {
            return false;
         }

         ColorAnimator(() -> this.on23(var1, li1ll1i111l1l1iilli1111il, var3, var4));
         return true;
      } else {
         return false;
      }
   }

   public void on23(UUID var1, String var2, long var3, int var5) {
      if (var1 != null && var3 >= 0L) {
         ColorAnimator(() -> {
            long i = this.map43.getOrDefault(var1, -1L);
            if (var3 > i) {
               this.map43.put(var1, var3);
               if (var2 != null && !var2.isBlank()) {
                  EmoteMetadata li1ll1i111l1l1iilli1111il = this.zClass114.find(var2).orElse(null);
                  if (li1ll1i111l1l1iilli1111il == null) {
                     this.on23(var1, false);
                  } else {
                     this.on23(var1, li1ll1i111l1l1iilli1111il, var5, EmoteLoopMode.call268);
                  }
               } else {
                  this.on23(var1, false);
               }
            }
         });
      }
   }

   public void ItemSpec(UUID var1) {
      if (var1 != null) {
         ColorAnimator(() -> this.on23(var1, true));
      }
   }

   public boolean TextScanner(UUID var1) {
      ActiveEmote illlillllllliili1li11i11lill_ii1il11l111ii11iil = this.map42.get(var1);
      return illlillllllliili1li11i11lill_ii1il11l111ii11iil != null && illlillllllliili1li11i11lill_ii1il11l111ii11iil.playback().isActive();
   }

   public String NbtItemSpec(UUID var1) {
      EmoteSyncState illlillllllliili1li11i11lill_illi1l1l1 = this.map41.get(var1);
      return illlillllllliili1li11i11lill_illi1l1l1 == null ? "" : illlillllllliili1li11i11lill_illi1l1l1.EventTracker().id();
   }

   public CosmeticEntry AutoTrap() {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient.player == null) {
         return CosmeticEntry.var15Var143;
      } else {
         UUID uuid = minecraftclient.player.getUuid();
         EmoteSyncState illlillllllliili1li11i11lill_illi1l1l1 = this.map41.get(uuid);
         if (illlillllllliili1li11i11lill_illi1l1l1 != null && illlillllllliili1li11i11lill_illi1l1l1.FastBreak() == EmoteLoopMode.call009) {
            long i = Math.max(0L, (System.nanoTime() - illlillllllliili1li11i11lill_illi1l1l1.FakePlayer()) / 50000000L);
            int j = (int)Math.min(2147483647L, illlillllllliili1li11i11lill_illi1l1l1.Emotes() + i);
            return new CosmeticEntry(illlillllllliili1li11i11lill_illi1l1l1.EventTracker().id(), this.atomicLong4.get(), j);
         } else {
            return new CosmeticEntry("", this.atomicLong4.get(), 0);
         }
      }
   }

   public void AutoUse() {
      ColorAnimator(this::AutoWeb);
   }

   public void on23(UUID var1, EmoteMetadata var2, int var3, EmoteLoopMode var4) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient.world != this.clientWorld2) {
         this.AutoWeb();
         this.clientWorld2 = minecraftclient.world;
      }

      if (this.on23(minecraftclient, var1, var4)) {
         this.on23(var1, var4 == EmoteLoopMode.call009);
      } else {
         EmoteSyncState illlillllllliili1li11i11lill_illi1l1l1 = new EmoteSyncState(
            var2, Math.max(0, var3), System.nanoTime(), this.atomicLong3.incrementAndGet(), var4
         );
         this.map41.put(var1, illlillllllliili1li11i11lill_illi1l1l1);
         if (var4 == EmoteLoopMode.call009) {
            this.on23(minecraftclient, var1);
         }

         this.on23(minecraftclient, var1, illlillllllliili1li11i11lill_illi1l1l1);
      }
   }

   public void on23(MinecraftClient var1) {
      if (var1.world != this.clientWorld2) {
         this.AutoWeb();
         this.clientWorld2 = var1.world;
      }

      if (var1.world != null) {
         long i = System.nanoTime();

         for (Entry<UUID, EmoteSyncState> entry : new ArrayList<>(this.map41.entrySet())) {
            UUID uuid = entry.getKey();
            EmoteSyncState illlillllllliili1li11i11lill_illi1l1l1 = entry.getValue();
            if (this.on23(var1, uuid, illlillllllliili1li11i11lill_illi1l1l1.FastBreak())) {
               this.on23(uuid, illlillllllliili1li11i11lill_illi1l1l1.FastBreak() == EmoteLoopMode.call009);
            } else {
               AbstractClientPlayerEntity abstractclientplayerentity = on23(var1.world, uuid);
               if (abstractclientplayerentity == null) {
                  if (i - illlillllllliili1li11i11lill_illi1l1l1.FakePlayer() > 30000000000L) {
                     this.on23(uuid, illlillllllliili1li11i11lill_illi1l1l1.FastBreak() == EmoteLoopMode.call009);
                  }
               } else {
                  this.on23(abstractclientplayerentity, illlillllllliili1li11i11lill_illi1l1l1, i);
                  ActiveEmote illlillllllliili1li11i11lill_ii1il11l111ii11iil = this.map42.get(uuid);
                  if (illlillllllliili1li11i11lill_ii1il11l111ii11iil != null
                     && illlillllllliili1li11i11lill_ii1il11l111ii11iil.sequence() == illlillllllliili1li11i11lill_illi1l1l1.ContainerHelper()
                     && !illlillllllliili1li11i11lill_ii1il11l111ii11iil.playback().isActive()) {
                     this.on23(uuid, illlillllllliili1li11i11lill_illi1l1l1.FastBreak() == EmoteLoopMode.call009);
                  }
               }
            }
         }
      }
   }

   public void on23(MinecraftClient var1, UUID var2, EmoteSyncState var3) {
      if (var1.world != null) {
         AbstractClientPlayerEntity abstractclientplayerentity = on23(var1.world, var2);
         if (abstractclientplayerentity != null) {
            this.on23(abstractclientplayerentity, var3, System.nanoTime());
         }
      }
   }

   public void on23(AbstractClientPlayerEntity var1, EmoteSyncState var2, long var3) {
      UUID uuid = var1.getUuid();
      ActiveEmote illlillllllliili1li11i11lill_ii1il11l111ii11iil = this.map42.get(uuid);
      if (illlillllllliili1li11i11lill_ii1il11l111ii11iil == null
         || illlillllllliili1li11i11lill_ii1il11l111ii11iil.player() != var1
         || illlillllllliili1li11i11lill_ii1il11l111ii11iil.sequence() != var2.ContainerHelper()) {
         if (illlillllllliili1li11i11lill_ii1il11l111ii11iil != null) {
            illlillllllliili1li11i11lill_ii1il11l111ii11iil.playback().stop();
            illlillllllliili1li11i11lill_ii1il11l111ii11iil.layer().setAnim(null);
         }

         AnimationContainer<EmotePlayback> animationcontainer = new AnimationContainer<>();
         PlayerAnimationAccess.getPlayerAnimManager(var1).addAnimLayer(int371, animationcontainer);
         long i = Math.max(0L, (var3 - var2.FakePlayer()) / 50000000L);
         int j = (int)Math.min(2147483647L, var2.Emotes() + i);
         EmotePlayback iili11iiiil111lil = new EmotePlayback(var1, var2.EventTracker(), j);
         animationcontainer.setAnim(iili11iiiil111lil);
         this.map42.put(uuid, new ActiveEmote(var1, animationcontainer, iili11iiiil111lil, var2.ContainerHelper()));
      }
   }

   public boolean on23(MinecraftClient var1, UUID var2, EmoteLoopMode var3) {
      if (var3 == EmoteLoopMode.call268) {
         return !this.render.Event29("cosmetics.emotes.render.friends").isEnabled();
      }

      boolean flag = var1.player != null && var1.player.getUuid().equals(var2);
      return flag && !this.render.Event29("cosmetics.emotes.render.self").isEnabled();
   }

   public void on23(UUID var1, boolean var2) {
      EmoteSyncState illlillllllliili1li11i11lill_illi1l1l1 = this.map41.remove(var1);
      ActiveEmote illlillllllliili1li11i11lill_ii1il11l111ii11iil = this.map42.remove(var1);
      if (illlillllllliili1li11i11lill_ii1il11l111ii11iil != null) {
         illlillllllliili1li11i11lill_ii1il11l111ii11iil.playback().stop();
         illlillllllliili1li11i11lill_ii1il11l111ii11iil.layer().setAnim(null);
      }

      if (var2 && illlillllllliili1li11i11lill_illi1l1l1 != null && illlillllllliili1li11i11lill_illi1l1l1.FastBreak() == EmoteLoopMode.call009) {
         this.on23(MinecraftClient.getInstance(), var1);
      }
   }

   public void on23(MinecraftClient var1, UUID var2) {
      if (var1.player != null && var1.player.getUuid().equals(var2)) {
         this.atomicLong4.incrementAndGet();
      }
   }

   public void AutoWeb() {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      boolean flag = false;
      if (minecraftclient.player != null) {
         EmoteSyncState illlillllllliili1li11i11lill_illi1l1l1 = this.map41.get(minecraftclient.player.getUuid());
         flag = illlillllllliili1li11i11lill_illi1l1l1 != null && illlillllllliili1li11i11lill_illi1l1l1.FastBreak() == EmoteLoopMode.call009;
      }

      for (ActiveEmote illlillllllliili1li11i11lill_ii1il11l111ii11iil : this.map42.values()) {
         illlillllllliili1li11i11lill_ii1il11l111ii11iil.playback().stop();
         illlillllllliili1li11i11lill_ii1il11l111ii11iil.layer().setAnim(null);
      }

      this.map42.clear();
      this.map41.clear();
      this.map43.clear();
      if (flag) {
         this.atomicLong4.incrementAndGet();
      }
   }

   public static void ColorAnimator(Runnable var0) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (!minecraftclient.isOnThread()) {
         minecraftclient.execute(var0);
      } else {
         var0.run();
      }
   }

   public void close() {
      this.AutoUse();
   }

   public static AbstractClientPlayerEntity on23(ClientWorld var0, UUID var1) {
      return var0.getPlayerByUuid(var1) instanceof AbstractClientPlayerEntity abstractclientplayerentity ? abstractclientplayerentity : null;
   }
}
