package org.zenith.module.combat;

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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.event.EventHookPacketProcess;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.EventTick;
import org.zenith.event.PacketEvent;
import org.zenith.event.PacketSendEvent;
import org.zenith.managers.TargetSelector;
import org.zenith.render.WorldRender;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.CooldownTimer;

@ModuleInfo(name = "FakeLag", description = "Delays outgoing packets near targets", category = Category.COMBAT)
public final class FakeLag extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final Queue<FakeLag.QueuedPacket> queue3;
   public static final List list14 = new ArrayList();
   public final MultiSelectSetting.Option modeSettingVar1597;
   public final MultiSelectSetting.Option modeSettingVar1598;
   public final ModeSetting.Option modeSetting3Var15934;
   public final ModeSetting modeSetting32;
   public final NumberSetting numberSetting2;
   public final ModeSetting.Option modeSetting3Var15935;
   public final CooldownTimer zClass06728;
   public final MultiSelectSetting modeSetting8;
   public static final FakeLag fakeLag = new FakeLag();
   public final NumberSetting minRange = new NumberSetting("module.fakeLag.minRange", 2.0F, 0.0F, 10.0F, 0.1F, "module.fakeLag.minRange.desc", "b");
   public boolean boolean49;
   public final NumberSetting numberSetting3;
   public final MultiSelectSetting.Option modeSettingVar1599;
   public final NumberSetting numberSetting4;
   public boolean boolean50;
   public final BooleanSetting booleanSetting3;
   public long long83;
   public final NumberSetting maxRange = new NumberSetting("module.fakeLag.maxRange", 5.0F, 0.0F, 10.0F, 0.1F, "module.fakeLag.maxRange.desc", "b");

   public FakeLag() {
      this.numberSetting4 = new NumberSetting("module.fakeLag.minDelay", 300.0F, 0.0F, 1000.0F, 25.0F, "module.fakeLag.minDelay.desc", "ms");
      this.numberSetting2 = new NumberSetting("module.fakeLag.maxDelay", 600.0F, 0.0F, 1000.0F, 25.0F, "module.fakeLag.maxDelay.desc", "ms");
      this.numberSetting3 = new NumberSetting("module.fakeLag.recoilTime", 250.0F, 0.0F, 1000.0F, 25.0F, "module.fakeLag.recoilTime.desc", "ms");
      this.booleanSetting3 = new BooleanSetting("module.fakeLag.renderBox", "module.fakeLag.renderBox.desc", true);
      this.modeSetting32 = new ModeSetting("module.fakeLag.mode", "module.fakeLag.mode.desc");
      this.modeSetting3Var15935 = new ModeSetting.Option(this.modeSetting32, "module.fakeLag.mode.constant");
      this.modeSetting3Var15934 = new ModeSetting.Option(this.modeSetting32, "module.fakeLag.mode.dynamic").int210();
      this.modeSetting8 = new MultiSelectSetting("module.fakeLag.flushOn", "module.fakeLag.flushOn.desc");
      this.modeSettingVar1597 = new MultiSelectSetting.Option(this.modeSetting8, "module.fakeLag.flushEntityInteract", true);
      this.modeSettingVar1599 = new MultiSelectSetting.Option(this.modeSetting8, "module.fakeLag.flushBlockInteract", true);
      this.modeSettingVar1598 = new MultiSelectSetting.Option(this.modeSetting8, "module.fakeLag.flushAction", true);
      this.queue3 = new ConcurrentLinkedQueue<>();
      this.zClass06728 = new CooldownTimer();
      this.long83 = this.call096();
   }

   @Override
   public void onEnable() {
      this.queue3.clear();
      this.boolean50 = false;
      this.boolean49 = false;
      this.long83 = this.call096();
      this.zClass06728.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.boolean174();
      this.boolean50 = false;
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         this.boolean50 = this.NbtItemSpec(minecraftClient3.player.getEntityPos()) != null;
      } else {
         this.boolean50 = false;
         this.call012();
      }
   }

   @EventTarget
   public void UiAnimation(EventHookPacketProcess var1) {
      if (minecraftClient3.player == null || minecraftClient3.world == null) {
         this.call012();
      } else if (!this.queue3.isEmpty()) {
         if (this.call127() || !this.zClass06728.EventModifyMouseRotationInput((long)this.numberSetting3.getCurrent())) {
            this.call058();
         } else if (this.call097()) {
            this.call098();
         } else if (this.call059() || this.call037()) {
            this.call058();
         }
      }
   }

   @EventTarget
   public void Easing(EventHookWorldRender var1) {
      if (this.booleanSetting3.isEnabled() && minecraftClient3.player != null) {
         Vec3d vec3d = this.call015();
         if (vec3d != null) {
            WorldRender.on23(minecraftClient3.player.getDimensions(minecraftClient3.player.getPose()).getBoxAt(vec3d), -1, 1.0F);
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.ItemScroller() != null && var1.Arrows()) {
         this.ItemRegistry(var1.ItemScroller());
      }
   }

   @EventTarget(3)
   public void on23(PacketSendEvent var1) {
      if (!var1.isCancelled() && !this.boolean49 && !Blink.blink.call045() && !TrapTp.trapTp.call045() && var1.ItemScroller() != null) {
         if (minecraftClient3.player != null && minecraftClient3.world != null) {
            Packet packet = var1.ItemScroller();
            if (this.call127()) {
               this.call058();
            } else if (!this.zClass06728.EventModifyMouseRotationInput((long)this.numberSetting3.getCurrent())) {
               this.call058();
            } else if (this.call097()) {
               this.call098();
            } else if (this.TextScanner(packet)) {
               this.call060();
            } else if (this.call059() || this.call037()) {
               this.call058();
            } else if (!this.ItemSpec(packet)) {
               var1.cancel();
               this.queue3.add(new FakeLag.QueuedPacket(packet, this.ColorAnimator(packet)));
            }
         } else {
            this.call012();
         }
      }
   }

   public void ItemRegistry(Packet<?> var1) {
      if (var1 instanceof DisconnectS2CPacket || var1 instanceof PlayerPositionLookS2CPacket) {
         this.call012();
         this.zClass06728.reset();
      } else if (var1 instanceof HealthUpdateS2CPacket) {
         this.call060();
      } else {
         if (var1 instanceof EntityVelocityUpdateS2CPacket entityvelocityupdates2cpacket
            && minecraftClient3.player != null
            && entityvelocityupdates2cpacket.getEntityId() == minecraftClient3.player.getId()
            && (
               entityvelocityupdates2cpacket.getVelocity().lengthSquared() != 0.0
            )) {
            this.call060();
            return;
         }

         if (var1 instanceof ExplosionS2CPacket explosions2cpacket) {
            explosions2cpacket.playerKnockback().ifPresent(var1x -> {
               if (var1x.x != 0.0 || var1x.y != 0.0 || var1x.z != 0.0) {
                  this.call060();
               }
            });
         }
      }
   }

   public boolean ItemSpec(Packet<?> var1) {
      return var1 instanceof ChatMessageC2SPacket || var1 instanceof CommandExecutionC2SPacket;
   }

   public boolean TextScanner(Packet<?> var1) {
      if (var1 instanceof ResourcePackStatusC2SPacket) {
         return true;
      } else if (!this.modeSettingVar1597.isEnabled() || !(var1 instanceof PlayerInteractEntityC2SPacket) && !(var1 instanceof HandSwingC2SPacket)) {
         return !this.modeSettingVar1599.isEnabled() || !(var1 instanceof PlayerInteractBlockC2SPacket) && !(var1 instanceof UpdateSignC2SPacket)
            ? this.modeSettingVar1598.isEnabled() && var1 instanceof PlayerActionC2SPacket
            : true;
      } else {
         return true;
      }
   }

   public boolean call127() {
      return minecraftClient3.player == null
         || minecraftClient3.world == null
         || minecraftClient3.player.isDead()
         || minecraftClient3.player.isTouchingWater()
         || minecraftClient3.currentScreen != null;
   }

   public boolean call037() {
      if (this.modeSetting3Var15935.isSelected()) {
         return false;
      }

      if (this.modeSetting3Var15934.isSelected() && this.boolean50) {
         Vec3d vec3d = this.call015();
         if (vec3d == null) {
            return false;
         }

         if (minecraftClient3.player == null) {
            return true;
         }

         LivingEntity livingentity = this.EnchantItemSpec(vec3d);
         if (livingentity == null) {
            return true;
         }

         Box box = minecraftClient3.player.getDimensions(minecraftClient3.player.getPose()).getBoxAt(vec3d);
         boolean flag = livingentity.getBoundingBox().intersects(box);
         double d0 = livingentity.getEntityPos().distanceTo(vec3d);
         double d1 = livingentity.getEntityPos().distanceTo(minecraftClient3.player.getEntityPos());
         return d0 < d1 || flag;
      } else {
         return true;
      }
   }

   public LivingEntity NbtItemSpec(Vec3d var1) {
      double d0 = Math.min(this.minRange.getCurrent(), this.maxRange.getCurrent());
      double d1 = Math.max(this.minRange.getCurrent(), this.maxRange.getCurrent());
      double d2 = d0 * d0;
      double d3 = d1 * d1;
      LivingEntity livingentity = null;
      double d4 = Double.MAX_VALUE;

      for (LivingEntity livingentity1 : this.SimpleItemBuilder(var1)) {
         if (!this.UiAnimation(livingentity1)) {
            double d5 = livingentity1.squaredDistanceTo(var1);
            if (d5 >= d2 && d5 <= d3 && d5 < d4) {
               livingentity = livingentity1;
               d4 = d5;
            }
         }
      }

      return livingentity;
   }

   public LivingEntity EnchantItemSpec(Vec3d var1) {
      double d0 = Math.max(this.minRange.getCurrent(), this.maxRange.getCurrent());
      double d1 = d0 * d0;
      LivingEntity livingentity = null;
      double d2 = Double.MAX_VALUE;

      for (LivingEntity livingentity1 : this.SimpleItemBuilder(var1)) {
         if (!this.UiAnimation(livingentity1)) {
            double d3 = livingentity1.getEntityPos().squaredDistanceTo(var1);
            if (d3 <= d1 && d3 < d2) {
               livingentity = livingentity1;
               d2 = d3;
            }
         }
      }

      return livingentity;
   }

   public Iterable<LivingEntity> SimpleItemBuilder(Vec3d var1) {
      if (minecraftClient3.world == null) {
         return List.of();
      }

      double d0 = Math.max(this.minRange.getCurrent(), this.maxRange.getCurrent()) + 2.0;
      Box box = new Box(
         var1.x - d0, var1.y - d0, var1.z - d0, var1.x + d0, var1.y + d0, var1.z + d0
      );
      return minecraftClient3.world.getEntitiesByClass(LivingEntity.class, box, var0 -> true);
   }

   public boolean UiAnimation(LivingEntity var1) {
      return var1 instanceof ArmorStandEntity || !TargetSelector.on23(list14, var1);
   }

   public Vec3d ColorAnimator(Packet<?> var1) {
      if (var1 instanceof PlayerMoveC2SPacket playermovec2spacket && minecraftClient3.player != null) {
         double d0 = playermovec2spacket.getX(Double.NaN);
         double d1 = playermovec2spacket.getY(Double.NaN);
         double d2 = playermovec2spacket.getZ(Double.NaN);
         return !Double.isNaN(d0) && !Double.isNaN(d1) && !Double.isNaN(d2) ? new Vec3d(d0, d1, d2) : null;
      } else {
         return null;
      }
   }

   public Vec3d call015() {
      for (FakeLag.QueuedPacket lilil1i111ll111li11l1l1_ii1il11l111ii11iil : this.queue3) {
         if (lilil1i111ll111li11l1l1_ii1il11l111ii11iil.call047() != null) {
            return lilil1i111ll111li11l1l1_ii1il11l111ii11iil.call047();
         }
      }

      return null;
   }

   public boolean call097() {
      FakeLag.QueuedPacket lilil1i111ll111li11l1l1_ii1il11l111ii11iil = (FakeLag.QueuedPacket)this.queue3.peek();
      return lilil1i111ll111li11l1l1_ii1il11l111ii11iil != null
         && System.currentTimeMillis() - lilil1i111ll111li11l1l1_ii1il11l111ii11iil.int392() >= this.long83;
   }

   public long call096() {
      int i = Math.round(Math.min(this.numberSetting4.getCurrent(), this.numberSetting2.getCurrent()));
      int j = Math.round(Math.max(this.numberSetting4.getCurrent(), this.numberSetting2.getCurrent()));
      return j <= i ? i : ThreadLocalRandom.current().nextInt(i, j + 1);
   }

   public boolean call059() {
      if (minecraftClient3.player != null && minecraftClient3.player.isUsingItem()) {
         ItemStack itemstack = minecraftClient3.player.getActiveItem();
         return itemstack.contains(DataComponentTypes.FOOD)
            || itemstack.isOf(Items.MILK_BUCKET)
            || itemstack.isOf(Items.POTION)
            || itemstack.isOf(Items.SPLASH_POTION)
            || itemstack.isOf(Items.LINGERING_POTION);
      } else {
         return false;
      }
   }

   public void call060() {
      this.boolean174();
      this.zClass06728.reset();
   }

   public void call058() {
      this.boolean174();
   }

   public void call098() {
      this.boolean174();
      this.long83 = this.call096();
   }

   public void boolean174() {
      if (!this.queue3.isEmpty() && minecraftClient3.getNetworkHandler() != null) {
         this.boolean49 = true;

         FakeLag.QueuedPacket lilil1i111ll111li11l1l1_ii1il11l111ii11iil;
         try {
            while ((lilil1i111ll111li11l1l1_ii1il11l111ii11iil = (FakeLag.QueuedPacket)this.queue3.poll()) != null) {
               minecraftClient3.getNetworkHandler().sendPacket(lilil1i111ll111li11l1l1_ii1il11l111ii11iil.call076());
            }
         } finally {
            this.boolean49 = false;
         }
      } else {
         this.queue3.clear();
      }
   }

   public void call012() {
      this.queue3.clear();
   }


   public record QueuedPacket(Packet<?> packet5, Vec3d vec3d31, long long119) {
      public QueuedPacket(Packet<?> var1, Vec3d var2) {
         this(var1, var2, System.currentTimeMillis());
      }

      public Packet<?> call076() {
         return this.packet5;
      }

      public Vec3d call047() {
         return this.vec3d31;
      }

      public long int392() {
         return this.long119;
      }
   }
}
