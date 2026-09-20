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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.core.BotGotoEntity;
import org.zenith.event.AttackEntityEvent;
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

@ModuleInfo(name = "Hit Particles", category = Category.RENDER, description = "Частицы при ударе по Entity")
public final class HitParticles extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final HitParticles hitParticles = new HitParticles();
   public final MultiSelectSetting modeSetting9 = double57();
   public final List<String> list17 = new ArrayList<>();
   public final ModeSetting physics = new ModeSetting(
      "module.hitParticles.physics", "module.hitParticles.physics.desc", () -> true, "module.hitParticles.spiral", "module.particles.boom"
   );
   public final NumberSetting count = new NumberSetting(
      "module.hitParticles.count", 50.0F, 5.0F, 100.0F, 5.0F, "module.hitParticles.count.desc", "x", () -> true, null
   );
   public final NumberSetting speed2 = new NumberSetting(
      "module.hitParticles.speed", 0.9F, 0.1F, 3.0F, 0.1F, "module.hitParticles.speed.desc", "x", () -> !this.physics.is(0), null
   );
   public final NumberSetting size = new NumberSetting(
      "module.hitParticles.size", 0.2F, 0.1F, 3.0F, 0.05F, "module.hitParticles.size.desc", "x", () -> true, null
   );
   public final NumberSetting lifetime = new NumberSetting(
      "module.hitParticles.lifetime", 20.0F, 10.0F, 200.0F, 10.0F, "module.hitParticles.lifetime.desc", "t", () -> true, null
   );
   public final NumberSetting spiralCount = new NumberSetting(
      "module.hitParticles.spiralCount", 2.0F, 1.0F, 15.0F, 1.0F, "module.hitParticles.spiralCount.desc", "x", () -> this.physics.is(0), null
   );
   public final NumberSetting angularSpeed = new NumberSetting(
      "module.hitParticles.angularSpeed", 2.0F, 1.0F, 15.0F, 1.0F, "module.hitParticles.angularSpeed.desc", "x", () -> this.physics.is(0), null
   );
   public final NumberSetting radiusSpeed = new NumberSetting(
      "module.hitParticles.radiusSpeed", 3.0F, 1.0F, 15.0F, 1.0F, "module.hitParticles.radiusSpeed.desc", "x", () -> this.physics.is(0), null
   );
   public final ModeSetting color = new ModeSetting(
      "module.hitParticles.color", "module.hitParticles.colorMode.desc", () -> !this.list17.isEmpty(), "module.particles.sync", "module.particles.custom"
   );
   public final ColorSetting customColor2 = new ColorSetting(
      "module.hitParticles.customColor", "module.hitParticles.customColor.desc", ArgbColor.var11934, () -> this.color.is(1)
   );
   public final SettingGroup settingsCat = new SettingGroup(
      "module.hitParticles.settingsCat", "module.hitParticles.settings.desc", () -> true, this.count, this.speed2, this.size, this.lifetime
   );
   public final SettingGroup spiralCat = new SettingGroup(
      "module.hitParticles.spiralCat", "module.hitParticles.spiral.desc", () -> this.physics.is(0), this.spiralCount, this.angularSpeed, this.radiusSpeed
   );
   public final List<BotGotoEntity> list18 = new ArrayList<>();

   @Override
   public List<Setting> getSettings() {
      return List.of(this.modeSetting9, this.physics, this.settingsCat, this.spiralCat, this.color, this.customColor2);
   }

   public static MultiSelectSetting double57() {
      MultiSelectSetting i1i1lll1liii1il1llll1 = new MultiSelectSetting("module.hitParticles.textures", "module.hitParticles.textureSelection.desc");

      for (String s : ParticleTextures.getZClass019()) {
         new MultiSelectSetting.Option(i1i1lll1liii1il1llll1, s, s.equals("particle.texture.firefly"));
      }

      return i1i1lll1liii1il1llll1;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.list18.clear();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.list18.clear();
   }

   @EventTarget
   public void ColorAnimator(AttackEntityEvent var1) {
      if (var1.ElytraTarget() == AttackEntityEvent.on23.call077) {
         Entity entity = var1.ElytraMotion();
         if (entity instanceof LivingEntity) {
            Vec3d vec3d = entity.getBoundingBox().getCenter();
            if (this.physics.is(0)) {
               this.ItemServiceBase(entity);
            } else {
               this.StringCodec(vec3d);
            }
         }
      }
   }

   @EventTarget
   public void on23(EventHookTickEvent var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && !this.list18.isEmpty()) {
         Vec3d vec3d = minecraftClient3.player.getEntityPos();
         int i = this.physics.is(0) ? 2 : 1;
         this.list18.removeIf(BotEntity::float304);

         for (BotGotoEntity l1lill1i1llii : this.list18) {
            l1lill1i1llii.UiAnimation(vec3d, i);
         }
      }
   }

   @EventTarget
   public void on23(EventHookWorldRender var1) {
      if (!this.list18.isEmpty()) {
         ParticleRenderer.on23(minecraftClient3.gameRenderer.getCamera());
         ParticleRenderer.on23(var1.ClanUpgrade(), var1.CropFarmer(), this.list18);
      }
   }

   public void ItemServiceBase(Entity var1) {
      this.double58();
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      double d0 = var1.getBoundingBox().getCenter().x;
      double d1 = var1.getY();
      double d2 = var1.getBoundingBox().getCenter().z;
      double d3 = var1.getWidth();
      double d4 = var1.getHeight();
      int i = (int)this.spiralCount.getCurrent();
      int j = (int)this.count.getCurrent();
      int k = (int)this.lifetime.getCurrent();
      float f = this.angularSpeed.getCurrent();
      float f1 = this.radiusSpeed.getCurrent();
      int l = 1 + threadlocalrandom.nextInt(Math.min(i, 3));
      int i1 = Math.max(1, j / l);

      for (int j1 = 0; j1 < l; j1++) {
         double d5 = (Math.PI * 2) / l * j1 + threadlocalrandom.nextDouble(-0.5, 0.5);
         double d6 = d3 * threadlocalrandom.nextDouble(0.7, 1.3);
         int k1 = i + threadlocalrandom.nextInt(-1, 2);
         if (k1 < 1) {
            k1 = 1;
         }

         boolean flag = threadlocalrandom.nextBoolean();
         double d7 = threadlocalrandom.nextDouble(0.0, d4 * 0.3);

         for (int l1 = 0; l1 < i1; l1++) {
            double d8 = (double)l1 / i1;
            double d9 = d5 + d8 * k1 * 2.0 * Math.PI * (flag ? 1 : -1);
            double d10 = d6 + threadlocalrandom.nextDouble(-0.1, 0.1);
            double d11 = d0 + Math.cos(d9) * d10;
            double d12 = d1 + d7 + d8 * (d4 - d7) + threadlocalrandom.nextDouble(-0.1, 0.1);
            double d13 = d2 + Math.sin(d9) * d10;
            Vec3d vec3d = new Vec3d(d11, d12, d13);
            int i2 = threadlocalrandom.nextInt(k / 2, k);
            float f2 = this.size.getCurrent() * threadlocalrandom.nextFloat(0.7F, 1.3F);
            ArgbColor i11ii1llliilllii1i1 = this.EventClick((j1 * i1 + l1) * 10);
            String s = this.double59();
            if (s != null) {
               float f3 = threadlocalrandom.nextFloat(0.0F, 360.0F);
               float f4 = threadlocalrandom.nextFloat(-1.5F, 1.5F);
               float f5 = f * threadlocalrandom.nextFloat(0.7F, 1.3F) * (flag ? 1 : -1);
               float f6 = f1 * threadlocalrandom.nextFloat(0.6F, 1.4F);
               BotGotoEntity l1lill1i1llii = new BotGotoEntity(vec3d, Vec3d.ZERO, i2, f2, i11ii1llliilllii1i1, s, f3, f4);
               l1lill1i1llii.on23(var1, f5, f6);
               this.list18.add(l1lill1i1llii);
            }
         }
      }
   }

   public void StringCodec(Vec3d var1) {
      this.double58();
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      int i = (int)this.count.getCurrent();
      float f = this.speed2.getCurrent();

      for (int j = 0; j < i; j++) {
         double d0 = threadlocalrandom.nextDouble(0.0, Math.PI * 2);
         double d1 = threadlocalrandom.nextDouble(-Math.PI / 6, Math.PI / 3);
         double d2 = Math.cos(d1);
         double d3 = Math.sin(d1);
         Vec3d vec3d = var1.add(
            threadlocalrandom.nextDouble(-0.3, 0.3), threadlocalrandom.nextDouble(0.0, 0.5), threadlocalrandom.nextDouble(-0.3, 0.3)
         );
         Vec3d vec3d1 = new Vec3d(
            Math.cos(d0) * d2 * threadlocalrandom.nextDouble(0.033, 0.08) * f,
            d3 * threadlocalrandom.nextDouble(0.053, 0.107) * f,
            Math.sin(d0) * d2 * threadlocalrandom.nextDouble(0.033, 0.08) * f
         );
         int k = (int)this.lifetime.getCurrent();
         int l = threadlocalrandom.nextInt(k / 2, k);
         float f1 = this.size.getCurrent();
         ArgbColor i11ii1llliilllii1i1 = this.EventClick(j * 10);
         String s = this.double59();
         if (s != null) {
            float f2 = threadlocalrandom.nextFloat(0.0F, 360.0F);
            float f3 = threadlocalrandom.nextBoolean() ? 0.0F : threadlocalrandom.nextFloat(-1.0F, 1.0F);
            this.list18.add(new BotGotoEntity(vec3d, vec3d1, l, f1, i11ii1llliilllii1i1, s, f2, f3));
         }
      }
   }

   public ArgbColor EventClick(int var1) {
      return this.color.is(0) ? ZenithClient.on23().TextScanner().getClientColor(var1) : this.customColor2.getColor();
   }

   public void double58() {
      this.list17.clear();
      if (this.modeSetting9 != null) {
         for (MultiSelectSetting.Option i1i1lll1liii1il1llll1_ii1il11l111ii11iil : this.modeSetting9.int212()) {
            if (i1i1lll1liii1il1llll1_ii1il11l111ii11iil.isEnabled()) {
               this.list17.add(i1i1lll1liii1il1llll1_ii1il11l111ii11iil.getKey());
            }
         }
      }
   }

   public String double59() {
      return this.list17.isEmpty() ? null : this.list17.get(ThreadLocalRandom.current().nextInt(this.list17.size()));
   }
}
