package org.zenith.module.misc;

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
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.base.font.Fonts;
import org.zenith.event.AttackEntityEvent;
import org.zenith.event.EventRenderScreenHook;
import org.zenith.event.EventTick;
import org.zenith.event.EventTriggerKeyEvent;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.setting.KeySetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.ScoreboardUtils;

@ModuleInfo(name = "FakePlayer", description = "", category = Category.MISC)
public class FakePlayer extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final FakePlayer fakePlayer = new FakePlayer();
   public final ModeSetting mode8 = new ModeSetting("Mode", "FakePlayer mode", "Default", "Teleportation");
   public final KeySetting startStopRecording = new KeySetting("Start/Stop Recording", "emty", -1);
   public final KeySetting startStopMoving = new KeySetting("Start/Stop Moving", "emty", -1);
   public final NumberSetting minDistance = new NumberSetting("Min Distance", 1.0F, 0.0F, 20.0F, 0.1F, "b", () -> this.mode8.is(1), null);
   public final NumberSetting maxDistance = new NumberSetting("Max Distance", 4.0F, 0.0F, 20.0F, 0.1F, "b", () -> this.mode8.is(1), null);
   public final NumberSetting minXZOffset = new NumberSetting("Min XZ Offset", 0.0F, 0.0F, 20.0F, 0.1F, "b", () -> this.mode8.is(1), null);
   public final NumberSetting maxXZOffset = new NumberSetting("Max XZ Offset", 4.0F, 0.0F, 20.0F, 0.1F, "b", () -> this.mode8.is(1), null);
   public final NumberSetting minYOffset = new NumberSetting("Min Y Offset", 0.0F, 0.0F, 10.0F, 0.1F, "b", () -> this.mode8.is(1), null);
   public final NumberSetting maxYOffset = new NumberSetting("Max Y Offset", 0.0F, 0.0F, 10.0F, 0.1F, "b", () -> this.mode8.is(1), null);
   public final NumberSetting minIdlingTicks = new NumberSetting("Min Idling Ticks", 5.0F, 0.0F, 200.0F, 1.0F, "t", () -> this.mode8.is(1), null);
   public final NumberSetting maxIdlingTicks = new NumberSetting("Max Idling Ticks", 20.0F, 0.0F, 200.0F, 1.0F, "t", () -> this.mode8.is(1), null);
   public boolean boolean51 = false;
   public boolean boolean52 = false;
   public int int97 = 0;
   public int int98 = 0;
   public int int99 = 0;
   public int int100 = Integer.MAX_VALUE;
   public OtherClientPlayerEntity otherClientPlayerEntity;
   public final List<FakePlayer.Snapshot> list15 = new ArrayList<>();

   @EventTarget
   public void UiAnimation(EventTick var1) {
      if (this.otherClientPlayerEntity != null && minecraftClient3.player != null) {
         if (this.int99 != this.mode8.getIndex()) {
            this.int99 = this.mode8.getIndex();
            this.int97 = 0;
            this.boolean51 = false;
            this.boolean52 = false;
            this.int98 = 0;
         }

         if (this.mode8.is(1)) {
            this.int373();
         } else if (this.boolean51) {
            this.list15
               .add(
                  new FakePlayer.Snapshot(
                     minecraftClient3.player.getX(),
                     minecraftClient3.player.getY(),
                     minecraftClient3.player.getZ(),
                     minecraftClient3.player.getYaw(),
                     minecraftClient3.player.getPitch(),
                     minecraftClient3.player.isSwimming(),
                     minecraftClient3.player.isSneaking()
                  )
               );
         } else if (this.boolean52 && !this.list15.isEmpty()) {
            this.int97++;
            if (this.int97 >= this.list15.size()) {
               this.int97 = 0;
               return;
            }

            FakePlayer.Snapshot l1i1llll1lliil11llll_ii1il11l111ii11iil = this.list15.get(this.int97);
            this.otherClientPlayerEntity.setYaw(l1i1llll1lliil11llll_ii1il11l111ii11iil.float87());
            this.otherClientPlayerEntity.setPitch(l1i1llll1lliil11llll_ii1il11l111ii11iil.float88());
            this.otherClientPlayerEntity.setHeadYaw(l1i1llll1lliil11llll_ii1il11l111ii11iil.float87());
            this.otherClientPlayerEntity.setSwimming(l1i1llll1lliil11llll_ii1il11l111ii11iil.boolean113());
            this.otherClientPlayerEntity.setSneaking(l1i1llll1lliil11llll_ii1il11l111ii11iil.boolean114());
            this.otherClientPlayerEntity
               .setPose(
                  l1i1llll1lliil11llll_ii1il11l111ii11iil.boolean113()
                     ? EntityPose.SWIMMING
                     : (l1i1llll1lliil11llll_ii1il11l111ii11iil.boolean114() ? EntityPose.CROUCHING : EntityPose.STANDING)
               );
            this.otherClientPlayerEntity
               .updateTrackedPosition(
                  l1i1llll1lliil11llll_ii1il11l111ii11iil.double42(),
                  l1i1llll1lliil11llll_ii1il11l111ii11iil.double43(),
                  l1i1llll1lliil11llll_ii1il11l111ii11iil.double44()
               );
            this.otherClientPlayerEntity
               .updateTrackedPositionAndAngles(
                  new Vec3d(
                     l1i1llll1lliil11llll_ii1il11l111ii11iil.double42(),
                     l1i1llll1lliil11llll_ii1il11l111ii11iil.double43(),
                     l1i1llll1lliil11llll_ii1il11l111ii11iil.double44()
                  ),
                  l1i1llll1lliil11llll_ii1il11l111ii11iil.float87(),
                  l1i1llll1lliil11llll_ii1il11l111ii11iil.float88()
               );
         } else {
            this.int97 = 0;
         }
      }
   }

   @Override
   public void onEnable() {
      if (minecraftClient3.world != null) {
         if (minecraftClient3.player != null) {
            this.UiAnimation(minecraftClient3.player.getEntityPos(), minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch());
         } else {
            this.UiAnimation(Vec3d.ZERO, 0.0F, 0.0F);
         }

         this.boolean51 = false;
         this.boolean52 = false;
         this.int97 = 0;
         this.int98 = 0;
         this.int99 = this.mode8.getIndex();
         super.onEnable();
      }
   }

   @Override
   public void onDisable() {
      this.on23(RemovalReason.KILLED);
      this.boolean51 = false;
      this.boolean52 = false;
      this.int97 = 0;
      this.int98 = 0;
      super.onDisable();
   }

   @EventTarget
   public void on23(AttackEntityEvent var1) {
   }

   @EventTarget
   public void Easing(EventTriggerKeyEvent var1) {
      if (this.mode8.is(0)) {
         if (var1.ItemRegistry(this.startStopRecording.getKeyCode())) {
            this.boolean51 = !this.boolean51;
            if (this.boolean51) {
               this.list15.clear();
            }
         }

         if (var1.ItemRegistry(this.startStopMoving.getKeyCode())) {
            this.boolean52 = !this.boolean52;
         }
      }
   }

   @EventTarget
   public void Easing(EventRenderScreenHook var1) {
      if (this.mode8.is(0)) {
         var1.WarpFarm()
            .drawText(
               Fonts.NEW_MEDIUM.getFont(10.0F),
               "Recoding " + this.boolean51 + "  " + ScoreboardUtils.EventPosHook(this.startStopRecording.getKeyCode()),
               20.0F,
               20.0F,
               ArgbColor.var11934
            );
         var1.WarpFarm()
            .drawText(
               Fonts.NEW_MEDIUM.getFont(10.0F),
               "Moving " + this.boolean52 + "  " + ScoreboardUtils.EventPosHook(this.startStopMoving.getKeyCode()),
               20.0F,
               35.0F,
               ArgbColor.var11934
            );
      } else {
         var1.WarpFarm().drawText(Fonts.NEW_MEDIUM.getFont(10.0F), "Teleportation idle: " + this.int98 + "t", 20.0F, 20.0F, ArgbColor.var11934);
      }
   }

   public void int373() {
      if (minecraftClient3.player != null) {
         if (this.int98 > 0) {
            this.int98--;
         } else {
            Vec3d vec3d = minecraftClient3.player.getEntityPos();
            double d0 = this.on23(this.minDistance, this.maxDistance);
            Vec3d vec3d1 = this.ItemServiceBase(d0);
            Vec3d vec3d2 = vec3d.add(vec3d1.x, this.Easing(this.minYOffset, this.maxYOffset), vec3d1.z);
            float f = this.ColorAnimator(vec3d2, vec3d);
            float f1 = 0.0F;
            this.on23(vec3d2, f, f1);
            this.int98 = this.UiAnimation(this.minIdlingTicks, this.maxIdlingTicks);
         }
      }
   }

   public void on23(Vec3d var1, float var2, float var3) {
      this.on23(RemovalReason.DISCARDED);
      this.UiAnimation(var1, var2, var3);
   }

   public void UiAnimation(Vec3d var1, float var2, float var3) {
      if (minecraftClient3.world != null) {
         this.otherClientPlayerEntity = new OtherClientPlayerEntity(minecraftClient3.world, new GameProfile(UUID.randomUUID(), "Fake"));
         this.otherClientPlayerEntity.setId(this.int374());
         this.otherClientPlayerEntity.refreshPositionAndAngles(var1, var2, var3);
         this.otherClientPlayerEntity.setHeadYaw(var2);
         this.otherClientPlayerEntity.bodyYaw = var2;
         this.otherClientPlayerEntity.lastBodyYaw = var2;
         this.otherClientPlayerEntity.lastHeadYaw = var2;
         minecraftClient3.world.addEntity(this.otherClientPlayerEntity);
      }
   }

   public void on23(RemovalReason var1) {
      if (this.otherClientPlayerEntity != null) {
         if (minecraftClient3.world != null
            && minecraftClient3.world.getEntityById(this.otherClientPlayerEntity.getId()) == this.otherClientPlayerEntity) {
            minecraftClient3.world.removeEntity(this.otherClientPlayerEntity.getId(), var1);
         } else {
            this.otherClientPlayerEntity.setRemoved(var1);
            this.otherClientPlayerEntity.onRemoved();
         }

         this.otherClientPlayerEntity = null;
      }
   }

   public int int374() {
      for (int i = 0; i < 1000000; i++) {
         if (this.int100 <= 2146483647) {
            this.int100 = Integer.MAX_VALUE;
         }

         int j = --this.int100;
         if (minecraftClient3.world == null || minecraftClient3.world.getEntityById(j) == null) {
            return j;
         }
      }

      return Integer.MAX_VALUE - ThreadLocalRandom.current().nextInt(1, 1000000);
   }

   public Vec3d ItemServiceBase(double var1) {
      if (var1 <= 0.0) {
         return Vec3d.ZERO;
      }

      double d0 = Math.min(this.on23(this.minXZOffset, this.maxXZOffset), var1);
      double d1 = Math.sqrt(var1 * var1 - d0 * d0);
      double d2 = this.NbtEditor(d1);
      double d3 = this.NbtEditor(d0);
      return ThreadLocalRandom.current().nextBoolean() ? new Vec3d(d2, 0.0, d3) : new Vec3d(d3, 0.0, d2);
   }

   public float ColorAnimator(Vec3d var1, Vec3d var2) {
      double d0 = var2.x - var1.x;
      double d1 = var2.z - var1.z;
      return MathHelper.wrapDegrees((float)Math.toDegrees(Math.atan2(d1, d0)) - 90.0F);
   }

   public float on23(NumberSetting var1, NumberSetting var2) {
      float f = Math.min(var1.getCurrent(), var2.getCurrent());
      float f1 = Math.max(var1.getCurrent(), var2.getCurrent());
      return f == f1 ? f : (float)ThreadLocalRandom.current().nextDouble(f, f1);
   }

   public int UiAnimation(NumberSetting var1, NumberSetting var2) {
      int i = Math.round(Math.min(var1.getCurrent(), var2.getCurrent()));
      int j = Math.round(Math.max(var1.getCurrent(), var2.getCurrent()));
      return i >= j ? i : ThreadLocalRandom.current().nextInt(i, j + 1);
   }

   public double Easing(NumberSetting var1, NumberSetting var2) {
      float f = this.on23(var1, var2);
      return f <= 0.0F ? 0.0 : this.NbtEditor(f);
   }

   public double NbtEditor(double var1) {
      return ThreadLocalRandom.current().nextBoolean() ? var1 : -var1;
   }

   @EventTarget
   public void UiAnimation(AttackEntityEvent var1) {
      if (this.otherClientPlayerEntity != null
         && minecraftClient3.player != null
         && var1.ElytraTarget() == AttackEntityEvent.on23.call077
         && this.otherClientPlayerEntity.isDead()) {
         this.otherClientPlayerEntity.setHealth(20.0F);
         new EntityStatusS2CPacket(this.otherClientPlayerEntity, (byte)35).apply(minecraftClient3.player.networkHandler);
      }
   }


   public record Snapshot(double double42, double double43, double double44, float float87, float float88, boolean boolean113, boolean boolean114) {
      public double x() {
         return this.double42;
      }

      public double y() {
         return this.double43;
      }

      public double z() {
         return this.double44;
      }

      public float yaw() {
         return this.float87;
      }

      public float pitch() {
         return this.float88;
      }

      public boolean call175() {
         return this.boolean113;
      }

      public boolean int378() {
         return this.boolean114;
      }
   }
}
