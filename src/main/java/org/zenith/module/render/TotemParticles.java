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


import net.minecraft.util.math.Box;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.core.SelectionOutline;
import org.zenith.event.EventHookTickEvent;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.EventUpdateHealth;
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

@ModuleInfo(name = "Totem Particles", category = Category.RENDER, description = "Частицы при активации тотема")
public final class TotemParticles extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final TotemParticles totemParticles = new TotemParticles();
   public final MultiSelectSetting modeSetting17 = MultiSelectSetting.on23(
      "module.totemParticles.textures", "module.totemParticles.textureSelection.desc", List.of(ParticleTextures.getZClass019())
   );
   public final List<String> list79 = new ArrayList<>();
   public final NumberSetting count4 = new NumberSetting(
      "module.totemParticles.count", 50.0F, 10.0F, 200.0F, 10.0F, "module.totemParticles.count.desc", "x", () -> true, null
   );
   public final NumberSetting speed9 = new NumberSetting(
      "module.totemParticles.speed", 1.9F, 0.1F, 5.0F, 0.1F, "module.totemParticles.speed.desc", "x", () -> true, null
   );
   public final NumberSetting size5 = new NumberSetting(
      "module.totemParticles.size", 0.3F, 0.1F, 3.0F, 0.1F, "module.totemParticles.size.desc", "x", () -> true, null
   );
   public final NumberSetting lifetime4 = new NumberSetting(
      "module.totemParticles.lifetime", 120.0F, 20.0F, 300.0F, 10.0F, "module.totemParticles.lifetime.desc", "t", () -> true, null
   );
   public final NumberSetting interval = new NumberSetting(
      "module.totemParticles.interval", 1.0F, 1.0F, 10.0F, 1.0F, "module.totemParticles.interval.desc", "t", () -> true, null
   );
   public final ModeSetting color4 = new ModeSetting(
      "module.totemParticles.color",
      "module.totemParticles.colorMode.desc",
      () -> true,
      "module.totemParticles.vanilla",
      "module.particles.sync",
      "module.particles.custom"
   );
   public final ColorSetting customColor5 = new ColorSetting(
      "module.totemParticles.customColor", "module.totemParticles.customColor.desc", ArgbColor.var11934, () -> this.color4.is(2)
   );
   public final SettingGroup settingsCat2 = new SettingGroup(
      "module.totemParticles.settingsCat",
      "module.totemParticles.settings.desc",
      () -> true,
      this.count4,
      this.speed9,
      this.size5,
      this.lifetime4,
      this.interval,
      this.color4,
      this.customColor5
   );
   public final List<SelectionOutline> list80 = new ArrayList<>();
   public final List<TotemParticles.Service> list81 = new ArrayList<>();

   @Override
   public List<Setting> getSettings() {
      return List.of(this.modeSetting17, this.settingsCat2);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.list80.clear();
      this.list81.clear();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.list80.clear();
      this.list81.clear();
   }

   @EventTarget
   public void on23(EventUpdateHealth var1) {
      EntityStatusS2CPacket entitystatuss2cpacket = var1.NoDelay();
      if (entitystatuss2cpacket.getStatus() == 35) {
         Entity entity = entitystatuss2cpacket.getEntity(minecraftClient3.world);
         if (entity != null) {
            this.FileLogger(entity);
         }
      }
   }

   @EventTarget
   public void on23(EventHookTickEvent var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         Vec3d vec3d = minecraftClient3.player.getEntityPos();
         this.float142();
         this.list80.removeIf(SelectionOutline::float304);

         for (SelectionOutline iiii11ll111l1llilll1l1illil11 : this.list80) {
            double d0 = iiii11ll111l1llilll1l1illil11.WallBypass().distanceTo(vec3d);
            if (d0 > 64.0) {
               iiii11ll111l1llilll1l1illil11.EventTick(iiii11ll111l1llilll1l1illil11.var14360());
            } else {
               iiii11ll111l1llilll1l1illil11.update();
            }
         }
      }
   }

   @EventTarget
   public void on23(EventHookWorldRender var1) {
      if (!this.list80.isEmpty()) {
         ParticleRenderer.on23(minecraftClient3.gameRenderer.getCamera());
         ParticleRenderer.on23(var1.ClanUpgrade(), var1.CropFarmer(), this.list80);
      }
   }

   public void FileLogger(Entity var1) {
      int i = (int)this.count4.getCurrent();
      int j = (int)this.interval.getCurrent();

      for (int k = 0; k < i; k++) {
         this.list81.add(new TotemParticles.Service(var1, 1, k * j));
      }
   }

   public void float142() {
      this.double58();
      this.list81.removeIf(var1 -> {
         if (var1.val310 >= var1.int170) {
            Vec3d vec3d = var1.WallBypass();
            if (vec3d != null) {
               this.on23(vec3d, var1.val413);
            }

            return true;
         } else {
            var1.val310++;
            return false;
         }
      });
   }

   public void on23(Vec3d var1, int var2) {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      float f = this.speed9.getCurrent();
      int i = (int)this.lifetime4.getCurrent();

      for (int j = 0; j < var2; j++) {
         double d0 = threadlocalrandom.nextDouble(0.0, Math.PI * 2);
         double d1 = threadlocalrandom.nextDouble(Math.PI / 6, Math.PI / 3);
         double d2 = threadlocalrandom.nextDouble(0.015, 0.035) * f;
         double d3 = Math.sin(d1) * d2;
         Vec3d vec3d = new Vec3d(Math.cos(d0) * d3, -Math.abs(Math.cos(d1) * d2 / 4.0), Math.sin(d0) * d3);
         float f1 = this.size5.getCurrent() * threadlocalrandom.nextFloat(0.8F, 1.2F);
         ArgbColor i11ii1llliilllii1i1 = this.EventInjectAddEntity(j);
         String s = this.double59();
         if (s != null) {
            float f2 = threadlocalrandom.nextFloat(0.0F, 360.0F);
            this.list80.add(new SelectionOutline(var1, vec3d, i, f1, i11ii1llliilllii1i1, s, f2, 0.0F));
         }
      }
   }

   public ArgbColor EventInjectAddEntity(int var1) {
      if (this.color4.is(2)) {
         return this.customColor5.getColor();
      }

      if (this.color4.is(1)) {
         return ZenithClient.on23().TextScanner().getClientColor(var1 * 10);
      }

      int i = ThreadLocalRandom.current().nextInt(60, 120);
      return ArgbColor.FileLogger(i / 360.0F, 0.9F, 1.0F);
   }

   public void double58() {
      this.list79.clear();
      if (this.modeSetting17 != null) {
         for (MultiSelectSetting.Option i1i1lll1liii1il1llll1_ii1il11l111ii11iil : this.modeSetting17.int212()) {
            if (i1i1lll1liii1il1llll1_ii1il11l111ii11iil.isEnabled()) {
               this.list79.add(i1i1lll1liii1il1llll1_ii1il11l111ii11iil.getKey());
            }
         }
      }
   }

   public String double59() {
      return this.list79.isEmpty() ? null : this.list79.get(ThreadLocalRandom.current().nextInt(this.list79.size()));
   }


   public static class Service {
      final Entity val188;
      final int val413;
      final int int170;
      int val310;

      Service(Entity var1, int var2, int var3) {
         this.val188 = var1;
         this.val413 = var2;
         this.int170 = var3;
         this.val310 = 0;
      }

      Vec3d WallBypass() {
         if (this.val188 != null && !this.val188.isRemoved()) {
            Box box = this.val188.getBoundingBox();
            return new Vec3d((box.minX + box.maxX) / 2.0, (box.minY + box.maxY) / 2.0, (box.minZ + box.maxZ) / 2.0);
         } else {
            return null;
         }
      }
   }
}
