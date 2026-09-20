package org.zenith.module.render;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.core.BotFollowEntity;
import org.zenith.core.BotGuardEntity;
import org.zenith.core.SpinMarker;
import org.zenith.event.EventHookTickEvent;
import org.zenith.event.EventHookWorldRender;
import org.zenith.managers.BotEntity;
import org.zenith.render.ParticleRenderer;
import org.zenith.render.ParticleTextures;
import org.zenith.setting.SettingGroup;
import org.zenith.setting.ColorSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.setting.Setting;
import org.zenith.util.ArgbColor;

@ModuleInfo(name = "World Particles", category = Category.RENDER, description = "Ambient частицы и светлячки в мире")
public final class WorldParticles extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final String string106 = "module.worldParticles.default";
   public static final String string107 = "module.worldParticles.spaceTest";
   public static final ArgbColor[] val480 = new ArgbColor[]{new ArgbColor(-1)};
   public static final WorldParticles worldParticles = new WorldParticles();
   public final ModeSetting renderMode = new ModeSetting(
      "module.worldParticles.renderMode", "module.worldParticles.renderMode.desc", "module.worldParticles.default", "module.worldParticles.spaceTest"
   );
   public final MultiSelectSetting modeSetting18 = double57();
   public final List<String> list90 = new ArrayList<>();
   public final MultiSelectSetting mode18 = new MultiSelectSetting("module.worldParticles.mode", "module.worldParticles.mode.desc");
   public final MultiSelectSetting.Option modeSettingVar15928 = new MultiSelectSetting.Option(this.mode18, "module.worldParticles.ambient", true);
   public final MultiSelectSetting.Option modeSettingVar15929 = new MultiSelectSetting.Option(this.mode18, "module.worldParticles.fireflies", true);
   public final NumberSetting count5 = new NumberSetting(
      "module.worldParticles.count",
      380.0F,
      20.0F,
      800.0F,
      20.0F,
      "module.worldParticles.particleCount.desc",
      "x",
      () -> !this.int324() && !this.list90.isEmpty() && this.modeSettingVar15928.isEnabled(),
      null
   );
   public final NumberSetting size6 = new NumberSetting(
      "module.worldParticles.size",
      0.8F,
      0.1F,
      6.0F,
      0.1F,
      "module.worldParticles.particleSize.desc",
      "x",
      () -> !this.int324() && !this.list90.isEmpty() && this.modeSettingVar15928.isEnabled(),
      null
   );
   public final ModeSetting physics2 = new ModeSetting(
      "module.worldParticles.physics",
      "module.worldParticles.physics.desc",
      () -> !this.int324() && !this.list90.isEmpty() && this.modeSettingVar15928.isEnabled(),
      "module.particles.drop",
      "module.particles.fly"
   );
   public final NumberSetting speed10 = new NumberSetting(
      "module.worldParticles.speed",
      0.9F,
      0.1F,
      3.0F,
      0.1F,
      "module.worldParticles.particleSpeed.desc",
      "x",
      () -> !this.int324() && !this.list90.isEmpty() && this.modeSettingVar15928.isEnabled(),
      null
   );
   public final ModeSetting color6 = new ModeSetting(
      "module.worldParticles.color",
      "module.worldParticles.colorMode.desc",
      () -> this.int324() || !this.list90.isEmpty(),
      "module.particles.sync",
      "module.particles.custom"
   );
   public final ColorSetting customColor7 = new ColorSetting(
      "module.worldParticles.customColor", "module.worldParticles.customColor.desc", ArgbColor.var11934, () -> this.color6.is(1)
   );
   public final NumberSetting ffCount = new NumberSetting(
      "module.worldParticles.ffCount",
      20.0F,
      5.0F,
      100.0F,
      5.0F,
      "module.worldParticles.fireFlyCount.desc",
      "x",
      () -> !this.int324() && this.modeSettingVar15929.isEnabled(),
      null
   );
   public final NumberSetting ffSize = new NumberSetting(
      "module.worldParticles.ffSize",
      0.6F,
      0.1F,
      2.0F,
      0.1F,
      "module.worldParticles.fireFlySize.desc",
      "x",
      () -> !this.int324() && this.modeSettingVar15929.isEnabled(),
      null
   );
   public final NumberSetting ffSpeed = new NumberSetting(
      "module.worldParticles.ffSpeed",
      1.4F,
      0.1F,
      2.0F,
      0.1F,
      "module.worldParticles.fireFlySpeed.desc",
      "x",
      () -> !this.int324() && this.modeSettingVar15929.isEnabled(),
      null
   );
   public final NumberSetting ffTrail = new NumberSetting(
      "module.worldParticles.ffTrail",
      7.5F,
      1.0F,
      15.0F,
      0.5F,
      "module.worldParticles.fireFlyTrailLength.desc",
      "b",
      () -> !this.int324() && this.modeSettingVar15929.isEnabled(),
      null
   );
   public final NumberSetting spaceCount = new NumberSetting(
      "module.worldParticles.spaceCount", 260.0F, 30.0F, 620.0F, 10.0F, "module.worldParticles.spaceCount.desc", "x", this::int324, null
   );
   public final NumberSetting spaceSize = new NumberSetting(
      "module.worldParticles.spaceSize", 0.72F, 0.1F, 2.8F, 0.05F, "module.worldParticles.spaceSize.desc", "x", this::int324, null
   );
   public final NumberSetting spaceSpeed = new NumberSetting(
      "module.worldParticles.spaceSpeed", 0.85F, 0.1F, 2.4F, 0.05F, "module.worldParticles.spaceSpeed.desc", "x", this::int324, null
   );
   public final NumberSetting spaceRange = new NumberSetting(
      "module.worldParticles.spaceRange", 52.0F, 16.0F, 86.0F, 2.0F, "module.worldParticles.spaceRange.desc", "b", this::int324, null
   );
   public final NumberSetting spaceTrail = new NumberSetting(
      "module.worldParticles.spaceTrail", 1.0F, 0.2F, 2.4F, 0.05F, "module.worldParticles.spaceTrailLength.desc", "x", this::int324, null
   );
   public final SettingGroup ambientCat = new SettingGroup(
      "module.worldParticles.ambientCat",
      "module.worldParticles.ambientCategory.desc",
      () -> !this.int324() && !this.list90.isEmpty() && this.modeSettingVar15928.isEnabled(),
      this.count5,
      this.size6,
      this.physics2,
      this.speed10
   );
   public final SettingGroup ffCat = new SettingGroup(
      "module.worldParticles.ffCat",
      "module.worldParticles.fireFliesCategory.desc",
      () -> !this.int324() && this.modeSettingVar15929.isEnabled(),
      this.ffCount,
      this.ffSize,
      this.ffSpeed,
      this.ffTrail
   );
   public final SettingGroup spaceCat = new SettingGroup(
      "module.worldParticles.spaceCat",
      "module.worldParticles.spaceCategory.desc",
      this::int324,
      this.spaceCount,
      this.spaceSize,
      this.spaceSpeed,
      this.spaceRange,
      this.spaceTrail
   );
   public final List<BotFollowEntity> list91 = new ArrayList<>();
   public final List<SpinMarker> list92 = new ArrayList<>();
   public final List<BotGuardEntity> list93 = new ArrayList<>();

   public WorldParticles() {
      this.modeSetting18.setVisible(() -> !this.int324());
      this.mode18.setVisible(() -> !this.int324());
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.renderMode, this.modeSetting18, this.mode18, this.ambientCat, this.ffCat, this.spaceCat, this.color6, this.customColor7);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.list91.clear();
      this.list92.clear();
      this.list93.clear();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.list91.clear();
      this.list92.clear();
      this.list93.clear();
   }

   @EventTarget
   public void on23(EventHookTickEvent var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         Vec3d vec3d = minecraftClient3.player.getEntityPos();
         if (this.int324()) {
            this.list91.clear();
            this.list92.clear();
            this.InventoryUtils(vec3d);
         } else {
            this.list93.clear();
            this.double58();
            if (this.modeSettingVar15928.isEnabled()) {
               this.CloudApiClient(vec3d);
            } else {
               this.list91.clear();
            }

            if (this.modeSettingVar15929.isEnabled()) {
               this.CloudUserProfile(vec3d);
            } else {
               this.list92.clear();
            }
         }
      }
   }

   @EventTarget
   public void on23(EventHookWorldRender var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         ParticleRenderer.on23(minecraftClient3.gameRenderer.getCamera());
         if (this.int324()) {
            if (!this.list93.isEmpty()) {
               ParticleRenderer.UiAnimation(var1.ClanUpgrade(), this.list93, var1.CropFarmer(), this.spaceTrail.getCurrent());
            }
         } else {
            if (!this.list91.isEmpty()) {
               ParticleRenderer.on23(var1.ClanUpgrade(), var1.CropFarmer(), this.list91);
            }

            if (!this.list92.isEmpty()) {
               ParticleRenderer.on23(var1.ClanUpgrade(), this.list92, var1.CropFarmer(), this.ffTrail.getCurrent());
            }
         }
      }
   }

   public void CloudApiClient(Vec3d var1) {
      if (this.list90.isEmpty()) {
         this.list91.clear();
      } else {
         int i = (int)this.count5.getCurrent();
         boolean flag = this.physics2.is(0);
         float f = this.speed10.getCurrent();
         this.list91.removeIf(BotEntity::float304);

         for (BotFollowEntity ii1li1ilii1l1ll1 : this.list91) {
            ii1li1ilii1l1ll1.on23(var1, flag, f);
         }

         while (this.list91.size() < i) {
            this.MediaTrackInfo(var1);
         }
      }
   }

   public void MediaTrackInfo(Vec3d var1) {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      Vec3d vec3d = new Vec3d(
         var1.x + threadlocalrandom.nextDouble(-48.0, 48.0),
         var1.y + threadlocalrandom.nextDouble(-10.0, 30.0),
         var1.z + threadlocalrandom.nextDouble(-48.0, 48.0)
      );
      Vec3d vec3d1;
      if (this.physics2.is(0)) {
         vec3d1 = new Vec3d(
            threadlocalrandom.nextDouble(-0.002, 0.002), threadlocalrandom.nextDouble(-0.005, -0.001), threadlocalrandom.nextDouble(-0.002, 0.002)
         );
      } else {
         vec3d1 = new Vec3d(threadlocalrandom.nextDouble(-0.01, 0.01), threadlocalrandom.nextDouble(-0.01, 0.01), threadlocalrandom.nextDouble(-0.01, 0.01));
      }

      int i = threadlocalrandom.nextInt(500, 1500);
      float f = this.size6.getCurrent();
      ArgbColor i11ii1llliilllii1i1 = this.EventClick(i * 2);
      String s = this.double59();
      if (s != null) {
         float f1 = threadlocalrandom.nextFloat(0.0F, 360.0F);
         float f2 = threadlocalrandom.nextBoolean() ? 0.0F : threadlocalrandom.nextFloat(-0.5F, 0.5F);
         this.list91.add(new BotFollowEntity(vec3d, vec3d1, i, f, i11ii1llliilllii1i1, s, f1, f2));
      }
   }

   public void CloudUserProfile(Vec3d var1) {
      int i = (int)this.ffCount.getCurrent();
      this.list92.removeIf(SpinMarker::float304);

      for (SpinMarker l11il1ilil1l : this.list92) {
         l11il1ilil1l.VelocityChangeEvent(var1);
      }

      while (this.list92.size() < i) {
         this.ModuleSnapshotDto(var1);
      }
   }

   public void ModuleSnapshotDto(Vec3d var1) {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      Vec3d vec3d = new Vec3d(
         var1.x + threadlocalrandom.nextDouble(-48.0, 48.0),
         var1.y + threadlocalrandom.nextDouble(-10.0, 30.0),
         var1.z + threadlocalrandom.nextDouble(-48.0, 48.0)
      );
      float f = this.ffSpeed.getCurrent();
      Vec3d vec3d1 = new Vec3d(
         threadlocalrandom.nextDouble(-0.2, 0.2) * f, threadlocalrandom.nextDouble(-0.2, 0.2) * f, threadlocalrandom.nextDouble(-0.2, 0.2) * f
      );
      int i = threadlocalrandom.nextInt(200, 400);
      float f1 = this.ffSize.getCurrent();
      ArgbColor i11ii1llliilllii1i1 = this.EventClick(i * 2);
      this.list92.add(new SpinMarker(vec3d, vec3d1, i, f1, i11ii1llliilllii1i1));
   }

   public void InventoryUtils(Vec3d var1) {
      int i = (int)this.spaceCount.getCurrent();
      float f = this.spaceSpeed.getCurrent();
      this.list93.removeIf(BotEntity::float304);

      while (this.list93.size() > i) {
         this.list93.removeLast();
      }

      for (BotGuardEntity ll1i1il1111liiil : this.list93) {
         ll1i1il1111liiil.ColorAnimator(var1, f);
      }

      while (this.list93.size() < i) {
         this.BotFeatureRegistry(var1);
      }
   }

   public void BotFeatureRegistry(Vec3d var1) {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      int i = threadlocalrandom.nextInt(720, 1680);
      int j = this.list93.size() * 43 + threadlocalrandom.nextInt(360);
      BotGuardEntity ll1i1il1111liiil = BotGuardEntity.on23(var1, this.spaceRange.getCurrent(), i, this.spaceSize.getCurrent(), this.EventHookTickEvent(j));
      this.list93.add(ll1i1il1111liiil);
   }

   public ArgbColor EventClick(int var1) {
      return this.color6.is(0) ? ZenithClient.on23().TextScanner().getClientColor(var1) : this.customColor7.getColor();
   }

   public ArgbColor EventHookTickEvent(int var1) {
      if (this.color6.is(1)) {
         return this.customColor7.getColor();
      }

      ArgbColor i11ii1llliilllii1i1 = ZenithClient.on23().TextScanner().getClientColor(var1);
      ArgbColor i11ii1llliilllii1i11 = val480[Math.floorMod(var1 / 73, val480.length)];
      return i11ii1llliilllii1i1.Easing(i11ii1llliilllii1i11, 0.55F).EventTick(0.12F);
   }

   public boolean int324() {
      return this.renderMode.is("module.worldParticles.spaceTest");
   }

   public void double58() {
      this.list90.clear();
      if (this.modeSetting18 != null) {
         for (MultiSelectSetting.Option i1i1lll1liii1il1llll1_ii1il11l111ii11iil : this.modeSetting18.int212()) {
            if (i1i1lll1liii1il1llll1_ii1il11l111ii11iil.isEnabled()) {
               this.list90.add(i1i1lll1liii1il1llll1_ii1il11l111ii11iil.getKey());
            }
         }
      }
   }

   public String double59() {
      return this.list90.isEmpty() ? null : this.list90.get(ThreadLocalRandom.current().nextInt(this.list90.size()));
   }

   public static MultiSelectSetting double57() {
      MultiSelectSetting i1i1lll1liii1il1llll1 = new MultiSelectSetting("module.worldParticles.textures", "module.worldParticles.textureSelection.desc");

      for (String s : ParticleTextures.getZClass019()) {
         new MultiSelectSetting.Option(i1i1lll1liii1il1llll1, s, s.equals("particle.texture.firefly"));
      }

      return i1i1lll1liii1il1llll1;
   }

   public NumberSetting int325() {
      return this.ffTrail;
   }
}
