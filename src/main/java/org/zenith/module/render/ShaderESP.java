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

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.zenith.ZenithClient;
import org.zenith.core.EffectEngine;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ColorSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ArgbColor;

@ModuleInfo(name = "ShaderESP", category = Category.RENDER, description = "module.shaderESP.desc")
public final class ShaderESP extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final ShaderESP shaderESP = new ShaderESP();
   public final MultiSelectSetting modeSetting16 = MultiSelectSetting.on23(
      "module.shaderESP.targets",
      "module.shaderESP.targets.desc",
      List.of(
         "module.shaderESP.players",
         "module.shaderESP.noArmor",
         "module.shaderESP.hostiles",
         "module.shaderESP.passives",
         "module.shaderESP.items",
         "module.shaderESP.crystals"
      )
   );
   public final NumberSetting maxDistance2 = new NumberSetting(
      "module.shaderESP.maxDistance", 90.0F, 6.0F, 240.0F, 1.0F, "module.shaderESP.maxDistance.desc", "m"
   );
   public final BooleanSetting throughWalls2 = new BooleanSetting("module.shaderESP.throughWalls", true);
   public final BooleanSetting ignoreSelf = new BooleanSetting("module.shaderESP.ignoreSelf", true);
   public final NumberSetting timeSpeed2 = new NumberSetting("module.shaderESP.timeSpeed", 1.0F, 0.0F, 5.0F, 0.05F, "module.shaderESP.timeSpeed.desc", "x");
   public final ModeSetting colorMode2 = new ModeSetting(
      "module.shaderESP.colorMode", "module.shaderESP.colorMode.desc", "module.shaderESP.sync", "module.shaderESP.custom"
   );
   public final NumberSetting syncAlpha = new NumberSetting(
      "module.shaderESP.syncAlpha", 204.0F, 0.0F, 255.0F, 1.0F, "module.shaderESP.syncAlpha.desc", "a", () -> this.colorMode2.is(0), null
   );
   public final ColorSetting outlineColor2 = new ColorSetting("module.shaderESP.outlineColor", new ArgbColor(255, 255, 255));
   public final ColorSetting firstFillColor2 = new ColorSetting(
      "module.shaderESP.firstFillColor", new ArgbColor(170, 120, 255, 110), () -> this.colorMode2.is(1)
   );
   public final ColorSetting secondFillColor2 = new ColorSetting(
      "module.shaderESP.secondFillColor", new ArgbColor(60, 210, 255, 110), () -> this.colorMode2.is(1)
   );

   public boolean BotFeatureRegistry(Entity var1) {
      if (!this.isEnabled() || minecraftClient3.player == null || minecraftClient3.world == null || var1 == null) {
         return false;
      } else if (!this.ServiceException(var1) && !this.CloudRouter(var1)) {
         return !this.throughWalls2.isEnabled() && !this.AnalyticsTracker(var1) ? false : this.ProtocolMessage(var1);
      } else {
         return false;
      }
   }

   public boolean call278() {
      return this.throughWalls2.isEnabled();
   }

   public float call179() {
      return on23(this.timeSpeed2);
   }

   public int call279() {
      return this.outlineColor2.getIntColor();
   }

   public int float208() {
      return this.colorMode2.is(0)
         ? ZenithClient.on23().TextScanner().getCurrentStyle().getPrimaryColor().getColor().SprintStateEvent(this.syncAlpha.getCurrent() / 255.0F).call001()
         : this.firstFillColor2.getIntColor();
   }

   public int call280() {
      return this.colorMode2.is(0)
         ? ZenithClient.on23()
            .TextScanner()
            .getCurrentStyle()
            .getSecondaryPrimaryColor()
            .getColor()
            .SprintStateEvent(this.syncAlpha.getCurrent() / 255.0F)
            .call001()
         : this.secondFillColor2.getIntColor();
   }

   public static float on23(NumberSetting var0) {
      return var0.getCurrent();
   }

   public boolean ServiceException(Entity var1) {
      if (var1.isRemoved()) {
         return true;
      } else {
         return this.ignoreSelf.isEnabled() && var1 == minecraftClient3.player
            ? true
            : var1 == minecraftClient3.getCameraEntity() && minecraftClient3.options.getPerspective().isFirstPerson();
      }
   }

   public boolean CloudRouter(Entity var1) {
      float f = on23(this.maxDistance2);
      return minecraftClient3.player.squaredDistanceTo(var1) > f * f;
   }

   public boolean ProtocolMessage(Entity var1) {
      if (var1 instanceof PlayerEntity playerentity) {
         return EffectEngine.ItemServiceBase(playerentity) == 0.0F
            ? this.modeSetting16.RotationUpdateStartEvent("module.shaderESP.noArmor")
            : this.modeSetting16.RotationUpdateStartEvent("module.shaderESP.players");
      } else if (var1 instanceof EndCrystalEntity) {
         return this.modeSetting16.RotationUpdateStartEvent("module.shaderESP.crystals");
      } else if (var1 instanceof ItemEntity) {
         return this.modeSetting16.RotationUpdateStartEvent("module.shaderESP.items");
      } else if (var1 instanceof HostileEntity) {
         return this.modeSetting16.RotationUpdateStartEvent("module.shaderESP.hostiles");
      } else {
         return var1 instanceof AnimalEntity
            ? this.modeSetting16.RotationUpdateStartEvent("module.shaderESP.passives")
            : var1 instanceof LivingEntity && this.modeSetting16.RotationUpdateStartEvent("module.shaderESP.hostiles");
      }
   }

   public boolean AnalyticsTracker(Entity var1) {
      if (minecraftClient3.world != null && minecraftClient3.gameRenderer != null && minecraftClient3.gameRenderer.getCamera() != null) {
         Vec3d vec3d = minecraftClient3.gameRenderer.getCamera().getCameraPos();
         Box box = var1.getBoundingBox();
         Vec3d vec3d1 = box.getCenter();
         Vec3d vec3d2 = new Vec3d(vec3d1.x, Math.min(box.maxY - 0.05, var1.getEyeY()), vec3d1.z);
         Vec3d vec3d3 = new Vec3d(vec3d1.x, box.minY + 0.1, vec3d1.z);
         return this.ItemSpec(vec3d, vec3d1) || this.ItemSpec(vec3d, vec3d2) || this.ItemSpec(vec3d, vec3d3);
      } else {
         return true;
      }
   }

   public boolean ItemSpec(Vec3d var1, Vec3d var2) {
      BlockHitResult blockhitresult = minecraftClient3.world
         .raycast(new RaycastContext(var1, var2, ShapeType.COLLIDER, FluidHandling.NONE, minecraftClient3.player));
      return blockhitresult.getType() == Type.MISS || blockhitresult.getPos().squaredDistanceTo(var1) + 1.0E-4 >= var2.squaredDistanceTo(var1);
   }
}
